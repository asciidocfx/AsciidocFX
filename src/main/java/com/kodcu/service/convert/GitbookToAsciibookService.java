package com.kodcu.service.convert;

import com.kodcu.controller.ApplicationController;
import com.kodcu.other.IOHelper;
import com.kodcu.service.DirectoryService;
import com.kodcu.service.ThreadService;
import com.kodcu.service.ui.TabService;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.nio.file.StandardOpenOption.*;

/**
 * Created by usta on 15.03.2015.
 */
@Lazy
@Component
public class GitbookToAsciibookService {

    private final Logger logger = LoggerFactory.getLogger(GitbookToAsciibookService.class);
    private final ScriptEngine scriptEngine;
    private final DirectoryService directoryService;
    private final TabService tabService;
    private final ThreadService threadService;
    private final CompletableFuture completableFuture = new CompletableFuture();
    private final ApplicationController controller;

    @Autowired
    public GitbookToAsciibookService(ScriptEngine scriptEngine, DirectoryService directoryService, TabService tabService, ThreadService threadService, ApplicationController controller) {
        this.scriptEngine = scriptEngine;
        this.directoryService = directoryService;
        this.tabService = tabService;
        this.threadService = threadService;
        this.controller = controller;

        completableFuture.runAsync(() -> {
            try {
                List<String> scripts = Arrays.asList("marked.js", "marked-extension.js");

                Path configPath = this.controller.getConfigPath();

                for (String script : scripts) {

                    Path resolve = configPath.resolve("public/js").resolve(script);
                    try(FileReader fileReader = new FileReader(resolve.toFile());){
                        scriptEngine.eval(fileReader);
                    }

                }

                completableFuture.complete(null);
            } catch (Exception e) {
                logger.error("Problem occured while initializing marked.js", e);
                completableFuture.completeExceptionally(e);
            }
        }, threadService.executor());
    }


    private void markdownToAsciidoc(String content, Consumer<String> next) {

        if (Objects.isNull(content))
            return;

        completableFuture.join();

        Object eval = "";
        Invocable invocable = (Invocable) scriptEngine;
        try {
            eval = invocable.invokeFunction("markdownToAsciidoc", content);
        } catch (Exception e) {
            logger.error("Problem occured while converting Markdown to Asciidoc", e);
        } finally {
            next.accept((String) eval);
        }

    }

    public void gitbookToAsciibook(Path gitbookDir, Path asciibookDir) {

        PathMatcher markdownMatcher = FileSystems.getDefault().getPathMatcher("glob:{**.md,**.markdown}");

        IOHelper.copyDirectory(gitbookDir, asciibookDir);

        Stream<Path> gitStream = IOHelper.find(asciibookDir, Integer.MAX_VALUE, (path, attr) -> markdownMatcher.matches(path));

        List<Path> markdownFileList = gitStream.collect(Collectors.toList());


        List<String> markdownFileNameList = markdownFileList.stream().map(e -> e.getFileName().toString())
                .collect(Collectors.toList());

        for (Path path : markdownFileList) {
            this.markdownToAsciidoc(IOHelper.readFile(path), result -> {
                String fileName = path.getFileName().toString();
                fileName = fileName.replaceAll("\\.md|\\.markdown", ".adoc");

                for (String name : markdownFileNameList) {
                    result = result.replace(name, name.replaceAll(".md|.markdown", ".adoc"));
                }

                LinkedList<String> stringList = new LinkedList<String>(Arrays.asList(result.split("\n")));
                LinkedList<String> stringLists = new LinkedList<String>(Arrays.asList(result.split("\n")));

                for (int i = 0; i < stringList.size(); i++) {
                    String s = stringList.get(i);
                    if (s.matches("=.*(\\w|\\d|\\s).*")) {
                        stringLists.set(i, "=" + s);
                    }
                }

                if ("SUMMARY.md".equalsIgnoreCase(path.getFileName().toString())) {
                    stringLists.addFirst(":numbered:\n");
                    stringLists.addFirst(":toc: left");
                    stringLists.addFirst(":lang: en");
                    stringLists.addFirst(":encoding: utf-8");
                    stringLists.addFirst(":doctype: book");
                    stringLists.addFirst("Author Name");
                    stringLists.addFirst("= Book Name");
                    List<String> collect = stringLists.stream().filter(item -> !item.trim().matches("=.*Summary")).collect(Collectors.toList());
                    stringLists = new LinkedList<String>(collect);
                }


                result = String.join("\n", stringLists);

                IOHelper.writeToFile(path.getParent().resolve(fileName), result, CREATE, TRUNCATE_EXISTING, WRITE);
            });
        }

        markdownFileList.forEach(IOHelper::deleteIfExists);

        directoryService.changeWorkigDir(asciibookDir);

        threadService.runActionLater(() -> {
            tabService.addTab(asciibookDir.resolve("SUMMARY.adoc"));
        });


    }

}

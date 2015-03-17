package com.kodcu.service.convert;

import com.kodcu.other.IOHelper;
import com.kodcu.service.DirectoryService;
import com.kodcu.service.ThreadService;
import com.kodcu.service.ui.TabService;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.nio.file.StandardOpenOption.*;

/**
 * Created by usta on 15.03.2015.
 */
@Component
public class GitbookToAsciibookService {

    private final ScriptEngineManager engineManager;
    private final ScriptEngine js;

    private final DirectoryService directoryService;
    private final TabService tabService;
    private final ThreadService threadService;

    @Autowired
    public GitbookToAsciibookService(DirectoryService directoryService, TabService tabService, ThreadService threadService) {
        this.directoryService = directoryService;
        this.tabService = tabService;
        this.threadService = threadService;

        engineManager = new ScriptEngineManager();
        js = engineManager.getEngineByName("js");

        try {
            InputStream markedStream = GitbookToAsciibookService.class.getResourceAsStream("/public/js/marked.js");
            InputStream markedExtensionStream = GitbookToAsciibookService.class.getResourceAsStream("/public/js/marked-extension.js");

            String marked = IOUtils.toString(markedStream, Charset.forName("UTF-8"));
            String markedExtension = IOUtils.toString(markedExtensionStream, Charset.forName("UTF-8"));

            js.eval(marked);
            js.eval(markedExtension);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void markdownToAsciidoc(String content, Consumer<String> next) {

        if (Objects.isNull(content))
            return;

        Object eval = "";
        Invocable invocable = (Invocable) js;
        try {
            eval = invocable.invokeFunction("markdownToAsciidoc", content);
        } catch (Exception e) {
            e.printStackTrace();
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
                fileName = fileName.replaceAll("\\.md|\\.markdown", ".asciidoc");

                for (String name : markdownFileNameList) {
                    result = result.replace(name, name.replaceAll(".md|.markdown", ".asciidoc"));
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
                }

                result = String.join("\n", stringLists);

                IOHelper.writeToFile(path.getParent().resolve(fileName), result, CREATE, TRUNCATE_EXISTING, WRITE);
            });
        }

        markdownFileList.forEach(IOHelper::deleteIfExists);

        directoryService.changeWorkigDir(asciibookDir);

        threadService.runActionLater(()->{
            tabService.addTab(asciibookDir.resolve("SUMMARY.asciidoc"));
        });



    }

}

package com.kodcu.service;

import com.kodcu.other.Current;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * Created by usta on 04.03.2015.
 */
@Component
public class MarkdownService {

    private final Logger logger = LoggerFactory.getLogger(MarkdownService.class);

    private final Current current;
    private final ThreadService threadService;
    private final ScriptEngineManager engineManager;
    private final ScriptEngine js;

    @Autowired
    public MarkdownService(Current current, ThreadService threadService) {
        this.current = current;
        this.threadService = threadService;
        engineManager = new ScriptEngineManager();
        js = engineManager.getEngineByName("js");

        try {
            InputStream markedStream = MarkdownService.class.getResourceAsStream("/public/js/marked.js");
            InputStream markedExtensionStream = MarkdownService.class.getResourceAsStream("/public/js/marked-extension.js");
            String marked = IOUtils.toString(markedStream, Charset.forName("UTF-8"));
            String markedExtension = IOUtils.toString(markedExtensionStream, Charset.forName("UTF-8"));
            IOUtils.closeQuietly(markedStream);
            IOUtils.closeQuietly(markedExtensionStream);

            js.eval(marked);
            js.eval(markedExtension);

        } catch (Exception e) {
            logger.error("Could not evaluate initial javascript", e);
        }
    }

    public void convertToAsciidoc(String content, Consumer<String> next) {
        threadService.runTaskLater(() -> {

            if (Objects.isNull(content))
                return;

            Object eval = "";
            js.put("markdownValue", content);
            try {
                eval = js.eval(String.format("markdownToAsciidoc(markdownValue)"));
            } catch (ScriptException e) {
                e.printStackTrace();
            } finally {
                next.accept((String) eval);
            }
        });
    }

    public void convert(String markdownOrAsciidoc, Consumer<String> next) {

        if (current.currentTab().isAsciidoc()) {
            next.accept(markdownOrAsciidoc);
            return;
        }

        convertToAsciidoc(markdownOrAsciidoc, next);

    }
}

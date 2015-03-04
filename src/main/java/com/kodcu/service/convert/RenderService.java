package com.kodcu.service.convert;

import com.kodcu.controller.ApplicationController;
import com.kodcu.other.Current;
import com.kodcu.other.IOHelper;
import com.kodcu.service.MarkdownService;
import com.kodcu.service.ThreadService;
import javafx.application.Platform;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import netscape.javascript.JSObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.script.ScriptException;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

/**
 * Created by usta on 19.07.2014.
 */
@Component
public class RenderService {

    @Autowired
    ApplicationController controller;

    @Autowired
    ThreadService threadService;

    @Autowired
    MarkdownService markdownService;

    @Autowired
    Current current;

    public void convertBasicHtml(String input, Consumer<String> step) {

        markdownService.convert(input, asciidoc -> {
            threadService.runActionLater(() -> {
                getWindow().setMember("editorValue", asciidoc);
                String rendered = execute(controller.getPreviewView(), "convertBasicHtml(editorValue)");
                step.accept(rendered);
            });
        });

    }

    public JSObject getWindow() {
        return (JSObject) controller.getPreviewView().getEngine().executeScript("window");
    }

    public String execute(WebEngine engine, String script) {
        return (String) engine.executeScript(script);
    }

    public String execute(WebView engine, String script) {
        String s = (String) engine.getEngine().executeScript(script);
        return s;
    }

    public void convertHtmlArticle(Consumer<String> step) {

        String input = current.currentEditorValue();
        markdownService.convert(input, asciidoc -> {
            threadService.runActionLater(() -> {
                getWindow().setMember("editorValue", asciidoc);
                String rendered = execute(controller.getPreviewView(), "convertHtmlArticle(editorValue)");
                step.accept(rendered);
            });
        });
    }

    public void convertHtmlBook(String input, Consumer<String> step) {

        markdownService.convert(input, asciidoc -> {
            threadService.runActionLater(() -> {
                getWindow().setMember("editorValue", asciidoc);
                String rendered = execute(controller.getPreviewView(),"convertHtmlBook(editorValue)");
                step.accept(rendered);
            });
        });
    }

    public void convertDocbook(String input, boolean includeHeader, Consumer<String> step) {

        markdownService.convert(input, asciidoc -> {
            threadService.runActionLater(() -> {
                getWindow().setMember("editorValue", asciidoc);
                String rendered = execute(controller.getPreviewView(), String.format("convertDocbook(editorValue,%b)", asciidoc, includeHeader));
                step.accept(rendered);
            });
        });
    }

    public void convertDocbookArticle(Consumer<String> step) {

        String input = current.currentEditorValue();
        markdownService.convert(input, asciidoc -> {
            threadService.runActionLater(() -> {
                getWindow().setMember("editorValue", asciidoc);
                String rendered = execute(controller.getPreviewView(), "convertDocbookArticle(editorValue)");
                step.accept(rendered);
            });
        });

    }
}

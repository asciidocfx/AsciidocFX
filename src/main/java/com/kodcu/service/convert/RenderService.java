package com.kodcu.service.convert;

import com.kodcu.controller.ApplicationController;
import com.kodcu.other.Current;
import com.kodcu.service.MarkdownService;
import com.kodcu.service.ThreadService;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import netscape.javascript.JSObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

/**
 * Created by usta on 19.07.2014.
 */
@Component
public class RenderService {

    private final ApplicationController controller;
    private final ThreadService threadService;
    private final MarkdownService markdownService;
    private final Current current;
    
    @Autowired
    public RenderService(final ApplicationController controller, final ThreadService threadService, final MarkdownService markdownService, final Current current) {
        this.controller = controller;
        this.threadService = threadService;
        this.markdownService = markdownService;
        this.current = current;
    }

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
        return  (String) engine.getEngine().executeScript(script);
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

    public void convertDocbookArticle(String input,Consumer<String> step) {

        markdownService.convert(input, asciidoc -> {
            threadService.runActionLater(() -> {
                getWindow().setMember("editorValue", asciidoc);
                String rendered = execute(controller.getPreviewView(), "convertDocbookArticle(editorValue)");
                step.accept(rendered);
            });
        });
    }
}

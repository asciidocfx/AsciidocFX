package com.kodcu.component;

import com.kodcu.controller.ApplicationController;
import com.kodcu.other.ConverterResult;
import com.kodcu.other.Current;
import com.kodcu.other.IOHelper;
import com.kodcu.service.ThreadService;
import javafx.scene.web.WebView;
import netscape.javascript.JSObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Created by usta on 09.04.2015.
 */
@Component
public class HtmlPane extends ViewPanel {

    @Autowired
    public HtmlPane(ThreadService threadService, ApplicationController controller, Current current) {
        super(threadService, controller, current);
    }

    public void refreshUI(String content) {
        this.setMember("lastRenderedValue", content);
        webEngine().executeScript("refreshUI(lastRenderedValue)");
    }

    public void updateBase64Url(int index, String imageBase64) {
        getWindow().call("updateBase64Url", index, imageBase64);
    }

    public WebView getWebView() {
        return webView;
    }

    public void startProgressBar() {
        threadService.runActionLater(() -> {
            webEngine().executeScript("startProgressBar()");
        });
    }

    public void stopProgressBar() {
        threadService.runActionLater(() -> {
            webEngine().executeScript("stopProgressBar()");
        });
    }

    public ConverterResult convertDocbook(String asciidoc) {
        this.setMember("editorValue", asciidoc);
        JSObject result = (JSObject) webEngine().executeScript(String.format("convertDocbook(editorValue)"));
        return new ConverterResult(result);
    }

    public String getTemplate(String templateName, String templateDir) throws IOException {

        Stream<Path> slide = Files.find(controller.getConfigPath().resolve("slide/templates").resolve(templateDir), Integer.MAX_VALUE, (path, basicFileAttributes) -> path.toString().contains(templateName));

        Optional<Path> first = slide.findFirst();

        if (!first.isPresent())
            return "";

        Path path = first.get();

        String template = IOHelper.readFile(path);
        return template;
    }

    public String findRenderedSelection(String content) {
        this.setMember("context", content);
        return (String) webEngine().executeScript("findRenderedSelection(context)");
    }

    public JSObject getWindow() {
        return (JSObject) webEngine().executeScript("window");
    }

    @Override
    public void browse() {
        controller.getHostServices()
                .showDocument(String.format("http://localhost:%d/index.html", controller.getPort()));
    }

    public void fillOutlines(JSObject doc) {
        getWindow().call("fillOutlines", doc);
    }

    public ConverterResult convertAsciidoc(String asciidoc) {
        this.setMember("editorValue", asciidoc);
        JSObject result = (JSObject) webEngine().executeScript("convertAsciidoc(editorValue)");

        return new ConverterResult(result);
    }

    public ConverterResult convertHtml(String asciidoc) {
        this.setMember("editorValue", asciidoc);
        JSObject result = (JSObject) webEngine().executeScript("convertHtml(editorValue)");

        return new ConverterResult(result);
    }
}

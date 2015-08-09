package com.kodcu.component;

import com.kodcu.config.DocbookConfigBean;
import com.kodcu.config.HtmlConfigBean;
import com.kodcu.config.OdfConfigBean;
import com.kodcu.config.PreviewConfigBean;
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
public class WorkerPane extends ViewPanel {

    private final PreviewConfigBean previewConfigBean;
    private final OdfConfigBean odfConfigBean;
    private final DocbookConfigBean docbookConfigBean;
    private final HtmlConfigBean htmlConfigBean;

    @Autowired
    public WorkerPane(ThreadService threadService, ApplicationController controller, Current current, PreviewConfigBean previewConfigBean, OdfConfigBean odfConfigBean, DocbookConfigBean docbookConfigBean, HtmlConfigBean htmlConfigBean) {
        super(threadService, controller, current);
        this.previewConfigBean = previewConfigBean;
        this.odfConfigBean = odfConfigBean;
        this.docbookConfigBean = docbookConfigBean;
        this.htmlConfigBean = htmlConfigBean;
    }

    public WebView getWebView() {
        return webView;
    }

    public ConverterResult convertDocbook(String asciidoc) {
        this.setMember("editorValue", asciidoc);
        this.setMember("docbookOptions", docbookConfigBean.getJSON().toString());
        JSObject result = (JSObject) webEngine().executeScript(String.format("convertDocbook(editorValue,docbookOptions)"));
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

    public String findRenderedSelection(String content) {
        this.setMember("context", content);
        return (String) webEngine().executeScript("findRenderedSelection(context)");
    }

    public ConverterResult convertAsciidoc(String asciidoc) {
        this.setMember("editorValue", asciidoc);
        this.setMember("asciidocOptions", previewConfigBean.getJSON().toString());
        JSObject result = (JSObject) webEngine().executeScript("convertAsciidoc(editorValue,asciidocOptions)");

        return new ConverterResult(result);
    }

    public ConverterResult convertHtml(String asciidoc) {
        this.setMember("editorValue", asciidoc);
        this.setMember("htmlOptions", htmlConfigBean.getJSON().toString());
        JSObject result = (JSObject) webEngine().executeScript("convertHtml(editorValue,htmlOptions)");

        return new ConverterResult(result);
    }

    public void convertOdf(String asciidoc) {
        this.setMember("editorValue", asciidoc);
        this.setMember("odfOptions", odfConfigBean.getJSON().toString());
        webEngine().executeScript("convertOdf(editorValue,odfOptions)");
    }
}

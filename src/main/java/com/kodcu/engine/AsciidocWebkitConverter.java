package com.kodcu.engine;

import com.kodcu.component.ViewPanel;
import com.kodcu.config.DocbookConfigBean;
import com.kodcu.config.HtmlConfigBean;
import com.kodcu.config.OdfConfigBean;
import com.kodcu.config.PreviewConfigBean;
import com.kodcu.controller.ApplicationController;
import com.kodcu.other.ConverterResult;
import com.kodcu.other.Current;
import com.kodcu.other.IOHelper;
import com.kodcu.service.ThreadService;
import javafx.application.Platform;
import javafx.scene.web.WebView;
import netscape.javascript.JSObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

/**
 * Created by usta on 09.04.2015.
 */
@Component("WebkitEngine")
public class AsciidocWebkitConverter extends ViewPanel implements AsciidocConvertible {

    private final PreviewConfigBean previewConfigBean;
    private final OdfConfigBean odfConfigBean;
    private final DocbookConfigBean docbookConfigBean;
    private final HtmlConfigBean htmlConfigBean;

    private Logger logger = LoggerFactory.getLogger(AsciidocWebkitConverter.class);

    @Autowired
    public AsciidocWebkitConverter(ThreadService threadService, ApplicationController controller, Current current, PreviewConfigBean previewConfigBean, OdfConfigBean odfConfigBean, DocbookConfigBean docbookConfigBean, HtmlConfigBean htmlConfigBean) {
        super(threadService, controller, current);
        this.previewConfigBean = previewConfigBean;
        this.odfConfigBean = odfConfigBean;
        this.docbookConfigBean = docbookConfigBean;
        this.htmlConfigBean = htmlConfigBean;
    }

    public WebView getWebView() {
        return webView;
    }

    public String getTemplate(String templateName, String templateDir) throws IOException {

        Stream<Path> slide = Files.find(controller.getConfigPath().resolve("slide/templates").resolve(templateDir), Integer.MAX_VALUE, (path, basicFileAttributes) -> path.toString().contains(templateName));

        Optional<Path> first = slide.findFirst();

        if (!first.isPresent()) {
            logger.error("Template name : {} not found in {}", templateName, templateDir);
            return "";
        }

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

    @Override
    public void fillOutlines(Object doc) {
        getWindow().call("fillOutlines", doc);
    }

    public String findRenderedSelection(String content) {
        this.setMember("context", content);
        return (String) webEngine().executeScript("findRenderedSelection(context)");
    }

    private ConverterResult convert(String functionName, String asciidoc, String config) {

        if (!Platform.isFxApplicationThread()) {
            final CompletableFuture<ConverterResult> completableFuture = new CompletableFuture<>();

            completableFuture.runAsync(() -> {
                threadService.runActionLater(() -> {
                    try {
                        ConverterResult result = convert(functionName, asciidoc, config);
                        completableFuture.complete(result);
                    } catch (Exception e) {
                        completableFuture.completeExceptionally(e);
                    }
                });
            }, threadService.executor());

            return completableFuture.join();
        }

//        long start = System.currentTimeMillis();

        this.setMember("editorValue", asciidoc);
        this.setMember("editorOptions", config);
        JSObject result = (JSObject) webEngine().executeScript(String.format("%s(editorValue,editorOptions)", functionName));
        ConverterResult converterResult = new ConverterResult(result);
//        logger.debug("Rendered in {}", (System.currentTimeMillis() - start));
        return converterResult;

    }

    @Override
    public ConverterResult convertDocbook(String asciidoc) {
        String config = docbookConfigBean.getJSON().toString();
        return convert("convertDocbook", asciidoc, config);
    }

    @Override
    public ConverterResult convertAsciidoc(String asciidoc) {
        String config = previewConfigBean.getJSON().toString();
        return convert("convertAsciidoc", asciidoc, config);
    }

    @Override
    public ConverterResult convertHtml(String asciidoc) {
        String config = htmlConfigBean.getJSON().toString();
        return convert("convertHtml", asciidoc, config);
    }

    @Override
    public void convertOdf(String asciidoc) {
        String config = odfConfigBean.getJSON().toString();
        convert("convertOdf", asciidoc, config);
    }

    public boolean isHtml(String text) {
        Object isHtml = this.call("isHtml", text);

        if (isHtml instanceof Boolean) {
            return (boolean) isHtml;
        }

        return false;
    }
}

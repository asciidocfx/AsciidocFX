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
import javafx.application.Platform;
import javafx.scene.web.WebView;
import netscape.javascript.JSObject;
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
@Component
public class HtmlPane extends ViewPanel {

    private final PreviewConfigBean previewConfigBean;
    private final OdfConfigBean odfConfigBean;
    private final DocbookConfigBean docbookConfigBean;
    private final HtmlConfigBean htmlConfigBean;
    private final ThreadService threadService;

    @Autowired
    public HtmlPane(ThreadService threadService, ApplicationController controller, Current current, PreviewConfigBean previewConfigBean, OdfConfigBean odfConfigBean, DocbookConfigBean docbookConfigBean, HtmlConfigBean htmlConfigBean) {
        super(threadService, controller, current);
        this.previewConfigBean = previewConfigBean;
        this.odfConfigBean = odfConfigBean;
        this.docbookConfigBean = docbookConfigBean;
        this.htmlConfigBean = htmlConfigBean;
        this.threadService = threadService;
    }

    public void refreshUI(String content) {
        threadService.runActionLater(()->{
            this.setMember("lastRenderedValue", content);
            webEngine().executeScript("refreshUI(lastRenderedValue)");
        });
    }

    public void updateBase64Url(int index, String imageBase64) {
        threadService.runActionLater(()->{
            getWindow().call("updateBase64Url", index, imageBase64);
        });
    }

    public WebView getWebView() {
        return webView;
    }

    public JSObject getWindow() {
        return (JSObject) webEngine().executeScript("window");
    }

    @Override
    public void browse() {
        controller.getHostServices()
                .showDocument(String.format("http://localhost:%d/index.html", controller.getPort()));
    }

    public void runScroller(String renderedSelection) {
        getWindow().call("runScroller", renderedSelection);
    }
}

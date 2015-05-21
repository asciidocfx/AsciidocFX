package com.kodcu.component;

import com.kodcu.controller.ApplicationController;
import com.kodcu.other.Current;
import com.kodcu.service.ThreadService;
import javafx.application.Platform;
import javafx.concurrent.Worker;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import netscape.javascript.JSObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by usta on 09.04.2015.
 */

public abstract class SlidePane extends AnchorPane {

    private final WebView webView;
    private final WebEngine webEngine;
    protected final Logger logger = LoggerFactory.getLogger(SlidePane.class);
    private final ThreadService threadService;
    private final ApplicationController controller;
    private final Current current;


    public SlidePane(ThreadService threadService, ApplicationController controller, Current current) {
        this.threadService = threadService;
        this.controller = controller;
        this.current = current;
        this.webView = new WebView();
        this.getChildren().add(webView);
        this.webEngine = webView.getEngine();
        initializeMargins();
        this.hide();
    }

    private void initializeMargins() {
        AnchorPane.setBottomAnchor(this, 0D);
        AnchorPane.setTopAnchor(this, 0D);
        AnchorPane.setLeftAnchor(this, 0D);
        AnchorPane.setRightAnchor(this, 0D);
        VBox.setVgrow(this, Priority.ALWAYS);
        AnchorPane.setBottomAnchor(webView, 0D);
        AnchorPane.setTopAnchor(webView, 0D);
        AnchorPane.setLeftAnchor(webView, 0D);
        AnchorPane.setRightAnchor(webView, 0D);
        VBox.setVgrow(webView, Priority.ALWAYS);
    }

    public void load(String url) {
        if (Objects.nonNull(url))
            Platform.runLater(() -> {
                webEngine.getLoadWorker().cancel();
                webEngine.load(url);
            });
        else
            logger.error("Url is not loaded. Reason: null reference");
    }

    public void hide() {
        super.setVisible(false);
    }

    public void show() {
        super.setVisible(true);
    }

    public String getLocation() {
        return webEngine.getLocation();
    }

    public void setOnSuccess(Runnable runnable) {
        webEngine.getLoadWorker().stateProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == Worker.State.SUCCEEDED)
                threadService.runActionLater(runnable);
        });
    }

    public abstract boolean isReady();
    public abstract void replaceSlides(String rendered);
    public abstract String getTemplate(String templateName, String templateDir) throws IOException;
    public abstract void flipThePage(String rendered);

    public void loadJs(String... jsPaths) {
        threadService.runTaskLater(() -> {
            threadService.runActionLater(() -> {
                for (String jsPath : jsPaths) {
                    String format = String.format("var scriptEl = document.createElement('script');\n" +
                            "scriptEl.setAttribute('src','http://localhost:%d/%s');\n" +
                            "document.querySelector('body').appendChild(scriptEl);", controller.getPort(), jsPath);
                    webEngine.executeScript(format);
                }
            });


        });
    }

    protected JSObject getWindow() {
        return (JSObject) webEngine.executeScript("window");
    }
}

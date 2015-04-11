package com.kodcu.component;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebEvent;
import javafx.scene.web.WebView;
import netscape.javascript.JSObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * Created by usta on 09.04.2015.
 */
@Component
public class SlidePane extends AnchorPane {

    private final WebView webView;
    private final WebEngine webEngine;
    private EventHandler<WebEvent<String>> readyHandler;
    private final JSObject window;
    private final Logger logger = LoggerFactory.getLogger(SlidePane.class);

    public SlidePane() {
        this.webView = new WebView();
        this.getChildren().add(webView);
        this.webEngine = webView.getEngine();
        window = (JSObject) webEngine.executeScript("window");
        this.webEngine.setOnAlert(event -> {
            if (Objects.nonNull(readyHandler))
                readyHandler.handle(event);
        });
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
            Platform.runLater(()->{
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

    public void setOnReady(EventHandler<WebEvent<String>> readyHandler) {
        this.readyHandler = readyHandler;
    }

    public boolean isReady() {
        try {
            return (Boolean) ((JSObject) window.eval("Reveal")).call("isReady");
        } catch (Exception ex) {
            logger.info(ex.getMessage(), ex);
            return false;
        }
    }

    public String getLocation() {
        return webEngine.getLocation();
    }

    public void replaceSlides(String rendered) {
        window.call("replaceSlides", rendered);
    }

    public void flipThePage(String rendered) {
        window.call("flipCurrentPage", rendered);
    }
}

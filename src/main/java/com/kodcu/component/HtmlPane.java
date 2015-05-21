package com.kodcu.component;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.concurrent.Worker;
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
public class HtmlPane extends AnchorPane {

    private final WebView webView;
    private final WebEngine webEngine;
    private EventHandler<WebEvent<String>> readyHandler;
    private final Logger logger = LoggerFactory.getLogger(HtmlPane.class);

    public HtmlPane() {
        this.webView = new WebView();
        this.webView.setContextMenuEnabled(false);
        this.getChildren().add(webView);
        this.webEngine = webView.getEngine();
        this.webEngine.setOnAlert(event -> {
            if (Objects.nonNull(readyHandler))
                readyHandler.handle(event);
        });
        initializeMargins();
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
            return (Boolean) ((JSObject) getWindow().eval("Reveal")).call("isReady");
        } catch (Exception ex) {
            logger.info(ex.getMessage(), ex);
            return false;
        }
    }

    public String getLocation() {
        return webEngine.getLocation();
    }

    public void setMember(String name, Object value) {
        getWindow().setMember(name, value);
    }

    public void refreshUI(String content) {
        this.setMember("lastRenderedValue", content);
        webEngine.executeScript("refreshUI(lastRenderedValue)");
    }

    public void updateBase64Url(int index, String imageBase64) {
        getWindow().call("updateBase64Url", index, imageBase64);
    }

    public void call(String methodName, Object... args) {
        getWindow().call(methodName, args);
    }

    public Object getMember(String name) {
        return getWindow().getMember(name);
    }

    public WebEngine getWebEngine() {
        return webEngine;
    }

    public WebView getWebView() {
        return webView;
    }

    public String convertDocbookArticle(String asciidoc) {
        this.setMember("editorValue", asciidoc);
        return (String) webEngine.executeScript("convertDocbookArticle(editorValue)");
    }

    public void startProgressBar() {
        Platform.runLater(()->{
            webEngine.executeScript("startProgressBar()");
        });
    }

    public void stopProgressBar() {
        Platform.runLater(() -> {
            webEngine.executeScript("stopProgressBar()");
        });
    }

    public String convertDocbook(String asciidoc, boolean includeHeader) {
        this.setMember("editorValue", asciidoc);
        return (String) webEngine.executeScript(String.format("convertDocbook(editorValue,%b)", includeHeader));
    }

    public String convertHtmlBook(String asciidoc) {
        this.setMember("editorValue", asciidoc);
        return (String) webEngine.executeScript("convertHtmlBook(editorValue)");
    }

    public String convertHtmlArticle(String asciidoc) {
        this.setMember("editorValue", asciidoc);
        return (String) webEngine.executeScript("convertHtmlArticle(editorValue)");
    }

    public void onscroll(Object pos, Object max) {

        if (Objects.isNull(pos) || Objects.isNull(max))
            return;

        Number position = (Number) pos; // current scroll position for editor
        Number maximum = (Number) max; // max scroll position for editor

        double currentY = (position.doubleValue() < 0) ? 0 : position.doubleValue();
        double ratio = (currentY * 100) / maximum.doubleValue();
        Integer browserMaxScroll = (Integer) webEngine.executeScript("document.documentElement.scrollHeight - document.documentElement.clientHeight;");
        double browserScrollOffset = (Double.valueOf(browserMaxScroll) * ratio) / 100.0;
        webEngine.executeScript(String.format("window.scrollTo(0, %f )", browserScrollOffset));
    }

    public void scrollToCurrentLine(String text) {
        try {
            this.call("runScroller", text);
        } catch (Exception e) {
            logger.debug(e.getMessage(), e);
        }
    }

    public String findRenderedSelection(String content) {
        this.setMember("context", content);
        return (String) webEngine.executeScript("findRenderedSelection(context)");
    }

    public String convertSlide(String asciidoc) {
        this.setMember("editorValue", asciidoc);
        return (String) webEngine.executeScript("convertSlide(editorValue)");
    }

    public String convertBasicHtml(String asciidoc) {
        this.setMember("editorValue", asciidoc);
        return (String) webEngine.executeScript("convertBasicHtml(editorValue)");
    }

    public JSObject getWindow() {
        return (JSObject) webEngine.executeScript("window");
    }
}

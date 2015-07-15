package com.kodcu.component;

import com.kodcu.controller.ApplicationController;
import com.kodcu.other.IOHelper;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.collections.ObservableList;
import javafx.concurrent.Worker;
import javafx.event.EventHandler;
import javafx.scene.control.ButtonType;
import javafx.scene.input.DragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebEvent;
import javafx.scene.web.WebView;
import javafx.util.Callback;
import netscape.javascript.JSObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.util.Objects;
import java.util.Optional;

/**
 * Created by usta on 09.04.2015.
 */
@Component
@Scope("prototype")
public class EditorPane extends AnchorPane {

    private final WebView webView;
    private EventHandler<WebEvent<String>> readyHandler;
    private final Logger logger = LoggerFactory.getLogger(EditorPane.class);
    private final ApplicationController controller;
    private String mode = "ace/mode/asciidoc";

    @Autowired
    public EditorPane(ApplicationController controller) {
        this.controller = controller;
        this.webView = new WebView();
        this.webView.setContextMenuEnabled(false);
        this.getChildren().add(webView);
        webEngine().setOnAlert(event -> {
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
                webEngine().load(url);
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

    public String getLocation() {
        return webEngine().getLocation();
    }

    public void setMember(String name, Object value) {
        getWindow().setMember(name, value);
    }

    public Object call(String methodName, Object... args) {
        return getWindow().call(methodName, args);
    }

    public void whenStateSucceed(ChangeListener<Worker.State> stateChangeListener) {
        webEngine().getLoadWorker().stateProperty().addListener(stateChangeListener);
    }

    public Object getMember(String name) {
        return getWindow().getMember(name);
    }

    public WebEngine webEngine() {
        return webView.getEngine();
    }

    public WebView getWebView() {
        return webView;
    }

    public void onClicked(EventHandler<MouseEvent> eventHandler) {
        webView.setOnMouseClicked(eventHandler);
    }

    public void dragDropped(EventHandler<DragEvent> eventHandler) {
        webView.setOnDragDropped(eventHandler);
    }

    public void confirmHandler(Callback<String, Boolean> confirmHandler) {
        webEngine().setConfirmHandler(confirmHandler);
    }

    public JSObject getWindow() {
        return (JSObject) webEngine().executeScript("window");
    }

    public String getEditorValue() {
        return (String) webEngine().executeScript("editor.getValue()");
    }

    public void setEditorValue(String value) {
        getWindow().setMember("editorValue",value);
        webEngine().executeScript("editor.setValue(editorValue)");
    }

    public void switchMode(Object... args) {
        this.call("switchMode", args);
    }

    public void rerender(Object... args) {
        this.call("rerender", args);
    }

    public void focus() {
        webView.requestFocus();
    }

    public void moveCursorTo(Integer lineno) {
        if (Objects.nonNull(lineno)) {
            webEngine().executeScript(String.format("editor.scrollToLine(%d,false,false,function(){})", (lineno - 3)));
            webEngine().executeScript(String.format("editor.gotoLine(%d,3,false)", (lineno)));
        }
    }

    public void changeEditorMode(Path path) {
        if (Objects.nonNull(path)) {
            String mode = (String) webEngine().executeScript(String.format("changeEditorMode('%s')", path.toUri().toString()));
            setMode(mode);
        }
    }

    public String editorMode() {
        return (String) this.call("editorMode", new Object[]{});
    }

    public void fillModeList(ObservableList modeList) {
        Platform.runLater(() -> {
            this.call("fillModeList", modeList);
        });
    }

    public boolean is(String mode) {
        return ("ace/mode/" + mode).equalsIgnoreCase(this.mode);
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public String getMode() {
        return mode;
    }
}

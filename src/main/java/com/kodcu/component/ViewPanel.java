package com.kodcu.component;

import com.kodcu.controller.ApplicationController;
import com.kodcu.other.Current;
import com.kodcu.service.ThreadService;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectProperty;
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

import java.util.Objects;

/**
 * Created by usta on 15.06.2015.
 */
public abstract class ViewPanel extends AnchorPane {

    private final Logger logger = LoggerFactory.getLogger(ViewPanel.class);

    protected final ThreadService threadService;
    protected final ApplicationController controller;
    protected final Current current;
    protected final WebView webView;

    protected ViewPanel(ThreadService threadService, ApplicationController controller, Current current) {
        this.threadService = threadService;
        this.controller = controller;
        this.current = current;
        this.webView = new WebView();
        this.webView.setContextMenuEnabled(false);
        this.getChildren().add(webView);
        initializeMargins();
    }

    protected void initializeMargins() {
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

    public abstract void browse();

    public void onscroll(Object pos, Object max) {

        if (Objects.isNull(pos) || Objects.isNull(max))
            return;

        Number position = (Number) pos; // current scroll position for editor
        Number maximum = (Number) max; // max scroll position for editor

        double currentY = (position.doubleValue() < 0) ? 0 : position.doubleValue();
        double ratio = (currentY * 100) / maximum.doubleValue();
        Integer browserMaxScroll = (Integer) webEngine().executeScript("document.documentElement.scrollHeight - document.documentElement.clientHeight;");
        double browserScrollOffset = (Double.valueOf(browserMaxScroll) * ratio) / 100.0;
        webEngine().executeScript(String.format("window.scrollTo(0, %f )", browserScrollOffset));
    }



    public WebEngine webEngine() {
        return webView.getEngine();
    }

    public void load(String url) {
        if (Objects.nonNull(url))
            Platform.runLater(() -> {
                webEngine().getLoadWorker().cancel();
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

    public String getLocation() {
        return webEngine().getLocation();
    }

    public void setMember(String name, Object value) {
        getWindow().setMember(name, value);
    }

    public void call(String methodName, Object... args) {
        threadService.runActionLater(()->{
            getWindow().call(methodName, args);
        });
    }

    public Object getMember(String name) {
        return getWindow().getMember(name);
    }

    public WebView getWebView() {
        return webView;
    }

    public void loadJs(String... jsPaths) {
        threadService.runTaskLater(() -> {
            threadService.runActionLater(() -> {
                for (String jsPath : jsPaths) {
                    threadService.runActionLater(() -> {
                        String format = String.format("var scriptEl = document.createElement('script');\n" +
                                "scriptEl.setAttribute('src','http://localhost:%d/%s');\n" +
                                "document.querySelector('body').appendChild(scriptEl);", controller.getPort(), jsPath);
                        webEngine().executeScript(format);
                    });
                }
            });
        });
    }

    public void setOnSuccess(Runnable runnable) {
        threadService.runActionLater(()->{
            Worker<Void> loadWorker = webEngine().getLoadWorker();
            ReadOnlyObjectProperty<Worker.State> stateProperty = loadWorker.stateProperty();
            stateProperty.addListener((observable, oldValue, newValue) -> {
                if (newValue == Worker.State.SUCCEEDED)
                    threadService.runActionLater(runnable);
            });
        });
    }


    public JSObject getWindow() {
        return (JSObject) webEngine().executeScript("window");
    }

}

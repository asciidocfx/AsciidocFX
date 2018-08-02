package com.kodedu.component;

import com.kodedu.config.EditorConfigBean;
import com.kodedu.controller.ApplicationController;
import com.kodedu.engine.AsciidocWebkitConverter;
import com.kodedu.other.Current;
import com.kodedu.service.ThreadService;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.web.WebView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import netscape.javascript.JSObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.InputStream;

/**
 * Created by usta on 09.04.2015.
 */
@Component
public class SlidePane extends ViewPanel {

    private String backend = "revealjs";
    private Logger logger = LoggerFactory.getLogger(SlidePane.class);
    private final AsciidocWebkitConverter asciidocWebkitConverter;

    @Autowired
    public SlidePane(ThreadService threadService, ApplicationController controller, Current current, AsciidocWebkitConverter asciidocWebkitConverter, EditorConfigBean editorConfigBean) {
        super(threadService, controller, current, editorConfigBean);
        this.asciidocWebkitConverter = asciidocWebkitConverter;

    }

    @PostConstruct
    public void afterInit() {
        threadService.runActionLater(() -> {
            getWindow().setMember("afx", controller);
            ReadOnlyObjectProperty<Worker.State> stateProperty = webEngine().getLoadWorker().stateProperty();
            WebView popupView = new WebView();
            Stage stage = new Stage();
            stage.initModality(Modality.WINDOW_MODAL);
            stage.setScene(new Scene(popupView));
            stage.setTitle("AsciidocFX");
            InputStream logoStream = SlidePane.class.getResourceAsStream("/logo.png");
            stage.getIcons().add(new Image(logoStream));
            webEngine().setCreatePopupHandler(param -> {
                if (!stage.isShowing()) {
                    stage.show();
                    popupView.requestFocus();
                }
                return popupView.getEngine();
            });
            stateProperty.addListener(this::stateListener);
        });
    }

    private void stateListener(ObservableValue<? extends Worker.State> observable, Worker.State oldValue, Worker.State newValue) {
        if (newValue == Worker.State.SUCCEEDED) {
            getWindow().setMember("afx", controller);
            if ("revealjs".equals(backend))
                this.loadJs("/afx/worker/js/?p=js/jquery.js", "/afx/worker/js/?p=js/reveal-extensions.js");
            if ("deckjs".equals(backend))
                this.loadJs("/afx/worker/js/?p=js/deck-extensions.js");
        }
    }

    public void replaceSlides(String rendered) {
        getWindow().setMember("afx", controller);
        String backendExt = backend + "Ext";
        try {
            ((JSObject) getWindow().eval(backendExt)).call("replaceSlides", rendered);
        } catch (Exception e) {
            logger.debug("{} is not found while replacing slide, but don't worry.", backendExt, e);
        }

    }

    @Override
    public void runScroller(String text) {
        String backendExt = backend + "Ext";
        try {
            ((JSObject) getWindow().eval(backendExt)).call("flipCurrentPage", text);
        } catch (Exception e) {
            logger.debug("{} is not found while flipping page, but don't worry.", backendExt, e);
        }
    }

    @Override
    public void scrollByPosition(String text) {
        if (stopScrolling.get())
            return;

        runScroller(text);
    }

    @Override
    public void scrollByLine(String lineno) {
        if (stopJumping.get())
            return;

        runScroller(lineno);
    }

    public String findRenderedSelection(String content) {
        this.setMember("context", content);
        return (String) webEngine().executeScript("findRenderedSelection(context)");
    }

    @Override
    public void browse() {
        super.browse();
    }

    public void setBackend(String backend) {
        this.backend = backend;
    }

    public String getBackend() {
        return backend;
    }
}

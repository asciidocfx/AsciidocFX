package com.kodedu.component;

import com.kodedu.config.EditorConfigBean;
import com.kodedu.controller.ApplicationController;
import com.kodedu.engine.AsciidocWebkitConverter;
import com.kodedu.other.Current;
import com.kodedu.service.DirectoryService;
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
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${application.slide.url}")
    private String slideUrl;
    private final AsciidocWebkitConverter asciidocWebkitConverter;
    private final DirectoryService directoryService;

    @Autowired
    public SlidePane(ThreadService threadService, ApplicationController controller, Current current, AsciidocWebkitConverter asciidocWebkitConverter, EditorConfigBean editorConfigBean, DirectoryService directoryService) {
        super(threadService, controller, current, editorConfigBean);
        this.asciidocWebkitConverter = asciidocWebkitConverter;

        this.directoryService = directoryService;
    }

    @PostConstruct
    public void afterInit() {
        threadService.runActionLater(() -> {
            getWindow().setMember("afx", controller);
            ReadOnlyObjectProperty<Worker.State> stateProperty = webEngine().getLoadWorker().stateProperty();
            stateProperty.addListener(this::stateListener);
        });
    }

    private void stateListener(ObservableValue<? extends Worker.State> observable, Worker.State oldValue, Worker.State newValue) {
        if (newValue == Worker.State.SUCCEEDED) {
            getWindow().setMember("afx", controller);
            if ("revealjs".equals(backend)) {
                this.loadJs("js/?p=js/jquery.js", "js/?p=js/reveal-extensions.js","js/?p=js/firebug-import.js");
            }
        }
    }

    public void replaceSlides(String rendered) {
        try {
            getSlideObject().call("replaceSlides", rendered);
        } catch (Exception e) {
//            logger.debug("{} is not found while replacing slide, but don't worry.", backendExt, e);
        }

    }

    @Override
    public void runScroller(String text) {
        try {
            getSlideObject().call("flipCurrentPage", text);
        } catch (Exception e) {
//            logger.debug("{} is not found while flipping page, but don't worry.", backendExt, e);
        }
    }

    private JSObject getSlideObject() {
        String backendExt = backend + "Ext";
        getWindow().setMember("afx", controller);
        return (JSObject) getWindow().eval(backendExt);
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

    public void printPdf() {
        controller.browseInDesktop(getPdfSlideUrl());
    }

    public String getSlideUrl() {
        return String.format(slideUrl, controller.getPort(), directoryService.interPath());
    }

    private String getPdfSlideUrl() {
        return getSlideUrl() + "?print-pdf";
    }
}

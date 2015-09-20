package com.kodcu.component;

import com.kodcu.controller.ApplicationController;
import com.kodcu.engine.AsciidocWebkitConverter;
import com.kodcu.other.Current;
import com.kodcu.service.ThreadService;
import netscape.javascript.JSObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by usta on 09.04.2015.
 */
@Component
public class SlidePane extends ViewPanel {

    private String backend = "revealjs";
    private Logger logger = LoggerFactory.getLogger(SlidePane.class);
    private final AsciidocWebkitConverter asciidocWebkitConverter;

    @Autowired
    public SlidePane(ThreadService threadService, ApplicationController controller, Current current, AsciidocWebkitConverter asciidocWebkitConverter) {
        super(threadService, controller, current);
        this.asciidocWebkitConverter = asciidocWebkitConverter;
    }

    public void replaceSlides(String rendered) {
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
    public void scrollByLine(String text) {
        if (stopJumping.get())
            return;

        String selection = asciidocWebkitConverter.findRenderedSelection(text);

        runScroller(selection);
    }

    public String findRenderedSelection(String content) {
        this.setMember("context", content);
        return (String) webEngine().executeScript("findRenderedSelection(context)");
    }

    public void injectExtensions() {
        this.setOnSuccess(() -> {
            if ("revealjs".equals(backend))
                this.loadJs("js/jquery.js", "js/reveal-extensions.js");
            if ("deckjs".equals(backend))
                this.loadJs("js/deck-extensions.js");
        });
    }

    @Override
    public void browse() {
        controller.getHostServices().showDocument(webEngine().getLocation());
    }

    public void setBackend(String backend) {
        this.backend = backend;
    }

    public String getBackend() {
        return backend;
    }
}

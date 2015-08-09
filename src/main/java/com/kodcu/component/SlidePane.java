package com.kodcu.component;

import com.kodcu.controller.ApplicationController;
import com.kodcu.other.Current;
import com.kodcu.other.IOHelper;
import com.kodcu.service.ThreadService;
import javafx.scene.control.ContextMenu;
import javafx.scene.input.MouseButton;
import netscape.javascript.JSObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Created by usta on 09.04.2015.
 */
@Component
public class SlidePane extends ViewPanel {

    private String backend = "revealjs";
    private Logger logger = LoggerFactory.getLogger(SlidePane.class);

    @Autowired
    public SlidePane(ThreadService threadService, ApplicationController controller, Current current) {
        super(threadService, controller, current);

        ContextMenu contextMenu = new ContextMenu(MenuItemBuilt.item("Reload page").click(event -> {
            super.webEngine().reload();
            this.injectExtensions();
        }));

        contextMenu.setAutoHide(true);

        this.getWebView().setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.SECONDARY) {
                contextMenu.show(this.getWebView(), event.getScreenX(), event.getScreenY());
            } else {
                contextMenu.hide();
            }
        });
    }

    public void replaceSlides(String rendered) {
        String backendExt = backend + "Ext";
        try{
            ((JSObject) getWindow().eval(backendExt)).call("replaceSlides", rendered);
        }catch (Exception e){
            logger.debug("{} is not found while replacing slide, but don't worry.",backendExt, e);
        }

    }

    public void flipThePage(String rendered) {
        String backendExt = backend + "Ext";
        try {
            ((JSObject) getWindow().eval(backendExt)).call("flipCurrentPage", rendered);
        } catch (Exception e) {
            logger.debug("{} is not found while flipping page, but don't worry.",backendExt, e);
        }
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

package com.kodcu.component;

import com.kodcu.controller.ApplicationController;
import com.kodcu.other.Current;
import com.kodcu.other.IOHelper;
import com.kodcu.service.ThreadService;
import javafx.application.Platform;
import javafx.concurrent.Worker;
import javafx.scene.control.ContextMenu;
import javafx.scene.input.MouseButton;
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
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Created by usta on 09.04.2015.
 */
@Component
public class SlidePane extends ViewPanel {

    @Autowired
    public SlidePane(ThreadService threadService, ApplicationController controller, Current current) {
        super(threadService,controller,current);
        
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
        ((JSObject) getWindow().eval(current.currentSlideType() + "Ext")).call("replaceSlides", rendered);
    }

    public void flipThePage(String rendered) {
        try {
            ((JSObject) getWindow().eval(current.currentSlideType() + "Ext")).call("flipCurrentPage", rendered);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @WebkitCall
    public String getTemplate(String templateName, String templateDir) throws IOException {

        Stream<Path> slide = Files.find(controller.getConfigPath().resolve("slide").resolve(templateDir), Integer.MAX_VALUE, (path, basicFileAttributes) -> path.toString().contains(templateName));

        Optional<Path> first = slide.findFirst();

        if (!first.isPresent())
            return "";

        Path path = first.get();

        String template = IOHelper.readFile(path);
        return template;
    }

    public String findRenderedSelection(String content) {
        this.setMember("context", content);
        return (String) webEngine().executeScript("findRenderedSelection(context)");
    }

    public void injectExtensions() {
        this.setOnSuccess(() -> {
            String slideType = current.currentSlideType();
            if ("revealjs".equals(slideType))
                this.loadJs("js/jquery.js", "js/reveal-extensions.js");
            if ("deckjs".equals(slideType))
                this.loadJs("js/deck-extensions.js");
        });
    }

    @Override
    public void browse() {
        controller.getHostServices().showDocument(webEngine().getLocation());
    }
}

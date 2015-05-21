package com.kodcu.component;

import com.kodcu.controller.ApplicationController;
import com.kodcu.other.Current;
import com.kodcu.other.IOHelper;
import com.kodcu.service.ThreadService;
import javafx.application.Platform;
import netscape.javascript.JSObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Created by usta on 04.05.2015.
 */
@Component("deckjs-bean")
public class DeckSlidePane extends SlidePane {

    private final ApplicationController controller;
    private final Current current;

    @Autowired
    public DeckSlidePane(ThreadService threadService, ApplicationController controller, Current current) {
        super(threadService, controller,current);
        this.controller = controller;
        this.current = current;
    }

    public boolean isReady() {
            return true;
    }

    @Override
    public void load(String url) {
        super.load(url);
        Platform.runLater(() -> {
            this.setOnSuccess(() -> {
                this.loadJs("js/deck-extensions.js");
            });
        });

    }

    public void replaceSlides(String rendered) {
        ((JSObject) getWindow().eval("deckExt")).call("replaceSlides", rendered);
    }

    @Override
    public String getTemplate(String templateName, String templateDir) throws IOException {

        Stream<Path> slide = Files.find(controller.getConfigPath().resolve("slide").resolve(templateDir), Integer.MAX_VALUE, (path, basicFileAttributes) -> path.toString().contains(templateName));

        Optional<Path> first = slide.findFirst();

        if (!first.isPresent())
            return "";

        Path path = first.get();

        String template = IOHelper.readFile(path);
        return template;
    }

    public void flipThePage(String rendered) {
        ((JSObject) getWindow().eval("deckExt")).call("flipCurrentPage", rendered);
    }
}

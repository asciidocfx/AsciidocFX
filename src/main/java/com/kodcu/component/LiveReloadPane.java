package com.kodcu.component;

import com.kodcu.controller.ApplicationController;
import com.kodcu.other.Current;
import com.kodcu.other.IOHelper;
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
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Created by usta on 09.04.2015.
 *
 * NOTE : getWebEngine i diğaerlerinde de böyle yap
 * *********************************************************
 */
@Component
public class LiveReloadPane extends ViewPanel {

    @Autowired
    public LiveReloadPane(ThreadService threadService, ApplicationController controller, Current current) {
        super(threadService,controller,current);
    }

    @Override
    public void browse() {
        controller.getHostServices()
                .showDocument(String.format("http://localhost:%d/livereload/index.reload", controller.getPort()));
    }

    public void initializeDiffReplacer() {
        threadService.runActionLater(()->{
            String diffhtml = IOHelper.readFile(LiveReloadPane.class.getResourceAsStream("/public/diffhtml.js"));
            String extension = IOHelper.readFile(LiveReloadPane.class.getResourceAsStream("/public/diffhtml-extension.js"));
            webEngine().executeScript(diffhtml);
            webEngine().executeScript(extension);
        });
    }

    public void updateDomdom() {
        threadService.runActionLater(()->{
            getWindow().call("updateDomdom", current.currentEditorValue());
        });
    }
}

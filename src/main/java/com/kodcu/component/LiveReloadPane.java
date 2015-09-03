package com.kodcu.component;

import com.kodcu.controller.ApplicationController;
import com.kodcu.other.Current;
import com.kodcu.other.IOHelper;
import com.kodcu.service.ThreadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.nio.file.Path;

/**
 * Created by usta on 09.04.2015.
 * <p>
 * NOTE : getWebEngine i diğaerlerinde de böyle yap
 * *********************************************************
 */
@Component
public class LiveReloadPane extends ViewPanel {

    @Autowired
    public LiveReloadPane(ThreadService threadService, ApplicationController controller, Current current) {
        super(threadService, controller, current);
        this.webView.setContextMenuEnabled(true);
    }

    @Override
    public void browse() {
        controller.getHostServices()
                .showDocument(webEngine().getLocation());
    }

    public void initializeDiffReplacer() {
        threadService.runTaskLater(() -> {
            Path configPath = controller.getConfigPath();
            String diffHtml = IOHelper.readFile(configPath.resolve("public/js/diffhtml.js"));
            String extension = IOHelper.readFile(configPath.resolve("public/js/diffhtml-extension.js"));
            threadService.runActionLater(() -> {
                webEngine().executeScript(diffHtml);
                webEngine().executeScript(extension);
            });
        });
    }

    public void updateDomdom() {
        threadService.runActionLater(() -> {
            getWindow().call("updateDomdom", current.currentEditorValue());
        });
    }
}

package com.kodedu.component;

import com.kodedu.config.EditorConfigBean;
import com.kodedu.controller.ApplicationController;
import com.kodedu.other.Current;
import com.kodedu.other.IOHelper;
import com.kodedu.other.PositionalXMLReader;
import com.kodedu.service.ThreadService;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import org.joox.JOOX;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.nio.file.Path;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by usta on 09.04.2015.
 */
@Component
public class LiveReloadPane extends ViewPanel {

    private ConcurrentHashMap<String, String> lineXPathMap = new ConcurrentHashMap<>();
    private BooleanProperty ready = new SimpleBooleanProperty(false);

    @Autowired
    public LiveReloadPane(ThreadService threadService, ApplicationController controller, Current current, EditorConfigBean editorConfigBean) {
        super(threadService, controller, current, editorConfigBean);

        setOnSuccess(() -> {
            this.setMember("afx", this);
            this.initializeDiffReplacer();
        });

        webEngine().documentProperty().addListener((observable, oldDom, dom) -> {

            if (Objects.nonNull(dom)) {

                threadService.runTaskLater(() -> {
                    try {
                        Document document = PositionalXMLReader.readXML(dom);

                        JOOX.$(document).find("*").each(context -> {
                            Element element = context.element();

                            String lineNumber = (String) element.getUserData("lineNumber");

                            lineXPathMap.put((Long.valueOf(lineNumber) + 2) + "", JOOX.$(context).xpath());
                        });

                    } catch (Exception e) {
                    }
                });

            }
        });
    }

    @Override
    public void browse() {
        super.browse();
    }

    @Override
    public void runScroller(String text) {
        // no-op
    }

    @Override
    public void scrollByPosition(String text) {
        // no-op
    }

    @Override
    public void scrollByLine(String lineno) {

        String xPath = lineXPathMap.get(lineno);

        Optional.ofNullable(xPath)
                .ifPresent(xp -> {
                    webEngine().executeScript(String.format("scrollByXPath(\"%s\",%s)", xp, lineno));
                });
    }

    public void initializeDiffReplacer() {
        threadService.runTaskLater(() -> {
            Path configPath = controller.getConfigPath();
            String diffHtml = IOHelper.readFile(configPath.resolve("public/js/diffhtml.js"));
            String extension = IOHelper.readFile(configPath.resolve("public/js/diffhtml-extension.js"));
            String live = IOHelper.readFile(configPath.resolve("public/js/live-extension.js"));
            threadService.runActionLater(() -> {
                webEngine().executeScript(live);
                webEngine().executeScript(diffHtml);
                webEngine().executeScript(extension);
                ready.setValue(true);
            });
        });
    }

    public void updateDomdom() {
        threadService.runActionLater(() -> {
            getWindow().call("updateDomdom", current.currentEditorValue());
        });
    }

    public boolean getReady() {
        return ready.get();
    }

    public BooleanProperty readyProperty() {
        return ready;
    }
}

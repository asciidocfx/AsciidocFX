package com.kodcu.component;

import com.kodcu.controller.ApplicationController;
import com.kodcu.other.Current;
import com.kodcu.other.IOHelper;
import com.kodcu.other.PositionalXMLReader;
import com.kodcu.service.ThreadService;
import org.joox.JOOX;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.nio.file.Path;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by usta on 09.04.2015.
 * <p>
 * NOTE : getWebEngine i diğaerlerinde de böyle yap
 * *********************************************************
 */
@Component
public class LiveReloadPane extends ViewPanel {

    private ConcurrentHashMap<String, String> lineXPathMap = new ConcurrentHashMap<>();

    @Autowired
    public LiveReloadPane(ThreadService threadService, ApplicationController controller, Current current) {
        super(threadService, controller, current);

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
        controller.getHostServices()
                .showDocument(webEngine().getLocation());
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

        if (Objects.nonNull(xPath)) {
            webEngine().executeScript(String.format("scrollByXPath('%s',%s)", xPath, lineno));
        }
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
            });
        });
    }

    public void updateDomdom() {
        threadService.runActionLater(() -> {
            getWindow().call("updateDomdom", current.currentEditorValue());
        });
    }


}

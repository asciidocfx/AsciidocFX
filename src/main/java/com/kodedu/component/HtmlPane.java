package com.kodedu.component;

import com.kodedu.config.*;
import com.kodedu.controller.ApplicationController;
import com.kodedu.other.Current;
import com.kodedu.service.DirectoryService;
import com.kodedu.service.ThreadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * Created by usta on 09.04.2015.
 */
@Component
public class HtmlPane extends ViewPanel {

    private final PreviewConfigBean previewConfigBean;
    private final DocbookConfigBean docbookConfigBean;
    private final HtmlConfigBean htmlConfigBean;
    private final ThreadService threadService;
    private final Environment environment;

    @Value("${application.index.url}")
    private String indexUrl;

    @Value("${application.preview.url}")
    private String previewUrl;

    private final DirectoryService directoryService;

    @Autowired
    public HtmlPane(ThreadService threadService, ApplicationController controller, Current current, PreviewConfigBean previewConfigBean, DocbookConfigBean docbookConfigBean, HtmlConfigBean htmlConfigBean, EditorConfigBean editorConfigBean, Environment environment, DirectoryService directoryService) {
        super(threadService, controller, current, editorConfigBean);
        this.previewConfigBean = previewConfigBean;
        this.docbookConfigBean = docbookConfigBean;
        this.htmlConfigBean = htmlConfigBean;
        this.threadService = threadService;
        this.environment = environment;
        this.directoryService = directoryService;
    }

    public void loadInitialUrl() {
        int port = Integer.parseInt(environment.getProperty("local.server.port"));
        threadService.runActionLater(() -> {
            load(String.format(previewUrl, port, directoryService.interPath()));
        }, true);
    }

    @Override
    public void load(String url) {
        super.load(url);
    }

    public void refreshUI(String content) {
        threadService.runActionLater(() -> {
            this.setMember("lastRenderedValue", content);
            webEngine().executeScript("refreshUI(lastRenderedValue)");
        });
    }

    public void updateBase64Url(int index, String imageBase64) {
        threadService.runActionLater(() -> {
            getWindow().call("updateBase64Url", index, imageBase64);
        });
    }

    @Override
    public void browse() {
        controller.browseInDesktop(String.format(indexUrl, controller.getPort(), directoryService.interPath()));
    }

    @Override
    public void browse(BrowserType browserType) {
        controller.browseInDesktop(browserType, String.format(indexUrl, controller.getPort(), directoryService.interPath()));
    }

    @Override
    public void runScroller(String text) {
        getWindow().call("runScroller", text);
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

        runScroller(text);
    }
}

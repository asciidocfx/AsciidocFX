package com.kodedu.component;

import com.kodedu.config.*;
import com.kodedu.controller.ApplicationController;
import com.kodedu.engine.AsciidocWebkitConverter;
import com.kodedu.other.Current;
import com.kodedu.service.DirectoryService;
import com.kodedu.service.ThreadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${application.index.url}")
    private String indexUrl;

    @Value("${application.preview.url}")
    private String previewUrl;

    private final AsciidocWebkitConverter asciidocWebkitConverter;
    private final DirectoryService directoryService;

    @Autowired
    public HtmlPane(ThreadService threadService, ApplicationController controller, Current current, PreviewConfigBean previewConfigBean, DocbookConfigBean docbookConfigBean, HtmlConfigBean htmlConfigBean, EditorConfigBean editorConfigBean, AsciidocWebkitConverter asciidocWebkitConverter, DirectoryService directoryService) {
        super(threadService, controller, current, editorConfigBean);
        this.previewConfigBean = previewConfigBean;
        this.docbookConfigBean = docbookConfigBean;
        this.htmlConfigBean = htmlConfigBean;
        this.threadService = threadService;
        this.asciidocWebkitConverter = asciidocWebkitConverter;
        this.directoryService = directoryService;
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

package com.kodcu.component;

import com.kodcu.config.DocbookConfigBean;
import com.kodcu.config.HtmlConfigBean;
import com.kodcu.config.OdfConfigBean;
import com.kodcu.config.PreviewConfigBean;
import com.kodcu.controller.ApplicationController;
import com.kodcu.engine.AsciidocWebkitConverter;
import com.kodcu.other.Current;
import com.kodcu.service.ThreadService;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Created by usta on 09.04.2015.
 */
@Component
public class HtmlPane extends ViewPanel {

    private final PreviewConfigBean previewConfigBean;
    private final OdfConfigBean odfConfigBean;
    private final DocbookConfigBean docbookConfigBean;
    private final HtmlConfigBean htmlConfigBean;
    private final ThreadService threadService;

    @Value("${application.index.url}")
    private String browseUrl;
    private final AsciidocWebkitConverter asciidocWebkitConverter;

    @Autowired
    public HtmlPane(ThreadService threadService, ApplicationController controller, Current current, PreviewConfigBean previewConfigBean, OdfConfigBean odfConfigBean, DocbookConfigBean docbookConfigBean, HtmlConfigBean htmlConfigBean, AsciidocWebkitConverter asciidocWebkitConverter) {
        super(threadService, controller, current);
        this.previewConfigBean = previewConfigBean;
        this.odfConfigBean = odfConfigBean;
        this.docbookConfigBean = docbookConfigBean;
        this.htmlConfigBean = htmlConfigBean;
        this.threadService = threadService;
        this.asciidocWebkitConverter = asciidocWebkitConverter;
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
        controller.getHostServices()
                .showDocument(String.format(browseUrl, controller.getPort()));
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

        String selection = asciidocWebkitConverter.findRenderedSelection(text);

        runScroller(selection);
    }
}

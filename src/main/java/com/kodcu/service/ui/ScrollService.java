package com.kodcu.service.ui;

import com.kodcu.controller.ApplicationController;
import javafx.scene.web.WebEngine;
import netscape.javascript.JSObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * Created by usta on 25.12.2014.
 */
@Component
public class ScrollService {

    private final Logger logger = LoggerFactory.getLogger(ScrollService.class);

    private final ApplicationController controller;

    @Autowired
    public ScrollService(final ApplicationController controller) {
        this.controller = controller;
    }

    public void onscroll(Object pos, Object max) {
        if (Objects.isNull(pos) || Objects.isNull(max))
            return;

        Number position = (Number) pos; // current scroll position for editor
        Number maximum = (Number) max; // max scroll position for editor

        double currentY = (position.doubleValue() < 0) ? 0 : position.doubleValue();
        double ratio = (currentY * 100) / maximum.doubleValue();
        WebEngine previewEngine = controller.getPreviewView().getEngine();
        Integer browserMaxScroll = (Integer) previewEngine.executeScript("document.documentElement.scrollHeight - document.documentElement.clientHeight;");
        double browserScrollOffset = (Double.valueOf(browserMaxScroll) * ratio) / 100.0;
        previewEngine.executeScript(String.format("window.scrollTo(0, %f )", browserScrollOffset));
    }

    public void scrollToCurrentLine(String text) {
        try {
            WebEngine engine = controller.getPreviewView().getEngine();
            ((JSObject) engine.executeScript("window")).call("runScroller", text);
        } catch (Exception e) {
            logger.debug(e.getMessage(), e);
        }
    }
}

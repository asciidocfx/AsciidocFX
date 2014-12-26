package com.kodcu.service.ui;

import com.kodcu.controller.ApplicationController;
import com.kodcu.other.IOHelper;
import javafx.scene.web.WebEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * Created by usta on 25.12.2014.
 */
@Component
public class ScrollService {

    @Autowired
    private ApplicationController controller;

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
        text = IOHelper.normalize(text);

        String format = String.format("runScroller('%s')", text);
        try {
            WebEngine engine = controller.getPreviewView().getEngine();
            engine.executeScript(format);
        } catch (Exception e) {

        }
    }
}

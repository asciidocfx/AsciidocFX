package com.kodcu.service.ui;

import com.kodcu.component.HtmlPane;
import com.kodcu.component.SlidePane;
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
    private final HtmlPane htmlPane;
    private SlidePane slidePane;

    @Autowired
    public ScrollService(final ApplicationController controller, final HtmlPane htmlPane) {
        this.controller = controller;
        this.htmlPane = htmlPane;
    }

    public void onscroll(Object pos, Object max) {
        if (Objects.isNull(pos) || Objects.isNull(max))
            return;

        Number position = (Number) pos; // current scroll position for editor
        Number maximum = (Number) max; // max scroll position for editor

        htmlPane.onscroll(position,maximum);
    }

    public void scrollToCurrentLine(String text) {
        if((htmlPane.isVisible()))
            htmlPane.scrollToCurrentLine(text);
        else{
            // slidePane in action
            String content = htmlPane.findRenderedSelection(text);
            System.out.println(content);
//        ekle    slidePane.flipThePage(content);
        }
    }
}

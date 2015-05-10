package com.kodcu.component;

import com.kodcu.controller.ApplicationController;
import com.kodcu.service.ThreadService;
import netscape.javascript.JSObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by usta on 04.05.2015.
 */
@Component
public class DeckSlidePane extends SlidePane {

    @Autowired
    public DeckSlidePane(ThreadService threadService, ApplicationController controller) {
        super(threadService, controller);
    }

    public boolean isReady() {
        if (true)
            return true;
        try {
            return (Boolean) ((JSObject) window.eval("Reveal")).call("isReady");
        } catch (Exception ex) {
            logger.info(ex.getMessage(), ex);
            return false;
        }
    }

    public void replaceSlides(String rendered) {
        ((JSObject) window.eval("deckExt")).call("replaceSlides", rendered);
    }

    public void flipThePage(String rendered) {
        ((JSObject) window.eval("deckExt")).call("flipCurrentPage", rendered);
    }
}

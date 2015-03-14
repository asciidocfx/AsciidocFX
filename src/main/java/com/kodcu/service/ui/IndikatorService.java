package com.kodcu.service.ui;

import com.kodcu.controller.ApplicationController;
import com.kodcu.service.ThreadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by usta on 01.09.2014.
 */
@Component
public class IndikatorService {

    private final ApplicationController asciiDocController;
    private final ThreadService threadService;
    
    @Autowired
    public IndikatorService(final ApplicationController asciiDocController, final ThreadService threadService) {
        this.asciiDocController = asciiDocController;
        this.threadService = threadService;
    }

    public void startCycle() {
        threadService.runActionLater(() -> {
            asciiDocController.getPreviewView().getEngine().executeScript("startProgressBar()");
//            asciiDocController.getIndikator().setVisible(true);
//            asciiDocController.getIndikator().setManaged(true);
        });
    }

    public void completeCycle() {
        threadService.runActionLater(() -> {
            asciiDocController.getPreviewView().getEngine().executeScript("stopProgressBar()");
//            asciiDocController.getIndikator().setManaged(false);
//            asciiDocController.getIndikator().setVisible(false);
        });
    }

    public void hideIndikator() {
        completeCycle();
    }


}

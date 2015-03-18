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

    private final ApplicationController applicationContoller;
    private final ThreadService threadService;
    
    @Autowired
    public IndikatorService(final ApplicationController applicationContoller, final ThreadService threadService) {
        this.applicationContoller = applicationContoller;
        this.threadService = threadService;
    }

    public void startCycle() {
        threadService.runActionLater(() -> {
            applicationContoller.getPreviewView().getEngine().executeScript("startProgressBar()");
        });
    }

    public void completeCycle() {
        threadService.runActionLater(() -> {
            applicationContoller.getPreviewView().getEngine().executeScript("stopProgressBar()");
        });
    }

}

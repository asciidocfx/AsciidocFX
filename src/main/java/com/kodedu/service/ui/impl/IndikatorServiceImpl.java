package com.kodedu.service.ui.impl;

import com.kodedu.controller.ApplicationController;
import com.kodedu.service.ThreadService;
import com.kodedu.service.ui.IndikatorService;

import javafx.animation.Timeline;
import javafx.scene.control.ProgressBar;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by usta on 01.09.2014.
 */
@Component(IndikatorService.label)
public class IndikatorServiceImpl implements IndikatorService {

    @Autowired
    private ApplicationController applicationContoller;

    @Autowired
    private ThreadService threadService;

    @Override
    public void startProgressBar() {
        threadService.runActionLater(() -> {
            ProgressBar progressBar = applicationContoller.getProgressBar();
            Timeline timeline = applicationContoller.getProgressBarTimeline();
            progressBar.setVisible(true);
            progressBar.setManaged(true);
            timeline.playFromStart();
        });
    }

    @Override
    public void stopProgressBar() {
        threadService.runActionLater(() -> {
            ProgressBar progressBar = applicationContoller.getProgressBar();
            Timeline timeline = applicationContoller.getProgressBarTimeline();
            progressBar.setVisible(false);
            progressBar.setManaged(false);
            timeline.stop();
        });
    }

}

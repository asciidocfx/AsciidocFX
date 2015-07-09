package com.kodcu.service.ui;

import com.kodcu.component.HtmlPane;
import com.kodcu.controller.ApplicationController;
import com.kodcu.service.ThreadService;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.control.ProgressBar;
import javafx.util.Duration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by usta on 01.09.2014.
 */
@Component
public class IndikatorService {

    private final ApplicationController applicationContoller;
    private final ThreadService threadService;
    private final HtmlPane htmlPane;
    
    @Autowired
    public IndikatorService(final ApplicationController applicationContoller, final ThreadService threadService, HtmlPane htmlPane) {
        this.applicationContoller = applicationContoller;
        this.threadService = threadService;
        this.htmlPane = htmlPane;
    }

    public void startProgressBar() {

        threadService.runActionLater(() -> {
            ProgressBar progressBar = applicationContoller.getProgressBar();
            Timeline timeline = applicationContoller.getProgressBarTimeline();
            progressBar.setVisible(true);
            progressBar.setManaged(true);
            timeline.playFromStart();
        });
    }

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

package com.kodedu.service.ui.impl;

import com.kodedu.controller.ApplicationController;
import com.kodedu.service.ThreadService;
import com.kodedu.service.ui.IndikatorService;
import javafx.animation.Timeline;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.ProgressBar;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.awt.*;

import static com.kodedu.helper.TaskbarHelper.getTaskBar;

/**
 * Created by usta on 01.09.2014.
 */
@Component(IndikatorService.label)
public class IndikatorServiceImpl implements IndikatorService {

    @Autowired
    private ApplicationController applicationContoller;

    @Autowired
    private ThreadService threadService;

    private static void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
        updateTaskbarProgress(newValue);
    }

    private static void updateTaskbarProgress(Number newValue) {
        try {
            getTaskBar()
                    .filter(t-> t.isSupported(Taskbar.Feature.PROGRESS_VALUE))
                    .ifPresent(t -> t.setProgressValue((int) (newValue.doubleValue() * 100)));
        } catch (Exception e) {

        }
    }

    @Override
    public void startProgressBar() {
        threadService.runActionLater(() -> {
            ProgressBar progressBar = applicationContoller.getProgressBar();
            progressBar.progressProperty().removeListener(IndikatorServiceImpl::changed);
            progressBar.progressProperty().addListener(IndikatorServiceImpl::changed);
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
            progressBar.progressProperty().removeListener(IndikatorServiceImpl::changed);
            Timeline timeline = applicationContoller.getProgressBarTimeline();
            progressBar.setVisible(false);
            progressBar.setManaged(false);
            timeline.stop();
            updateTaskbarProgress(-1);
        });
    }

}

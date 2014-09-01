package com.kodcu.service;

import com.kodcu.controller.AsciiDocController;
import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.util.Duration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by usta on 01.09.2014.
 */
@Component
public class IndikatorService {

    @Autowired
    private AsciiDocController asciiDocController;

    public void startCycle() {
        Platform.runLater(() -> {
            asciiDocController.getIndikator().setVisible(true);
            FadeTransition fadeIn = new FadeTransition(Duration.seconds(4));
            fadeIn.setNode(asciiDocController.getIndikator());
            fadeIn.setFromValue(0.0);
            fadeIn.setToValue(1.0);
            fadeIn.setCycleCount(1);
            fadeIn.setAutoReverse(false);
            fadeIn.playFromStart();
        });
    }

    public void completeCycle() {
        Platform.runLater(() -> {
            asciiDocController.getIndikator().setProgress(1);
            FadeTransition fadeOut = new FadeTransition(Duration.seconds(4));
            fadeOut.setNode(asciiDocController.getIndikator());
            fadeOut.setFromValue(1.0);
            fadeOut.setToValue(0.0);
            fadeOut.setCycleCount(1);
            fadeOut.setAutoReverse(false);
            fadeOut.playFromStart();
        });
    }

    public void hideIndikator() {
        Platform.runLater(() -> {
            asciiDocController.getIndikator().setProgress(-1);
            asciiDocController.getIndikator().setVisible(false);
        });
    }
}

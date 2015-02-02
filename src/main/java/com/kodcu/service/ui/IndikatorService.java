package com.kodcu.service.ui;

import com.kodcu.controller.ApplicationController;
import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by usta on 01.09.2014.
 */
@Component
public class IndikatorService {

    @Autowired
    private ApplicationController asciiDocController;

    public void startCycle() {
        Platform.runLater(() -> {
            asciiDocController.getIndikator().setVisible(true);
            asciiDocController.getIndikator().setManaged(true);
        });
    }

    public void completeCycle() {
        Platform.runLater(() -> {
            asciiDocController.getIndikator().setManaged(false);
            asciiDocController.getIndikator().setVisible(false);
        });
    }

    public void hideIndikator() {
        completeCycle();
    }


}

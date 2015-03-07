package com.kodcu.component;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.util.Optional;

/**
 * Created by usta on 06.03.2015.
 */
public class SaveAlert extends Alert {

    public SaveAlert() {
        super(Alert.AlertType.WARNING, null, ButtonType.YES, ButtonType.CANCEL);
        super.setTitle("Warning");
        super.setHeaderText("This document is not saved. Do you want to close it?");
    }


    public static Optional<ButtonType> alert() {
        SaveAlert saveAlert = new SaveAlert();
        return saveAlert.showAndWait();
    }
}

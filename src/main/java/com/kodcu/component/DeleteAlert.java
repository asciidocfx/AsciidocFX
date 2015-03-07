package com.kodcu.component;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.util.Optional;

/**
 * Created by usta on 06.03.2015.
 */
public class DeleteAlert extends Alert {

    public DeleteAlert() {
        super(AlertType.WARNING, null, ButtonType.YES, ButtonType.CANCEL);
        super.setTitle("Warning");
        super.setHeaderText("Do you want to remove file(s)?");
    }


    public static Optional<ButtonType> alert() {
        DeleteAlert saveAlert = new DeleteAlert();
        return saveAlert.showAndWait();
    }
}

package com.kodcu.component;

import javafx.scene.control.ButtonType;

import java.util.Optional;

/**
 * Created by usta on 06.03.2015.
 */
public final class DeleteAlert extends AlertDialog {

    public DeleteAlert() {
        super.setHeaderText("Do you want to remove file(s)?");
    }


    public static Optional<ButtonType> alert() {
        DeleteAlert deleteAlert = new DeleteAlert();
        deleteAlert.setDefaultIcon();
        return deleteAlert.showAndWait();
    }
}

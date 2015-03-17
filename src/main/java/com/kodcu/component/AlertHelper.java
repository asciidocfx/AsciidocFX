package com.kodcu.component;

import javafx.scene.control.ButtonType;

import java.util.Optional;

/**
 * Created by usta on 06.03.2015.
 */
public final class AlertHelper extends AlertDialog {

    public static Optional<ButtonType> deleteAlert() {
        AlertHelper deleteAlert = new AlertHelper();
        deleteAlert.setHeaderText("Do you want to remove file(s)?");
        deleteAlert.setDefaultIcon();
        return deleteAlert.showAndWait();
    }

    public static Optional<ButtonType> nullDirectoryAlert() {
        AlertHelper deleteAlert = new AlertHelper();
        deleteAlert.setHeaderText("Please select directorie(s)");
        deleteAlert.setDefaultIcon();
        return deleteAlert.showAndWait();
    }
}

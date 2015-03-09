package com.kodcu.component;

import javafx.scene.control.ButtonType;
import java.util.Optional;

/**
 * Created by usta on 06.03.2015.
 */
public final class SaveAlert extends AlertDialog {

    public SaveAlert() {
        super.setHeaderText("This document is not saved. Do you want to close it?");
    }


    public static Optional<ButtonType> alert() {
        SaveAlert saveAlert = new SaveAlert();
        saveAlert.setDefaultIcon();
        return saveAlert.showAndWait();
    }
}

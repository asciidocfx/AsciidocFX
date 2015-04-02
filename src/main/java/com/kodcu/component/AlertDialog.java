package com.kodcu.component;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

/**
 * Created by Hakan on 3/9/2015.
 */
public class AlertDialog extends Alert implements DefenderDialog {

    public AlertDialog() {
        super(AlertType.WARNING, null, ButtonType.YES, ButtonType.CANCEL);
        super.setTitle("Warning");
        setDefaultIcon(super.getDialogPane());
    }

    public AlertDialog(AlertType alertType) {
        super(alertType);
        setDefaultIcon(super.getDialogPane());
    }

    public AlertDialog(AlertType alertType, String contentText, ButtonType... buttons) {
        super(alertType, contentText, buttons);
        setDefaultIcon(super.getDialogPane());
    }

}

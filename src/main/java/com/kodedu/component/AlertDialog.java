package com.kodedu.component;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Modality;

/**
 * Created by Hakan on 3/9/2015.
 */
public class AlertDialog extends WindowModalAlert implements DefenderDialog {

    public AlertDialog() {
        super(AlertType.WARNING, null, ButtonType.YES, ButtonType.CANCEL);
        super.setTitle("Warning");
        super.initModality(Modality.WINDOW_MODAL);
        setDefaultIcon(super.getDialogPane());
    }

    public AlertDialog(AlertType alertType) {
        super(alertType);
        super.initModality(Modality.WINDOW_MODAL);
        setDefaultIcon(super.getDialogPane());
    }

    public AlertDialog(AlertType alertType, String contentText, ButtonType... buttons) {
        super(alertType, contentText, buttons);
        super.initModality(Modality.WINDOW_MODAL);
        setDefaultIcon(super.getDialogPane());
    }

}

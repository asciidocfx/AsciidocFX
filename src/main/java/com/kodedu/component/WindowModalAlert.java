package com.kodedu.component;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Modality;

public class WindowModalAlert extends Alert {
    public WindowModalAlert(AlertType alertType) {
        super(alertType);
        super.initModality(Modality.WINDOW_MODAL);
    }

    public WindowModalAlert(AlertType alertType, String contentText, ButtonType... buttons) {
        super(alertType, contentText, buttons);
        super.initModality(Modality.WINDOW_MODAL);
    }

}

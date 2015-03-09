package com.kodcu.component;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.stage.Stage;

/**
 * Created by Hakan on 3/9/2015.
 */
public class AlertDialog extends Alert {

    public AlertDialog() {
        super(AlertType.WARNING, null, ButtonType.YES, ButtonType.CANCEL);
        super.setTitle("Warning");
    }

    public void setDefaultIcon(){
        Stage stage = (Stage) super.getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image(getClass().getResourceAsStream("/logo.png")));
    }

}

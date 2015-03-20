package com.kodcu.component;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Hakan on 3/9/2015.
 */
public class AlertDialog extends Alert {

    public AlertDialog() {
        super(AlertType.WARNING, null, ButtonType.YES, ButtonType.CANCEL);
        super.setTitle("Warning");
        setDefaultIcon();
    }

    public AlertDialog(AlertType alertType) {
        super(alertType);
        setDefaultIcon();
    }

    public AlertDialog(AlertType alertType, String contentText, ButtonType... buttons) {
        super(alertType, contentText, buttons);
        setDefaultIcon();
    }

    public void setDefaultIcon(){
        Stage stage = (Stage) super.getDialogPane().getScene().getWindow();
        try(InputStream logoStream = getClass().getResourceAsStream("/logo.png");){
            stage.getIcons().add(new Image(logoStream));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

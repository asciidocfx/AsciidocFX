package com.kodedu.component;

import javafx.scene.control.Alert;
import javafx.scene.control.DialogPane;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.Tooltip;
import javafx.stage.Stage;

/**
 * Created by Hakan on 4/1/2015.
 */
public class TextDialog extends TextInputDialog implements DefenderDialog {

    public TextDialog() {
        showAlwaysOnTop(super.getDialogPane());
    }

    public TextDialog(String defaultValue) {
        super(defaultValue);
        showAlwaysOnTop(super.getDialogPane());
    }

    public TextDialog(String contentText, String title) {
        super.setContentText(contentText);
        super.setTitle(title);
        setDefaultIcon(super.getDialogPane());
        showAlwaysOnTop(super.getDialogPane());
    }

    public void setKeyReleaseEvent(String regex) {
        super.getEditor().setOnKeyReleased(event -> {
            String text = super.getEditor().getText();
            boolean matches = text.matches(regex);
            if (matches) {
                super.getEditor().setStyle("-fx-border-color: blue; -fx-focus-color: blue;");
            } else {
                super.getEditor().setStyle("-fx-border-color: red; -fx-focus-color: red;");
            }
        });
    }

    public void setToolTip(String tip) {
        Tooltip.install(super.getEditor(), new Tooltip(tip));
    }

    private static void showAlwaysOnTop(DialogPane dialogPane) {
        ((Stage) dialogPane.getScene().getWindow()).setAlwaysOnTop(true);
    }

    private static void showAlwaysOnTop(Alert alert) {
        showAlwaysOnTop(alert.getDialogPane());
    }

}

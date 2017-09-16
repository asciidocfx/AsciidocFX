package com.kodedu.component;

import javafx.scene.control.TextInputDialog;
import javafx.scene.control.Tooltip;

/**
 * Created by Hakan on 4/1/2015.
 */
public class TextDialog extends TextInputDialog implements DefenderDialog {

    public TextDialog() {
    }

    public TextDialog(String defaultValue) {
        super(defaultValue);
    }

    public TextDialog(String contentText, String title) {
        super.setContentText(contentText);
        super.setTitle(title);
        setDefaultIcon(super.getDialogPane());
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

}

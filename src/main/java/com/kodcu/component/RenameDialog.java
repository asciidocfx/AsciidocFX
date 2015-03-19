package com.kodcu.component;

import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;

/**
 * Created by usta on 16.03.2015.
 */
public class RenameDialog extends TextInputDialog {

    public static RenameDialog create() {
        RenameDialog dialog = new RenameDialog();
        dialog.setContentText("Enter new file name ");
        dialog.setTitle("Rename file ");
        TextField editor = dialog.getEditor();
        editor.setOnKeyReleased(event -> {
            String text = editor.getText().trim();
            boolean matches = text.matches("^[^\\\\/:?*\"<>|]+$");
            if (matches) {
                editor.setStyle("-fx-border-color: blue; -fx-focus-color: blue;");
            } else {
                editor.setStyle("-fx-border-color: red; -fx-focus-color: red;");
            }
        });
        return dialog;
    }
}

package com.kodcu.component;

import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.Tooltip;

/**
 * Created by usta on 16.03.2015.
 */
public class SaveDialog extends TextInputDialog {

    public static SaveDialog create() {
        SaveDialog dialog = new SaveDialog();
        dialog.setContentText("Enter new file name ");
        dialog.setTitle("Create new file ");
        TextField editor = dialog.getEditor();
        Tooltip.install(editor, new Tooltip("**.{asc,adoc,ad,asiidoc,md,markdown,txt} allowed"));
        editor.setOnKeyReleased(event -> {
            String text = editor.getText();
            boolean matches = text.matches("^[^\\\\/:?*\"<>|]+\\.(asc|md|adoc|asciidoc|ad|markdown|txt)");
            if (text.contains(".") && matches) {
                editor.setStyle("-fx-border-color: blue; -fx-focus-color: blue;");
            } else {
                editor.setStyle("-fx-border-color: red; -fx-focus-color: red;");
            }
        });

        return dialog;
    }
}

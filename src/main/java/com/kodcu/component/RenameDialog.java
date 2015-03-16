package com.kodcu.component;

import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.Tooltip;

/**
 * Created by usta on 16.03.2015.
 */
public class RenameDialog extends TextInputDialog {

    public static RenameDialog create() {
        RenameDialog dialog = new RenameDialog();
        dialog.setContentText("Enter new file name ");
        dialog.setTitle("Rename file ");
        return dialog;
    }
}

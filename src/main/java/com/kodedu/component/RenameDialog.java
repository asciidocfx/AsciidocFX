package com.kodedu.component;

/**
 * Created by usta on 16.03.2015.
 */
public final class RenameDialog extends TextDialog {

    public RenameDialog(String content, String title) {
        super(content, title);
    }

    public static RenameDialog create() {
        RenameDialog dialog = new RenameDialog("Enter new file name ", "Rename file ");
        dialog.setKeyReleaseEvent("^[^\\\\/:?*\"<>|]+$");
        return dialog;
    }
}

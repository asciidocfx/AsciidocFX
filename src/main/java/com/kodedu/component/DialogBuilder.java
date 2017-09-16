package com.kodedu.component;

/**
 * Created by usta on 16.03.2015.
 */
public final class DialogBuilder extends TextDialog {

    public final static String FILE_NAME_REGEX = ".*\\S.*";
    public final static String FOLDER_NAME_REGEX = ".*\\S.*";

    public DialogBuilder(String content, String title) {
        super(content, title);
    }

    public static DialogBuilder newFileDialog() {
        DialogBuilder dialog = new DialogBuilder("Enter new file name ", "Create new file ");
        dialog.setToolTip("Enter new file name");

        dialog.setKeyReleaseEvent(FILE_NAME_REGEX);
        return dialog;
    }

    public static DialogBuilder newFolderDialog() {
        DialogBuilder dialog = new DialogBuilder("Enter new directory name ", "Create new directory ");
        dialog.setToolTip("Enter new directory name");
        dialog.setKeyReleaseEvent(FOLDER_NAME_REGEX);
        return dialog;
    }
}

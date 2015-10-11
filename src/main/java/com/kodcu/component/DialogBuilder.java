package com.kodcu.component;

/**
 * Created by usta on 16.03.2015.
 */
public final class DialogBuilder extends TextDialog {

    public final static String FILE_NAME_REGEX = "^[\\w\\-\\\\/]*\\.[\\w\\-]+$";
    public final static String FOLDER_NAME_REGEX = "^[\\w\\-]*\\.*[\\w\\-]+$";

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
        DialogBuilder dialog = new DialogBuilder("Enter new folder name ", "Create new folder ");
        dialog.setToolTip("Enter new folder name");
        dialog.setKeyReleaseEvent(FOLDER_NAME_REGEX);
        return dialog;
    }
}

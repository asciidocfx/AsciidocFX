package com.kodcu.component;

/**
 * Created by usta on 16.03.2015.
 */
public final class SaveDialog extends TextDialog {

    public SaveDialog(String content, String title) {
        super(content, title);
    }

    public static SaveDialog create() {
        SaveDialog dialog = new SaveDialog("Enter new file name ", "Create new file ");
        dialog.setToolTip("**.{asc,adoc,ad,asiidoc,md,markdown,txt} allowed");
        dialog.setKeyReleaseEvent("^[^\\\\/:?*\"<>|]+\\.(asc|md|adoc|asciidoc|ad|markdown|txt)");
        return dialog;
    }
}

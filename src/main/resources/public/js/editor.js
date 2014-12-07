function waitForConfig(fontSize, theme, speed) {
        editor.setFontSize(Number(fontSize));
        editor.setTheme(theme);
        editor.setScrollSpeed(Number(speed));
}

function waitForGetValue() {

    app.textListener(editor.getValue());

}

function waitForSetValue(content) {

    editor.setValue(content);
    editor.clearSelection();
    editor.session.setScrollTop(0);
    editor.focus();

}
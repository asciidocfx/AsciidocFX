
function waitForConfig() {
    try {
        editor.setFontSize(Number('%s'));
        editor.setTheme("ace/theme/%s");
        editor.setScrollSpeed(Number('%s'));
    }
    catch (e) {
        setTimeout(waitForConfig, 100);
    }

}

waitForConfig();
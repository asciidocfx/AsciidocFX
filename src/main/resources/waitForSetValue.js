function waitForSetValue() {
    try {
        editor.setValue('%s');
        editor.clearSelection();
        editor.session.setScrollTop(0);
        editor.focus();
    }
    catch (e) {
        setTimeout(waitForSetValue, 100);
    }

}

waitForSetValue();
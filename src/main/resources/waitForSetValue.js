function waitForSetValue() {
    try {
        editor.setValue('%s');
    }
    catch (e) {
        setTimeout(waitForSetValue, 100);
    }

}

waitForSetValue();
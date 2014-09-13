function waitForGetValue() {

    try {
        app.textListener(editor.getValue());
    }
    catch (e) {
        setTimeout(waitForGetValue, 100);
    }
}

waitForGetValue();
function waitForGetValue() {

    try {
        app.textListener(null, null, editor.getValue());
    }
    catch (e) {
        setTimeout(waitForGetValue, 100);
    }
}

waitForGetValue();
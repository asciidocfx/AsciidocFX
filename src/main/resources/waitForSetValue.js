function waitForSetValue() {
    try {
        editor.setValue('%s');
        editor.clearSelection();
        editor.session.setScrollTop(0);
        editor.focus();
        setTimeout(function(){
            app.wildcardAppendListener();
        },1000);
    }
    catch (e) {
        setTimeout(waitForSetValue, 100);
    }

}

waitForSetValue();
//diff.addTransitionState('attached', function (element) {
//    // Fade in the main container after it's attached into the DOM.
//    if (element.tagName == "style" || element.tagName == "STYLE") {
//        afx.reloadLive()
//    }
//});

diff.addTransitionState('textChanged', function (el, old, current) {
    afx.debug(el, old, current);

    //return;
    //if(el){
    //    if (el.tagName == "style" || el.tagName == "STYLE") {
    //        afx.reloadLive()
    //    }
    //}
});

function createEmptyDom() {
    document.open();
    document.write("<html><head></head><body></body></html>");
    document.close();
    return document.querySelector('html');
}

function updateDomdom(value) {
    var element = document.querySelector('html') || createEmptyDom();

    if (value) {
        if (value.length > 0) {
            if (value[0] != "<") {
                value = "<div>" + value + "</div>"
            }
        }
    }

    diff.outerHTML(element, value);
}
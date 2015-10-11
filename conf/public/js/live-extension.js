function scrollByXPath(xPath) {
    var iterator = document.evaluate(xPath, document, null, XPathResult.ANY_TYPE, null);

    if(iterator){
        var element = iterator.iterateNext();

        if (element) {
            element.scrollIntoView(true);
        }
    }

}

alert("LIVE_LOADED");
function createEmptyDom() {
    document.open();
    document.write("<html></html>");
    document.close();
    return document.querySelector('html');
}

function updateDomdom(value) {
    var element = document.querySelector('html') || createEmptyDom();
    element.outerDiffHTML = value;
}
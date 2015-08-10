diff.enableProllyfill();
document.DISABLE_WORKER = true;

function createEmptyDom() {
    document.open();
    document.write("<html></html>");
    document.close();
    return document.querySelector('html');
}

function updateDomdom(value) {
    var element = document.querySelector('html') || createEmptyDom();

    element.diffOuterHTML = value;

    element.addTransitionState('added', function (newElement) {
        document.querySelector("input").value = "newElement: " + newElement;
    });


}
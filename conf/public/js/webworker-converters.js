function getOption(options) {
    return Opal.hash(JSON.parse(options));
}

var fillOutAction = new BufferedAction();
function convertBackend(taskId, content, options) {

    var doc = asciidoctor.$load(content, getOption(options));
    var rendered = doc.$convert();

    self.postMessage(JSON.stringify({
        type: "afx",
        func: "completeWebWorker",
        parameters: [taskId, rendered, doc.$backend(), doc.doctype]
    }));

    fillOutAction.buff(function () {
        fillOutlines(doc);
    }, 1000);
}

function convertAsciidoc(taskId, content, options) {

    convertBackend(taskId, content, options);

}

function convertOdf(taskId, content, options) {

    convertBackend(taskId, content, options);
}

function convertHtml(taskId, content, options) {

    convertBackend(taskId, content, options);
}

function convertDocbook(taskId, content, options) {

    convertBackend(taskId, content, options);
}

function findRenderedSelection(content) {
    return asciidoctor.$render(content);
}
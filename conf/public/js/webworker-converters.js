function getOption(options) {
    return Opal.hash(JSON.parse(options));
}

function convertBackend(taskId, content, options) {

    var doc = Opal.Asciidoctor.$load(content, getOption(options));
    var rendered = doc.$convert();

    var result = {
        taskId: taskId,
        rendered: rendered,
        doctype: doc.doctype,
        backend: doc.$backend()
    };

    self.postMessage(JSON.stringify(result));

    fillOutAction.buff(function () {
        fillOutlines(doc);
    }, 1000);
}

var fillOutAction = new BufferedAction();
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
    return Opal.Asciidoctor.$render(content);
}
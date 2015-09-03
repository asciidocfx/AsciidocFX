function getOption(options) {
    return Opal.hash(JSON.parse(options));
}

var fillOutAction = new BufferedAction();
function convertAsciidoc(content, options) {

    var rendered = "";

    var doc = Opal.Asciidoctor.$load(content, getOption(options));

    fillOutAction.buff(function () {
        afx.fillOutlines(doc);
    }, 3000);

    rendered = doc.$convert();

    return {
        rendered: rendered,
        doctype: doc.doctype,
        backend: doc.$backend()
    };
}

function convertOdf(content, options) {

    var doc = Opal.Asciidoctor.$load(content, getOption(options));

    return doc.$convert();
}

function convertHtml(content, options) {

    var doc = Opal.Asciidoctor.$load(content, getOption(options));

    return {
        rendered: doc.$render(),
        doctype: doc.doctype,
        backend: doc.$backend()
    };
}

function convertDocbook(content, options) {

    var doc = Opal.Asciidoctor.$load(content, getOption(options));

    return {
        rendered: doc.$render(),
        doctype: doc.doctype,
        backend: doc.$backend()
    };
}

function findRenderedSelection(content) {
    return Opal.Asciidoctor.$render(content);
}
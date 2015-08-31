if ((typeof setTimeout) == "undefined") {
    setTimeout = function () {
        arguments[0]();
    }

    clearTimeout = function () {
    };
}

function getOption(options) {
    return Opal.hash(JSON.parse(options));
}

var filloutTimeout;
function convertAsciidoc(content, options) {

    var rendered = "";

    var doc = Opal.Asciidoctor.$load(content, getOption(options));

    if (filloutTimeout)
        clearTimeout(filloutTimeout);

    filloutTimeout = setTimeout(function () {
        try {
            afx.fillOutlines(doc);
        }
        catch (e) {
            throw e;
        }
    }, 1000);

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
function getOption(options) {
    return Opal.hash(JSON.parse(options));
}

function convertBackend(content, options) {
    var doc = Opal.Asciidoctor.$load(content, getOption(options));
    var rendered = doc.$convert();

    var result = {
        rendered: rendered,
        doctype: doc.doctype,
        backend: doc.$backend()
    };

    return result;
}

function convertAsciidoc(content, options) {
 return  convertBackend(content, options);
}

function convertOdf(content, options) {
    return  convertBackend(content, options);
}

function convertHtml(content, options) {
    return  convertBackend(content, options);
}

function convertDocbook(content, options) {
    return  convertBackend(content, options);
}

function findRenderedSelection(content) {
    return Opal.Asciidoctor.$render(content);
}
function renderToHtml() {

    try {
        var rendered = Opal.Asciidoctor.$render('%s');
        app.getLastRendered().setValue(rendered);
    }
    catch (e) {
        setTimeout(renderToHtml, 100);
    }
}

renderToHtml();

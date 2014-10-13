function renderToHtml(content) {

    try {
        var rendered = Opal.Asciidoctor.$render(content);
        app.getLastRendered().setValue(rendered);
    }
    catch (e) {
        setTimeout(renderToHtml, 100);
    }
}

renderToHtml('%s');

function renderToHtml(content) {

    var rendered = Opal.Asciidoctor.$render(content);

    return rendered;
}

function scrollTo60(position) {
    $(window).scrollTop(position - 60);
}

function runScroller(content) {

    var renderedSelection = Opal.Asciidoctor.$render(content);

    renderedSelection = renderedSelection.replace(new RegExp("\n|\r|\n\r", "ig"), "");

    if ($(renderedSelection).is(".imageblock")) {
        var src = $(renderedSelection).find("img").attr("src");
        scrollTo60($("img[src='" + src + "']").offset().top);
        return;
    }

    if ($(renderedSelection).is(".admonitionblock")) {
        renderedSelection = $(renderedSelection).find(".content");
    }

    var search = $("*:contains(" + $(renderedSelection).text() + "):last");
    scrollTo60(search.offset().top);

}
function convertBasicHtml(content) {
    var options = Opal.hash2(['backend', 'safe', 'attributes'], {
      backend: 'html5',
      safe: 'safeMode',
      attributes: 'showtitle icons=font@ source-highlighter=highlight.js platform=opal platform-opal env=browser env-browser'
    });

    return Opal.Asciidoctor.$convert(content, options);
}

function convertHtmlBook(content) {
    var options = Opal.hash2(['backend', 'safe', 'attributes',"header_footer"], {
        backend: 'html5',
        safe: 'safeMode',
        attributes: 'showtitle icons=font@ source-highlighter=highlight.js platform=opal platform-opal env=browser env-browser'
    , 'header_footer': true
    });
    var rendered = Opal.Asciidoctor.$render(content, options);

    return rendered;
}

function convertHtmlArticle(content) {
    var hash2 = Opal.hash2(['attributes', 'header_footer'],
        {
            'attributes': ['backend=html5', 'doctype=article'],
            'header_footer': true
        });
    var rendered = Opal.Asciidoctor.$render(content, hash2);

    return rendered;
}

function convertDocbook(content,includeHeader) {

    var hash2 = Opal.hash2(['attributes', 'header_footer'],
        {
            'attributes': ['backend=docbook5', 'doctype=book'],
            'header_footer': includeHeader
        });
    var rendered = Opal.Asciidoctor.$render(content, hash2);

    return rendered;
}

function convertDocbookArticle(content) {

    var hash2 = Opal.hash2(['attributes', 'header_footer'],
        {
            'attributes': ['backend=docbook5', 'doctype=article'],
            'header_footer': true
        });
    var rendered = Opal.Asciidoctor.$render(content, hash2);

    return rendered;
}

function scrollTo60(position) {
    $(window).scrollTop(position - 60);
}

function runScroller(content) {

    var renderedSelection = Opal.Asciidoctor.$render(content);

    if(renderedSelection.trim() == "")
        return;
    
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

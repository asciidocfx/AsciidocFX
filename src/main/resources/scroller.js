function runScroller() {
    
    var renderedSelection = Opal.Asciidoctor.$render('%s');

    renderedSelection = renderedSelection.replace(new RegExp("\n|\r|\n\r", "ig"), "");

    var search = $("*:contains(" + $(renderedSelection).text() + "):last");

    if (search.length == 1) {
        var top = search.offset().top;
        $(window).scrollTop(search.offset().top);
    }

}

try{
    runScroller();
}
catch (e){}




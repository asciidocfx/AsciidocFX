function scrollToo(position){
    $(window).scrollTop(position - 60 );
}

function runScroller() {

    var renderedSelection = Opal.Asciidoctor.$render('%s');

    renderedSelection = renderedSelection.replace(new RegExp("\n|\r|\n\r", "ig"), "");

    if($(renderedSelection).is(".imageblock")){
        var src = $(renderedSelection).find("img").attr("src");
        scrollToo($("img[src='"+ src+"']").offset().top);
        return;
    }

    if($(renderedSelection).is(".admonitionblock")){
        renderedSelection = $(renderedSelection).find(".content");
    }

    var search = $("*:contains(" + $(renderedSelection).text() + "):last");
    scrollToo(search.offset().top);

}

runScroller();



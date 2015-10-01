window.revealjsExt = {};
revealjsExt.replaceSlides = function (data) {
    var div = document.createElement("div");
    div.innerHTML = data;
    document.querySelector("div.slides").innerHTML = div.querySelector("div.slides").innerHTML;
    setTimeout(initializeReveal, 50);
}

revealjsExt.flipCurrentPage = function (lineno) {

    var closest = $(".data-line-" + lineno).closest("section.stack");

    if (closest.length > 0) {
        Reveal.slide(revealjsExt.getSlideNumber(closest));
    } else {
        closest = revealjsExt.findNextNode(lineno).closest("section.stack");
        if (closest.length > 0) {
            Reveal.slide(revealjsExt.getSlideNumber(closest));
        }
    }
};

revealjsExt.getSlideNumber = function (closest) {
    var getSlide = $("div.slides").find("section.stack").index(closest.get(0));
    return getSlide;
}

revealjsExt.findNextNode = function (lineno) {
    var node;
    for (var i = 0; i < 100; i++) {
        lineno++;
        node = $("div.slides").find("section.stack").find(".data-line-" + lineno);
        if (node.length > 0)
            break;
    }
    return node || $("<div></div>");
}

$("body").on("click", function (event) {

    var elem = $(event.target);

    if (elem.is("[class*=data-line]")) {
        var line = elem.attr('class').match(/data-line-(\d+)/)[1];
        afx.moveCursorTo(line);
    } else {
        var closest = elem.closest("[class*=data-line]");
        if (closest.length > 0) {
            var line = closest.attr('class').match(/data-line-(\d+)/)[1];
            afx.moveCursorTo(line);
        }
    }
});

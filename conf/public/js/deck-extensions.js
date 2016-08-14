window.deckjsExt = {};
deckjsExt.replaceSlides = function (data) {
    var div = document.createElement("div");
    div.innerHTML = data;
    document.querySelector(".deck-container").innerHTML = div.querySelector(".deck-container").innerHTML;
    setTimeout(function () {
        $.deck('.slide');
    }, 50);
};

deckjsExt.flipCurrentPage = function (lineno) {

    var closest = $("div.deck-container").find(".data-line-" + lineno).closest("section.slide");

    if (closest.length > 0) {
        setTimeout(function () {
            $.deck('go', deckjsExt.getSlideNumber(closest));
        }, 50);
    } else {
        closest = deckjsExt.findNextNode(lineno).closest("section.slide");
        if (closest.length > 0) {
            setTimeout(function () {
                $.deck('go', deckjsExt.getSlideNumber(closest));
            }, 50);
        }
    }
};

deckjsExt.getSlideNumber = function (closest) {
    var getSlide = $("div.deck-container").find("section.slide").index(closest.get(0));
    return getSlide;
}

deckjsExt.findNextNode = function (lineno) {
    var node;
    for (var i = 0; i < 10; i++) {
        lineno++;
        node = $("div.deck-container").find("section.slide").find(".data-line-" + lineno);
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
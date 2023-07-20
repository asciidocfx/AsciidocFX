document.addEventListener("DOMNodeInserted", function (event) {
    var element = $(event.target);

    if (element.is("#content")) {
        element.on("click", function (event) {
            var elem = $(event.target);
            if (elem.is("[class*=data-line]")) {
                var line = elem.attr('class').match(/data-line-(\d+)/)[1];
                afx.moveCursorTo(line);
            } else {
                var line = elem.closest("[class*=data-line]").attr('class').match(/data-line-(\d+)/)[1];
                afx.moveCursorTo(line);
            }
        });
    }
});
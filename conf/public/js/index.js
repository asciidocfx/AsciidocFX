var soket = new SockJS("/ws");
var sourceHighlightAction = new BufferedAction();

function refreshUI(data) {

    // $(".asciidocfx-container").remove();
    morphdom(document.documentElement, data, morphdomOptions);

    sourceHighlightAction.buff(function () {
        document.querySelectorAll('pre.highlight > code[data-lang]')
            .forEach((block) => {
                hljs.highlightBlock(block);
            });
        if (hljs.initLineNumbersOnLoad) {
            hljs.initLineNumbersOnLoad();
        }
        // prettyPrint();
    }, 1000);

}

soket.onmessage = function (e) {
    refreshUI(e.data);
};

soket.onerror = soket.onclose = function (e) {
    $(".row.connection-closed").show();
};
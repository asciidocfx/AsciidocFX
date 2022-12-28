var $placeholder = $("#placeholder");

var clearCacheAction = new BufferedAction();

function clearImageCache(imageName) {
    clearCacheAction.buff(function () {
        $placeholder.find("img").each(function () {
            var image = $(this);
            var srcAttr = image.attr("src");
            if (srcAttr) {
                image.attr("src", srcAttr);
            }
        });
    }, 500);
}

function imageToBase64Url() {

    window.clonedContent = $placeholder.clone();
    clonedContent.find("img").each(function (index) {
        afx.imageToBase64Url(this.src, index);
    });
}

function updateBase64Url(index, base64) {

    var imageUrl = "data:image/png;base64," + base64;
    clonedContent.find("img").eq(index).attr("src", imageUrl);
    afx.cutCopy(clonedContent.html());
}

var sourceHighlightAction = new BufferedAction();

function refreshUI(data) {

    // $(".asciidocfx-container").remove();
    morphdom(document.documentElement, data, morphdomOptions);

    sourceHighlightAction.buff(function () {
        document.querySelectorAll('pre.highlight > code[data-lang]')
            .forEach((block) => {
                hljs.highlightBlock(block);
            });
        // prettyPrint();
    }, 1000);

}

(function () {
    alert("PREVIEW_LOADED");
})();
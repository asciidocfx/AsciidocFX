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

    if (data.lastIndexOf("<!DOCTYPE html>", 0) === 0) {
        data = "<p>You sent full DOCTYPE!</p>";
    }

    $placeholder.html(data);

    sourceHighlightAction.buff(function () {
        document.querySelectorAll('pre code').forEach((block) => {
            hljs.highlightBlock(block);
        });

        prettyPrint();
    }, 1000);

}

(function () {
    alert("PREVIEW_LOADED");
})();
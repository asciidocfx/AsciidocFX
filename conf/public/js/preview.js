var $placeholder = $("#placeholder");

function clearImageCache(imageName) {
    $placeholder.find("img").each(function () {
        var image = $(this);
        var srcAttr = image.attr("src");
        if (srcAttr) {
            if (srcAttr.indexOf(imageName) != -1) {
                var imageCacheNumber = Math.floor(Math.random() * (999999999999 - 2)) + 1;
                image.attr("src", srcAttr.split("?")[0] + "?cache=" + imageCacheNumber);
            }
        }
    });
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

var hljsAction = new BufferedAction();
function refreshUI(data) {

    $placeholder.html(data);

    hljsAction.buff(function () {
        $('pre').children("code").each(function () {
            if (!$(this).hasClass("hljs")) {
                hljs.highlightBlock(this);
            }
        });
    }, 1000);

}

alert("PREVIEW_LOADED");
var clearCacheAction = new BufferedAction();

function clearImageCache(imageName) {
    clearCacheAction.buff(function () {
        $("body").find("img").each(function () {
            var image = $(this);
            var srcAttr = image.attr("src");
            if (srcAttr) {
                image.attr("src", srcAttr);
            }
        });
    }, 500);
}

function imageToBase64Url() {

    window.clonedContent = $("body").clone();
    clonedContent.find("img").each(function (index) {
        afx.imageToBase64Url(this.src, index);
    });
}

function updateBase64Url(index, base64) {

    var imageUrl = "data:image/png;base64," + base64;
    clonedContent.find("img").eq(index).attr("src", imageUrl);
    afx.cutCopy(clonedContent.html());
}

function refreshUI(data) {

    morphdom(document.documentElement, data, morphdomOptions);

}

(function () {
    alert("PREVIEW_LOADED");
})();
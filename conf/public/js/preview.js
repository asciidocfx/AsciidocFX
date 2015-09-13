var imageCacheNumber = Math.floor(Math.random() * (999999999999 - 2)) + 1;

function clearImageCache() {
    imageCacheNumber = Math.floor(Math.random() * (999999999999 - 2)) + 1;

    var content = $("#placeholder");
    content.find("img").each(function () {
        var srcAttr = $(this).attr("src");
        if (srcAttr)
            $(this).attr("src", srcAttr.split("?")[0]);
    });
    refreshUI(content.html());
}

function imageToBase64Url() {

    window.clonedContent = $("#placeholder").clone();
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

    var $data = $("<div></div>").append(data);
    $data.find("img").each(function () {
        var $image = $(this);
        var attr = $image.attr("src");
        if (attr)
            $image.attr("src", attr + "?cache=" + imageCacheNumber + "&parent=" + (attr.match(/\.\./g) || []).length);
    });

    $("#placeholder").html($data.html());

    hljsAction.buff(function () {
        $('pre').children("code").each(function () {
            if (!$(this).hasClass("hljs")) {
                hljs.highlightBlock(this);
            }
        });
    }, 1000);

}

alert("PREVIEW_LOADED");
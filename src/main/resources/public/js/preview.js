var imageCacheNumber = Math.floor(Math.random() * (999999999999 - 2)) + 1;

function clearImageCache() {
    imageCacheNumber = Math.floor(Math.random() * (999999999999 - 2)) + 1;
    function getPathFromUrl(url) {
        return url.split("?")[0];
    }

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

function isHtml(html) {
    var div = $("<div></div>");
    div.append(html);
    return div.find("div,span,p,br,b,strong,h1,h2,h2,h4,h5,h6,pre,code,table,section,img,a,sub,sup,del,u").length > 5;
}

function updateBase64Url(index, base64) {

    var imageUrl = "data:image/png;base64," + base64;
    clonedContent.find("img").eq(index).attr("src", imageUrl);
    afx.cutCopy(clonedContent.html());
}

function refreshUI(data) {

    var $data = $("<div></div>").append(data);
    $data.find("img").each(function () {
        var attr = $(this).attr("src");
        if (attr)
            $(this).attr("src", attr + "?cache=" + imageCacheNumber + "&parent=" + (attr.match(/\.\./g) || []).length);
    });

    $("#placeholder").html($data.html());
    $('pre code').each(function (i, e) {
        hljs.highlightBlock(e)
    });

}

document.onkeydown = function (e) {
    e = e || window.event;
    if (e.keyCode == 123) {
        if (!$("#firebug-script").length) {
            var script = $("<script>")
            script.attr("id", "firebug-script");
            script.attr("src", "http://getfirebug.com/firebug-lite.js#startOpened=true");
            $("body").append(script);
        }
    }

};

var progressBar;

function startProgressBar() {
    var svg = document.querySelector('#container > svg');
    if (svg)
        document.querySelector('#container').removeChild(svg);

    progressBar = new ProgressBar.Line('#container', {
        color: '#FCB03C',
        duration: 20000
    });
    progressBar.animate(1);
}

function stopProgressBar() {
    if (progressBar)
        progressBar.animate(1, {
            duration: 800
        }, function () {
            setTimeout(function () {
                var svg = document.querySelector('#container > svg');
                if (svg)
                    progressBar._container.removeChild(svg);
            }, 500);
        });
}

alert("PREVIEW_LOADED");
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
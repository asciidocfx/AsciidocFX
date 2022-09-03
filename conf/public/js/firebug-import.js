// https://github.com/firebug/firebug-lite/releases/tag/firebug1.2
function showFirebug() {
    let firebugScript = document.querySelector("#firebug-script");
    if (!firebugScript) {
        let script = document.createElement('script');
        script.setAttribute("id", "firebug-script");
        script.setAttribute('src', '/afx/resource/js/firebug/?p=js/firebug/firebug-lite-compressed.js');
        document.body.appendChild(script);
        setTimeout(function () {
            if (window.firebug) {
                window.firebug.env.css = "/afx/resource/js/firebug/?p=js/firebug/firebug-lite.css";
                window.firebug.init();
            }
        }, 1000);
    }
}
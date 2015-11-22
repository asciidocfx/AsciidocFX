function sendConsole(message) {
    self.postMessage({
        type: "log",
        message: message
    });
}
var console = {
    log: sendConsole,
    debug: sendConsole,
    warn: sendConsole,
    error: sendConsole,
    info: sendConsole
};

var afx = {};

importScripts("/afx/resource/js/buffhelper.js");
importScripts("/afx/resource/js/asciidoctor-all.js");
importScripts("/afx/resource/js/webworker-converters.js");

var buff = new BufferedAction();
self.onmessage = function (e) {
    try {

        var data = e.data;

        var func = data.func;
        var content = data.content;
        var options = data.options;
        var taskId = data.taskId;

        if (func && func in self) {

            buff.buff(function () {
                self[func](taskId, content, options);
            }, 100);
        }

    }
    catch (e) {
        console.log(JSON.stringify(e));
    }
};

self.onerror = function (e) {
    console.log(JSON.stringify(e));
};
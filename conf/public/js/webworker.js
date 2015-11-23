function sendConsole(message, taskId) {
    self.postMessage(JSON.stringify({
        type: "log",
        message: message,
        taskId: taskId
    }));
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
importScripts("/afx/resource/js/jade.js");
importScripts("/afx/resource/js/asciidoctor-all.js");
importScripts("/afx/resource/js/asciidoctor-docbook.js");
importScripts("/afx/resource/js/asciidoctor-data-line.js");
importScripts("/afx/resource/js/asciidoctor-chart-block.js");
importScripts("/afx/resource/js/asciidoctor-tree-block.js");
importScripts("/afx/resource/js/asciidoctor-uml-block.js");
importScripts("/afx/resource/js/asciidoctor-reveal.js");
importScripts("/afx/resource/js/asciidoctor-deck.js");
importScripts("/afx/resource/js/outliner.js");
importScripts("/afx/resource/js/webworker-converters.js");

var buff = new BufferedAction();
var lastTaskId = "";
self.onmessage = function (e) {
    try {

        var data = JSON.parse(e.data) || {};

        var func = data.func;
        var content = data.content;
        var options = data.options;
        var taskId = data.taskId;
        lastTaskId = taskId || "";

        if (func && func in self) {

            buff.buff(function () {
                self[func](taskId, content, options);
            }, 10);
        }

    }
    catch (e) {
        console.log(e, lastTaskId);
    }
};

self.onerror = function (e) {
    console.log(e, lastTaskId);
};
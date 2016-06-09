function sendConsole(message, level) {
    self.postMessage(JSON.stringify({
        type: "log",
        level: level,
        message: message,
        taskId: lastTaskId
    }));
}

var console = {};

console.log = function (msg) {
    sendConsole(msg, "log");
};

console.debug = function (msg) {
    sendConsole(msg, "debug");
};

console.warn = function (msg) {
    sendConsole(msg, "warn");
};

console.error = function (msg) {
    sendConsole(msg, "error");
};

console.info = function (msg) {
    sendConsole(msg, "info");
};

var afx = {};

importScripts("/afx/resource/js/buffhelper.js");
importScripts("/afx/resource/js/ajax.js");
importScripts("/afx/resource/js/jade.js");
importScripts("/afx/resource/js/asciidoctor-all.js");
importScripts("/afx/resource/js/asciidoctor-docbook.js");
importScripts("/afx/resource/js/asciidoctor-data-line.js");
importScripts("/afx/resource/js/asciidoctor-data-uri.js");
importScripts("/afx/resource/js/asciidoctor-chart-block.js");
importScripts("/afx/resource/js/asciidoctor-tree-block.js");
importScripts("/afx/resource/js/asciidoctor-block-extensions.js");
importScripts("/afx/resource/js/asciidoctor-block-macro-extensions.js");
importScripts("/afx/resource/js/asciidoctor-reveal.js");
importScripts("/afx/resource/js/asciidoctor-deck.js");
importScripts("/afx/resource/js/outliner.js");
importScripts("/afx/resource/js/webworker-converters.js");

self.onerror = function (e) {
    console.error(e);
};

var lastTaskId = "";
self.onmessage = function (e) {
    try {

        if (!e.data) {
            return;
        }

        var data = JSON.parse(e.data) || {};

        var func = data.func;
        var content = data.content;
        var options = data.options;
        var taskId = data.taskId;
        lastTaskId = taskId || "";

        if (func && func in self) {
            self[func](taskId, content, options);
        }

    }
    catch (e) {
        console.error(e);
    }
};

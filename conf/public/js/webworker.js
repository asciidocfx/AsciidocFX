function sendConsole(message, level) {

    if (message.constructor == Error) {
        var stack = message.stack.replace(/^[^\(]+?[\n$]/gm, '')
            .replace(/^\s+at\s+/gm, '')
            .replace(/^Object.<anonymous>\s*\(/gm, '{anonymous}()@')
            .split('\n');
        message = (message + '\n' + stack);
    }

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

importScripts("/afx/resource/?p=js/buffhelper.js");
importScripts("/afx/resource/?p=js/ajax.js");
importScripts("/afx/resource/?p=js/jade.js");
importScripts("/afx/resource/?p=js/md5.js");
importScripts("/afx/resource/?p=js/prototypes.js");
importScripts("/afx/resource/?p=js/asciidoctor-all.js");
importScripts("/afx/resource/?p=js/asciidoctor-docbook.js");
importScripts("/afx/resource/?p=js/asciidoctor-data-line.js");
importScripts("/afx/resource/?p=js/asciidoctor-data-uri.js");
importScripts("/afx/resource/?p=js/asciidoctor-chart-block.js");
importScripts("/afx/resource/?p=js/asciidoctor-extension-helpers.js");
importScripts("/afx/resource/?p=js/asciidoctor-block-extensions.js");
importScripts("/afx/resource/?p=js/asciidoctor-block-macro-extensions.js");
importScripts("/afx/resource/?p=js/asciidoctor-inline-macro-extensions.js");
importScripts("/afx/resource/?p=js/asciidoctor-reveal.js");
importScripts("/afx/resource/?p=js/asciidoctor-deck.js");
importScripts("/afx/resource/?p=js/outliner.js");
importScripts("/afx/resource/?p=js/webworker-converters.js");

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

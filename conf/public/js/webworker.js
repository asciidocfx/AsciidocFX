var afx = {};

// importScripts("/afx/resource/js/?p=js/request-logger.js");
importScripts("/afx/resource/js/?p=js/webworker-console.js");
importScripts("/afx/resource/js/?p=js/buffhelper.js");
importScripts("/afx/resource/js/?p=js/ajax.js");
importScripts("/afx/resource/js/?p=js/jade.js");
importScripts("/afx/resource/js/?p=js/md5.js");
importScripts("/afx/resource/js/?p=js/prototypes.js");
importScripts("/afx/resource/js/?p=js/asciidoctor-browser.js");
var asciidoctor = Asciidoctor({runtime: {platform: 'browser'}});
importScripts("/afx/resource/js/?p=js/asciidoctor-docbook.js");
Asciidoctor.DocBook();
importScripts("/afx/resource/js/?p=js/asciidoctor-data-line.js");
importScripts("/afx/resource/js/?p=js/asciidoctor-data-uri.js");
importScripts("/afx/resource/js/?p=js/asciidoctor-chart-block.js");
importScripts("/afx/resource/js/?p=js/asciidoctor-extension-helpers.js");
importScripts("/afx/resource/js/?p=js/asciidoctor-block-extensions.js");

// // TODO: compare behaviour for asciimath
importScripts("/afx/resource/js/?p=js/asciidoctor-block-macro-extensions.js");
importScripts("/afx/resource/js/?p=js/asciidoctor-inline-macro-extensions.js");

importScripts("/afx/resource/js/?p=js/asciidoctor-reveal.js");
importScripts("/afx/resource/js/?p=js/asciidoctor-deck.js");
importScripts("/afx/resource/js/?p=js/outliner.js");
importScripts("/afx/resource/js/?p=js/webworker-converters.js");

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
            var self2 = self[func];
            if(self2){
                self2(taskId, content, options);
            }

        }

    }
    catch (e) {
        console.error(e);
    }
};

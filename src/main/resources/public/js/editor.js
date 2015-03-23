var editor = ace.edit("editor");
editor.renderer.setShowGutter(false);
editor.setHighlightActiveLine(false);
editor.getSession().setMode("ace/mode/asciidoc");
editor.renderer.setScrollMargin(10, 10, 10, 10);
editor.getSession().setUseWrapMode(true);
editor.setShowPrintMargin(false);
editor.setBehavioursEnabled(true);
editor.session.setFoldStyle("manual");
editor.setOptions({
    enableSnippets: true,
    dragEnabled: false
});
editor.setScrollSpeed("0.1");
editor.setTheme("ace/theme/ace");

var lastEditorRow = 0;
var initialized = false;
var timeouter;
var updateDelay = 100;

//var maxTop = editor.renderer.layerConfig.maxHeight - editor.renderer.$size.scrollerHeight + editor.renderer.scrollMargin.bottom;
//app.onscroll(editor.getSession().getScrollTop(), maxTop);

var updateScrollPosition = function (scroll) {

    var row = editor.renderer.getFirstFullyVisibleRow();

    if (lastEditorRow == row)
        return;

    for (var i = 0; i < 10; i++) { // try ten times
        var trimmed = editor.session.getLine(row).trim();
        if ((trimmed == "") || (trimmed.match(/\(\(\(.*?\)\)\)/))) {
            row++;
        }
        else {
            break;
        }
    }

    var range = sketch.searchBlockPosition(row);
    if (range) {

        if (lastEditorRow == range.start.row)
            return;
        lastEditorRow = range.start.row;

        var blockText = editor.session.getTextRange(range);
        app.scrollToCurrentLine(blockText);
    }
    else {
        lastEditorRow = row;

        var lineText = editor.session.getLine(row);

        if (lineText.trim() == "")
            return;

        app.scrollToCurrentLine(lineText);
    }
};

editor.getSession().on('changeScrollTop', function (scroll) {

    var firstly = editor.getFirstVisibleRow();

    var interval = setInterval(function () {
        if (firstly == editor.getFirstVisibleRow())
            return;
        clearInterval(interval);
        updateScrollPosition(scroll)
    }, 50);
});

editor.getSession().selection.on('changeCursor', function (e) {

    var row = editor.getCursorPosition().row;

    if (lastEditorRow == row)
        return;

    var range = sketch.searchBlockPosition(row);
    if (range) {

        if (lastEditorRow == range.start.row)
            return;
        lastEditorRow = range.start.row;

        var blockText = editor.session.getTextRange(range);
        app.scrollToCurrentLine(blockText);
    }
    else {
        lastEditorRow = row;

        var lineText = editor.session.getLine(row);

        if (lineText.trim() == "")
            return;

        app.scrollToCurrentLine(lineText);
    }
});

editor.getSession().on('change', function (obj) {
    if (initialized)
        app.appendWildcard();

    if (timeouter)
        clearTimeout(timeouter);

    sketch.refreshConstructList();

    timeouter = setTimeout(function () {
        app.textListener(editor.getValue());

        var length = editor.session.getLength();

        if (length > 1000)
            updateDelay = 1000;
        else if (length > 2000)
            updateDelay = 2000;
        else
            updateDelay = length;

    }, updateDelay);

});


function updateOptions() {
    editor.setOptions({
        fontFamily: app.getConfig().getFontFamily(),
        fontSize: app.getConfig().getFontSize()
    });
    editor.setTheme(app.getConfig().getTheme());
    editor.setScrollSpeed(app.getConfig().getScrollSpeed());
    editor.focus();
}

function setEditorValue(content) {

    editor.setValue(content);
    editor.clearSelection();
    editor.session.setScrollTop(-100);
    editor.focus();

}

function switchMode(index) {
    if (index == 0)
        editor.getSession().setMode("ace/mode/asciidoc");
    if (index == 1)
        editor.getSession().setMode("ace/mode/markdown");
}

function rerender() {
    app.textListener(editor.getValue());
}

function setInitialized() {
    initialized = true;
}
var editor = ace.edit("editor");
var modelist = ace.require("ace/ext/modelist");
editor.renderer.setShowGutter(false);
editor.setHighlightActiveLine(false);
editor.getSession().setMode("ace/mode/asciidoc");
editor.getSession().setUseWrapMode(true);
editor.setShowPrintMargin(false);
editor.setBehavioursEnabled(true);
editor.session.setFoldStyle("manual");
editor.setOptions({
    enableSnippets: true,
    dragEnabled: true
});
editor.setScrollSpeed("0.1");
editor.setTheme("ace/theme/xcode");

var lastEditorRow = 0;
var afterFirstChange = false;
var timeouter;
var updateDelay = 100;

//var maxTop = editor.renderer.layerConfig.maxHeight - editor.renderer.$size.scrollerHeight + editor.renderer.scrollMargin.bottom;
//afx.onscroll(editor.getSession().getScrollTop(), maxTop);

var updateScrollPosition = function (scroll) {

    var row = editor.renderer.getFirstFullyVisibleRow();

    if (lastEditorRow == row)
        return;

    for (var i = 0; i < 10; i++) { // try ten times
        var trimmed = editor.session.getLine(row).trim();
        if ((trimmed == "") || (trimmed.match(/\(\(\(.*?\)\)\)/)) || (trimmed.match(/'''/)) || (trimmed.match(/\[\[.*?\]\]/))) {
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
        afx.scrollToCurrentLine(blockText);
    }
    else {
        lastEditorRow = row;

        var lineText = editor.session.getLine(row);

        if (lineText.trim() == "")
            return;

        afx.scrollToCurrentLine(lineText);
    }
};

editor.getSession().on('changeScrollTop', function (scroll) {

    var maxTop = editor.renderer.layerConfig.maxHeight - editor.renderer.$size.scrollerHeight + editor.renderer.scrollMargin.bottom;
    var scrollTop = editor.getSession().getScrollTop();


    if (Math.abs(maxTop - scrollTop) < 10 || scrollTop < 10 || afx.isLiveReloadPane()) {
        afx.onscroll(scrollTop, maxTop);
        return;
    }

    var firstly = editor.getFirstVisibleRow();

    var interval = setInterval(function () {
        if (firstly == editor.getFirstVisibleRow())
            return;
        clearInterval(interval);
        updateScrollPosition(scroll)
    }, 50);
});

function updateStatusBox() {
    var cursorPosition = editor.getCursorPosition();
    var row = cursorPosition.row;
    var column = cursorPosition.column;

    var lineCount = editor.session.getLength();
    var wordCount = editor.session.getValue().length;

    afx.updateStatusBox(row, column, lineCount, wordCount);
}

editor.getSession().selection.on('changeCursor', function (e) {

    var cursorPosition = editor.getCursorPosition();
    var row = cursorPosition.row;

    updateStatusBox();

    if (lastEditorRow == row)
        return;

    var range = sketch.searchBlockPosition(row);
    if (range) {

        if (lastEditorRow == range.start.row)
            return;
        lastEditorRow = range.start.row;

        var blockText = editor.session.getTextRange(range);
        afx.scrollToCurrentLine(blockText);
    }
    else {
        lastEditorRow = row;

        var lineText = editor.session.getLine(row);

        if (lineText.trim() == "")
            return;

        afx.scrollToCurrentLine(lineText);
    }
});

var editorChangeListener = function (obj) {

    if (afterFirstChange)
        afx.appendWildcard();

    if (timeouter)
        clearTimeout(timeouter);

    sketch.refreshConstructList();

    timeouter = setTimeout(function () {
        afx.textListener(editor.getValue(), editorMode());

        var length = editor.session.getLength();

        if (length > 1000)
            updateDelay = 1000;
        else if (length > 2000)
            updateDelay = 2000;
        else
            updateDelay = length;

    }, updateDelay);

};

function updateOptions() {
    var editorConfigBean = afx.getEditorConfigBean();
    editor.setOptions({
        fontFamily: editorConfigBean.getFontFamily(),
        fontSize: editorConfigBean.getFontSize()
    });
    var themes = editorConfigBean.getEditorTheme();
    if (themes.size() > 0) {
        changeTheme(themes.get(0));
    }

    changeScrollSpeed(editorConfigBean.getScrollSpeed());
    setShowGutter(editorConfigBean.getShowGutter());
    setUseWrapMode(editorConfigBean.getShowGutter());
    setWrapLimitRange(editorConfigBean.getWrapLimit());

    editor.focus();
}

function changeTheme(theme) {
    editor.setTheme("ace/theme/" + theme);
}

function changeFontSize(fontSize) {
    editor.setFontSize(fontSize);
}

function changeScrollSpeed(value){
    editor.setScrollSpeed(value);
}

function setShowGutter(showGutter) {
    editor.renderer.setShowGutter(showGutter);
}

function setUseWrapMode(useWrapMode) {
    editor.getSession().setUseWrapMode(useWrapMode);
}

function setWrapLimitRange(wrapLimitRange) {
    editor.getSession().setWrapLimitRange(wrapLimitRange, wrapLimitRange);
}

function setEditorValue(content) {

    editor.setValue(content);
    editor.clearSelection();
    editor.session.setScrollTop(-100);
    editor.focus();
    afterFirstChange = true;

}

var emmetRegex = /^ace\/mode\/(css|less|scss|sass|stylus|html|php|twig|ejs|handlebars)$/;
function switchMode(mode) {
    if (mode) {
        editor.getSession().setMode(mode);
        if (emmetRegex.test(mode)) {
            initializeEmmet(mode);
        }
    }
}

function changeEditorMode(filePath) {
    var mode = modelist.getModeForPath(filePath).mode;
    editor.getSession().setMode(mode);

    if (emmetRegex.test(mode))
        initializeEmmet(mode);

    return mode;
}

function addAnnotation(row, column, text, type) {
    editor.renderer.setShowGutter(true);
    editor.session.setOption("useWorker", false);
    editor.getSession().setAnnotations([{row: row, column: column, text: text, type: type}]);
    editor.scrollToLine(row, false, false, function () {
    });
}

function initializeEmmet(mode) {

    ace.require("ace/ext/emmet");

    ["js/emmet.js", "ace/src/ext-emmet.js"].forEach(function (path, index) {
        var script = document.createElement("script");
        script.src = path;
        document.querySelector("body").appendChild(script);

        if (index == 1) {
            script.onload = function () {
                editor.setOption("enableEmmet", true);
            }
        }
    });

}

function rerender() {
    afx.textListener(editor.getValue(), editorMode());
    updateStatusBox();
}

function editorMode() {
    var mode = editor.getSession().getMode().$id;
    return mode.replace("ace/mode/", "");
}

function setInitialized() {
    editor.getSession().on('change', editorChangeListener);
    editor.renderer.setScrollMargin(10, 10, 10, 10);
    updateStatusBox();
}
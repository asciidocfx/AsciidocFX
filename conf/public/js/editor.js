var editor = ace.edit("editor");
editor.renderer.setScrollMargin(10, 0, 10, 0);
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
var renderTimeout;
var refreshTimeout;
var updateDelay = 100;

//var maxTop = editor.renderer.layerConfig.maxHeight - editor.renderer.$size.scrollerHeight + editor.renderer.scrollMargin.bottom;
//afx.onscroll(editor.getSession().getScrollTop(), maxTop);


function updateHtmlScroll() {
    var row = editor.renderer.getFirstFullyVisibleRow();
    afx.scrollByLine(row + "");
}

function updateMarkupScroll(row) {

    if (lastEditorRow == row)
        return;

    afx.scrollByLine(row + "");

    lastEditorRow = row;
};

editor.getSession().on('changeScrollTop', function (scroll) {

    var maxTop = editor.renderer.layerConfig.maxHeight - editor.renderer.$size.scrollerHeight + editor.renderer.scrollMargin.bottom;
    var scrollTop = editor.getSession().getScrollTop();

    if (Math.abs(maxTop - scrollTop) < 10 || scrollTop < 10) {
        afx.onscroll(scrollTop, maxTop);
        return;
    }

    var mode = editorMode();

    var firstly = editor.getFirstVisibleRow();

    var interval = setInterval(function () {
        if (firstly == editor.getFirstVisibleRow())
            return;
        clearInterval(interval);

        if (mode == "asciidoc" || mode == "markdown") {
            var row = editor.renderer.getFirstFullyVisibleRow();
            updateMarkupScroll(row);
        }
        else if (mode == "html") {
            updateHtmlScroll();
        }
    }, 50);
});

var updateStatusAction = new BufferedAction();
function updateStatusBox() {

    updateStatusAction.buff(function () {
        var cursorPosition = editor.getCursorPosition();
        var row = cursorPosition.row;
        var column = cursorPosition.column;

        var lineCount = editor.session.getLength();
        var wordCount = editor.session.getValue().length;

        afx.updateStatusBox(row, column, lineCount, wordCount);
    }, 1000);

}

editor.getSession().selection.on('changeCursor', function (e) {

    var cursorPosition = editor.getCursorPosition();
    var row = cursorPosition.row;

    updateStatusBox();

    updateMarkupScroll(row);

});

var constructListAction = new BufferedAction();
var renderAction = new BufferedAction();
var editorChangeListener = function (obj) {

    if (afterFirstChange)
        afx.appendWildcard();

    renderAction.buff(function () {
        afx.textListener(editor.getValue(), editorMode());
    }, 100);

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
    setWrapLimitRange(editorConfigBean.getWrapLimit());
    setUseWrapMode(editorConfigBean.getUseWrapMode());

    editor.focus();
}

function changeTheme(theme) {
    editor.setTheme("ace/theme/" + theme);
}

function changeFontSize(fontSize) {
    editor.setFontSize(fontSize);
}

function changeScrollSpeed(value) {
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

function getSelectionOrAll() {
    var min = arguments.length > 0 ? arguments[0] : 0;
    var selection = editor.session.getTextRange(editor.getSelectionRange());

    if (selection.length > min) {
        return selection;
    }
    else {
        return editor.session.getValue();
    }
}

function setEditorValue(content) {
    var pos;
    if (afterFirstChange)
        pos = editor.session.selection.toJSON();
    editor.setValue(content, 1);
    editor.session.setScrollTop(-100);
    if (afterFirstChange)
        editor.session.selection.fromJSON(pos);
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

var rerenderAction = new BufferedAction();
function rerender() {
    rerenderAction.buff(function () {
        afx.textListener(editor.getValue(), editorMode());
        updateStatusBox();
    }, 100);
}

function editorMode() {
    var mode = editor.session.$modeId;
    return mode.replace("ace/mode/", "");
}

function setInitialized() {
    editor.getSession().on('change', editorChangeListener);
    updateStatusBox();
}

function resetUndoManager() {
    editor.getSession().setUndoManager(new ace.UndoManager());
}
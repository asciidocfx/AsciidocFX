var editor = ace.edit("editor");
editor.renderer.setScrollMargin(10, 0, 10, 0);
var modelist = ace.require("ace/ext/modelist");
var range = ace.require("ace/range");
var TokenIterator = ace.require("ace/token_iterator").TokenIterator;
editor.renderer.setShowGutter(false);
editor.setHighlightActiveLine(false);
setupSession(editor.getSession());
editor.setShowPrintMargin(false);
editor.setBehavioursEnabled(true);
setFoldStyle("default");
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

function setupSession(session){
    session.setMode("ace/mode/asciidoc");
    session.setUseWrapMode(true);
    session.$selectLongWords = true;
    session.on('changeScrollTop', function (scroll) {
        var maxTop = editor.renderer.layerConfig.maxHeight - editor.renderer.$size.scrollerHeight + editor.renderer.scrollMargin.bottom;
        var scrollTop = session.getScrollTop();

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

        checkSpelling();
    });
    session.selection.on('changeCursor', function (e) {
        var cursorPosition = editor.getCursorPosition();
        var row = cursorPosition.row;

        updateStatusBox();

        updateMarkupScroll(row);

    });
}

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


var renderAction = new BufferedAction();
var editorChangeListener = function (obj) {

    if (afterFirstChange)
        editorPane.appendWildcard();

    renderAction.buff(function () {
        afx.textListener(editor.getValue(), editorMode(), editorPane.getPath());
    }, 100);

    checkSpelling();

};

function clearTypoMarkers() {
    if (markers.length) {
        var marker = markers.pop();
        while (marker) {
            editor.getSession().removeMarker(marker);
            marker = markers.pop();
        }
    }
}

var spellcheckAction = new BufferedAction();
function checkSpelling() {

    clearTypoMarkers();
    spellcheckAction.buff(function () {
        afx.processTokens();
    }, 1000);
}

function getTokenList() {
    var tokenit = new TokenIterator(editor.getSession(), editor.getFirstVisibleRow(), 0);
    var currentToken = tokenit.getCurrentToken();

    var allTokens = [];

    while (currentToken) {
        allTokens.push({
            type: currentToken.type,
            value: currentToken.value,
            row: tokenit.getCurrentTokenRow(),
            start: tokenit.getCurrentTokenColumn(),
            end: tokenit.getCurrentTokenColumn() + currentToken.value.length
        });

        if (editor.getLastVisibleRow() < tokenit.getCurrentTokenRow()) {
            break;
        }

        currentToken = tokenit.stepForward();
    }
    return allTokens;
}

function updateOptions() {
    var editorConfigBean = afx.getEditorConfigBean();
    editor.setOptions({
        fontFamily: editorConfigBean.getAceFontFamily(),
        fontSize: editorConfigBean.getAceFontSize()
    });
    var themes = editorConfigBean.getAceTheme();
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

function changeFontFamily(family) {
    if (family) {
        editor.setOptions({
            fontFamily: family,
        });
    }
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

    ["/afx/resource/js/?p=js/emmet.js", "/afx/resource/ace/src/?p=ace/src/ext-emmet.js"].forEach(function (path, index) {
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
        afx.textListener(editor.getValue(), editorMode(), editorPane.getPath());
        updateStatusBox();
    }, 100);
    checkSpelling();
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

var markers = [];
function addTypo(row, start, end, tokenClass) {
    var marker = editor.getSession().addMarker(
        new range.Range(row, start, row, end), tokenClass, "typo"
    );

    markers.push(marker);
}

function checkWordSuggestions() {
    var cursorPosition = editor.getCursorPosition();
    var selectionIsEmpty = editor.selection.isEmpty();
    if (selectionIsEmpty) {
        editor.selection.selectWord();
    }
    var selectedText = editor.getSelectedText();
    if (selectedText && "" != selectedText.trim()) {
        afx.checkWordSuggestions(selectedText);
    }

    if (selectionIsEmpty) {
        editor.selection.clearSelection();
        editor.moveCursorToPosition(cursorPosition);
    }

}

function replaceMisspelled(suggestion) {
    var cursorPosition = editor.getCursorPosition();
    var selectionIsEmpty = editor.selection.isEmpty();
    if (selectionIsEmpty) {
        editor.selection.selectWord();
    }
    editor.session.replace(editor.selection.getRange(), suggestion);
    editor.selection.clearSelection();
    if (selectionIsEmpty) {
        editor.moveCursorToPosition(cursorPosition);
    }

}

function setFoldStyle(style) {

    if (!style || style == "" || style == "default") {
        var mode = editorMode();
        if (mode == "asciidoc" || mode == "markdown") {
            setFoldStyle("manual");
        }
        else {
            setFoldStyle("markbeginend");
        }
    }
    else {
        editor.session.setFoldStyle(style);
    }
}

function getCursorCoordinates() {
    return editor.renderer.textToScreenCoordinates(editor.getCursorPosition());
}

var onThemeLoaded = function () {
    editorPane.onThemeLoaded();
};

editor.renderer.on("themeLoaded", onThemeLoaded);

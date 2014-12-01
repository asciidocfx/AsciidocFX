// default keys https://searchcode.com/codesearch/view/58959997/
editor.commands.addCommand({
    name: 'cut-1',
    bindKey: {win: 'Ctrl-X', mac: 'Command-X'},
    exec: function (editor) {

        var textRange = editor.session.getTextRange(editor.getSelectionRange());

        if (textRange.length == 0) {
            editor.removeLines();
            return;
        }

        app.cutCopy(textRange);
        editor.remove(editor.getSelectionRange());

    },
    readOnly: true
});

editor.commands.addCommand({
    name: 'close-1',
    bindKey: {win: 'Ctrl-W', mac: 'Command-W'},
    exec: function (editor) {
        app.saveAndCloseCurrentTab();
    },
    readOnly: true
});

editor.commands.addCommand({
    name: 'copy-1',
    bindKey: {win: 'Ctrl-C', mac: 'Command-C'},
    exec: function (editor) {
        app.cutCopy(editor.session.getTextRange(editor.getSelectionRange()));
    },
    readOnly: false
});

editor.commands.addCommand({
    name: 'paste-1',
    bindKey: {win: 'Ctrl-V', mac: 'Command-V'},
    exec: function (editor) {
        editor.insert(app.paste());
    },
    readOnly: true
});

editor.commands.addCommand({
    name: 'ctrl-enter-1',
    bindKey: {win: 'Ctrl-Enter', mac: 'Command-Enter'},
    exec: function (editor) {
        editor.insert("\r\n");
    },
    readOnly: true
});

editor.commands.addCommand({
    name: 'ctrl-duplicate',
    bindKey: {win: 'Ctrl-D', mac: 'Command-D'},
    exec: function (editor) {
        editor.copyLinesDown();
    },
    readOnly: true
});

editor.commands.addCommand({
    name: 'right-brace-1',
    bindKey: {win: 'Ctrl-Alt-0', mac: 'Command-Alt-0'},
    exec: function (editor) {
        editor.insert("}");
    },
    readOnly: true
});

function formatText(editor, matcher, character) {
    var range = editor.getSelectionRange();
    var text = editor.session.getTextRange(range);

    if (matcher(text)) {
        text = text.substring(1, text.length - 1);
        editor.session.replace(range, text);
    }
    else {
        var virtualRange = editor.getSelectionRange();
        virtualRange.setStart(range.start.row, range.start.column - 1);
        virtualRange.setEnd(range.end.row, range.end.column + 1);

        var virtual_text = editor.session.getTextRange(virtualRange);
        if (matcher(virtual_text)) {
            editor.session.replace(virtualRange, text);
        }
        else {
            editor.session.replace(range, character + text + character);
            if (range.end.column == range.start.column) {
                editor.navigateTo(range.end.row, range.end.column + 1);
            }
        }
    }
}

function boldText() {
    formatText(editor, matchBoldText, "*");
}

function italicizeText() {
    formatText(editor, matchItalicizedText, "_");
}

function superScript() {
    formatText(editor, matchSuperScriptText, "^");
}

function subScript() {
    formatText(editor, matchSubScriptText, "~");
}

function addSourceCode() {
    var range = editor.getSelectionRange();
    editor.removeToLineStart();

    editor.insert("[source,java]\n----\n\n----");


    editor.gotoLine(range.end.row + 3, 0, true);
}

function addImageSection() {
    editor.removeToLineStart();
    editor.insert("image::images/image.png[]");
}

function addHeading() {
    var cursorPosition = editor.getCursorPosition();
    cursorPosition.column = 0;
    var session = editor.getSession();
    var line = session.getLine(cursorPosition.row);
    var first = line[0] || "";
    session.insert(cursorPosition, (first == "=") ? "=" : "= ");
}

function addOlList(){
    var cursorPosition = editor.getCursorPosition();
    cursorPosition.column = 0;
    var session = editor.getSession();
    session.insert(cursorPosition,"1. ");
}

function addUlList(){
    var cursorPosition = editor.getCursorPosition();
    cursorPosition.column = 0;
    var session = editor.getSession();
    session.insert(cursorPosition,"* ");
}

editor.commands.addCommand({
    name: 'bold-selected',
    bindKey: {win: 'Ctrl-B', mac: 'Command-B'},
    exec: boldText,
    readOnly: true
});

editor.commands.addCommand({
    name: 'codify-selected',
    bindKey: {win: 'Ctrl-Shift-C', mac: 'Command-Shift-C'},
    exec: function (editor) {

        formatText(editor, matchCode, "`");
    },
    readOnly: true
});

editor.commands.addCommand({
    name: 'italicize-selected',
    bindKey: {
        win: 'Ctrl-i|Ctrl-İ|Ctrl-ı|Ctrl-I',
        mac: 'Command-i|Command-İ|Command-ı|Command-I'
    },
    exec: italicizeText,
    readOnly: true
});

editor.commands.addCommand({
    name: 'source-generate',
    bindKey: {win: 'Tab', mac: 'Tab'},
    exec: function (editor, selection) {

        var cursorPosition = editor.getCursorPosition();
        var currentRow = cursorPosition.row;
        var currentColumn = cursorPosition.column;

        var range = editor.getSelectionRange();
        range.start.row = currentRow;
        range.start.column = 0;
        range.end.row = currentRow;
        range.end.column = currentColumn;

        var textRange = editor.session.getTextRange(range);

        // img tab
        if (textRange == "img") {
            addImageSection();
            return;
        }

        // book tab
        if (textRange == "book") { // source generator
            editor.removeToLineStart();

            editor.insert("= Book Name\nAuthor's Name\n:doctype: book\n:encoding: utf-8\n:lang: tr\n:toc:\n:numbered:\n\n\n");

            return;
        }

        // src tab
        if (textRange == "src") { // source generator
            addSourceCode();
            return;
        }
        // src,ruby or src.ruby tab
        var srcMatch = textRange.match(/src(\.|,)(\w+)/);
        if (Array.isArray(srcMatch)) {
            if (srcMatch.length == 3) {
                editor.removeToLineStart();

                var langName = srcMatch[2];

                editor.insert("[source," + langName + "]\n----\n\n----");

                editor.gotoLine(range.end.row + 3, 0, true);
                return;
            }
        }

//            "tbl3,2" tab
        var tableMatch = textRange.match(/tbl(\d+)(\.|,)(\d+)/);

        if (Array.isArray(tableMatch))
            if (tableMatch.length == 4) { // table generator

                editor.removeToLineStart();

                var row = tableMatch[1];
                var column = tableMatch[3];

                var tablePopupCtrl = app.getTablePopupController();
                tablePopupCtrl.createBasicTable(row, column);
                return;
            }

        editor.indent();
    },
    readOnly: true
});

editor.addEventListener("mousewheel", mouseWheelHandler);

function mouseWheelHandler(event) {
    if(!event)
        return;
    event = window.event;

    if(event.ctrlKey && editor.getValue().length){
        var divEditor = document.getElementById('editor');
        var editorStyle = window.getComputedStyle(divEditor, null).getPropertyValue("font-size");
        var fontSize = parseInt(editorStyle); 

        if(event.wheelDelta < 0 && fontSize > 8){
            //mouse scroll down - min size 8
            divEditor.style.fontSize = (fontSize - 1) + "px";
        }
        else if(event.wheelDelta >= 0 && fontSize < 24){ 
            //mouse scroll up - max size 24
            divEditor.style.fontSize = (fontSize + 1) + "px";
        }
    }
}

function matchBoldText(text) {
    return text.match(/\*.*\*/g);
}

function matchItalicizedText(text) {
    return text.match(/\_.*\_/g);
}

function matchSuperScriptText(text) {
    return text.match(/\^.*\^/g);
}

function matchSubScriptText(text) {
    return text.match(/\~.*\~/g);
}

function matchCode(text) {
    return text.match(/\`.*\`/g);
}
function addNewCommand(key,value){
    var name = key + value;
    editor.commands.addCommand({
        name: name,
        bindKey: {
            win: key,
            mac: key
        },
        exec: function (editor) {
            console.log(value);
            editor.insert(value);
        },
        readOnly: true
    });
}
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

function formatText(editor, matcher, firstCharacter, lastCharacter) {
    var range = editor.getSelectionRange();
    var text = editor.session.getTextRange(range);
    var firstCharLength = firstCharacter.length;
    var lastCharLength = lastCharacter.length;

    if (matcher(text)) {
        text = text.substring(firstCharLength, text.length - lastCharLength);
        editor.session.replace(range, text);
    }
    else {
        var virtualRange = editor.getSelectionRange();
        virtualRange.setStart(range.start.row, range.start.column - firstCharLength);
        virtualRange.setEnd(range.end.row, range.end.column + lastCharLength);

        var virtualText = editor.session.getTextRange(virtualRange);
        if (matcher(virtualText)) {
            editor.session.replace(virtualRange, text);
        }
        else {
            editor.session.replace(range, firstCharacter + text + lastCharacter);
            if (range.end.column == range.start.column) {
                editor.navigateTo(range.end.row, range.end.column + firstCharLength);
            }
        }
    }
}

function boldText() {
    formatText(editor, matchBoldText, "*", "*");
}

function italicizeText() {
    formatText(editor, matchItalicizedText, "_", "_");
}

function superScript() {
    formatText(editor, matchSuperScriptText, "^", "^");
}

function subScript() {
    formatText(editor, matchSubScriptText, "~", "~");
}

function underlinedText() {
    formatText(editor, matchHtmlTagText, "+++<u>", "</u>+++");
}

function addStrikeThroughText() {
    formatText(editor, matchHtmlTagText, "+++<del>", "</del>+++");
}

function isURL(text) {
    var myRegExp = /^(.*?)(?:\S+(?::\S*)?@)?(?:(?!10(?:\.\d{1,3}){3})(?!127(?:\.\d{1,3}){3})(?!169\.254(?:\.\d{1,3}){2})(?!192\.168(?:\.\d{1,3}){2})(?!172\.(?:1[6-9]|2\d|3[0-1])(?:\.\d{1,3}){2})(?:[1-9]\d?|1\d\d|2[01]\d|22[0-3])(?:\.(?:1?\d{1,2}|2[0-4]\d|25[0-5])){2}(?:\.(?:[1-9]\d?|1\d\d|2[0-4]\d|25[0-4]))|(?:(?:[a-z\u00a1-\uffff0-9]+-?)*[a-z\u00a1-\uffff0-9]+)(?:\.(?:[a-z\u00a1-\uffff0-9]+-?)*[a-z\u00a1-\uffff0-9]+)*(?:\.(?:[a-z\u00a1-\uffff]{2,})))(?::\d{2,5})?(?:\/[^\s]*)?$/i;

    return myRegExp.test(text);

}

function addHyperLink() {
    var cursorPosition = editor.getCursorPosition();
    var session = editor.getSession();
    var pasted = app.paste();
    if (isURL(pasted)) {
        if (pasted.indexOf("http") == -1)
            session.insert(cursorPosition, "http://" + pasted + "[text]");
        else
            session.insert(cursorPosition, pasted + "[text]");
        return;
    }
    session.insert(cursorPosition, "http://url[text]");
}

function addSourceCode() {
    var range = editor.getSelectionRange();
    editor.removeToLineStart();

    editor.insert("[source,java]\n----\n\n----");

    editor.gotoLine(range.end.row + 3, 0, true);
}

function addMathBlock() {
    var range = editor.getSelectionRange();
    editor.removeToLineStart();

    editor.insert("[math,file=\"\"]\n--\n\n--");

    editor.gotoLine(range.end.row + 3, 0, true);
}

function addUmlBlock() {
    var range = editor.getSelectionRange();
    editor.removeToLineStart();

    editor.insert("[uml,file=\"\"]\n--\n\n--");

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

function addOlList() {
    var cursorPosition = editor.getCursorPosition();
    cursorPosition.column = 0;
    var session = editor.getSession();
    session.insert(cursorPosition, "1. ");
}

function addUlList() {
    var cursorPosition = editor.getCursorPosition();
    cursorPosition.column = 0;
    var session = editor.getSession();
    session.insert(cursorPosition, "* ");
}

editor.commands.addCommand({
    name: 'underline-selected',
    bindKey: {win: 'Ctrl-U', mac: 'Command-U'},
    exec: underlinedText,
    readOnly: true
});

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
        console.log("in old");
        formatText(editor, matchCode, "`", "`");
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
    name: 'firebug-lite',
    bindKey: {
        win: 'F12',
        mac: 'F12'
    },
    exec: function () {
        if (!$("body").find("#firebug-script").length)
            $("body").append("<script id='firebug-script' type='text/javascript' src='http://getfirebug.com/firebug-lite.js#startOpened=true'></script>")
    },
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

            editor.insert("= Book Name\nAuthor's Name\n:doctype: book\n:encoding: utf-8\n:lang: en\n:toc:\n:numbered:\n\n\n");

            return;
        }


        // math tab
        if (textRange == "math") { // math block generator
            addMathBlock();
            return;
        }

        // uml tab
        if (textRange == "uml") { // uml block generator
            addUmlBlock();
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
    if (!event)
        return;
    event = window.event;

    if (event.ctrlKey && editor.getValue().length) {

        var fontSize = parseInt(editor.getFontSize());

        if (event.wheelDelta < 0 && fontSize > 8) {
            //mouse scroll down - min size 8
            editor.setFontSize((fontSize - 1) + "px");
        }
        else if (event.wheelDelta >= 0 && fontSize < 24) {
            //mouse scroll up - max size 24
            editor.setFontSize((fontSize + 1) + "px");
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

function matchHtmlTagText(text) {
    return text.match(/(\+\+\+\<(u|del)\>).*(\<\/(u|del)\>\+\+\+)/g);
}


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

editor.commands.addCommand({
    name: 'bold-selected',
    bindKey: {win: 'Ctrl-B', mac: 'Command-B'},
    exec: function (editor) {
        var text = editor.session.getTextRange(editor.getSelectionRange());
        editor.session.replace(editor.selection.getRange(), "*" + text + "*")
    },
    readOnly: true
});

editor.commands.addCommand({
    name: 'codify-selected',
    bindKey: {win: 'Ctrl-Shift-C', mac: 'Command-Shift-C'},
    exec: function (editor) {
        var text = editor.session.getTextRange(editor.getSelectionRange());
        editor.session.replace(editor.selection.getRange(), "`" + text + "`")
    },
    readOnly: true
});


editor.commands.addCommand({
    name: 'italicize-selected',
    bindKey: {
        win: 'Ctrl-i|Ctrl-İ|Ctrl-ı|Ctrl-I',
        mac: 'Command-i|Command-İ|Command-ı|Command-I'
    },
    exec: function (editor) {
        var text = editor.session.getTextRange(editor.getSelectionRange());
        editor.session.replace(editor.selection.getRange(), "_" + text + "_")
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
            editor.removeToLineStart();
            editor.insert("image::images/image.png[]");
            return;
        }

        // book tab
        if (textRange == "book") { // source generator
            editor.removeToLineStart();

            editor.insert("= Book Name\nAuthor's Name\n:doctype: book\n:encoding: utf-8\n:lang: tr\n\n\n");

            return;
        }

        // src tab
        if (textRange == "src") { // source generator
            editor.removeToLineStart();

            editor.insert("[source,java]\n----\n\n----");

            editor.gotoLine(range.end.row + 3, 0, true);
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
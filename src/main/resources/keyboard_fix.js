editor.commands.addCommand({
    name: 'cut-1',
    bindKey: {win: 'Ctrl-X'},
    exec: function(editor) {
        console.log(arguments);
//        editor.insert("Pasted");
        app.cutCopy(editor.session.getTextRange(editor.getSelectionRange()));
        editor.remove(editor.getSelectionRange());
//        editor.clearSelection();
    },
    readOnly: true // false if this command should not apply in readOnly mode
});

editor.commands.addCommand({
    name: 'copy-1',
    bindKey: {win: 'Ctrl-C'},
    exec: function(editor) {
        console.log(arguments);
//        editor.insert("Pasted");

        app.cutCopy(editor.session.getTextRange(editor.getSelectionRange()));
    },
    readOnly: false // false if this command should not apply in readOnly mode
});

editor.commands.addCommand({
    name: 'paste-1',
    bindKey: {win: 'Ctrl-V'},
    exec: function(editor) {
        editor.insert(app.paste());
    },
    readOnly: true // false if this command should not apply in readOnly mode
});

editor.commands.addCommand({
    name: 'ctrl-enter-1',
    bindKey: {win: 'Ctrl-Enter'},
    exec: function(editor) {
        editor.insert("\r\n");
    },
    readOnly: true // false if this command should not apply in readOnly mode
});

editor.commands.addCommand({
    name: 'right-brace-1',
    bindKey: {win: 'Ctrl-Alt-0'},
    exec: function(editor) {
        editor.insert("}");
    },
    readOnly: true // false if this command should not apply in readOnly mode
});
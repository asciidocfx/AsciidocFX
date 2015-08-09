function addNewCommand(key, value) {
    var name = key + value;
    editor.commands.addCommand({
        name: name,
        bindKey: {
            win: key,
            mac: key
        },
        exec: function (editor) {
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

        afx.cutCopy(editor.getCopyText());
        editor.execCommand("cut");

    },
    readOnly: true
});

editor.commands.addCommand({
    name: 'close-1',
    bindKey: {win: 'Ctrl-W', mac: 'Command-W'},
    exec: function (editor) {
        afx.saveAndCloseCurrentTab();
    },
    readOnly: true
});

editor.commands.addCommand({
    name: 'copy-1',
    bindKey: {win: 'Ctrl-C', mac: 'Command-C'},
    exec: function (editor) {
        afx.cutCopy(editor.getCopyText());
        editor.execCommand("copy");
    },
    readOnly: false
});

editor.commands.addCommand({
    name: 'paste-1',
    bindKey: {win: 'Ctrl-V', mac: 'Command-V'},
    exec: function (editor) {

        afx.paste();

        //editor.execCommand("paste");
    },
    readOnly: true
});

editor.commands.addCommand({
    name: 'paste-raw-1',
    bindKey: {win: 'Ctrl-Shift-V', mac: 'Command-Shift-V'},
    exec: function (editor) {
        //afx.pasteRaw();
        var text = afx.clipboardValue();

        if (!editor.inMultiSelectMode || editor.inVirtualSelectionMode) {
            editor.insert(text);
        } else {
            var lines = text.split(/\r\n|\r|\n/);
            var ranges = editor.selection.rangeList.ranges;
            if (lines.length > ranges.length || lines.length < 2 || !lines[1]) {
                return editor.commands.exec("insertstring", editor, text);
            }

            for (var i = ranges.length; i--;) {
                var range = ranges[i];
                if (!range.isEmpty()) {
                    editor.session.remove(range);
                }

                editor.session.insert(range.start, lines[i]);
            }
        }
    },
    readOnly: true
});

editor.commands.addCommand({
    name: 'ctrl-enter-1',
    bindKey: {win: 'Ctrl-Enter', mac: 'Command-Enter'},
    exec: function (editor) {
        editor.insert("\n");
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

var formatText = function (editor, matcher, firstCharacter, lastCharacter) {

    var range = editor.getSelectionRange();
    var selectedText = editor.session.getTextRange(range);
    var firstCharLength = firstCharacter.length;
    var lastCharLength = lastCharacter.length;

    var decorated = matcher(selectedText);
    if (decorated) {
        selectedText = decorated[2];
        editor.session.replace(range, selectedText);
    }
    else if (!matchAnyTextFormatting(selectedText, firstCharLength, lastCharLength, matcher)) {

        if (isInlineAsciiDocFormatting(firstCharacter, lastCharacter)) {

            var virtualRange = updateVirtualRange(1, 1);
            var virtualText = editor.session.getTextRange(virtualRange);

            if (!controlSpaceBoundary(selectedText, virtualText)) {
                firstCharacter = firstCharacter.concat(lastCharacter);
                lastCharacter = lastCharacter.concat(lastCharacter);
                firstCharLength = firstCharacter.length;
            }
        }

        editor.session.replace(range, firstCharacter.concat(selectedText, lastCharacter));

        if (range.end.column == range.start.column)
            editor.navigateTo(range.end.row, range.end.column + firstCharLength);
    }

    function matchAnyTextFormatting(selectedText, prefixLength, suffixLength, matcher) {
        var copyOfPL = prefixLength,
            copyOfSL = suffixLength,
            attempt = false;

        var virtualRange = updateVirtualRange(prefixLength, suffixLength);
        var virtualText = editor.session.getTextRange(virtualRange);

        // find nested chars such as **abc**, ****, **
        while (matcher(virtualText)) {
            var previousText = virtualText;
            attempt = true;
            copyOfPL += prefixLength;
            copyOfSL += suffixLength;

            virtualRange = updateVirtualRange(copyOfPL, copyOfSL);
            virtualText = editor.session.getTextRange(virtualRange);

            if (previousText == virtualText)
                break;
        }

        if (attempt) {
            virtualRange = updateVirtualRange(copyOfPL - prefixLength, copyOfSL - suffixLength);
            editor.session.replace(virtualRange, selectedText);
            return attempt;
        }

        return attempt;
    }

    function isInlineAsciiDocFormatting(firstChar, lastChar) {
        if (editor.getSession().getMode().$id != "ace/mode/asciidoc")
            return false;

        return [["*", "*"], ["_", "_"], ["`", "`"], ["[underline]#", "#"], ["[line-through]#", "#"]].some(function (element, index, array) {
            return (firstChar === element[0] && lastChar === element[1]);
        });
    }

    function updateVirtualRange(startColumnOffSet, endColumnOffSet) {
        var range = virtualRange = editor.getSelectionRange();
        virtualRange.setStart(range.start.row, (range.start.column - startColumnOffSet));
        virtualRange.setEnd(range.end.row, (range.end.column + endColumnOffSet));
        return virtualRange;
    }

    function controlSpaceBoundary(mainText, virtualText) {
        return (virtualText == mainText) ||
            (virtualText.match('^( )(' + escapeSpecialChars(mainText) + ')( )$')) ||
            (virtualText.match('^( )(' + escapeSpecialChars(mainText) + ')$')) ||
            (virtualText.match('^(' + escapeSpecialChars(mainText) + ')( )$'));
    }

    function escapeSpecialChars(text) {
        return text.replace(/[\-\[\]\/\{\}\(\)\*\+\?\.\\\^\$\|]/g, "\\$&");
    }
}

function addFxChart(chartType) {
    var range = editor.getSelectionRange();
    editor.removeToLineStart();
    editor.insert("[chart," + chartType + ",file=\"\"]\n--\n\n--");
    editor.gotoLine(range.end.row + 3, 0, true);
}

var editorMenu = {
    asciidoc: {
        boldText: function () {
            formatText(editor, matchBoldText, "*", "*");
        },
        italicizeText: function () {
            formatText(editor, matchItalicizedText, "_", "_");
        },
        superScript: function () {
            formatText(editor, matchSuperScriptText, "^", "^");
        },
        subScript: function () {
            formatText(editor, matchSubScriptText, "~", "~");
        },
        underlinedText: function () {
            formatText(editor, matchUnderlineText, "[underline]#", "#");
        },
        addStrikeThroughText: function () {
            formatText(editor, matchLineThroughText, "[line-through]#", "#");
        },
        highlightedText: function () {
            formatText(editor, matchHighlightedText, "#", "#");
        },
        addHyperLink: function () {
            var cursorPosition = editor.getCursorPosition();
            var session = editor.getSession();
            var pasted = afx.clipboardValue();
            if (isURL(pasted)) {
                if (pasted.indexOf("http") == -1)
                    session.insert(cursorPosition, "http://" + pasted + "[text]");
                else
                    session.insert(cursorPosition, pasted + "[text]");
                return;
            }
            session.insert(cursorPosition, "http://url[text]");
        },
        addSourceCode: function (lang) {
            var range = editor.getSelectionRange();
            editor.removeToLineStart();

            editor.insert("[source," + lang + "]\n----\n\n----");

            editor.gotoLine(range.end.row + 3, 0, true);
        },
        addQuote: function () {
            var range = editor.getSelectionRange();
            editor.removeToLineStart();

            editor.insert("[quote,Rūmī]\n____\nPatience is the key to joy.\n____");

            editor.gotoLine(range.end.row + 3, 0, true);
        },
        addImageSection: function () {
            editor.removeToLineStart();
            editor.insert("image::images/image.png[]");
        },
        addHeading: function () {
            var cursorPosition = editor.getCursorPosition();
            cursorPosition.column = 0;
            var session = editor.getSession();
            var line = session.getLine(cursorPosition.row);
            var first = line[0] || "";
            session.insert(cursorPosition, (first == "=") ? "=" : "= ");
        },
        addOlList: function () {
            var cursorPosition = editor.getCursorPosition();
            cursorPosition.column = 0;
            var session = editor.getSession();
            session.insert(cursorPosition, "1. ");
        },
        addUlList: function () {
            var cursorPosition = editor.getCursorPosition();
            cursorPosition.column = 0;
            var session = editor.getSession();
            session.insert(cursorPosition, "* ");
        },
        addAdmonition: function (type) {
            var range = editor.getSelectionRange();
            editor.removeToLineStart();
            editor.insert("[" + type + "]\n====\n\n====");
            editor.gotoLine(range.end.row + 3, 0, true);
        },
        addSidebarBlock: function () {
            var range = editor.getSelectionRange();
            editor.removeToLineStart();
            editor.insert(".Title\n****\n\n****");
            editor.gotoLine(range.end.row + 3, 0, true);
        },
        addExampleBlock: function () {
            var range = editor.getSelectionRange();
            editor.removeToLineStart();
            editor.insert(".Title\n====\n\n====");
            editor.gotoLine(range.end.row + 3, 0, true);
        },
        addPassthroughBlock: function () {
            var range = editor.getSelectionRange();
            editor.removeToLineStart();
            editor.insert("++++\n\n++++");
            editor.gotoLine(range.end.row + 2, 0, true);
        },
        addIndexSelection: function () {
            var range = editor.getSelectionRange();
            var selectedText = editor.session.getTextRange(range);
            if (selectedText)
                if (selectedText.trim() != "")
                    editor.insert("(((" + selectedText + ")))" + selectedText);
        },
        addBookHeader: function () {
            editor.removeToLineStart();
            editor.insert("= Book Name\nAuthor Name\n:doctype: book\n:encoding: utf-8\n:lang: en\n:toc: left\n:numbered:\n\n\n");
        },
        addArticleHeader: function () {
            editor.removeToLineStart();
            editor.insert("= Article Name\nAuthor Name\n:doctype: article\n:encoding: utf-8\n:lang: en\n:toc: left\n:numbered:\n\n\n");
        },
        addColophon: function () {
            editor.removeToLineStart();
            editor.insert("[colophon]\n== Example Colophon\n\nText at the end of a book describing facts about its production.");
        },
        addPreface: function () {
            editor.removeToLineStart();
            editor.insert("[preface]\n== Example Preface\n\nOptional preface.");
        },
        addDedication: function () {
            editor.removeToLineStart();
            editor.insert("[dedication]\n== Example Dedication\n\nOptional dedication.");
        },
        addAppendix: function () {
            editor.removeToLineStart();
            editor.insert("[appendix]\n== Example Appendix\n\nOne or more optional appendixes go here at section level 1.");
        },
        addGlossary: function () {
            editor.removeToLineStart();
            editor.insert("[glossary]\n== Example Glossary\n\n" +
                "Glossaries are optional. Glossaries entries are an example of a style of AsciiDoc labeled lists.\n\n" +
                "[glossary]\n" +
                "A glossary term::\n\tThe corresponding (indented) definition.\n\n" +
                "A second glossary term::\n\tThe corresponding (indented) definition.");
        },
        addBibliography: function () {
            editor.removeToLineStart();
            editor.insert("[bibliography]\n" +
                "== Example Bibliography\n" +
                "\n" +
                "The bibliography list is a style of AsciiDoc bulleted list.\n" +
                "\n" +
                "[bibliography]\n" +
                ".Books\n" +
                "- [[[taoup]]] Eric Steven Raymond. 'The Art of Unix\n" +
                "  Programming'. Addison-Wesley. ISBN 0-13-142901-9.\n" +
                "- [[[walsh-muellner]]] Norman Walsh & Leonard Muellner.\n" +
                "  'DocBook - The Definitive Guide'. O'Reilly & Associates. 1999.\n" +
                "  ISBN 1-56592-580-7.");
        },
        addIndex: function () {
            editor.removeToLineStart();
            editor.insert("[index]\n" +
                "== Example Index\n" +
                "////////////////////////////////////////////////////////////////\n" +
                "The index is normally left completely empty, it's contents being\n" +
                "generated automatically by the DocBook toolchain.\n" +
                "////////////////////////////////////////////////////////////////  ");
        },
        addMathBlock: function () {
            var range = editor.getSelectionRange();
            editor.removeToLineStart();
            editor.insert("[math,file=\"\"]\n--\n\n--");
            editor.gotoLine(range.end.row + 3, 0, true);
        },
        addUmlBlock: function () {
            var range = editor.getSelectionRange();
            editor.removeToLineStart();
            editor.insert("[uml,file=\"\"]\n--\n\n--");
            editor.gotoLine(range.end.row + 3, 0, true);
        },
        addDitaaBlock: function () {
            var range = editor.getSelectionRange();
            editor.removeToLineStart();
            editor.insert("[ditaa,file=\"\"]\n--\n\n--");
            editor.gotoLine(range.end.row + 3, 0, true);
        },        
        addTreeBlock: function () {
            var range = editor.getSelectionRange();
            editor.removeToLineStart();
            editor.insert("[tree,file=\"\"]\n--\n\n--");
            editor.gotoLine(range.end.row + 3, 0, true);
        },
        addPieChart: function () {
            addFxChart("pie");
        },
        addBarChart: function () {
            addFxChart("bar");
        },
        addAreaChart: function () {
            addFxChart("area");
        },
        addLineChart: function () {
            addFxChart("line");
        },
        addScatterChart: function () {
            addFxChart("scatter");
        },
        addBubbleChart: function () {
            addFxChart("bubble");
        },
        addStackedAreaChart: function () {
            addFxChart("\"stacked-area\"");
        },
        addStackedBarChart: function () {
            addFxChart("\"stacked-bar\"");
        }
    },
    markdown: {
        boldText: function () {
            formatText(editor, matchBoldText, "**", "**");
        },
        italicizeText: function () {
            formatText(editor, matchItalicizedText, "_", "_");
        },
        superScript: function () {
            formatText(editor, matchMarkdownSuperScriptText, "<sup>", "</sup>");
        },
        subScript: function () {
            formatText(editor, matchMarkdownSubScriptText, "<sub>", "</sub>");
        },
        underlinedText: function () {
            formatText(editor, matchMarkdownUnderlineText, "<u>", "</u>");
        },
        addStrikeThroughText: function () {
            formatText(editor, matchMarkdownStrikeThroughText, "~~", "~~");
        },
        highlightedText: function () {
            formatText(editor, matchHighlightedText, "#", "#");
        },
        addHyperLink: function () {
            var cursorPosition = editor.getCursorPosition();
            var session = editor.getSession();
            var pasted = afx.clipboardValue();
            if (isURL(pasted)) {
                session.insert(cursorPosition, "[text](" + pasted + ")");
                return;
            }
            session.insert(cursorPosition, "[text](url)");
        },
        addSourceCode: function (lang) {
            var range = editor.getSelectionRange();
            editor.removeToLineStart();
            editor.insert("```" + lang + "\n\n```");
            editor.gotoLine(range.end.row + 2, 0, true);
        },
        addQuote: function () {
            var range = editor.getSelectionRange();
            editor.removeToLineStart();

            editor.insert("\n> Patience is the key to joy.\n");
        },
        addImageSection: function () {
            editor.removeToLineStart();
            editor.insert("![Alt text](images/image.png)");
        },
        addHeading: function () {
            var cursorPosition = editor.getCursorPosition();
            cursorPosition.column = 0;
            var session = editor.getSession();
            var line = session.getLine(cursorPosition.row);
            var first = line[0] || "";
            session.insert(cursorPosition, (first == "#") ? "#" : "# ");
        },
        addOlList: function () {
            var cursorPosition = editor.getCursorPosition();
            cursorPosition.column = 0;
            var session = editor.getSession();
            session.insert(cursorPosition, "1. ");
        },
        addUlList: function () {
            var cursorPosition = editor.getCursorPosition();
            cursorPosition.column = 0;
            var session = editor.getSession();
            session.insert(cursorPosition, "* ");
        }
    }
}

function isURL(text) {
    var myRegExp = /^(.*?)(?:\S+(?::\S*)?@)?(?:(?!10(?:\.\d{1,3}){3})(?!127(?:\.\d{1,3}){3})(?!169\.254(?:\.\d{1,3}){2})(?!192\.168(?:\.\d{1,3}){2})(?!172\.(?:1[6-9]|2\d|3[0-1])(?:\.\d{1,3}){2})(?:[1-9]\d?|1\d\d|2[01]\d|22[0-3])(?:\.(?:1?\d{1,2}|2[0-4]\d|25[0-5])){2}(?:\.(?:[1-9]\d?|1\d\d|2[0-4]\d|25[0-4]))|(?:(?:[a-z\u00a1-\uffff0-9]+-?)*[a-z\u00a1-\uffff0-9]+)(?:\.(?:[a-z\u00a1-\uffff0-9]+-?)*[a-z\u00a1-\uffff0-9]+)*(?:\.(?:[a-z\u00a1-\uffff]{2,})))(?::\d{2,5})?(?:\/[^\s]*)?$/i;

    return myRegExp.test(text);
}

function showLineNumbers() {
    editor.renderer.getShowGutter() ? editor.renderer.setShowGutter(false) : editor.renderer.setShowGutter(true);
}

editor.commands.addCommand({
    name: 'underline-selected',
    bindKey: {win: 'Ctrl-U', mac: 'Command-U'},
    exec: function () {
        afx.getShortcutProvider().getProvider().addUnderline();
    },
    readOnly: true
});

editor.commands.addCommand({
    name: 'bold-selected',
    bindKey: {win: 'Ctrl-B', mac: 'Command-B'},
    exec: function () {
        afx.getShortcutProvider().getProvider().addBold();
    },
    readOnly: true
});

editor.commands.addCommand({
    name: 'highlight-selected',
    bindKey: {win: 'Ctrl-H', mac: 'Command-H'},
    exec: function () {
        afx.getShortcutProvider().getProvider().addHighlight();
    },
    readOnly: true
});

editor.commands.addCommand({
    name: 'line-numbers',
    bindKey: {win: 'Ctrl-L', mac: 'Command-L'},
    exec: showLineNumbers,
    readOnly: true
});

editor.commands.addCommand({
    name: 'codify-selected',
    bindKey: {win: 'Ctrl-Shift-C', mac: 'Command-Shift-C'},
    exec: function (editor) {
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
    exec: function () {
        afx.getShortcutProvider().getProvider().addItalic();
    },
    readOnly: true
});

editor.commands.addCommand({
    name: 'firebug-lite',
    bindKey: {
        win: 'F12',
        mac: 'F12'
    },
    exec: function () {
        if (!document.querySelectorAll("#firebug-script").length) {
            var head = document.querySelector("head");
            var js = document.createElement("script");
            js.src = "http://getfirebug.com/firebug-lite.js#startOpened=true";
            head.appendChild(js);
        }
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
            afx.getShortcutProvider().getProvider().addImage();
            return;
        }

        // book tab
        if (textRange == "book") { // source generator
            afx.getShortcutProvider().getProvider().addBookHeader();
            return;
        }

        // article tab
        if (textRange == "article") { // source generator
            afx.getShortcutProvider().getProvider().addArticleHeader();
            return;
        }

        // math tab
        if (textRange == "math") { // math block generator
            afx.getShortcutProvider().getProvider().addMathBlock();
            return;
        }

        // uml tab
        if (textRange == "uml") { // uml block generator
            afx.getShortcutProvider().getProvider().addUmlBlock();
            return;
        }
        
        // ditaa tab
        if (textRange == "ditaa") { // ditaa block generator
            afx.getShortcutProvider().getProvider().addDitaaBlock();
            return;
        }

        // tree tab
        if (textRange == "tree") { // tree block generator
            afx.getShortcutProvider().getProvider().addTreeBlock();
            return;
        }

        // quote tab
        if (textRange == "quote") { // quote block generator
            afx.getShortcutProvider().getProvider().addQuote();
            return;
        }

        // src tab
        if (textRange == "src") { // source generator
            afx.getShortcutProvider().getProvider().addCode("");
            return;
        }
        // src,ruby or src.ruby tab
        var srcMatch = textRange.match(/src(\.|,)(\w+)/);
        if (Array.isArray(srcMatch)) {
            if (srcMatch.length == 3) {
                var lang = srcMatch[2];
                afx.getShortcutProvider().getProvider().addCode(lang);
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

                afx.getShortcutProvider().getProvider().addBasicTable(row, column);
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
    return text.match(/^(\*{1,2})(.*?)(\*{1,2})$/);
}

function matchItalicizedText(text) {
    return text.match(/^(\_{1,2})(.*?)(\_{1,2})$/);
}

function matchSuperScriptText(text) {
    return text.match(/^(\^)(.*?)(\^)$/);
}

function matchSubScriptText(text) {
    return text.match(/^(\~)(.*?)(\~)$/);
}

function matchCode(text) {
    return text.match(/^(\`{1,2})(.*?)(\`{1,2})$/);
}

function matchHighlightedText(text) {
    return text.match(/^(\#)(.*?)(\#)$/);
}

function matchMarkdownStrikeThroughText(text) {
    return text.match(/^(\~\~)(.*?)(\~\~)$/);
}

function matchLineThroughText(text) {
    return text.match(/^(?:\[line-through\])(\#{1,2})([^\#]*?)(\1)$/);
}

function matchUnderlineText(text) {
    return text.match(/^(?:\[underline\])(\#{1,2})([^\#]*?)(\1)$/);
}

function matchMarkdownUnderlineText(text) {
    return text.match(/^(\<(?:u)\>)(.*?)(\<\/(?:u)\>)$/);
}

function matchMarkdownSuperScriptText(text) {
    return text.match(/^(\<(?:sup)\>)(.*?)(\<\/(?:sup)\>)$/);
}

function matchMarkdownSubScriptText(text) {
    return text.match(/^(\<(?:sub)\>)(.*?)(\<\/(?:sub)\>)$/);
}

confirm("command:ready")
var md2AscRenderer = new marked.Renderer();

md2AscRenderer.heading = function (text, level) {
    return "\n" + repeatStuff("=", level) + " " + text + "\n";
};
md2AscRenderer.hr = function () {
    return "\n'''\n";
};

md2AscRenderer.list = function (body, ordered) {
    var symbol = ordered ? "." : "*";
    return body.replace(/\$list-start\$/g, symbol) + "\n";
};

md2AscRenderer.listitem = function (text) {
    return '\n$list-start$ ' + text.replace("\n", "").trim();
};

md2AscRenderer.paragraph = function (text) {
    // if <b>ise</b> hatalı
    //return "\n[%hardbreaks]";
    return "\n" + text + "\n";
};

md2AscRenderer.table = function (header, body) {
    var headerContent = "\n[width=\"100%\",options=\"header\"]";
    var start = "\n|====";
    var end = "\n|====\n";
    return headerContent + start + body + end;
};

md2AscRenderer.tablerow = function (content) {
    return "\n" + content;
};

md2AscRenderer.tablecell = function (content, flags) {
    return "| " + content + " ";
};

md2AscRenderer.blockquote = function (quote) {
    return "\n\n[quote]\n____" + quote + "\n____\n\n";
};

md2AscRenderer.code = function (code, lang, escaped) {
    if (!lang) {
        return "\n\n----\n" + code + "\n----\n\n"
    }
    return "\n\n[source," + lang + "]\n----\n" + code + "\n----\n\n";
};

md2AscRenderer.codespan = function (text) {
    //text = text.replace(/\*/g, "\\*");
    text = text.replace(/\*\*/g, "{asterisk}{asterisk}");
    if ((text.match(/#/g) || "").length % 2 == 1)
        text = text.replace(/#/g, "\\#");

    return "`" + text + "`";
};

md2AscRenderer.strong = function (text) {
    return '**' + text + '**';
};

md2AscRenderer.em = function (text) {
    return '__' + text + '__';
};

md2AscRenderer.br = function (text) {
    return text + ' +\n';
};

md2AscRenderer.del = function (text) {
    return '+++<del>' + text + '</del>+++';
};

md2AscRenderer.link = function (href, title, text) {
    var relative = !href.match(/(https?|ftp|irc|mailto|email).*/);
    return "\n" + ( relative ? "link:" : "") + href + "[" + text + "]\n";
};

md2AscRenderer.image = function (href, title, text) {
    var alt = text ? "alt=" + text + (title ? "," : "") : "";
    var imageTitle = title ? "title=" + title : "";
    return "\nimage::" + href + "[" + alt + imageTitle + "]\n";
};

function markdownToAsciidoc(input) {
    var result = "";
    try {
        var result = marked(input, {renderer: md2AscRenderer});
    }
    catch (e) {
        throw  e;
    }
    return result;
}

function repeatStuff(s, n) {
    var a = [];
    while (a.length < n) {
        a.push(s);
    }
    return a.join('');
}


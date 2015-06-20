var defaultLanguage = undefined;
function getDefaultLanguage() {
    if (!defaultLanguage && afx)
        defaultLanguage = afx.getConfig().getDefaultLanguage();
    return defaultLanguage;
}

function fillOutlines(doc) {
    afx.clearOutline();
    var sections = doc.$sections();
    sections.forEach(function (section, i) {

        var level = section.$level();
        var title = section.$title();
        var lineno = section.$lineno();
        var id = section.$id();
        var parentLineNo = lineno;

        afx.fillOutline(null, level, title, lineno, id);
        fillOutlinesSubSections(section);

    });
    afx.finishOutline();
}

function fillOutlinesSubSections(section) {
    var sections = section.$sections();
    var parentLineNo = section.$lineno();
    sections.forEach(function (subsection, i) {

        var level = subsection.$level();
        var title = subsection.$title();
        var lineno = subsection.$lineno();
        var id = subsection.$id();

        afx.fillOutline(parentLineNo, level, title, lineno, id);
        fillOutlinesSubSections(subsection);
    });
}

function convertAsciidoc(content) {

    var rendered = "";

    var options = Opal.hash2(['safe', 'attributes', 'sourcemap'], {
        safe: 'safe',
        attributes: 'showtitle icons=font@ source-highlighter=highlight.js platform=opal platform-opal env=browser env-browser idprefix idseparator=- ',
        sourcemap: true
    });

    var doc = Opal.Asciidoctor.$load(content, options);

    try {
        afx.fillOutlines(doc);
    }
    catch (e) {
        throw e;
    }

    doc.attributes.keys["lang"] = doc.attributes.keys["lang"] || getDefaultLanguage();

    rendered = doc.$convert();


    return {
        rendered: rendered,
        doctype: doc.doctype,
        backend: doc.$backend()
    };
}

function scrollTo60(position) {
    $(window).scrollTop(position - 60);
}

function findRenderedSelection(content) {
    return Opal.Asciidoctor.$render(content);
}

function runScroller(content) {

    var renderedSelection = findRenderedSelection(content);

    if (renderedSelection.trim() == "") {
        return;
    }
    else if ($(renderedSelection).css("page-break-after")) {
        //page break element
        return;
    }
    else if ($(renderedSelection).is("hr")) {
        // horizontal rules
        return;
    }
    else if ($(renderedSelection).is(".imageblock")) {
        var src = $(renderedSelection).find("img").attr("src");
        scrollTo60($("img[src^='" + src + "']").offset().top);
        return;
    }

    if ($(renderedSelection).is(".admonitionblock")) {
        scrollToElement(".admonitionblock", $(renderedSelection).find(".content").text());
    }
    else if ($(renderedSelection).is(".exampleblock")) {
        scrollToUniqueElement(".exampleblock", $(renderedSelection).text(), /^Example \d+\./, "Example 1.");
    }
    else if ($(renderedSelection).is(".listingblock")) {
        scrollToElement(".listingblock", $(renderedSelection).text());
    }
    else if ($(renderedSelection).is(".literalblock")) {
        scrollToElement(".literalblock", $(renderedSelection).text());
    }
    else if ($(renderedSelection).is(".openblock")) {
        scrollToElement(".openblock", $(renderedSelection).text());
    }
    else if ($(renderedSelection).is(".sidebarblock")) {
        scrollToElement(".sidebarblock", $(renderedSelection).text());
    }
    else if ($(renderedSelection).is(".quoteblock")) {
        scrollToElement(".quoteblock > blockquote, .verseblock > .content", $(renderedSelection).find(".paragraph").text());
    }
    else if ($(renderedSelection).is(".paragraph")) {
        scrollToElement(".paragraph, .quoteblock > blockquote", $($(renderedSelection).get(0)).text());
    }
    else if ($(renderedSelection).is(".ulist")) {
        scrollToElement(".ulist:not(.checklist) > ul > li", $(renderedSelection).text());
    }
    else if ($(renderedSelection).is(".olist")) {
        scrollToElement(".olist > ol > li", $(renderedSelection).text());
    }
    else if ($(renderedSelection).is(".colist")) {
        scrollToUniqueElement(".colist > table > tbody > tr", $(renderedSelection).text(), /^\d+ */, "");
    }
    else if ($(renderedSelection).is(".tableblock")) {
        scrollToUniqueElement("table.tableblock, table.frame-all", $(renderedSelection).text(), /^Table \d+\./, "Table 1.");
    }
    else {
        var header = false;

        $("*:header").each(function () {
            if ($(this).text().trim() == $(renderedSelection).text().trim()) {
                header = true;
                scrollTo60($(this).offset().top);
                return false;
            }
        });

        if (!header) {
            var search = $("*:contains(" + $(renderedSelection).text().trim() + "):last");
            scrollTo60(search.offset().top);
        }
    }
}

function scrollToElement(elements, content) {
    if (content.trim() == "")
        return;

    $(elements).each(function () {
        if (simplify($(this).text()) == simplify(content)) {
            scrollTo60($(this).offset().top);
            return false;
        }
    });
}

function scrollToUniqueElement(elements, content, pattern, expected) {
    $(elements).each(function () {
        var element = $(this).text().trim().replace(pattern, expected);
        if (simplify(element) == simplify(content)) {
            scrollTo60($(this).offset().top);
            return false;
        }
    });
}

function simplify(text) {
    return text.replace(/( |\n|\r|\t|\")/g, "");
}

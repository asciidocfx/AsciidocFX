window.deckExt = {};
deckExt.replaceSlides = function (data) {
    var div = document.createElement("div");
    div.innerHTML = data;
    document.querySelector("div.slides").innerHTML = div.querySelector("div.slides").innerHTML;
    setTimeout(initializeReveal, 50);
}

deckExt.flipCurrentPage = function (renderedSelection) {

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
        deckExt.flipPage($("img[src^='" + src + "']").get(0));
        return;
    }

    if ($(renderedSelection).is(".admonitionblock")) {
        deckExt.findElement(".admonitionblock", $(renderedSelection).find(".content").text());
    }
    else if ($(renderedSelection).is(".exampleblock")) {
        deckExt.findUniqueElement(".exampleblock", $(renderedSelection).text(), /^Example \d+\./, "Example 1.");
    }
    else if ($(renderedSelection).is(".listingblock")) {
        deckExt.findElement(".listingblock", $(renderedSelection).text());
    }
    else if ($(renderedSelection).is(".literalblock")) {
        deckExt.findElement(".literalblock", $(renderedSelection).text());
    }
    else if ($(renderedSelection).is(".openblock")) {
        deckExt.findElement(".openblock", $(renderedSelection).text());
    }
    else if ($(renderedSelection).is(".sidebarblock")) {
        deckExt.findElement(".sidebarblock", $(renderedSelection).text());
    }
    else if ($(renderedSelection).is(".quoteblock")) {
        deckExt.findElement(".quoteblock > blockquote, .verseblock > .content", $(renderedSelection).find(".paragraph").text());
    }
    else if ($(renderedSelection).is(".paragraph")) {
        deckExt.findElement(".paragraph, .quoteblock > blockquote", $($(renderedSelection).get(0)).text());
    }
    else if ($(renderedSelection).is(".ulist")) {
        deckExt.findElement(".ulist:not(.checklist) > ul > li", $(renderedSelection).text());
    }
    else if ($(renderedSelection).is(".olist")) {
        deckExt.findUniqueElement("table > tbody > tr", $(renderedSelection).text(), /^\d+ */, "");
    }
    else if ($(renderedSelection).is(".colist")) {
        deckExt.findUniqueElement(".colist > table > tbody > tr", $(renderedSelection).text(), /^\d+ */, "");
    }
    else if ($(renderedSelection).is(".tableblock")) {
        deckExt.findUniqueElement("table.table-block.apply.call", $(renderedSelection).text(), /^Table \d+\./, "Table 1.");
    }
    else {
        var header = false;

        $("*:header").each(function () {
            if ($(this).text().trim() == $(renderedSelection).text().trim()) {
                header = true;
                deckExt.flipPage($(this).get(0));
                return false;
            }
        });

        if (!header) {
            var search = $("*:contains(" + $(renderedSelection).text().trim() + "):last");
            deckExt.flipPage(search.get(0));
        }
    }
};

deckExt.findElement = function (elements, content) {
    if (content.trim() == "")
        return;

    $(elements).each(function () {
        if (deckExt.simplify($(this).text()) == deckExt.simplify(content)) {
            deckExt.flipPage($(this).get(0));
            return false;
        }
    });
};

deckExt.findUniqueElement = function (elements, content, pattern, expected) {
    $(elements).each(function () {
        var element = $(this).text().trim().replace(pattern, expected);
        content = content.trim();
        if (deckExt.simplify(element) == deckExt.simplify(content)) {
            deckExt.flipPage($(this).get(0));
            return false;
        }
    });
};

deckExt.flipPage = function (element) {
    $("section[id]").each(function (index, section) {
        if ($(element).parents("section[id]").get(0) == section) {
            Reveal.slide(index, 0, undefined);
            return false;
        }
    });
};

deckExt.simplify = function (text) {
    return text.replace(/( |\n|\r|\t|\")/g, "");
};
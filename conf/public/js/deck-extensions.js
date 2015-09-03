window.deckjsExt = {};
deckjsExt.replaceSlides = function(data) {
    var div = document.createElement("div");
    div.innerHTML = data;
    document.querySelector(".deck-container").innerHTML = div.querySelector(".deck-container").innerHTML;
    setTimeout(function(){
        $.deck('.slide');
    },50);
}

deckjsExt.flipCurrentPage = function (renderedSelection) {

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
        deckjsExt.flipPage($("img[src^='" + src + "']").get(0));
        return;
    }

    if ($(renderedSelection).is(".admonitionblock")) {
        deckjsExt.findElement(".admonitionblock", $(renderedSelection).find(".content").text());
    }
    else if ($(renderedSelection).is(".exampleblock")) {
        deckjsExt.findUniqueElement(".exampleblock", $(renderedSelection).text(), /^Example \d+\./, "Example 1.");
    }
    else if ($(renderedSelection).is(".listingblock")) {
        deckjsExt.findElement(".listingblock", $(renderedSelection).text());
    }
    else if ($(renderedSelection).is(".literalblock")) {
        deckjsExt.findElement(".literalblock", $(renderedSelection).text());
    }
    else if ($(renderedSelection).is(".openblock")) {
        deckjsExt.findElement(".openblock", $(renderedSelection).text());
    }
    else if ($(renderedSelection).is(".sidebarblock")) {
        deckjsExt.findElement(".sidebarblock", $(renderedSelection).text());
    }
    else if ($(renderedSelection).is(".quoteblock")) {
        deckjsExt.findElement(".quoteblock > blockquote, .verseblock > .content", $(renderedSelection).find(".paragraph").text());
    }
    else if ($(renderedSelection).is(".paragraph")) {
        deckjsExt.findElement(".paragraph, .quoteblock > blockquote", $($(renderedSelection).get(0)).text());
    }
    else if ($(renderedSelection).is(".ulist")) {
        deckjsExt.findElement(".ulist:not(.checklist) > ul > li", $(renderedSelection).text());
    }
    else if ($(renderedSelection).is(".olist")) {
        deckjsExt.findUniqueElement("table > tbody > tr", $(renderedSelection).text(), /^\d+ */, "");
    }
    else if ($(renderedSelection).is(".colist")) {
        deckjsExt.findUniqueElement(".colist > table > tbody > tr", $(renderedSelection).text(), /^\d+ */, "");
    }
    else if ($(renderedSelection).is(".tableblock")) {
        deckjsExt.findUniqueElement("table.table-block.apply.call", $(renderedSelection).text(), /^Table \d+\./, "Table 1.");
    }
    else {
        var header = false;

        $("*:header").each(function () {
            if ($(this).text().trim() == $(renderedSelection).text().trim()) {
                header = true;
                deckjsExt.flipPage($(this).get(0));
                return false;
            }
        });

        if (!header) {
            var search = $("*:contains(" + $(renderedSelection).text().trim() + "):last");
            deckjsExt.flipPage(search.get(0));
        }
    }
};

deckjsExt.findElement = function (elements, content) {
    if (content.trim() == "")
        return;

    $(elements).each(function () {
        if (deckjsExt.simplify($(this).text()) == deckjsExt.simplify(content)) {
            deckjsExt.flipPage($(this).get(0));
            return false;
        }
    });
};

deckjsExt.findUniqueElement = function (elements, content, pattern, expected) {
    $(elements).each(function () {
        var element = $(this).text().trim().replace(pattern, expected);
        content = content.trim();
        if (deckjsExt.simplify(element) == deckjsExt.simplify(content)) {
            deckjsExt.flipPage($(this).get(0));
            return false;
        }
    });
};

deckjsExt.flipPage = function (element) {
    $("section[id]").each(function (index, section) {
        if ($(element).parents("section[id]").get(0) == section) {
            //Reveal.slide(index, 0, undefined);
            $.deck('go', index);
            return false;
        }
    });
};

deckjsExt.simplify = function (text) {
    return text.replace(/( |\n|\r|\t|\")/g, "");
};
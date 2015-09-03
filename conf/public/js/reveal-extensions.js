window.revealjsExt = {};
revealjsExt.replaceSlides = function (data) {
    var div = document.createElement("div");
    div.innerHTML = data;
    document.querySelector("div.slides").innerHTML = div.querySelector("div.slides").innerHTML;
    setTimeout(initializeReveal, 50);
}

revealjsExt.flipCurrentPage = function (renderedSelection) {

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
        revealjsExt.flipPage($("img[src^='" + src + "']").get(0));
        return;
    }

    if ($(renderedSelection).is(".admonitionblock")) {
        revealjsExt.findElement(".admonitionblock", $(renderedSelection).find(".content").text());
    }
    else if ($(renderedSelection).is(".exampleblock")) {
        revealjsExt.findUniqueElement(".exampleblock", $(renderedSelection).text(), /^Example \d+\./, "Example 1.");
    }
    else if ($(renderedSelection).is(".listingblock")) {
        revealjsExt.findElement(".listingblock", $(renderedSelection).text());
    }
    else if ($(renderedSelection).is(".literalblock")) {
        revealjsExt.findElement(".literalblock", $(renderedSelection).text());
    }
    else if ($(renderedSelection).is(".openblock")) {
        revealjsExt.findElement(".openblock", $(renderedSelection).text());
    }
    else if ($(renderedSelection).is(".sidebarblock")) {
        revealjsExt.findElement(".sidebarblock", $(renderedSelection).text());
    }
    else if ($(renderedSelection).is(".quoteblock")) {
        revealjsExt.findElement(".quoteblock > blockquote, .verseblock > .content", $(renderedSelection).find(".paragraph").text());
    }
    else if ($(renderedSelection).is(".paragraph")) {
        revealjsExt.findElement(".paragraph, .quoteblock > blockquote", $($(renderedSelection).get(0)).text());
    }
    else if ($(renderedSelection).is(".ulist")) {
        revealjsExt.findElement(".ulist:not(.checklist) > ul > li", $(renderedSelection).text());
    }
    else if ($(renderedSelection).is(".olist")) {
        revealjsExt.findUniqueElement("table > tbody > tr", $(renderedSelection).text(), /^\d+ */, "");
    }
    else if ($(renderedSelection).is(".colist")) {
        revealjsExt.findUniqueElement(".colist > table > tbody > tr", $(renderedSelection).text(), /^\d+ */, "");
    }
    else if ($(renderedSelection).is(".tableblock")) {
        revealjsExt.findUniqueElement("table.table-block.apply.call", $(renderedSelection).text(), /^Table \d+\./, "Table 1.");
    }
    else {
        var header = false;

        $("*:header").each(function () {
            if ($(this).text().trim() == $(renderedSelection).text().trim()) {
                header = true;
                revealjsExt.flipPage($(this).get(0));
                return false;
            }
        });

        if (!header) {
            var search = $("*:contains(" + $(renderedSelection).text().trim() + "):last");
            revealjsExt.flipPage(search.get(0));
        }
    }
};

revealjsExt.findElement = function (elements, content) {
    if (content.trim() == "")
        return;

    $(elements).each(function () {
        if (revealjsExt.simplify($(this).text()) == revealjsExt.simplify(content)) {
            revealjsExt.flipPage($(this).get(0));
            return false;
        }
    });
};

revealjsExt.findUniqueElement = function (elements, content, pattern, expected) {
    $(elements).each(function () {
        var element = $(this).text().trim().replace(pattern, expected);
        content = content.trim();
        if (revealjsExt.simplify(element) == revealjsExt.simplify(content)) {
            revealjsExt.flipPage($(this).get(0));
            return false;
        }
    });
};

revealjsExt.flipPage = function (element) {
    $("section[id]").each(function (index, section) {
        if ($(element).parents("section[id]").get(0) == section) {
            Reveal.slide(index, 0, undefined);
            return false;
        }
    });
};

revealjsExt.simplify = function (text) {
    return text.replace(/( |\n|\r|\t|\")/g, "");
};
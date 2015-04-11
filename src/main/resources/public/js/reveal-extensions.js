window.revealExt = {};
revealExt.replaceSlides = function (data) {
    var div = document.createElement("div");
    div.innerHTML = data;
    document.querySelector("div.slides").innerHTML = div.querySelector("div.slides").innerHTML;
    setTimeout(function () {
        revealExt.initializeReveal()
    }, 50);
}

revealExt.flipCurrentPage = function (renderedSelection) {

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
        revealExt.flipPage($("img[src^='" + src + "']").get(0));
        return;
    }

    if ($(renderedSelection).is(".admonitionblock")) {
        revealExt.findElement(".admonitionblock", $(renderedSelection).find(".content").text());
    }
    else if ($(renderedSelection).is(".exampleblock")) {
        revealExt.findUniqueElement(".exampleblock", $(renderedSelection).text(), /^Example \d+\./, "Example 1.");
    }
    else if ($(renderedSelection).is(".listingblock")) {
        revealExt.findElement(".listingblock", $(renderedSelection).text());
    }
    else if ($(renderedSelection).is(".literalblock")) {
        revealExt.findElement(".literalblock", $(renderedSelection).text());
    }
    else if ($(renderedSelection).is(".openblock")) {
        revealExt.findElement(".openblock", $(renderedSelection).text());
    }
    else if ($(renderedSelection).is(".sidebarblock")) {
        revealExt.findElement(".sidebarblock", $(renderedSelection).text());
    }
    else if ($(renderedSelection).is(".quoteblock")) {
        revealExt.findElement(".quoteblock > blockquote, .verseblock > .content", $(renderedSelection).find(".paragraph").text());
    }
    else if ($(renderedSelection).is(".paragraph")) {
        revealExt.findElement(".paragraph, .quoteblock > blockquote", $($(renderedSelection).get(0)).text());
    }
    else if ($(renderedSelection).is(".ulist")) {
        revealExt.findElement(".ulist:not(.checklist) > ul > li", $(renderedSelection).text());
    }
    else if ($(renderedSelection).is(".olist")) {
        revealExt.findUniqueElement("table > tbody > tr", $(renderedSelection).text(), /^\d+ */, "");
    }
    else if ($(renderedSelection).is(".colist")) {
        revealExt.findUniqueElement(".colist > table > tbody > tr", $(renderedSelection).text(), /^\d+ */, "");
    }
    else if ($(renderedSelection).is(".tableblock")) {
        revealExt.findUniqueElement("table.table-block.apply.call", $(renderedSelection).text(), /^Table \d+\./, "Table 1.");
    }
    else {
        var header = false;

        $("*:header").each(function () {
            if ($(this).text().trim() == $(renderedSelection).text().trim()) {
                header = true;
                revealExt.flipPage($(this).get(0));
                return false;
            }
        });

        if (!header) {
            var search = $("*:contains(" + $(renderedSelection).text().trim() + "):last");
            revealExt.flipPage(search.get(0));
        }
    }
};

revealExt.findElement = function (elements, content) {
    if (content.trim() == "")
        return;

    $(elements).each(function () {
        if (revealExt.simplify($(this).text()) == revealExt.simplify(content)) {
            revealExt.flipPage($(this).get(0));
            return false;
        }
    });
};

revealExt.findUniqueElement = function (elements, content, pattern, expected) {
    $(elements).each(function () {
        var element = $(this).text().trim().replace(pattern, expected);
        content = content.trim();
        if (revealExt.simplify(element) == revealExt.simplify(content)) {
            revealExt.flipPage($(this).get(0));
            return false;
        }
    });
};

revealExt.flipPage = function (element) {
    $("section[id]").each(function (index, section) {
        if ($(element).parents("section[id]").get(0) == section) {
            Reveal.slide(index, 0, undefined);
            return false;
        }
    });
};

revealExt.simplify = function (text) {
    return text.replace(/( |\n|\r|\t|\")/g, "");
};
window.bespokejsExt = {};
bespokejsExt.replaceSlides = function(data) {
    var div = document.createElement("div");
    div.innerHTML = data;
    document.querySelector("article").innerHTML = div.querySelector("article").innerHTML;
    setTimeout(function(){
        //$.deck('.slide');
    },50);
}

bespokejsExt.flipCurrentPage = function (renderedSelection) {

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
        bespokejsExt.flipPage($("img[src^='" + src + "']").get(0));
        return;
    }

    if ($(renderedSelection).is(".admonitionblock")) {
        bespokejsExt.findElement(".admonitionblock", $(renderedSelection).find(".content").text());
    }
    else if ($(renderedSelection).is(".exampleblock")) {
        bespokejsExt.findUniqueElement(".exampleblock", $(renderedSelection).text(), /^Example \d+\./, "Example 1.");
    }
    else if ($(renderedSelection).is(".listingblock")) {
        bespokejsExt.findElement(".listingblock", $(renderedSelection).text());
    }
    else if ($(renderedSelection).is(".literalblock")) {
        bespokejsExt.findElement(".literalblock", $(renderedSelection).text());
    }
    else if ($(renderedSelection).is(".openblock")) {
        bespokejsExt.findElement(".openblock", $(renderedSelection).text());
    }
    else if ($(renderedSelection).is(".sidebarblock")) {
        bespokejsExt.findElement(".sidebarblock", $(renderedSelection).text());
    }
    else if ($(renderedSelection).is(".quoteblock")) {
        bespokejsExt.findElement(".quoteblock > blockquote, .verseblock > .content", $(renderedSelection).find(".paragraph").text());
    }
    else if ($(renderedSelection).is(".paragraph")) {
        bespokejsExt.findElement(".paragraph, .quoteblock > blockquote", $($(renderedSelection).get(0)).text());
    }
    else if ($(renderedSelection).is(".ulist")) {
        bespokejsExt.findElement(".ulist:not(.checklist) > ul > li", $(renderedSelection).text());
    }
    else if ($(renderedSelection).is(".olist")) {
        bespokejsExt.findUniqueElement("table > tbody > tr", $(renderedSelection).text(), /^\d+ */, "");
    }
    else if ($(renderedSelection).is(".colist")) {
        bespokejsExt.findUniqueElement(".colist > table > tbody > tr", $(renderedSelection).text(), /^\d+ */, "");
    }
    else if ($(renderedSelection).is(".tableblock")) {
        bespokejsExt.findUniqueElement("table.table-block.apply.call", $(renderedSelection).text(), /^Table \d+\./, "Table 1.");
    }
    else {
        var header = false;

        $("*:header").each(function () {
            if ($(this).text().trim() == $(renderedSelection).text().trim()) {
                header = true;
                bespokejsExt.flipPage($(this).get(0));
                return false;
            }
        });

        if (!header) {
            var search = $("*:contains(" + $(renderedSelection).text().trim() + "):last");
            bespokejsExt.flipPage(search.get(0));
        }
    }
};

bespokejsExt.findElement = function (elements, content) {
    if (content.trim() == "")
        return;

    $(elements).each(function () {
        if (bespokejsExt.simplify($(this).text()) == bespokejsExt.simplify(content)) {
            bespokejsExt.flipPage($(this).get(0));
            return false;
        }
    });
};

bespokejsExt.findUniqueElement = function (elements, content, pattern, expected) {
    $(elements).each(function () {
        var element = $(this).text().trim().replace(pattern, expected);
        content = content.trim();
        if (bespokejsExt.simplify(element) == bespokejsExt.simplify(content)) {
            bespokejsExt.flipPage($(this).get(0));
            return false;
        }
    });
};

bespokejsExt.flipPage = function (element) {
    $("section[id]").each(function (index, section) {
        if ($(element).parents("section[id]").get(0) == section) {
            $.deck('go', index);
            return false;
        }
    });
};

bespokejsExt.simplify = function (text) {
    return text.replace(/( |\n|\r|\t|\")/g, "");
};
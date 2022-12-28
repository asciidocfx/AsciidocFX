function isEqualScript(fromEl, toEl) {
    if (fromEl.nodeName === "SCRIPT" && toEl.nodeName === "SCRIPT") {
        let $fromEl = $(fromEl);
        let $toEl = $(toEl);
        let fromSrc = $fromEl.attr("src");
        let toSrc = $toEl.attr("src");
        if (fromSrc && toSrc && (fromSrc === toSrc) && fromSrc.length > 1) {
            return true;
        } else {
            let fromHtml = $fromEl.html();
            let toHtml = $toEl.html();
            if (fromHtml && toHtml && (fromHtml === toHtml) && fromHtml.length > 1) {
                return true
            }
        }
    }
    return false;
}


function replaceScript(toEl, fromEl) {
    let script = document.createElement('script');
    [...toEl.attributes].forEach(attr => {
        script.setAttribute(attr.nodeName, attr.nodeValue)
    })

    let last = $("script#jquery");
    if (last.length && $(toEl).hasClass("ordered")) {
        console.log("Moving before:", fromEl, last);
        $(fromEl).insertBefore(last);
    }
    script.innerHTML = toEl.innerHTML;
    try {
        fromEl.replaceWith(script);
    } catch (e) {
        console.error(e);
    }
}

var morphdomOptions = {
    onNodeAdded: function (node) {
        if (node.nodeName === 'SCRIPT') {
            this.replaceScript(node, node);
        }
    },
    onBeforeNodeAdded: function (node) {
        if (node.nodeName === "SCRIPT") {
            let $node = $(node);
            $node.addClass("ordered");
        }
        return node;
    },
    onBeforeNodeDiscarded: function (node) {
        if (node.nodeName === "SCRIPT") {
            let src = node.getAttribute("src");
            if (src && src.includes("/afx/resource/js")) {
                return false;
            }
        } else if (node.nodeName === "LINK") {
            let src = node.getAttribute("href");
            if (src && (src.includes("/afx/resource/css") || src.includes("/afx/dynamic/css"))) {
                return false;
            }
        }
        if (node.nodeName !== "#text") {
            console.log("Discarded", node)
        }
        return true;
    }, onBeforeElUpdated: function (fromEl, toEl) {
        if (fromEl.nodeName === "SCRIPT" && toEl.nodeName === "SCRIPT") {
            console.log("onBeforeElUpdated:", fromEl, toEl)
            this.replaceScript(toEl, fromEl);
            return false;
        } else {
            if (fromEl.isEqualNode(toEl)) {
                console.log("Equal Node", fromEl, toEl)
                return false;
            }
        }
        return true;
    }
};
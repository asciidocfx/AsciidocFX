let afterFunctions = [];
const onAfterChanges = rafThrottle(() => {
    afterFunctions.forEach(f => f())
    afterFunctions = [];
});

function replaceScript(toEl, fromEl) {
    let script = document.createElement('script');
    [...toEl.attributes].forEach(attr => {
        script.setAttribute(attr.nodeName, attr.nodeValue)
    })

    let $putJs = $("script#put-js");
    if ($putJs.length) {
        console.log("Moving script", fromEl, "before", $putJs.get(0));
        $(fromEl).insertBefore($putJs);
    }
    script.innerHTML = toEl.innerHTML;
    afterFunctions.push(function () {
        try {
            fromEl.replaceWith(script);
        } catch (e) {
            console.error(e);
        }
    });
    onAfterChanges();
}

var addedScripts = [];
var morphdomOptions = {
    onNodeAdded: function (node) {
        if (node.nodeName === 'SCRIPT') {
            addedScripts.push(node);
            console.log("New script added", node);
            replaceScript(node, node);
        }
    },
    onElUpdated: function (el) {
        onAfterChanges();
    },
    onBeforeNodeAdded: function (node) {
        onAfterChanges();
        return node;
    },
    onBeforeNodeDiscarded: function (node) {
        onAfterChanges();
        if (node.nodeName === "SCRIPT") {
            let src = node.getAttribute("src");
            if (src && src.includes("/afx/resource/js")) {
                return false;
            }
            let index = addedScripts.findIndex(n => n.isEqualNode(node));
            if (index >= 0) {
                addedScripts.splice(index, 1);
                return false;
            }
        } else if (node.nodeName === "LINK") {
            let src = node.getAttribute("href");
            if (src && (src.includes("/afx/resource/css") || src.includes("/afx/dynamic/css"))) {
                return false;
            }
        }
        if (node.nodeName !== "#text") {
            console.log("Discarded node", node)
        }
        return true;
    }, onBeforeElUpdated: function (fromEl, toEl) {
        onAfterChanges();
        if (fromEl.nodeName === "SCRIPT" && toEl.nodeName === "SCRIPT") {
            console.log("Element", fromEl, "will updated to", toEl)
            replaceScript(toEl, fromEl);
            return false;
        } else {
            if (fromEl.isEqualNode(toEl)) {
                console.log("Skipping update (Equal node)", fromEl, toEl)
                return false;
            }
        }
        return true;
    }
};
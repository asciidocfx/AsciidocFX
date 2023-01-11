function runScroller(lineno) {
    //lineno++;
    var node = $("body").find(".data-line-" + lineno);
    if (node.length > 0) {
        node.get(0).scrollIntoView(true);
    }
    else {
        var latestNode = findLowerBound(lineno);
        if (latestNode.length > 0) {
            var nextNode = latestNode.next();
            if (nextNode.length > 0) {

                var latestClass = latestNode.attr('class');
                var nextClass = nextNode.attr('class');

                if (latestClass && nextClass) {

                    var minMatch = latestClass.match(/data-line-(\d+)/);
                    var maxMatch = nextClass.match(/data-line-(\d+)/);

                    if (maxMatch && minMatch) {
                        if (minMatch.length && maxMatch.length) {

                            var min = minMatch[1];
                            var max = maxMatch[1];

                            var div = (Math.abs(lineno - min) * 100) / Math.abs(max - min);

                            var minTop = latestNode.offset().top;
                            var maxTop = nextNode.offset().top;

                            var result = ((Math.abs(maxTop - minTop) * div) / 150) + Math.abs(minTop);

                            if (result != Infinity && result > 0)
                                window.scrollTo(0, result);
                        }
                    }
                }
            }
        }
    }
}

function findLowerBound(lineno) {
    var node;
    while (lineno >= 0) {
        lineno--;
        node = $("body").find(".data-line-" + lineno);
        if (node.length > 0)
            break;
    }
    return node || $("<div></div>");
}
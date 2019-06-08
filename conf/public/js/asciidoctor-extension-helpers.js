function latexmathWrap(content) {
    return "\\[\n" + content + "\n\\]";
}

function asciimathWrap(content) {
    return "\\$\n" + content + "\n\\$";
}

function parseStems(parent, content, nodename) {

    var finalContent = content;
    var name = nodename.replace(/_/g,"");

    var stemAttr = parent.$document().$attr('stem', "asciimath");

    console.log({stemAttr: stemAttr});

    stemAttr = stemAttr.replace(/_/g,"");

    if (stemAttr == "asciimath") {
        // default is asciimath
        finalContent = asciimathWrap(content);
    }

    if (stemAttr.indexOf("tex") != -1) { // latexmath
        finalContent = latexmathWrap(content);
    }

    if (stemAttr == "mathml") {
        // mathml: nothing to change
        finalContent = content;
    }

    if (name == "latexmath") {
        finalContent = latexmathWrap(content);
    }

    if (name == "asciimath") {
        finalContent = asciimathWrap(content);
    }

    if (name == "mathml") {
        // mathml: nothing to change
        finalContent = content;
    }

    return finalContent;
}
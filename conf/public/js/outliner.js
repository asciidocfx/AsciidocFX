function fillOutlines(doc) {
    //afx.clearOutline();

    postMessage(JSON.stringify({
        type:"afx",
        func:"clearOutline",
        parameters :[]
    }));

    var sections = doc.$sections();
    sections.forEach(function (section, i) {

        var level = section.$level();
        var title = section.title;
        var lineno = section.$lineno();
        var id = section.$id();
        var parentLineNo = lineno;

        //afx.fillOutline(null, level, title, lineno, id);

        postMessage(JSON.stringify({
            type:"afx",
            func:"fillOutline",
            parameters :[null, level, title, lineno, id]
        }));

        fillOutlinesSubSections(section);

    });
    //afx.finishOutline();
    postMessage(JSON.stringify({
        type:"afx",
        func:"finishOutline",
        parameters :[]
    }));
}

function fillOutlinesSubSections(section) {
    var sections = section.$sections();
    var parentLineNo = section.$lineno();
    sections.forEach(function (subsection, i) {

        var level = subsection.$level();
        var title = subsection.$title();
        var lineno = subsection.$lineno();
        var id = subsection.$id();

        //afx.fillOutline(parentLineNo, level, title, lineno, id);

        postMessage(JSON.stringify({
            type:"afx",
            func:"fillOutline",
            parameters :[parentLineNo, level, title, lineno, id]
        }));

        fillOutlinesSubSections(subsection);
    });
}
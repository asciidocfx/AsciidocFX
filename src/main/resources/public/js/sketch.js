/*
  This js file is charge of sketching/collecting some specific asciidoctor constructs such as delimited-blocks and single-line elements
  in order to disable unintended scroll movement of the pre-processed document when cursor is changed in a displayed area in the asciidoc editor.
  In other words, we eliminate such collected structures created in an asciidoc file to make better tracking the changes in the preview document. 

  See more about which elements we basically try to ignore:

    http://asciidoctor.org/docs/asciidoc-syntax-quick-reference/#more-delimited-blocks
    http://asciidoctor.org/docs/asciidoc-writers-guide/#building-blocks-in-asciidoc
    http://asciidoctor.org/docs/user-manual/#delimiter-lines
    http://asciidoctor.org/docs/asciidoc-syntax-quick-reference/#horizontal-rules-and-page-breaks
    http://asciidoctor.org/docs/asciidoc-syntax-quick-reference/#markdown-compatibility-asciidoctor-only
    
    Demo file shows all the ignored structures: https://dl.dropboxusercontent.com/u/15056258/demo.txt

    IMPORTANT: Even some regex functions provide typing more than 4 characters as asciidoctor does (e.g. matchListingBlock(text)) while generating blocks.
               *Don't do it* if you want to make use of the asciidoctor efficiently (do only sufficient condition) 
               as on the Asciidoctor website they mention that: "AsciiDoc allows delimited lines to be longer than 4 characters. *Don’t do it*"
*/

function matchListingBlock(text){
    return text.match(/^\-{4,}(\s)*$/m);
}

function matchMultiCommentBlock(text){
    return text.match(/^\/{4,}(\s)*$/m);
}

function matchExampleBlock(text){
    return text.match(/^\={4,}(\s)*$/m);
}

function matchLiteralBlock(text){
    return text.match(/^\.{4,}(\s)*$/m);
}

function matchOpenBlock(text){
    return text.match(/^\-{2}(\s)*$/m);
}

function matchPassBlock(text){
    return text.match(/^\+{4,}(\s)*$/m);
}

function matchQuoteBlock(text){
    return text.match(/^(\_{4,}|\"{2})(\s)*$/m);
}

function matchAbbQuote(text){
    return text.match(/^\".*\"$/m);
}

function matchSidebarBlock(text){
    return text.match(/^\*{4,}(\s)*$/m);
}

function matchTableBlock(text){
    return text.match(/^(\||\,|\:)\={3,}(\s)*$/m);
}

function matchLineComment(text){
    return text.match(/^\/{2}.*$/m);
}

function matchPageBreak(text){
    return text.match(/^\<{3,}(\s)*$/m);
}

function matchHorizontalRule(text){
    return text.match(/^\'{3,}(\s)*$/m);
}

function matchMarkdownListingBlock(text){
    return text.match(/^\`{3,}.*$/m);
}

function matchMarkdownQuote(text){
    return text.match(/^((\> .*)|(\>))$/m);
}

function matchMarkdownRules(text){
    return text.match(/^((\-{3})|(\- \- \-)|(\*{3})|(\* \* \*))(\s)*$/m);
}

sketch = {
    // contains the specified structures of asciidoctor declared by user in an asciidoc file.
    constructList: undefined,    
    active : false,
    setActive: function(active){
        this.active = active;
    },    
    collectSpecificConstructs: function (){
        var totalLength = editor.session.getLength();
        var listingPointer = -1,
            multiLineCommentPointer = -1, 
            examplePointer = -1, 
            literalPointer = -1,
            openPointer = -1,
            passPointer = -1,
            quotePointer = -1,
            slidebarPointer = -1,
            tablePointer = -1;

        if(totalLength == 0)
            return;

        this.constructList = {};

        for (var line = 0; line < totalLength; line++) {

            var text = editor.session.getLine(line);
            var textLength = text.length;

            if(matchListingBlock(text)){
                listingPointer = formMultiLineConstruct("Listing",listingPointer,line,textLength);
            }
            else if(matchMarkdownListingBlock(text)){
                listingPointer = formMultiLineConstruct("MarkdownCode",listingPointer,line,textLength);
            }
            else if(matchMultiCommentBlock(text)){
                multiLineCommentPointer = formMultiLineConstruct("MultiLineComment",multiLineCommentPointer,line,textLength);
            }
            else if(matchExampleBlock(text)){
                examplePointer = formMultiLineConstruct("Example",examplePointer,line,textLength);
            }
            else if(matchLiteralBlock(text)){
                literalPointer = formMultiLineConstruct("Literal",literalPointer,line,textLength);
            }
            else if(matchOpenBlock(text)){
                if(listingPointer > -1)
                    continue;
                openPointer = formMultiLineConstruct("Open",openPointer,line,textLength);
            }
            else if(matchPassBlock(text)){
                passPointer = formMultiLineConstruct("Pass",passPointer,line,textLength);
            }
            else if(matchQuoteBlock(text)){
                quotePointer = formMultiLineConstruct("Quote",quotePointer,line,textLength);
            }
            else if(matchSidebarBlock(text)){
                if(listingPointer > -1)
                    continue;
                slidebarPointer = formMultiLineConstruct("Sidebar",slidebarPointer,line,textLength);
            }
            else if(matchTableBlock(text)){
                tablePointer = formMultiLineConstruct("Table",tablePointer,line,textLength);
            }
            else if(matchAbbQuote(text)){
                formSingleLineConstruct("AbbreviatedQuote",line,textLength);
            }
            else if(matchLineComment(text)){
                formSingleLineConstruct("LineComment",line,textLength);
            }
            else if(matchPageBreak(text)){
                formSingleLineConstruct("PageBreak",line,textLength);
            }
            else if(matchHorizontalRule(text)){
                formSingleLineConstruct("HorizontalRule",line,textLength);
            }
            else if(matchMarkdownQuote(text)){
                if(listingPointer > -1)
                    continue;
                formSingleLineConstruct("MarkdownQuote",line,textLength);
            }
            else if(matchMarkdownRules(text)){
                if(listingPointer > -1)
                    continue;
                formSingleLineConstruct("MarkdownRules",line,textLength);
            }
        }

        function ADConstruct(type,startRow) {
            this.type = type;
            this.startRow = startRow;
            this.startColumn = 0;
            this.endRow = -1;
            this.endColumn = -1;
        }

        function formMultiLineConstruct(type,pointer,line,textLength) {

            if(pointer < 0){
                pointer = line;
                var name = type.concat("::",pointer);
                sketch.constructList[name] = new ADConstruct(name,pointer);
            }
            else{
                var name = type.concat("::",pointer);
                var construct = sketch.constructList[name];
                construct.endRow = line;
                construct.endColumn = textLength;
                sketch.constructList[name] = construct; 
                pointer = -1;
            }

            return pointer;
        }

        function formSingleLineConstruct(type,line,textLength) {

            var name = type.concat("::",line);
            var construct = new ADConstruct(name,line);
            construct.endRow = line;
            construct.endColumn = textLength; 
            sketch.constructList[name] = construct;

        }
    },
    refreshExcludedConstructList: function () {
        this.collectSpecificConstructs();
    },
    excludedPosition: function (row) {

        for(var iterate in this.constructList){
            var element = this.constructList[iterate];
            if(element.endRow != -1 && element.startRow <= row && row <= element.endRow) {
                return true;
            }
        }

        return false;
    }
};
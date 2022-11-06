package com.kodedu.service.extension.math;

import org.asciidoctor.ast.ContentNode;
import org.asciidoctor.extension.Reader;

import java.util.Map;
import java.util.Objects;

public interface MathStemPreProcessor {

    default String preProcessContent(ContentNode parent, Reader reader, Map<String, Object> attributes,
                                    String content, String name) {
        var finalContent = content;
        var stemAttr = (String) attributes.getOrDefault("stem", "no_stem");

        // :stem: -> ""
        if (Objects.isNull(stemAttr) || stemAttr.isBlank()) {
            stemAttr = "asciimath";
        }

        stemAttr = stemAttr.toLowerCase();

        if (stemAttr.equals("asciimath")) {
            // default is asciimath
            finalContent = asciimathWrap(content);
        }

        if (stemAttr.contains("tex")) { // latexmath
            finalContent = latexmathWrap(content);
        }

        if (stemAttr.equals("mathml")) {
            // mathml: nothing to change
            finalContent = content;
        }

        if (name.startsWith("latexmath")) {
            finalContent = latexmathWrap(content);
        }

        if (name.startsWith("asciimath")) {
            finalContent = asciimathWrap(content);
        }

        if (name.startsWith("mathml")) {
            // mathml: nothing to change
            finalContent = content;
        }

        return finalContent;
    }



    default String latexmathWrap(String content){
        return "\\[\n" + content + "\n\\]";
    }

    default String asciimathWrap(String content) {
        return "\\$\n" + content + "\n\\$";
    }
}

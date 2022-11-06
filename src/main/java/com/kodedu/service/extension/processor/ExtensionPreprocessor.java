package com.kodedu.service.extension.processor;

import org.asciidoctor.ast.Document;
import org.asciidoctor.extension.Preprocessor;
import org.asciidoctor.extension.PreprocessorReader;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Scope("prototype")
public class ExtensionPreprocessor extends Preprocessor {

    private final List<String> blockExtensions = List.of("stem", "asciimath", "latexmath", "mathml", "math", "plantuml",
            "uml", "ditaa", "graphviz", "tree", "mermaid", "chart");

    private final List<String> mathExtensions = List.of("stem", "asciimath", "latexmath", "mathml", "tex");

    @Override
    public void process(Document document, PreprocessorReader reader) {
        List<String> lines = reader.readLines();
        List<String> newLines = new ArrayList<>();

        for (String line : lines) {
            line = correctUmlBlocks(line);
            line = correctTargetInBlocks(line);
            line = correctInlineMathExtensions(line);
            line = correctBlockMathExtensions(line);
            newLines.add(line);
        }

        reader.restoreLines(newLines);
    }

    private String correctBlockMathExtensions(String line) {
        for (String mathExtension : mathExtensions) {
            if (line.startsWith("[" + mathExtension)) {
                line = line.replace("[" + mathExtension, "[" + mathExtension + "_");
            }
        }
        return line;
    }

    private String correctInlineMathExtensions(String line) {
        for (String mathExtension : mathExtensions) {
            String textStart = mathExtension + ":";
            String textInMiddle = " " + mathExtension + ":";
            if (line.startsWith(textStart) || line.contains(textInMiddle)) {
                line = line.replace(textStart, mathExtension + "_:");
            }
        }
        return line;
    }

    private String correctTargetInBlocks(String line) {
        for (String extension : blockExtensions) {
            if (line.startsWith("[" + extension + ",") && line.contains("file=\"")) {
                line = line.replace("file=\"", "target=\"");
                break;
            }
        }
        return line;
    }

    private String correctUmlBlocks(String line) {
        line = line.replaceAll("\\[uml,", "[plantuml,");
        line = line.replaceAll("\\[uml]", "[plantuml]");
        return line;
    }
}

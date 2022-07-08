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

    private final List<String> extensions = List.of("stem", "asciimath", "latexmath", "mathml", "math", "plantuml",
            "uml", "ditaa", "graphviz", "tree", "mermaid","chart");

    @Override
    public void process(Document document, PreprocessorReader reader) {
        List<String> lines = reader.readLines();
        List<String> newLines = new ArrayList<>();

        for (String line : lines) {
            line = line.replaceAll("\\[uml,", "[plantuml,");
            line = line.replaceAll("\\[uml]", "[plantuml]");
            for (String extension : extensions) {
                if (line.startsWith("[" + extension + ",") && line.contains("file=\"")) {
                    line = line.replace("file=\"", "target=\"");
                    break;
                }
            }
            newLines.add(line);
        }

        reader.restoreLines(newLines);
    }
}

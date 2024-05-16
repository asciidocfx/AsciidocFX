package com.kodedu.service.extension.processor;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
@Scope("prototype")
public class ExtensionPreprocessor {

    private static final List<String> blockExtensions = List.of("stem", "asciimath", "latexmath", "mathml", "math", "plantuml",
            "uml", "ditaa", "graphviz", "tree", "mermaid", "chart");

    private static final List<String> mathExtensions = List.of("stem", "asciimath", "latexmath", "mathml", "tex");

    public static String correctExtensionBlocks(String content) {

        if (Objects.isNull(content)) {
            return null;
        }

        String[] lines = content.split("\\R");
        return Arrays.stream(lines)
                .map(ExtensionPreprocessor::correctUmlBlocks)
                .map(ExtensionPreprocessor::correctTargetInBlocks)
                .map(ExtensionPreprocessor::correctInlineMathExtensions)
                .map(ExtensionPreprocessor::correctBlockMathExtensions)
                .collect(Collectors.joining("\n"));

    }

    private static String correctBlockMathExtensions(String line) {
        for (String mathExtension : mathExtensions) {
            if (line.startsWith("[" + mathExtension)) {
                line = line.replace("[" + mathExtension, "[" + mathExtension + "_");
            }
        }
        return line;
    }

    private static String correctInlineMathExtensions(String line) {
        for (String mathExtension : mathExtensions) {
            String textStart = mathExtension + ":";
            String textInMiddle = " " + mathExtension + ":";
            if (line.startsWith(textStart) || line.contains(textInMiddle)) {
                line = line.replace(textStart, mathExtension + "_:");
            }
        }
        return line;
    }

    private static String correctTargetInBlocks(String line) {
        for (String extension : blockExtensions) {
            if (line.startsWith("[" + extension + ",") && line.contains("file=\"")) {
                line = line.replace("file=\"", "target=\"");
                break;
            }
        }
        return line;
    }

    private static String correctUmlBlocks(String line) {
        line = line.replaceAll("\\[uml,", "[plantuml,");
        line = line.replaceAll("\\[uml]", "[plantuml]");
        return line;
    }
}

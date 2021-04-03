package com.kodedu.other;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class ContentFixes {

    private static final List<String> extensions = Arrays.asList("stem", "asciimath", "latexmath", "mathml",
            "math", "plantuml", "uml", "ditaa", "graphviz", "tree", "mermaid");

    // stem: to s_t_e_m:
    public static String encodeExtensionNames(String content) {
        for (String extension : extensions) {
            String replacement = extension.toUpperCase();
            content = getString(content, extension, replacement);
        }
        return content;
    }

    // s_t_e_m: to stem:
    // good to have if extensions in comment block
    public static String decodeExtensionNames(String content) {
        for (String extension : extensions) {
            String replacement = extension.toUpperCase();
            content = getString(content, replacement, extension);
        }
        return content;
    }



    public static String fixLineEnding(String rendered) {

        if (Objects.isNull(rendered)) {
            return null;
        }

        return rendered.replaceAll("\\R", "\n");
    }

    private static String getString(String content, String extension, String replacement) {
        content = content.replaceAll("\\[" + extension, "[" + replacement);
        content = content.replaceAll(extension + "::", replacement + "::");
        content = content.replaceAll(extension + ":\\[", replacement + ":[");
        return content;
    }
}

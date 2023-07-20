package com.kodedu.service.extension.processor;

import com.kodedu.other.RefProps;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class XrefHelper {

    private static final String xRefRegex = "<<(.*?)(?:,(.*?))?>>|xref::?([^\\[\\]]+)\\[(.*?)\\]";
    private static final Pattern xRefPattern = Pattern.compile(xRefRegex);

    public static Matcher matcher(String content) {
        return xRefPattern.matcher(content);
    }

    public static int getLineNumber(String input, int index) {
        String[] lines = input.split("\\r?\\n");
        int lineNumber = 1;
        int currentIndex = 0;

        for (String line : lines) {
            currentIndex += line.length() + 1; // +1 to account for newline character
            if (currentIndex > index) {
                break;
            }
            lineNumber++;
        }

        return lineNumber;
    }

    public static Map<String, List<RefProps>> parseXrefs(String docfile, String content) {

        Matcher matcher = XrefHelper.matcher(content);

        /*
        <<notice>>                                  1 > notice
        <<notice,abc>>                              1 > notice, 2 > abc
        xref:link-macro-attributes[]                3 > link-macro-attributes
        xref:document-b.adoc#section-b[Section B]   3 > document-b.adoc#section-b, 4 > Section B
         */
        Map<String, List<RefProps>> xrefMap = matcher.results().map(m -> {
            String firstTerm = m.group(1);
            String secondTerm = m.group(2);
            String xrefLabel = m.group(3);
            String xrefAttributes = m.group(4);

            String xRefId = Objects.requireNonNullElse(firstTerm, xrefLabel);

            int startIndex = matcher.start();
            int lineNumber = getLineNumber(content, startIndex);

            return new RefProps(docfile, lineNumber, xRefId, true);
        }).distinct().collect(Collectors.groupingBy(x -> x.file()));
        return xrefMap;
    }
}

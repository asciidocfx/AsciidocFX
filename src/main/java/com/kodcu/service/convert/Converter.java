package com.kodcu.service.convert;

import com.kodcu.other.IOHelper;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by usta on 20.03.2015.
 */
public class Converter {

    private final Pattern ascIncludeRegex = Pattern.compile("(?<=include::)(?<path>.*?)(?=\\[(.*?)\\])");
    private final Pattern mdIncludeRegex = Pattern.compile("\\[.*?\\]\\((?<path>.*\\.(md|markdown|asc|adoc|asciidoc|ad|txt))\\)");

    protected void traverseLines(List<String> lines, StringBuffer buffer, Path rootPath) {

        for (String line : lines) {

            Matcher ascMatcher = ascIncludeRegex.matcher(line);
            Matcher markdownMatcher = mdIncludeRegex.matcher(line);

            if (ascMatcher.find()) {
                String chapterPath = ascMatcher.group("path");
                Path chapterFile = rootPath.resolve(chapterPath);
                String chapterContent = IOHelper.readFile(chapterFile);
                traverseLines(Arrays.asList(chapterContent.split("\\r?\\n")), buffer, chapterFile.getParent());
            } else if (markdownMatcher.find()) {
                String chapterPath = markdownMatcher.group("path");
                Path chapterFile = rootPath.resolve(chapterPath);
                String chapterContent = IOHelper.readFile(chapterFile);
                traverseLines(Arrays.asList(chapterContent.split("\\r?\\n")), buffer, chapterFile.getParent());
            } else
                traverseLine(line, buffer);
        }
    }

    protected void traverseLine(String line, StringBuffer buffer) {
        buffer.append(line + "\n");
    }
}

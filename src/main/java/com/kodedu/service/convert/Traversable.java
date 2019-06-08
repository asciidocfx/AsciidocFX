package com.kodedu.service.convert;

import com.kodedu.helper.IOHelper;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by usta on 20.03.2015.
 */
public interface Traversable {

    public Pattern ascIncludeRegex = Pattern.compile("(?<=include::)(?<path>.*?)(?=\\[(.*?)\\])");
    public Pattern mdIncludeRegex = Pattern.compile("\\[.*?\\]\\((?<path>.*\\.(md|markdown|asc|adoc|asciidoc|ad|txt))\\)");

    public default void traverseLines(List<String> lines, StringBuffer buffer, Path rootPath) {

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

    public default void traverseLine(String line, StringBuffer buffer) {
        buffer.append(line + "\n");
    }
}

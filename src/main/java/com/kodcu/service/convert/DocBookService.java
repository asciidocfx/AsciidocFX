package com.kodcu.service.convert;

import com.kodcu.other.Current;
import com.kodcu.other.IOHelper;
import org.joox.Match;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.xml.sax.InputSource;

import java.io.StringReader;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.joox.JOOX.$;

/**
 * Created by usta on 19.07.2014.
 */
@Component
public class DocBookService {

    private final Pattern ascIncludeRegex = Pattern.compile("(?<=include::)(?<path>.*?)(?=\\[(.*?)\\])");
    private final Pattern mdIncludeRegex = Pattern.compile("\\[.*?\\]\\((?<path>.*\\.(md|markdown|asc|adoc|asciidoc|ad|txt))\\)");

    private final RenderService docConverter;
    private final Current current;

    @Autowired
    public DocBookService(final RenderService docConverter, final Current current) {
        this.docConverter = docConverter;
        this.current = current;
    }

    private void traverseLines(List<String> lines, StringBuffer buffer, Path rootPath) {

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

    private void traverseLine(String line, StringBuffer buffer) {
        if (line.matches("^=+ +.*:.*"))
            line = line.replace(":", "00HEADER00COLON00");
        buffer.append(line + "\n");
    }

    public void generateDocbook(Consumer<String> step) {

        StringBuffer outputBuffer = new StringBuffer();

        Path currentTabPath = current.currentPath().get();
        Path currentTabPathDir = currentTabPath.getParent();

        StringBuffer stringBuffer = new StringBuffer();

        traverseLines(Arrays.asList(current.currentEditorValue().split("\\r?\\n")), stringBuffer, currentTabPathDir);

        String text = stringBuffer.toString();

        docConverter.convertDocbook(text, true, docBookHeaderContent -> {
            docBookHeaderContent = docBookHeaderContent.replace("<?xml version=\"1.0\" encoding=\"UTF-8\"?>", "<?xml version=\"1.1\" encoding=\"UTF-8\"?>");

            StringReader bookReader = new StringReader(docBookHeaderContent);
            Match rootDocument = IOHelper.$(new InputSource(bookReader));
            bookReader.close();

//            // makes figure centering
            rootDocument.find("figure").find("imagedata").attr("align", "center");

            // remove callout's duplicated refs and pick last
            rootDocument.find("callout").forEach(elem -> {
                String arearefs = $(elem).attr("arearefs");
                String[] cos = arearefs.split(" ");
                if (cos.length > 1)
                    $(elem).attr("arearefs", cos[cos.length - 1]);
            });

            outputBuffer.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
            outputBuffer.append("\n");
            outputBuffer.append("<?asciidoc-toc?>");
            outputBuffer.append("\n");
            outputBuffer.append("<?asciidoc-numbered?>");
            outputBuffer.append("\n");
            outputBuffer.append(rootDocument.content());
            String result = outputBuffer.toString();
            result = result.replace("00HEADER00COLON00", ":");
            step.accept(result);
        });
    }

    public void generateDocbookArticle(Consumer<String> step) {

        StringBuilder outputBuffer = new StringBuilder();
        Path currentTabPath = current.currentPath().get();
        Path currentTabPathDir = currentTabPath.getParent();

        StringBuffer stringBuffer = new StringBuffer();

//        traverseLines(Arrays.asList(current.currentEditorValue().split("\\r?\\n")), stringBuffer, currentTabPathDir);
//        String input = stringBuffer.toString();

        docConverter.convertDocbookArticle(current.currentEditorValue(), docbook -> {

            docbook = docbook.replace("<?xml version=\"1.0\" encoding=\"UTF-8\"?>", "<?xml version=\"1.1\" encoding=\"UTF-8\"?>");
            StringReader bookReader = new StringReader(docbook);
            Match rootDocument = IOHelper.$(new InputSource(bookReader));
            bookReader.close();

            // changes formalpara to figure bug fix
            rootDocument.find("imageobject").parents("formalpara").each((context) -> {
                $(context).rename("figure");
            });

            // makes figure centering
            rootDocument.find("figure").find("imagedata").attr("align", "center");

            // remove callout's duplicated refs and pick last
            rootDocument.find("callout").forEach(elem -> {
                String arearefs = $(elem).attr("arearefs");
                String[] cos = arearefs.split(" ");
                if (cos.length > 1)
                    $(elem).attr("arearefs", cos[cos.length - 1]);
            });

            outputBuffer.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
            outputBuffer.append("\n");
            outputBuffer.append("<?asciidoc-toc?>\n");
            outputBuffer.append("<?asciidoc-numbered?>\n");
            outputBuffer.append(rootDocument.content());
            String result = outputBuffer.toString();
            result = result.replace("00HEADER00COLON00", ":");
            step.accept(result);
        });
    }
}

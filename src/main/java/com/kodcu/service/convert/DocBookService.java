package com.kodcu.service.convert;

import com.kodcu.other.Current;
import com.kodcu.other.IOHelper;
import com.kodcu.service.ThreadService;
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
public class DocBookService extends Converter {

    private final RenderService docConverter;
    private final Current current;
    private final ThreadService threadService;

    @Autowired
    public DocBookService(final RenderService docConverter, final Current current, ThreadService threadService) {
        this.docConverter = docConverter;
        this.current = current;
        this.threadService = threadService;
    }

    @Override
    protected void traverseLine(String line, StringBuffer buffer) {
        if (line.matches("^=+ +.*:.*")) // Replace : in headers for a asciidoctor bug
            line = line.replace(":", "00HEADER00COLON00");
        super.traverseLine(line, buffer);
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

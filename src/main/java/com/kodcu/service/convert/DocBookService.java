package com.kodcu.service.convert;

import com.kodcu.other.Current;
import com.kodcu.other.IOHelper;
import com.kodcu.service.MarkdownService;
import org.joox.Match;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger logger = LoggerFactory.getLogger(DocBookService.class);

    private Pattern ascIncludeRegex = Pattern.compile("(?<=include::)(?<path>.*?)(?=\\[(.*?)\\])");
    private Pattern mdIncludeRegex = Pattern.compile("\\[.*?\\]\\((?<path>.*?)\\)");

    @Autowired
    private RenderService docConverter;

    @Autowired
    private Current current;
    @Autowired
    private MarkdownService markdownService;

    public void generateDocbook(Consumer<String> step) {

        StringBuilder builder = new StringBuilder();

            Path currentTabPath = current.currentPath().get();
            Path currentTabPathDir = currentTabPath.getParent();
            String tabText = current.getCurrentTabText().replace("*", "").trim();

            List<String> bookAscLines = Arrays.asList(current.currentEditorValue().split("\\r?\\n"));
            for (int i = 0; i < bookAscLines.size(); i++) {
                String bookAscLine = bookAscLines.get(i);

                Matcher matcher = ascIncludeRegex.matcher(bookAscLine);

                if (matcher.find()) {
                    String chapterPath = matcher.group("path");
                    String chapterContent = IOHelper.readFile(currentTabPathDir.resolve(chapterPath));
                    bookAscLines.set(i, "\n\n" + chapterContent + "\n\n");
                }

                if(tabText.contains("SUMMARY")){
                    matcher = mdIncludeRegex.matcher(bookAscLine);

                    if (matcher.find()) {
                        String chapterPath = matcher.group("path");
                        String chapterContent = IOHelper.readFile(currentTabPathDir.resolve(chapterPath));
                        bookAscLines.set(i, "\n\n" + chapterContent + "\n\n");
                    }
                }

            }

            StringBuffer allAscContent = new StringBuffer();
            bookAscLines.forEach(content -> {
                allAscContent.append(content);
                allAscContent.append("\n");
            });

            String text = allAscContent.toString();

            docConverter.convertDocbook(text, true,docBookHeaderContent->{
                docBookHeaderContent = docBookHeaderContent.replace("<?xml version=\"1.0\" encoding=\"UTF-8\"?>","<?xml version=\"1.1\" encoding=\"UTF-8\"?>");

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

                builder.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
                builder.append("\n");
                builder.append("<?asciidoc-toc?>");
                builder.append("\n");
                builder.append("<?asciidoc-numbered?>");
                builder.append("\n");
                builder.append(rootDocument.content());
                step.accept(builder.toString());
            });
    }

    public void generateDocbookArticle(Consumer<String> step) {

            docConverter.convertDocbookArticle(docbook -> {
                StringBuilder builder = new StringBuilder();
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


                builder.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
                builder.append("\n");
                builder.append("<?asciidoc-toc?>\n");
                builder.append("<?asciidoc-numbered?>\n");
                builder.append(rootDocument.content());
                step.accept(builder.toString());
            });
    }
}

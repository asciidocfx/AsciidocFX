package com.kodcu.service.convert;

import com.kodcu.controller.ApplicationController;
import com.kodcu.other.Current;
import com.kodcu.other.IOHelper;
import com.kodcu.service.PathResolverService;
import com.kodcu.service.ui.IndikatorService;
import javafx.application.Platform;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;
import static org.joox.JOOX.$;
import static org.joox.JOOX.builder;

/**
 * Created by usta on 19.07.2014.
 */
@Component
public class DocBookService {

    private static final Logger logger = LoggerFactory.getLogger(DocBookService.class);

    private Pattern compiledRegex = Pattern.compile("(?<=include::)(.*?)(?=\\[(.*?)\\])");

    @Autowired
    private RenderService docConverter;

    @Autowired
    private PathResolverService bookPathResolver;

    @Autowired
    private ApplicationController asciiDocController;

    @Autowired
    private Current current;
//
//    @Autowired
//    private IndikatorService indikatorService;

    public String generateDocbook() {

        StringBuilder builder = new StringBuilder();

        try {


            Path currentTabPath = current.currentPath().get();
            Path currentTabPathDir = currentTabPath.getParent();
            String tabText = current.getCurrentTabText().replace("*", "").trim();

            List<String> bookAscLines = Arrays.asList(current.currentEditorValue().split("\\r?\\n"));
            for (int i = 0; i < bookAscLines.size(); i++) {
                String bookAscLine = bookAscLines.get(i);

                Matcher matcher = compiledRegex.matcher(bookAscLine);

                if (matcher.find()) {
                    String chapterPath = matcher.group();
                    String chapterContent = IOHelper.readFile(currentTabPathDir.resolve(chapterPath));
                    bookAscLines.set(i, "\n\n" + chapterContent + "\n\n");
                }

            }

            StringBuffer allAscContent = new StringBuffer();
            bookAscLines.forEach(content -> {
                allAscContent.append(content);
                allAscContent.append("\n");
            });

            String docBookHeaderContent = docConverter.convertDocbook(allAscContent.toString(), true);

            StringReader bookReader = new StringReader(docBookHeaderContent);
            Match rootDocument = $(new InputSource(bookReader));
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

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
//            indikatorService.hideIndikator();
        }

        return builder.toString();
    }

    public String generateDocbookArticle() {

        StringBuilder builder = new StringBuilder();
        try {

            String docbook = docConverter.convertDocbookArticle();

            StringReader bookReader = new StringReader(docbook);
            Match rootDocument = $(new InputSource(bookReader));
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

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }


        return builder.toString();
    }
}

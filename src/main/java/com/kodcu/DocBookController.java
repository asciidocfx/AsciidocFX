package com.kodcu;

import javafx.scene.web.WebEngine;
import org.joox.Match;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.joox.JOOX.$;

/**
 * Created by usta on 19.07.2014.
 */
@Component
public class DocBookController {

    @Autowired
    private AsciiDocConverter docConverter;

    public String generateDocbook(WebEngine webEngine, Path path) throws IOException, SAXException {

        Path bookAsc = path.resolve("book.asc");

        if(!Files.exists(bookAsc))
            return "There is no book.asc file..";

        String bookRoot = docConverter.asciidocToDocbook(webEngine, IOHelper.readFile(bookAsc), true);

        Match rootDocument ;

        try(StringReader bookReader = new StringReader(bookRoot);){
            rootDocument = $(new InputSource(bookReader));
        }

        List<Element> simparas = rootDocument.find("simpara").get();

        for (Element elem : simparas) {
            Path chapterPath = path.resolve($(elem).find("link").attr("href"));
            String chapterPart = docConverter.asciidocToDocbook(webEngine, IOHelper.readFile(path.resolve(chapterPath)), true);

            try(StringReader chapterReader = new StringReader(chapterPart);){
                Match chapterPartial = $(new InputSource(chapterReader)).find("chapter");
                $(elem).replaceWith(chapterPartial);
            }
        };


        StringBuilder builder=new StringBuilder();
        builder.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        builder.append("\n");
        builder.append("<?asciidoc-toc?>");
        builder.append("\n");
        builder.append("<?asciidoc-numbered?>");
        builder.append("\n");
        builder.append(rootDocument.content());


        return builder.toString();
    }
}

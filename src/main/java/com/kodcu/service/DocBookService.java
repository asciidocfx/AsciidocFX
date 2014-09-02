package com.kodcu.service;

import com.kodcu.controller.AsciiDocController;
import com.kodcu.other.IOHelper;
import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.scene.web.WebEngine;
import javafx.util.Duration;
import org.joox.Match;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;
import static org.joox.JOOX.$;

/**
 * Created by usta on 19.07.2014.
 */
@Component
public class DocBookService {

    private static final Logger logger = LoggerFactory.getLogger(DocBookService.class);

    @Autowired
    private AsciiDoctorRenderService docConverter;

    @Autowired
    private AsciiDocController asciiDocController;

    @Autowired
    private IndikatorService indikatorService;


    public void generateDocbook(WebEngine webEngine, Path currentPath, boolean showIndicator) {
        try {
            Path bookAsc = currentPath.resolve("book.asc");

            if (!Files.exists(bookAsc)) {
                IOHelper.writeToFile(currentPath.resolve("book.xml"), "There is no book.asc file..", CREATE, TRUNCATE_EXISTING);
                return;
            }

            if (showIndicator)
                indikatorService.startCycle();


            String bookRoot = docConverter.asciidocToDocbook(webEngine, IOHelper.readFile(bookAsc), true);

            Match rootDocument;

            try (StringReader bookReader = new StringReader(bookRoot);) {
                rootDocument = $(new InputSource(bookReader));
            }

            List<Element> simparas = rootDocument.find("simpara").get();

            for (Element elem : simparas) {
                Match link = $(elem).find("link");
                if(link.isEmpty())
                    continue; // chapter link değilse
                Path chapterPath = currentPath.resolve(link.attr("href"));
                String chapterPart = docConverter.asciidocToDocbook(webEngine, IOHelper.readFile(currentPath.resolve(chapterPath)), true);

                try (StringReader chapterReader = new StringReader(chapterPart);) {
                    Match chapterPartial = $(new InputSource(chapterReader)).find("chapter");
                    chapterPartial
                            .find("imagedata");
//                            .attr("width", "100%")
//                            .attr("contentwidth", "100%")
//                            .attr("scalefit", "1");

                    /// formparam to figure fix
                    chapterPartial.find("imageobject").parents("formalpara").each((context)->{
                        $(context).rename("figure");
                    });

                    ///
                    $(elem).replaceWith(chapterPartial);
                }
            };


            List<String> chapterExtractList=Arrays.asList("dedication","preface","appendix","bibliography","glossary","colophon","index");
            chapterExtractList.forEach(wrapper->{
                rootDocument.find(wrapper).after(rootDocument.find(wrapper).find("chapter"));
            });

            StringBuilder builder = new StringBuilder();
            builder.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
            builder.append("\n");
            builder.append("<?asciidoc-toc?>");
            builder.append("\n");
            builder.append("<?asciidoc-numbered?>");
            builder.append("\n");
            builder.append(rootDocument.content());

            IOHelper.writeToFile(currentPath.resolve("book.xml"), builder.toString(), CREATE, TRUNCATE_EXISTING);

//            if (showIndicator)
//                asciiDocController.getHostServices().showDocument(currentPath.resolve("book.xml").toUri().toString());

            if (showIndicator)
            {
                indikatorService.completeCycle();
                asciiDocController.setLastConvertedFile(Optional.of(currentPath.resolve("book.xml")));
            }
        } catch (Exception ex) {
            logger.error(ex.getMessage(),ex);
        } finally {
            indikatorService.hideIndikator();
        }
    }
}

package com.kodcu.service;

import com.kodcu.controller.AsciiDocController;
import com.kodcu.other.IOHelper;
import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.scene.web.WebEngine;
import javafx.util.Duration;
import org.joox.Match;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;
import static org.joox.JOOX.$;

/**
 * Created by usta on 19.07.2014.
 */
@Component
public class DocBookService {

    @Autowired
    private AsciiDoctorRenderService docConverter;

    @Autowired
    private AsciiDocController asciiDocController;


    public void generateDocbook(WebEngine webEngine, Path currentPath, boolean showIndicator) {
        try {
            Path bookAsc = currentPath.resolve("book.asc");

            if (!Files.exists(bookAsc)) {
                IOHelper.writeToFile(currentPath.resolve("book.xml"), "There is no book.asc file..", CREATE, TRUNCATE_EXISTING);
                return;
            }

            if (showIndicator)
                Platform.runLater(() -> {
                    asciiDocController.getIndikator().setVisible(true);
                    FadeTransition fadeIn = new FadeTransition(Duration.seconds(4));
                    fadeIn.setNode(asciiDocController.getIndikator());
                    fadeIn.setFromValue(0.0);
                    fadeIn.setToValue(1.0);
                    fadeIn.setCycleCount(1);
                    fadeIn.setAutoReverse(false);
                    fadeIn.playFromStart();
                });


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

            if (showIndicator)
                asciiDocController.getHostServices().showDocument(currentPath.resolve("book.xml").toUri().toString());

            if (showIndicator)
                Platform.runLater(() -> {

                    asciiDocController.getIndikator().setProgress(1);
                    FadeTransition fadeOut = new FadeTransition(Duration.seconds(4));
                    fadeOut.setNode(asciiDocController.getIndikator());
                    fadeOut.setFromValue(1.0);
                    fadeOut.setToValue(0.0);
                    fadeOut.setCycleCount(1);
                    fadeOut.setAutoReverse(false);
                    fadeOut.playFromStart();

                });
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            Platform.runLater(() -> {
                asciiDocController.getIndikator().setProgress(-1);
                asciiDocController.getIndikator().setVisible(false);
            });
        }
    }
}

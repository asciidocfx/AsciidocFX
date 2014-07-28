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
import java.util.List;

import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;
import static org.joox.JOOX.$;

/**
 * Created by usta on 19.07.2014.
 */
@Component
public class HtmlBookService {

    @Autowired
    private AsciiDoctorRenderService docConverter;

    @Autowired
    private AsciiDocController asciiDocController;

    public void generateHtml(WebEngine webEngine, Path currentPath, boolean showIndicator) {
        try {
            Path bookAsc = currentPath.resolve("book.asc");

            if (!Files.exists(bookAsc)) {
                IOHelper.writeToFile(currentPath.resolve("book.html"), "There is no book.asc file..", CREATE, TRUNCATE_EXISTING);
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

            StringBuffer allAscPart = new StringBuffer();

            String rootAsc = IOHelper.readFile(bookAsc);
            allAscPart.append(rootAsc);

            String bookRoot = docConverter.asciidocToDocbook(webEngine, rootAsc, true);

            Match rootDocument;

            try (StringReader bookReader = new StringReader(bookRoot);) {
                rootDocument = $(new InputSource(bookReader));
            }

            List<Element> simparas = rootDocument.find("simpara").get();

            for (Element elem : simparas) {
                Path chapterPath = currentPath.resolve($(elem).find("link").attr("href"));
                String chapterPart = IOHelper.readFile(currentPath.resolve(chapterPath));
                allAscPart.append("\n");
                allAscPart.append(chapterPart);

            };

            String allHtml = docConverter.asciidocToHtml(webEngine, allAscPart.toString());

            IOHelper.writeToFile(currentPath.resolve("book.html"), allHtml, CREATE, TRUNCATE_EXISTING);

            if (showIndicator)
                asciiDocController.getHostServices().showDocument(currentPath.resolve("book.html").toUri().toString());

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

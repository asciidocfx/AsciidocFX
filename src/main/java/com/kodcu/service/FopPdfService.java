package com.kodcu.service;

import com.kodcu.controller.AsciiDocController;
import com.kodcu.other.InputHandlerExtended;
import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.util.Duration;
import org.apache.fop.apps.FOUserAgent;
import org.apache.fop.apps.FopFactory;
import org.apache.fop.cli.InputHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.FileOutputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Vector;

/**
 * Created by usta on 26.07.2014.
 */
@Component
public class FopPdfService {

    @Autowired
    private AsciiDocController asciiDocController;

    Logger logger = LoggerFactory.getLogger(FopPdfService.class);

    public void generate(Path currentPath, Path appDir) {

        Path bookXml = currentPath.resolve("book.xml");

        if (Files.notExists(bookXml)) {
            return;
        }

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

        try {

            Vector params = new Vector();
            params.add("body.font.family");
            params.add("Arial");
            params.add("title.font.family");
            params.add("Arial");
            params.add("highlight.xslthl.config");
            params.add(appDir.resolve("doco/docbook-config/xslthl-config.xml").toUri().toString());
            params.add("admon.graphics.path");
            params.add(appDir.resolve("doco/docbook/images/").toUri().toString());
            params.add("callout.graphics.path");
            params.add(appDir.resolve("doco/docbook/images/callouts/").toUri().toString());

            InputHandler handler = new InputHandler(bookXml.toFile(), appDir.resolve("doco/docbook-config/fo-pdf.xsl").toFile(), params);

            FopFactory fopFactory = FopFactory.newInstance();

            fopFactory.setUserConfig(appDir.resolve("doco/docbook-config/fop.xconf").toFile());
            try (FileOutputStream outputStream = new FileOutputStream(currentPath.resolve("book.pdf").toFile());) {
                FOUserAgent userAgent = new FOUserAgent(fopFactory);
                handler.renderTo(userAgent, "application/pdf", outputStream);
            } finally {

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

                asciiDocController.getHostServices().showDocument(currentPath.resolve("book.pdf").toUri().toString());
            }

        } catch (Exception ex) {
            logger.debug(ex.getMessage(), ex);
            ex.printStackTrace();
        } finally {
            Platform.runLater(() -> {
                asciiDocController.getIndikator().setProgress(-1);
                asciiDocController.getIndikator().setVisible(false);
            });
        }
    }
}

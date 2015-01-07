package com.kodcu.service.convert;

import com.kodcu.controller.ApplicationController;
import com.kodcu.other.Current;
import com.kodcu.service.ui.IndikatorService;
import javafx.application.Platform;
import org.apache.fop.apps.FOUserAgent;
import org.apache.fop.apps.FopFactory;
import org.apache.fop.cli.InputHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.FileOutputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Vector;

import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;
import static java.nio.file.StandardOpenOption.WRITE;

/**
 * Created by usta on 26.07.2014.
 */
@Component
public class FopPdfService {

    @Autowired
    private ApplicationController asciiDocController;

    @Autowired
    private IndikatorService indikatorService;

    @Autowired
    private Current current;

    private static final Logger logger = LoggerFactory.getLogger(FopPdfService.class);

    public void generateBook(Path currentPath, Path configPath) {

        Path bookXml = currentPath.resolve("book.xml");

        if (Files.notExists(bookXml)) {
            return;
        }

        indikatorService.startCycle();

        try {

            Vector params = new Vector();
            params.add("body.font.family");
            params.add("Arial");
            params.add("title.font.family");
            params.add("Arial");
            params.add("highlight.xslthl.config");
            params.add(configPath.resolve("docbook-config/xslthl-config.xml").toUri().toASCIIString());
            params.add("admon.graphics.path");
            params.add(configPath.resolve("docbook/images/").toUri().toASCIIString());
            params.add("callout.graphics.path");
            params.add(configPath.resolve("docbook/images/callouts/").toUri().toASCIIString());

            InputHandler handler = new InputHandler(bookXml.toFile(), configPath.resolve("docbook-config/fo-pdf.xsl").toFile(), params);

            FopFactory fopFactory = FopFactory.newInstance();

            fopFactory.setUserConfig(configPath.resolve("docbook-config/fop.xconf").toUri().toASCIIString());
            try (FileOutputStream outputStream = new FileOutputStream(currentPath.resolve("book.pdf").toFile());) {
                FOUserAgent userAgent = new FOUserAgent(fopFactory);
                handler.renderTo(userAgent, "application/pdf", outputStream);
            } finally {

                indikatorService.completeCycle();

                Platform.runLater(()->{
                    asciiDocController.getRecentFiles().add(0, currentPath.resolve("book.pdf").toString());
                });

            }

        } catch (Exception e) {
            logger.error(e.getMessage(),e);
        } finally {
            indikatorService.hideIndikator();
        }
    }


    public void generateArticle(Path currentPath, Path configPath,String docBook) {

        indikatorService.startCycle();

        try {

            Vector params = new Vector();
            params.add("body.font.family");
            params.add("Arial");
            params.add("title.font.family");
            params.add("Arial");
            params.add("highlight.xslthl.config");
            params.add(configPath.resolve("docbook-config/xslthl-config.xml").toUri().toASCIIString());
            params.add("admon.graphics.path");
            params.add(configPath.resolve("docbook/images/").toUri().toASCIIString());
            params.add("callout.graphics.path");
            params.add(configPath.resolve("docbook/images/callouts/").toUri().toASCIIString());

            String tabText = current.getCurrentTabText().replace("*", "").trim();
            String tabTextDocbook = tabText.concat(".xml");
            Path articlePath = currentPath.resolve(tabTextDocbook);
            Files.write(articlePath, docBook.getBytes(Charset.forName("UTF-8")), CREATE, TRUNCATE_EXISTING, WRITE);
            InputHandler handler = new InputHandler(articlePath.toFile(), configPath.resolve("docbook-config/fo-pdf.xsl").toFile(), params);

            FopFactory fopFactory = FopFactory.newInstance();

            fopFactory.setUserConfig(configPath.resolve("docbook-config/fop.xconf").toUri().toASCIIString());
            try (FileOutputStream outputStream = new FileOutputStream(currentPath.resolve(tabText.concat(".pdf")).toFile());) {
                FOUserAgent userAgent = new FOUserAgent(fopFactory);
                handler.renderTo(userAgent, "application/pdf", outputStream);
            } finally {

                indikatorService.completeCycle();

                Platform.runLater(()->{

                    String o = currentPath.resolve(tabText.concat(".pdf")).toString();
                    asciiDocController.getRecentFiles().remove(o);
                    asciiDocController.getRecentFiles().add(0, o);
                });

            }

        } catch (Exception e) {
            logger.error(e.getMessage(),e);
        } finally {
            indikatorService.hideIndikator();
        }
    }
}

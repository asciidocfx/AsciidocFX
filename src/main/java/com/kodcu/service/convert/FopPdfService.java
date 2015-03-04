package com.kodcu.service.convert;

import com.kodcu.controller.ApplicationController;
import com.kodcu.other.Current;
import com.kodcu.other.IOHelper;
import com.kodcu.service.DirectoryService;
import com.kodcu.service.ThreadService;
import com.kodcu.service.ui.IndikatorService;
import javafx.stage.FileChooser;
import org.apache.fop.apps.FOUserAgent;
import org.apache.fop.apps.FopFactory;
import org.apache.fop.cli.InputHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Vector;

import static java.nio.file.StandardOpenOption.*;

/**
 * Created by usta on 26.07.2014.
 */
@Component
public class FopPdfService {

    @Autowired
    private ApplicationController asciiDocController;

    @Autowired
    private DocBookService docBookService;

    @Autowired
    private IndikatorService indikatorService;

    @Autowired
    private ThreadService threadService;

    @Autowired
    private DirectoryService directoryService;

    @Autowired
    private Current current;

    private Path pdfPath;

    private static final Logger logger = LoggerFactory.getLogger(FopPdfService.class);

    private void produce(boolean askPath, InputHandler handler, FopFactory fopFactory, Path docbookTempfile) {
        Path currentTabPath = current.currentPath().get();
        Path currentTabPathDir = currentTabPath.getParent();
        String tabText = current.getCurrentTabText().replace("*", "").trim();
        threadService.runActionLater(() -> {
            if (askPath) {
                FileChooser fileChooser = directoryService.newFileChooser("Save PDF file");
                fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("PDF", "*.pdf"));
                pdfPath = fileChooser.showSaveDialog(null).toPath();
            } else
                pdfPath = currentTabPathDir.resolve(tabText + ".pdf");

            threadService.runTaskLater(() -> {
                indikatorService.startCycle();
                try (FileOutputStream outputStream = new FileOutputStream(pdfPath.toFile());) {
                    FOUserAgent userAgent = new FOUserAgent(fopFactory);
                    handler.renderTo(userAgent, "application/pdf", outputStream);
                    Files.deleteIfExists(docbookTempfile);
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                } finally {

                    indikatorService.completeCycle();
                    indikatorService.hideIndikator();

                    threadService.runActionLater(() -> {
                        asciiDocController.getRecentFiles().remove(pdfPath.toString());
                        asciiDocController.getRecentFiles().add(0, pdfPath.toString());
                    });
                }
            });
        });
    }

    public void generateBook(boolean askPath) {


        try {

            Path currentTabPath = current.currentPath().get();
            Path currentTabPathDir = currentTabPath.getParent();
            Path configPath = asciiDocController.getConfigPath();
            String tabText = current.getCurrentTabText().replace("*", "").trim();

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

            docBookService.generateDocbook(docbook->{
                Path docbookTempfile = IOHelper.createTempFile(currentTabPathDir, ".xml");
                IOHelper.writeToFile(docbookTempfile, docbook, CREATE, WRITE, TRUNCATE_EXISTING);

                InputHandler handler = new InputHandler(docbookTempfile.toFile(), configPath.resolve("docbook-config/fo-pdf.xsl").toFile(), params);

                FopFactory fopFactory = FopFactory.newInstance();

                IOHelper.setUserConfig(fopFactory,configPath.resolve("docbook-config/fop.xconf").toUri().toASCIIString());

                this.produce(askPath, handler, fopFactory, docbookTempfile);
            });

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }


    public void generateArticle(boolean askPath) {

        try {

            Path currentTabPath = current.currentPath().get();
            Path currentTabPathDir = currentTabPath.getParent();
            Path configPath = asciiDocController.getConfigPath();
            String tabText = current.getCurrentTabText().replace("*", "").trim();

            docBookService.generateDocbookArticle(docbook->{
                Path docbookTempfile = IOHelper.createTempFile(currentTabPathDir, ".xml");
                IOHelper.writeToFile(docbookTempfile, docbook, CREATE, WRITE, TRUNCATE_EXISTING);

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

                InputHandler handler = new InputHandler(docbookTempfile.toFile(), configPath.resolve("docbook-config/fo-pdf.xsl").toFile(), params);

                FopFactory fopFactory = FopFactory.newInstance();

                IOHelper.setUserConfig(fopFactory,configPath.resolve("docbook-config/fop.xconf").toUri().toASCIIString());

                this.produce(askPath, handler, fopFactory, docbookTempfile);
            });



        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }
}

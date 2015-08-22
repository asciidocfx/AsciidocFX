package com.kodcu.service.convert.pdf;

import com.kodcu.controller.ApplicationController;
import com.kodcu.other.Current;
import com.kodcu.other.IOHelper;
import com.kodcu.service.DirectoryService;
import com.kodcu.service.PathResolverService;
import com.kodcu.service.ThreadService;
import com.kodcu.service.convert.docbook.DocBookConverter;
import com.kodcu.service.ui.IndikatorService;
import javafx.stage.FileChooser;
import org.apache.avalon.framework.configuration.DefaultConfigurationBuilder;
import org.apache.fop.apps.*;
import org.apache.fop.cli.InputHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.xml.sax.SAXException;

import javax.xml.transform.*;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamSource;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Vector;
import java.util.function.Consumer;

import static java.nio.file.StandardOpenOption.*;

/**
 * Created by usta on 09.04.2015.
 */
@Component
public class PdfBookConverter extends AbstractPdfConverter {

    private final Logger logger = LoggerFactory.getLogger(PdfBookConverter.class);

    private final ApplicationController asciiDocController;
    private final DocBookConverter docBookConverter;
    private final IndikatorService indikatorService;
    private final ThreadService threadService;
    private final DirectoryService directoryService;
    private final Current current;
    private final PathResolverService pathResolverService;

    @Autowired
    public PdfBookConverter(final ApplicationController asciiDocController, final DocBookConverter docBookConverter,
                            final IndikatorService indikatorService,
                            final ThreadService threadService, final DirectoryService directoryService, final Current current, PathResolverService pathResolverService) {
        super(asciiDocController, docBookConverter, indikatorService, threadService, directoryService, current, pathResolverService);
        this.asciiDocController = asciiDocController;
        this.docBookConverter = docBookConverter;
        this.indikatorService = indikatorService;
        this.threadService = threadService;
        this.directoryService = directoryService;
        this.current = current;
        this.pathResolverService = pathResolverService;

    }


    @Override
    public void convert(boolean askPath, Consumer<String>... nextStep) {

        final Path currentTabPath = current.currentPath().get();
        final Path currentTabPathDir = currentTabPath.getParent();
        final Path configPath = asciiDocController.getConfigPath();
        final String tabText = current.getCurrentTabText().replace("*", "").trim();

        threadService.runActionLater(() -> {

            Path pdfPath = null;
            if (askPath) {
                final FileChooser fileChooser = directoryService.newFileChooser("Save PDF file");
                fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("PDF", "*.pdf"));
                pdfPath = fileChooser.showSaveDialog(null).toPath();
            } else
                pdfPath = currentTabPathDir.resolve(tabText + ".pdf");

            final Path finalPdfPath1 = pdfPath;

            docBookConverter.convert(false, docbook -> {

                final Path finalPdfPath = finalPdfPath1;
                threadService.runTaskLater(() -> {
                    indikatorService.startProgressBar();
                    logger.debug("PDF conversion started");

                    final Path docbookTempfile = IOHelper.createTempFile(currentTabPathDir, ".xml");
                    IOHelper.writeToFile(docbookTempfile, docbook, CREATE, WRITE, TRUNCATE_EXISTING);

                    try (OutputStream outputStream = new FileOutputStream(finalPdfPath.toFile());) {
                        // Setup XSLT
                        TransformerFactory factory = TransformerFactory.newInstance();
                        Transformer transformer = factory.newTransformer(new StreamSource(configPath.resolve("docbook-config/fo-pdf.xsl").toFile()));
//                        transformer.setParameter("versionParam", "1.0");
                        transformer.setParameter("highlight.xslthl.config", configPath.resolve("docbook-config/xslthl-config.xml").toUri().toASCIIString());
                        transformer.setParameter("admon.graphics.path", configPath.resolve("docbook/images/").toUri().toASCIIString());
                        transformer.setParameter("callout.graphics.path", configPath.resolve("docbook/images/callouts/").toUri().toASCIIString());


                        FopFactory fopFactory = new FopFactoryBuilder(currentTabPathDir.toUri())
                                .setSourceResolution(300)
                                .setTargetResolution(300)
                                .setPageHeight("9in")
                                .setPageWidth("6in")
                                .setStrictFOValidation(false)
                                .setStrictUserConfigValidation(false)
                                .setAccessibility(true)
                                .build();

                        FOUserAgent foUserAgent = fopFactory.newFOUserAgent();
                        Fop fop = fopFactory.newFop(MimeConstants.MIME_PDF, foUserAgent, outputStream);

                        // Setup input for XSLT transformation
                        Source src = new StreamSource(docbookTempfile.toFile());

                        // Resulting SAX events (the generated FO) must be piped through to FOP
                        Result res = new SAXResult(fop.getDefaultHandler());

                        // Step 6: Start XSLT transformation and FOP processing
                        transformer.transform(src, res);

                        Files.deleteIfExists(docbookTempfile);

                    } catch (Exception e) {
                        logger.error("Problem occured while converting to PDF", e);
                    } finally {
                        indikatorService.stopProgressBar();
                        logger.debug("PDF conversion ended");

                        asciiDocController.addRemoveRecentList(finalPdfPath);
                    }

                });
            });
        });

//            final Vector<String> params = new Vector<>();
//            params.add("body.font.family");
//            params.add("Arial");
//            params.add("title.font.family");
//            params.add("Arial");
//            params.add("highlight.xslthl.config");
//            params.add(configPath.resolve("docbook-config/xslthl-config.xml").toUri().toASCIIString());
//            params.add("admon.graphics.path");
//            params.add(configPath.resolve("docbook/images/").toUri().toASCIIString());
//            params.add("callout.graphics.path");
//            params.add(configPath.resolve("docbook/images/callouts/").toUri().toASCIIString());
    }
}

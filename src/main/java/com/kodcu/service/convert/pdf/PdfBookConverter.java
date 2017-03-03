package com.kodcu.service.convert.pdf;

import com.kodcu.controller.ApplicationController;
import com.kodcu.other.Current;
import com.kodcu.other.ExtensionFilters;
import com.kodcu.other.IOHelper;
import com.kodcu.service.DirectoryService;
import com.kodcu.service.PathResolverService;
import com.kodcu.service.ThreadService;
import com.kodcu.service.convert.DocumentConverter;
import com.kodcu.service.convert.docbook.DocBookConverter;
import com.kodcu.service.ui.IndikatorService;
import org.apache.fop.apps.*;
import org.apache.fop.apps.MimeConstants;
import org.apache.xmlgraphics.image.loader.ImageContext;
import org.apache.xmlgraphics.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamSource;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.function.Consumer;

import static java.nio.file.StandardOpenOption.*;

/**
 * Created by usta on 09.04.2015.
 */
@Component
public class PdfBookConverter implements DocumentConverter<String> {

    private final Logger logger = LoggerFactory.getLogger(PdfBookConverter.class);

    private final ApplicationController asciiDocController;
    private final DocBookConverter docBookConverter;
    private final IndikatorService indikatorService;
    private final ThreadService threadService;
    private final DirectoryService directoryService;
    private final Current current;
    private final PathResolverService pathResolverService;
    private FopFactory fopFactory;

    @Autowired
    public PdfBookConverter(final ApplicationController asciiDocController, final DocBookConverter docBookConverter,
                            final IndikatorService indikatorService,
                            final ThreadService threadService, final DirectoryService directoryService, final Current current, PathResolverService pathResolverService) {
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

        threadService.runActionLater(() -> {

            final Path pdfPath = directoryService.getSaveOutputPath(ExtensionFilters.PDF, askPath);

            docBookConverter.convert(false, docbook -> {

                indikatorService.startProgressBar();
                logger.debug("PDF conversion started");

                final Path docbookTempfile = IOHelper.createTempFile(currentTabPathDir, ".xml");
                IOHelper.writeToFile(docbookTempfile, docbook, CREATE, WRITE, TRUNCATE_EXISTING);

                try (OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(pdfPath.toFile()));) {
                    // Setup XSLT
                    TransformerFactory factory = TransformerFactory.newInstance();
                    Transformer transformer = factory.newTransformer(new StreamSource(configPath.resolve("docbook-config/fo-pdf.xsl").toFile()));
                    transformer.setParameter("highlight.xslthl.config", configPath.resolve("docbook-config/xslthl-config.xml").toUri().toASCIIString());
                    transformer.setParameter("admon.graphics.path", configPath.resolve("docbook/images/").toUri().toASCIIString());
                    transformer.setParameter("callout.graphics.path", configPath.resolve("docbook/images/callouts/").toUri().toASCIIString());

                    if (Objects.isNull(fopFactory)) {
                        fopFactory = FopFactory.newInstance(configPath.resolve("docbook-config/fop.xconf.xml").toFile());
                    }

                    Fop fop = fopFactory.newFop(MimeConstants.MIME_PDF, outputStream);

                    // Setup input for XSLT transformation
                    Source src = new StreamSource(docbookTempfile.toFile());

                    // Resulting SAX events (the generated FO) must be piped through to FOP
                    Result res = new SAXResult(fop.getDefaultHandler());

                    // Step 6: Start XSLT transformation and FOP processing
                    transformer.transform(src, res);

                    Files.deleteIfExists(docbookTempfile);

                    // Result processing
                    FormattingResults foResults = fop.getResults();

                    logger.info("Generated {} pages in total.", foResults.getPageCount());

                } catch (Exception e) {
                    logger.error("Problem occured while converting to PDF", e);
                } finally {
                    indikatorService.stopProgressBar();
                    logger.debug("PDF conversion ended");

                    asciiDocController.addRemoveRecentList(pdfPath);
                }
            });
        });

    }
}

package com.kodedu.service.convert.pdf;

import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;
import static java.nio.file.StandardOpenOption.WRITE;

import com.kodedu.controller.ApplicationController;
import com.kodedu.helper.IOHelper;
import com.kodedu.other.Current;
import com.kodedu.other.ExtensionFilters;
import com.kodedu.other.RenderResult;
import com.kodedu.service.DirectoryService;
import com.kodedu.service.PathResolverService;
import com.kodedu.service.ThreadService;
import com.kodedu.service.convert.DocumentConverter;
import com.kodedu.service.convert.docbook.DocBookConverter;
import com.kodedu.service.ui.IndikatorService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.function.Consumer;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.fop.apps.Fop;
import org.apache.fop.apps.FopFactory;
import org.apache.fop.apps.FormattingResults;
import org.apache.fop.apps.MimeConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by usta on 09.04.2015.
 */
@Component
public class FopPdfBookConverter implements DocumentConverter<RenderResult> {

    private final Logger logger = LoggerFactory.getLogger(FopPdfBookConverter.class);

    private final ApplicationController asciiDocController;
    private final DocBookConverter docBookConverter;
    private final IndikatorService indikatorService;
    private final ThreadService threadService;
    private final DirectoryService directoryService;
    private final Current current;
    private final PathResolverService pathResolverService;
    private FopFactory fopFactory;

    @Autowired
    public FopPdfBookConverter(final ApplicationController asciiDocController, final DocBookConverter docBookConverter,
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
    public void convert(boolean askPath, Consumer<RenderResult>... nextStep) {

        final Path currentTabPath = current.currentPath().get();
        final Path currentTabPathDir = currentTabPath.getParent();
        final Path configPath = asciiDocController.getConfigPath();

        threadService.runActionLater(() -> {

            final Path pdfPath = directoryService.getSaveOutputPath(ExtensionFilters.PDF, askPath);

            docBookConverter.convert(false, docbook -> {

                indikatorService.startProgressBar();
                logger.debug("PDF conversion started");

                final Path docbookTempfile = IOHelper.createTempFile(currentTabPathDir, ".xml");
                IOHelper.writeToFile(docbookTempfile, docbook.getContent(), CREATE, WRITE, TRUNCATE_EXISTING);

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
                    IOHelper.close(outputStream);
                    onSuccessfulConversation(nextStep, pdfPath.toFile());

                } catch (Exception e) {
                    logger.error("Problem occured while converting to PDF", e);
                    onFailedConversation(nextStep, e);
                } finally {
                    indikatorService.stopProgressBar();
                    logger.debug("PDF conversion ended");

                    asciiDocController.addRemoveRecentList(pdfPath);
                }
            });
        });

    }
}

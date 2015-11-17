package com.kodcu.service.convert.pdf;

import com.kodcu.controller.ApplicationController;
import com.kodcu.other.Current;
import com.kodcu.other.ExtensionFilters;
import com.kodcu.other.IOHelper;
import com.kodcu.service.DirectoryService;
import com.kodcu.service.PathResolverService;
import com.kodcu.service.ThreadService;
import com.kodcu.service.convert.docbook.DocBookConverter;
import com.kodcu.service.ui.IndikatorService;
import org.apache.fop.apps.FopFactory;
import org.apache.fop.cli.InputHandler;
import org.asciidoctor.Asciidoctor;
import org.asciidoctor.Options;
import org.asciidoctor.SafeMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
    private Asciidoctor asciidoctor;

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

        try {

            Path pdfPath = directoryService.getSaveOutputPath(ExtensionFilters.PDF, askPath);

            final Path path = current.currentPath().get();

            Options options = new Options();
            options.setBackend("pdf");
            options.setBaseDir(path.getParent().toString());
            options.setSafe(SafeMode.SAFE);

            asciidoctor.convertFile(path.toFile(), options);

            if (true)
                return;

            indikatorService.startProgressBar();
            logger.debug("PDF conversion started");

            final Path currentTabPath = current.currentPath().get();
            final Path currentTabPathDir = currentTabPath.getParent();
            final Path configPath = asciiDocController.getConfigPath();

            final Vector<String> params = new Vector<>();
//            params.add("body.font.family");
//            params.add("Arial Unicode MS");
//            params.add("title.font.family");
//            params.add("Arial Unicode MS");
            params.add("highlight.xslthl.config");
            params.add(configPath.resolve("docbook-config/xslthl-config.xml").toUri().toASCIIString());
            params.add("admon.graphics.path");
            params.add(configPath.resolve("docbook/images/").toUri().toASCIIString());
            params.add("callout.graphics.path");
            params.add(configPath.resolve("docbook/images/callouts/").toUri().toASCIIString());

            docBookConverter.convert(false, docbook -> {
                final Path docbookTempfile = IOHelper.createTempFile(currentTabPathDir, ".xml");
                IOHelper.writeToFile(docbookTempfile, docbook, CREATE, WRITE, TRUNCATE_EXISTING);

                InputHandler handler = new InputHandler(docbookTempfile.toFile(), configPath.resolve("docbook-config/fo-pdf.xsl").toFile(), params);

                FopFactory fopFactory = FopFactory.newInstance();

                IOHelper.setUserConfig(fopFactory, configPath.resolve("docbook-config/fop.xconf.xml").toUri().toASCIIString());

                super.produce(askPath, handler, fopFactory, docbookTempfile, pdfPath);
            });

        } catch (Exception e) {
            logger.error("Problem occured while converting to PDF", e);
        }
    }
}

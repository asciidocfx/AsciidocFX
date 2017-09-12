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
import org.apache.fop.apps.Fop;
import org.apache.fop.apps.FopFactory;
import org.apache.fop.apps.FormattingResults;
import org.apache.fop.apps.MimeConstants;
import org.asciidoctor.Asciidoctor;
import org.asciidoctor.OptionsBuilder;
import org.asciidoctor.SafeMode;
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
@Component("newPdfConverter")
public class NewPdfBookConverter implements DocumentConverter<String> {

    private final Logger logger = LoggerFactory.getLogger(NewPdfBookConverter.class);

    private final ApplicationController asciiDocController;
    private final DocBookConverter docBookConverter;
    private final IndikatorService indikatorService;
    private final ThreadService threadService;
    private final DirectoryService directoryService;
    private final Current current;
    private final PathResolverService pathResolverService;
    private FopFactory fopFactory;

    @Autowired
    public NewPdfBookConverter(final ApplicationController asciiDocController, final DocBookConverter docBookConverter,
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
    public synchronized void convert(boolean askPath, Consumer<String>... nextStep) {

        final Path currentTabPath = current.currentPath().get();
        final Path currentTabPathDir = currentTabPath.getParent();
        final Path configPath = asciiDocController.getConfigPath();

        final Path pdfPath = directoryService.getSaveOutputPath(ExtensionFilters.PDF, askPath);

        Asciidoctor asciidoctor = Asciidoctor.Factory.create();

        asciidoctor.convertFile(currentTabPath.toFile(), OptionsBuilder.options()
                .backend("PDF")
                .safe(SafeMode.UNSAFE)
                .toFile(pdfPath.toFile())
                .toFile(true)
                .asMap());


    }
}

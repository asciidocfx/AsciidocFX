package com.kodedu.service.convert.pdf;

import com.kodedu.controller.ApplicationController;
import com.kodedu.other.Current;
import com.kodedu.other.ExtensionFilters;
import com.kodedu.other.IOHelper;
import com.kodedu.service.DirectoryService;
import com.kodedu.service.PathResolverService;
import com.kodedu.service.ThreadService;
import com.kodedu.service.convert.DocumentConverter;
import com.kodedu.service.convert.docbook.DocBookConverter;
import com.kodedu.service.ui.IndikatorService;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.fop.apps.Fop;
import org.apache.fop.apps.FopFactory;
import org.apache.fop.apps.FormattingResults;
import org.apache.fop.apps.MimeConstants;
import org.asciidoctor.Asciidoctor;
import org.asciidoctor.OptionsBuilder;
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
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
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
    	
    	String asciidoc = current.currentEditorValue();

        threadService.runActionLater(() -> {

            final Path pdfPath = directoryService.getSaveOutputPath(ExtensionFilters.PDF, askPath);
            
            File destFile = pdfPath.toFile();
            
            indikatorService.startProgressBar();
            logger.debug("PDF conversion started");                
           
            Asciidoctor doctor = Asciidoctor.Factory.create();  
            
			doctor.convert(
					asciidoc,
            	    OptionsBuilder.options()
            	    .baseDir(destFile.getParentFile())         	    
                    .toFile(destFile)                    
                    .backend("pdf"));
			
			 indikatorService.stopProgressBar();
             logger.debug("PDF conversion ended");

             asciiDocController.addRemoveRecentList(pdfPath);
                
            
        });

    }
}
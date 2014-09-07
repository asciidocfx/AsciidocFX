package com.kodcu.service;

import com.icl.saxon.TransformerFactoryImpl;
import com.kodcu.controller.AsciiDocController;
import com.kodcu.other.IOHelper;
import org.apache.commons.io.FileUtils;
import org.joox.Match;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.zeroturnaround.zip.ZipUtil;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.BufferedWriter;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.Optional;

import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;
import static java.nio.file.StandardOpenOption.WRITE;
import static org.joox.JOOX.$;

/**
 * Created by usta on 30.08.2014.
 */
@Component
public class Html5BookService {

    private static final Logger logger = LoggerFactory.getLogger(Html5BookService.class);

    @Autowired
    private AsciiDocController asciiDocController;

    @Autowired
    private IndikatorService indikatorService;

    public void produceXhtml5(Path currentPath, Path configPath) {

        Path bookXml = currentPath.resolve("book.xml");

        if (Files.notExists(bookXml)) {
            return;
        }

        indikatorService.startCycle();

        try {
            Path epubTemp = Files.createTempDirectory("epub");

            TransformerFactory factory = new TransformerFactoryImpl();
            File xslFile = configPath.resolve("docbook/xhtml5/docbook.xsl").toFile();
            StreamSource xslSource = new StreamSource(xslFile);
            Transformer transformer = factory.newTransformer(xslSource);
            StreamSource xmlSource = new StreamSource(currentPath.resolve("book.xml").toFile());

            try(BufferedWriter bufferedWriter = Files.newBufferedWriter(currentPath.resolve("book.html"), CREATE, WRITE, TRUNCATE_EXISTING);){
                transformer.transform(xmlSource, new StreamResult(bufferedWriter));
            }

            indikatorService.completeCycle();
            asciiDocController.setLastConvertedFile(Optional.of(currentPath.resolve("book.epub")));

        } catch (Exception ex) {
            logger.error(ex.getMessage(),ex);
        }
        finally {
            indikatorService.hideIndikator();
        }
    }
}

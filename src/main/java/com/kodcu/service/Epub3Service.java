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
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Optional;

import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;
import static java.nio.file.StandardOpenOption.WRITE;
import static org.joox.JOOX.$;

/**
 * Created by usta on 30.08.2014.
 */
@Component
public class Epub3Service {

    private static final Logger logger = LoggerFactory.getLogger(Epub3Service.class);

    @Autowired
    private AsciiDocController asciiDocController;

    @Autowired
    private IndikatorService indikatorService;

    public void produceEpub3(Path currentPath, Path configPath) {

        Path bookXml = currentPath.resolve("book.xml");

        if (Files.notExists(bookXml)) {
            return;
        }

        indikatorService.startCycle();

        try {
            Path epubTemp = Files.createTempDirectory("epub");

            TransformerFactory factory = new TransformerFactoryImpl();
            File xslFile = configPath.resolve("docbook/epub3/chunk.xsl").toFile();
            StreamSource xslSource = new StreamSource(xslFile);
            Transformer transformer = factory.newTransformer(xslSource);
            transformer.setParameter("base.dir", epubTemp.resolve("OEBPS").toString());
            StreamSource xmlSource = new StreamSource(currentPath.resolve("book.xml").toFile());
            transformer.transform(xmlSource, new StreamResult());

            Path containerXml = epubTemp.resolve("META-INF/container.xml");

            Match root = $(containerXml.toFile());
            root
                .find("rootfile")
                .attr("full-path", "OEBPS/package.opf");

            StringBuilder builder = new StringBuilder();
            builder.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");

            Match wrapper = $("wrapper");
            wrapper.append(root);
            builder.append(wrapper.content());

            root.write(containerXml.toFile());

            IOHelper.writeToFile(containerXml, builder.toString(), TRUNCATE_EXISTING, WRITE);

            Path epubOut = epubTemp.resolve("book.epub");
            FileUtils.copyDirectoryToDirectory(currentPath.resolve("images").toFile(), epubTemp.resolve("OEBPS").toFile());
            FileUtils.copyDirectoryToDirectory(configPath.resolve("docbook/images/callouts").toFile(), epubTemp.resolve("OEBPS/images").toFile());
            ZipUtil.pack(epubTemp.toFile(), epubOut.toFile());
            ZipUtil.removeEntry(epubOut.toFile(),"book.epub");
            Files.move(epubOut, currentPath.resolve("book.epub"), StandardCopyOption.REPLACE_EXISTING);

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

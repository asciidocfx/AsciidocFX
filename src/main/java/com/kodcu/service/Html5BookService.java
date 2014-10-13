package com.kodcu.service;

import com.icl.saxon.TransformerFactoryImpl;
import com.kodcu.controller.AsciiDocController;
import com.kodcu.other.IOHelper;
import javafx.scene.web.WebEngine;
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
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    private Pattern compiledRegex = Pattern.compile("(?<=include::)(.*?)(?=\\[(.*?)\\])");

    @Autowired
    private AsciiDocController asciiDocController;

    @Autowired
    private BookPathResolverService bookPathResolver;

    @Autowired
    private AsciiDoctorRenderService docConverter;

    @Autowired
    private IndikatorService indikatorService;

    public void produceXhtml5(WebEngine webEngine,Path currentPath, Path configPath)  {

        try{
            Path bookAsc = bookPathResolver.resolve(currentPath);

            if (Objects.isNull(bookAsc)) {
                IOHelper.writeToFile(currentPath.resolve("book.xml"), "There is no book.asc file..", CREATE, TRUNCATE_EXISTING);
                return;
            }

            indikatorService.startCycle();

            List<String> bookAscLines = Files.readAllLines(bookAsc);
            StringBuffer allAscChapters=new StringBuffer();

            for (int i = 0; i < bookAscLines.size(); i++) {
                String bookAscLine = bookAscLines.get(i);

                Matcher matcher = compiledRegex.matcher(bookAscLine);

                if(matcher.find())
                {
                    String chapterPath = matcher.group();
                    String chapterContent = IOHelper.readFile(currentPath.resolve(chapterPath));
                    allAscChapters.append(chapterContent);
                    allAscChapters.append("\n\n");
                }
                else{
                    allAscChapters.append(bookAscLine);
                    allAscChapters.append("\n");
                }

            }

            String bookXmlAsciidoc = allAscChapters.toString();

            String htmlContent = docConverter.generateHtml(webEngine, bookXmlAsciidoc);

            IOHelper.writeToFile(currentPath.resolve("book.html"), htmlContent, CREATE, TRUNCATE_EXISTING);

            asciiDocController.setLastConvertedFile(Optional.of(currentPath.resolve("book.html")));

            indikatorService.completeCycle();

        }
        catch (Exception ex){
            ex.printStackTrace();
            logger.error(ex.getMessage(),ex);
        }
        finally {
            indikatorService.hideIndikator();
        }


    }
}

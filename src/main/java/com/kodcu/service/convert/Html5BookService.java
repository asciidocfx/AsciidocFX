package com.kodcu.service.convert;

import com.kodcu.controller.ApplicationController;
import com.kodcu.other.Current;
import com.kodcu.other.IOHelper;
import com.kodcu.service.DirectoryService;
import com.kodcu.service.PathResolverService;
import com.kodcu.service.ThreadService;
import com.kodcu.service.ui.IndikatorService;
import javafx.application.Platform;
import javafx.stage.FileChooser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;

/**
 * Created by usta on 30.08.2014.
 */
@Component
public class Html5BookService {

    private static final Logger logger = LoggerFactory.getLogger(Html5BookService.class);

    private Pattern compiledRegex = Pattern.compile("(?<=include::)(.*?)(?=\\[(.*?)\\])");

    @Autowired
    private ApplicationController asciiDocController;

    @Autowired
    private ThreadService threadService;

    @Autowired
    private PathResolverService bookPathResolver;

    @Autowired
    private DirectoryService directoryService;

    @Autowired
    private RenderService renderService;

    @Autowired
    private Current current;

    @Autowired
    private IndikatorService indikatorService;

    private Path htmlBookPath;

    public void convertHtmlBook(boolean askPath) {

        try {

            Path currentTabPath = current.currentPath().get();
            Path currentTabPathDir = currentTabPath.getParent();
            String tabText = current.getCurrentTabText().replace("*", "").trim();


            if (askPath) {
                FileChooser fileChooser = directoryService.newFileChooser("Save HTML file");
                fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("HTML", "*.html"));
                htmlBookPath = fileChooser.showSaveDialog(null).toPath();
            } else
                htmlBookPath = currentTabPathDir.resolve(tabText + ".html");

            indikatorService.startCycle();

            List<String> bookAscLines = Arrays.asList(current.currentEditorValue().split("\\r?\\n"));
            StringBuffer allAscChapters = new StringBuffer();

            for (int i = 0; i < bookAscLines.size(); i++) {
                String bookAscLine = bookAscLines.get(i);

                Matcher matcher = compiledRegex.matcher(bookAscLine);

                if (matcher.find()) {
                    String chapterPath = matcher.group();
                    String chapterContent = IOHelper.readFile(currentTabPathDir.resolve(chapterPath));
                    allAscChapters.append(chapterContent);
                    allAscChapters.append("\n\n");
                } else {
                    allAscChapters.append(bookAscLine);
                    allAscChapters.append("\n");
                }

            }

            String bookXmlAsciidoc = allAscChapters.toString();

            renderService.convertHtmlBook(bookXmlAsciidoc,htmlContent->{
                IOHelper.writeToFile(htmlBookPath, htmlContent, CREATE, TRUNCATE_EXISTING);

                threadService.runActionLater(() -> {
                    asciiDocController.getRecentFiles().remove(htmlBookPath.toString());
                    asciiDocController.getRecentFiles().add(0, htmlBookPath.toString());
                });

                indikatorService.completeCycle();
            });

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            indikatorService.hideIndikator();
        }

    }
}

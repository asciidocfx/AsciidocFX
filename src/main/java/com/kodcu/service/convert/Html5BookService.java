package com.kodcu.service.convert;

import com.kodcu.controller.ApplicationController;
import com.kodcu.other.Current;
import com.kodcu.other.IOHelper;
import com.kodcu.service.DirectoryService;
import com.kodcu.service.ThreadService;
import com.kodcu.service.ui.IndikatorService;
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

    private final Logger logger = LoggerFactory.getLogger(Html5BookService.class);

    private final Pattern compiledRegex = Pattern.compile("(?<=include::)(.*?)(?=\\[(.*?)\\])");

    private final ApplicationController asciiDocController;
    private final ThreadService threadService;
    private final DirectoryService directoryService;
    private final RenderService renderService;
    private final Current current;
    private final IndikatorService indikatorService;

    private Path htmlBookPath;

    @Autowired
    public Html5BookService(final ApplicationController asciiDocController, final ThreadService threadService,
            final DirectoryService directoryService, final RenderService renderService, final Current current,
            IndikatorService indikatorService) {
        this.asciiDocController = asciiDocController;
        this.threadService = threadService;
        this.directoryService = directoryService;
        this.renderService = renderService;
        this.current = current;
        this.indikatorService = indikatorService;
    }



    public void convertHtmlBook(boolean askPath) {

        try {

            final Path currentTabPath = current.currentPath().get();
            final Path currentTabPathDir = currentTabPath.getParent();
            final String tabText = current.getCurrentTabText().replace("*", "").trim();


            if (askPath) {
                final FileChooser fileChooser = directoryService.newFileChooser("Save HTML file");
                fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("HTML", "*.html"));
                htmlBookPath = fileChooser.showSaveDialog(null).toPath();
            } else
                htmlBookPath = currentTabPathDir.resolve(tabText + ".html");

            indikatorService.startCycle();

            List<String> bookAscLines = Arrays.asList(current.currentEditorValue().split("\\r?\\n"));
            StringBuffer allAscChapters = new StringBuffer();

            for (int i = 0; i < bookAscLines.size(); i++) {
                final String bookAscLine = bookAscLines.get(i);

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

            final String bookXmlAsciidoc = allAscChapters.toString();

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
            indikatorService.completeCycle();
        }

    }
}

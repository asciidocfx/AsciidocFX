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
import java.util.regex.Pattern;

import static java.nio.file.StandardOpenOption.*;

/**
 * Created by usta on 30.08.2014.
 */
@Component
public class Html5ArticleService {

    private static final Logger logger = LoggerFactory.getLogger(Html5ArticleService.class);

    private Pattern compiledRegex = Pattern.compile("(?<=include::)(.*?)(?=\\[(.*?)\\])");

    @Autowired
    private ApplicationController asciiDocController;

    @Autowired
    private ThreadService threadService;

    @Autowired
    private RenderService renderService;

    @Autowired
    private Current current;

    @Autowired
    private DirectoryService directoryService;

    @Autowired
    private IndikatorService indikatorService;

    private Path htmlArticlePath;

    public void convertHtmlArticle(boolean askPath) {

        Path currentTabPath = current.currentPath().get();
        Path currentTabPathDir = currentTabPath.getParent();

        String html = renderService.convertHtmlArticle();
        String tabText = current.getCurrentTabText().replace("*", "").trim();

        threadService.runActionLater(() -> {
            if (askPath) {
                FileChooser fileChooser = directoryService.newFileChooser("Save HTML file");
                fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("HTML", "*.html"));
                htmlArticlePath = fileChooser.showSaveDialog(null).toPath();
            } else
                htmlArticlePath = currentTabPathDir.resolve(tabText.concat(".html"));

            indikatorService.startCycle();

            threadService.runTaskLater(()->{
                IOHelper.writeToFile(htmlArticlePath, html, CREATE, TRUNCATE_EXISTING, WRITE);

                threadService.runActionLater(()->{
                    indikatorService.hideIndikator();
                    asciiDocController.getRecentFiles().remove(htmlArticlePath.toString());
                    asciiDocController.getRecentFiles().add(0, htmlArticlePath.toString());
                });
            });

        });

    }
}

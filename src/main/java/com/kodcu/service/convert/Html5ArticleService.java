package com.kodcu.service.convert;

import com.kodcu.controller.ApplicationController;
import com.kodcu.other.Current;
import com.kodcu.other.IOHelper;
import com.kodcu.service.DirectoryService;
import com.kodcu.service.ThreadService;
import com.kodcu.service.ui.IndikatorService;
import javafx.stage.FileChooser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.nio.file.Path;

import static java.nio.file.StandardOpenOption.*;

/**
 * Created by usta on 30.08.2014.
 */
@Component
public class Html5ArticleService {

    private final ApplicationController asciiDocController;
    private final ThreadService threadService;
    private final RenderService renderService;
    private final Current current;
    private final DirectoryService directoryService;
    private final IndikatorService indikatorService;

    private Path htmlArticlePath;

    @Autowired
    public Html5ArticleService(ApplicationController asciiDocController, ThreadService threadService,
            RenderService renderService, Current current, DirectoryService directoryService, IndikatorService indikatorService) {
        this.asciiDocController = asciiDocController;
        this.threadService = threadService;
        this.renderService = renderService;
        this.current = current;
        this.directoryService = directoryService;
        this.indikatorService = indikatorService;
    }

    public void convertHtmlArticle(boolean askPath) {

        final Path currentTabPath = current.currentPath().get();
        final Path currentTabPathDir = currentTabPath.getParent();

        renderService.convertHtmlArticle(html->{
            String tabText = current.getCurrentTabText().replace("*", "").trim();

            threadService.runActionLater(() -> {
                if (askPath) {
                    final FileChooser fileChooser = directoryService.newFileChooser("Save HTML file");
                    fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("HTML", "*.html"));
                    htmlArticlePath = fileChooser.showSaveDialog(null).toPath();
                } else
                    htmlArticlePath = currentTabPathDir.resolve(tabText.concat(".html"));

                indikatorService.startCycle();

                threadService.runTaskLater(() -> {
                    IOHelper.writeToFile(htmlArticlePath, html, CREATE, TRUNCATE_EXISTING, WRITE);

                    threadService.runActionLater(() -> {
                        indikatorService.hideIndikator();
                        asciiDocController.getRecentFiles().remove(htmlArticlePath.toString());
                        asciiDocController.getRecentFiles().add(0, htmlArticlePath.toString());
                    });
                });

            });
        });

    }
}

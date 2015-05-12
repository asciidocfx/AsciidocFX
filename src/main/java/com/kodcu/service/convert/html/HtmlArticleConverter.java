package com.kodcu.service.convert.html;

import com.kodcu.component.HtmlPane;
import com.kodcu.controller.ApplicationController;
import com.kodcu.other.Current;
import com.kodcu.other.IOHelper;
import com.kodcu.service.DirectoryService;
import com.kodcu.service.MarkdownService;
import com.kodcu.service.ThreadService;
import com.kodcu.service.convert.DocumentConverter;
import com.kodcu.service.ui.IndikatorService;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;
import netscape.javascript.JSObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.util.function.Consumer;

import static java.nio.file.StandardOpenOption.*;

/**
 * Created by usta on 30.08.2014.
 */
@Component
public class HtmlArticleConverter implements DocumentConverter<String> {

    private final ApplicationController asciiDocController;
    private final ThreadService threadService;
    private final Current current;
    private final DirectoryService directoryService;
    private final IndikatorService indikatorService;
    private final MarkdownService markdownService;
    private final ApplicationController controller;
    private final HtmlPane htmlPane;

    private Path htmlArticlePath;

    @Autowired
    public HtmlArticleConverter(ApplicationController asciiDocController, ThreadService threadService,
                                Current current, DirectoryService directoryService, IndikatorService indikatorService, MarkdownService markdownService, ApplicationController controller, HtmlPane htmlPane) {
        this.asciiDocController = asciiDocController;
        this.threadService = threadService;
        this.current = current;
        this.directoryService = directoryService;
        this.indikatorService = indikatorService;
        this.markdownService = markdownService;
        this.controller = controller;
        this.htmlPane = htmlPane;
    }

    @Override
    public void convert(boolean askPath, Consumer<String>... nextStep) {

        final Path currentTabPath = current.currentPath().get();
        final Path currentTabPathDir = currentTabPath.getParent();

        String input = current.currentEditorValue();
        markdownService.convert(input, asciidoc -> {
            threadService.runActionLater(() -> {

                String rendered = htmlPane.convertHtmlArticle(asciidoc);

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
                        IOHelper.writeToFile(htmlArticlePath, rendered, CREATE, TRUNCATE_EXISTING, WRITE);

                        threadService.runActionLater(() -> {
                            indikatorService.completeCycle();
                            asciiDocController.getRecentFilesList().remove(htmlArticlePath.toString());
                            asciiDocController.getRecentFilesList().add(0, htmlArticlePath.toString());
                        });
                    });

                });
            });
        });

    }
}

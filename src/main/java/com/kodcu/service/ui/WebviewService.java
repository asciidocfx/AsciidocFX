package com.kodcu.service.ui;

import com.kodcu.component.MenuItemBuilt;
import com.kodcu.controller.ApplicationController;
import com.kodcu.other.Current;
import com.kodcu.service.*;

import com.kodcu.service.extension.AsciiTreeGenerator;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseButton;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

/**
 * Created by usta on 25.12.2014.
 */
@Component
public class WebviewService {

    private final ApplicationController controller;
    private final PathResolverService pathResolver;
    private final ThreadService threadService;
    private final ParserService parserService;
    private final MarkdownService markdownService;
    private final Current current;

    private Optional<DocumentService> documentService = Optional.empty();
    private final AsciiTreeGenerator asciiTreeGenerator;

    @Autowired
    public WebviewService(final ApplicationController controller, final PathResolverService pathResolver, final ThreadService threadService,
                          final ParserService parserService, final MarkdownService markdownService, final Current current, AsciiTreeGenerator asciiTreeGenerator) {
        this.controller = controller;
        this.pathResolver = pathResolver;
        this.threadService = threadService;
        this.parserService = parserService;
        this.markdownService = markdownService;
        this.current = current;
        this.asciiTreeGenerator = asciiTreeGenerator;
    }

    public void setDocumentService(DocumentService documentService) {
        this.documentService = Optional.ofNullable(documentService);
    }

    public WebView createWebView() {

        WebView webView = new WebView();
        WebEngine webEngine = webView.getEngine();
        webEngine.load(String.format("http://localhost:%d/editor.html", controller.getPort()));
        webView.setContextMenuEnabled(false);
        ContextMenu menu = new ContextMenu();

        MenuItem copy = MenuItemBuilt.item("Copy").onclick(event1 -> {
            controller.cutCopy(current.currentEditorSelection());
        });
        MenuItem paste = MenuItemBuilt.item("Paste").onclick(e -> {
            controller.paste(false);
        });
        MenuItem pasteRaw = MenuItemBuilt.item("Paste raw").onclick(e -> {
            current.insertEditorValue(controller.pasteRaw());
        });
        MenuItem convert = MenuItemBuilt.item("Markdown to Asciidoc").onclick(e -> {
            markdownService.convertToAsciidoc(current.currentEditorValue(),
                    content -> {
                        threadService.runActionLater(() -> {
                            documentService.ifPresent(d -> d.newDoc(content));
                            });
                    });
        });

        webView.setOnMouseClicked(event -> {

            if (menu.getItems().size() == 0) {
                menu.getItems().addAll(copy, paste, pasteRaw, convert);
            }

            if (menu.isShowing()) {
                menu.hide();
            }
            if (event.getButton() == MouseButton.SECONDARY) {
                boolean markdown = current.currentTab().isMarkdown();
                convert.setVisible(markdown);
                menu.show(webView, event.getScreenX(), event.getScreenY());
            }
        });

        webView.setOnDragDropped(event -> {
            Dragboard dragboard = event.getDragboard();
            boolean success = false;

            if (dragboard.hasFiles()) {

                List<File> dragboardFiles = dragboard.getFiles();

                if (dragboardFiles.size() == 1) {
                    Path path = dragboardFiles.get(0).toPath();
                    if (Files.isDirectory(path)) {

                        StringBuffer buffer = new StringBuffer();
                        buffer.append("[treee,file=\"\"]");
                        buffer.append("\n--\n");
                        buffer.append(asciiTreeGenerator.generate(path));
                        buffer.append("\n--");
                        current.insertEditorValue(buffer.toString());
                        success = true;
                    }
                }

                Optional<String> block = parserService.toImageBlock(dragboardFiles);
                if (block.isPresent()) {
                    current.insertEditorValue(block.get());
                    success = true;
                } else {
                    block = parserService.toIncludeBlock(dragboardFiles);
                    if (block.isPresent()) {
                        current.insertEditorValue(block.get());
                        success = true;
                    }
                }

            }

            if (dragboard.hasHtml() && !success) {
                Optional<String> block = parserService.toWebImageBlock(dragboard.getHtml());
                if (block.isPresent()) {
                    current.insertEditorValue(block.get());
                    success = true;
                }
            }

            if (dragboard.hasString() && !success) {
                current.insertEditorValue(dragboard.getString());
                success = true;
            }

            event.setDropCompleted(success);
            event.consume();
        });

        return webView;
    }
}

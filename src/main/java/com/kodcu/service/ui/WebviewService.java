package com.kodcu.service.ui;

import com.kodcu.component.MenuItemBuilt;
import com.kodcu.controller.ApplicationController;
import com.kodcu.other.Current;
import com.kodcu.other.IOHelper;
import com.kodcu.service.*;
import com.kodcu.service.convert.RenderService;
import javafx.concurrent.Worker;
import javafx.event.EventHandler;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseButton;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebEvent;
import javafx.scene.web.WebView;
import javafx.util.Callback;
import netscape.javascript.JSObject;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

/**
 * Created by usta on 25.12.2014.
 */
@Component
public class WebviewService {

    @Autowired
    private ApplicationController controller;

    @Autowired
    private PathResolverService pathResolver;

    @Autowired
    private ThreadService threadService;

    @Autowired
    private ParserService parserService;

    @Autowired
    private MarkdownService markdownService;

    @Autowired
    private Current current;

    @Autowired
    private DocumentService documentService;

    public WebView createWebView() {

        WebView webView = new WebView();
        WebEngine webEngine = webView.getEngine();
        webEngine.load(String.format("http://localhost:%d/editor.html", controller.getPort()));
        webView.setContextMenuEnabled(false);
        ContextMenu menu = new ContextMenu();

        webView.setOnMouseClicked(event -> {

            if (menu.getItems().size() == 0) {
                MenuItem copy = MenuItemBuilt.item("Copy").onclick(event1 -> {
                    controller.cutCopy(current.currentEditorSelection());
                });
                MenuItem paste = MenuItemBuilt.item("Paste").onclick(e -> {
                    current.insertEditorValue(controller.paste());
                });
                MenuItem pasteRaw = MenuItemBuilt.item("Paste raw").onclick(e -> {
                    current.insertEditorValue(controller.pasteRaw());
                });
                MenuItem convert = MenuItemBuilt.item("Markdown to Asciidoc").onclick(e -> {
                    markdownService.convertToAsciidoc(current.currentEditorValue(), content -> {
                        threadService.runActionLater(() -> {
                            documentService.newDoc(content);
                        });
                    });
                });
                menu.getItems().addAll(copy, paste, pasteRaw, convert);
            }

            if (menu.isShowing()) {
                menu.hide();
            }
            if (event.getButton() == MouseButton.SECONDARY) {
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

                        Iterator<File> files = FileUtils.iterateFilesAndDirs(path.toFile(), TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE);

                        StringBuffer buffer = new StringBuffer();
                        buffer.append("[tree,file=\"\"]");
                        buffer.append("\n--\n");
                        buffer.append("#" + path.getFileName().toString());

                        while (files.hasNext()) {
                            File next = files.next();

                            Path relativize = path.relativize(next.toPath());

                            Path path1 = relativize.getName(0);
                            if ("".equals(path1.toString()) || pathResolver.isHidden(path1))
                                continue;

                            String hash = String.join("", Collections.nCopies(relativize.getNameCount() + 1, "#"));

                            buffer.append("\n");
                            buffer.append(hash);
                            buffer.append(relativize.getFileName().toString());

                        }
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

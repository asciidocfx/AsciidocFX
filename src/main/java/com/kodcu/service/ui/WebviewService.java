package com.kodcu.service.ui;

import com.kodcu.controller.ApplicationController;
import com.kodcu.other.Current;
import com.kodcu.service.ParserService;
import com.kodcu.service.PathResolverService;
import com.kodcu.service.ThreadService;
import javafx.concurrent.Worker;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseButton;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import netscape.javascript.JSObject;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

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
    private Current current;

    public WebView createWebView() {

        WebView webView = new WebView();
        webView.setContextMenuEnabled(false);
        ContextMenu menu = new ContextMenu();

        webView.setOnMouseClicked(event -> {

            if (menu.getItems().size() == 0) {
                MenuItem copy = new MenuItem("Copy");
                copy.setOnAction(event1 -> {
                    controller.cutCopy(current.currentEditorSelection());
                });
                MenuItem paste = new MenuItem("Paste");
                paste.setOnAction(event1 -> {
                    current.insertEditorValue(controller.paste());
                });
                menu.getItems().addAll(copy, paste);
            }

            if (menu.isShowing()) {
                menu.hide();
            }
            if (event.getButton() == MouseButton.SECONDARY) {
                menu.show(webView, event.getScreenX(), event.getScreenY());
            }
        });

        WebEngine webEngine = webView.getEngine();

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

        webEngine.getLoadWorker().stateProperty().addListener((observable, oldValue, newValue) -> {

            if (newValue == Worker.State.SUCCEEDED) {
                JSObject window = (JSObject) webEngine.executeScript("window");
                if (window.getMember("app").equals("undefined")) {
                    threadService.runActionLater(new Runnable() {
                        @Override
                        public void run() {
                            window.setMember("app", controller);
                            try{
                                current.currentEngine().executeScript("updateOptions()");
                                controller.applySohrtCuts();
                            }catch (Exception e){
                                threadService.runActionLater(this);
                            }
                        }
                    });
                }
            }

        });

        webEngine.load(String.format("http://localhost:%d/editor.html", controller.getPort()));
        return webView;
    }
}

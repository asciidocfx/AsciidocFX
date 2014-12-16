package com.kodcu.service;

import com.kodcu.controller.AsciiDocController;
import com.kodcu.other.Item;
import javafx.application.Platform;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by usta on 12.07.2014.
 */
@Component
public class FileBrowseService {

    @Autowired
    private AsciiDocController asciiDocController;

    @Autowired
    private PathResolverService pathResolver;

    private TreeItem<Item> rootItem;

    public void browse(final TreeView<Item> treeView, final AsciiDocController controller, final Path browserPath) {

        Platform.runLater(() -> {

            rootItem = new TreeItem<>(new Item(browserPath, String.format("Workdir (%s)", browserPath)));
            rootItem.setExpanded(true);
            final List<Path> files = new LinkedList<>();
            try {
                Files.newDirectoryStream(browserPath).forEach(path -> files.add(path));
            } catch (final IOException e) {
                e.printStackTrace();
            }

            treeView.setRoot(rootItem);

            Collections.sort(files);
            files.forEach(path -> {
                addToTreeView(path);
            });

        });
    }

    private void addToTreeView(Path path) {

        if (pathResolver.isHidden(path))
            return;

        if (Files.isDirectory(path) || pathResolver.isAsciidoc(path) || pathResolver.isImage(path))
            rootItem.getChildren().add(new TreeItem<>(new Item(path)));

    }

}

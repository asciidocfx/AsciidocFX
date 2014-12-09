package com.kodcu.service;

import com.kodcu.controller.AsciiDocController;
import com.kodcu.other.Item;
import javafx.application.Platform;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.*;

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

    public void browse(TreeView<Item> treeView, AsciiDocController controller, Path browserPath) {

        Platform.runLater(() -> {

            rootItem = new TreeItem<>(new Item(browserPath, String.format("Working Directory (%s)", browserPath)));
            rootItem.setExpanded(true);
            DirectoryStream<Path> files = null;
            try {
                files = Files.newDirectoryStream(browserPath);
            } catch (IOException e) {
                e.printStackTrace();
            }

            treeView.setRoot(rootItem);

            files.forEach(path -> {
                addToTreeView(path);
            });

        });
    }

    private void addToTreeView(Path path) {

        if (pathResolver.isHidden(path))
            return;

        if (Files.isDirectory(path) || pathResolver.isAsciidoc(path))
            rootItem.getChildren().add(new TreeItem<>(new Item(path)));

    }

}

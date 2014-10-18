package com.kodcu.service;

import com.kodcu.controller.AsciiDocController;
import com.kodcu.other.Item;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.util.Callback;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.*;
import java.util.Objects;

/**
 * Created by usta on 12.07.2014.
 */
@Component
public class FileBrowseService {

    @Autowired
    AsciiDocController asciiDocController;

    private TreeItem<Item> rootItem;
    private PathMatcher matcher = FileSystems.getDefault().getPathMatcher("glob:**.{asciidoc,adoc,asc,ad,txt}");

    public void browse(TreeView<Item> treeView, AsciiDocController controller, String browserPath)   {

        Platform.runLater(() -> {

            rootItem = new TreeItem<>(new Item(Paths.get(browserPath), String.format("Working Directory (%s)", browserPath)));
            rootItem.setExpanded(true);
            DirectoryStream<Path> files = null;
            try {
                Path dir = Paths.get(browserPath);
                files = Files.newDirectoryStream(dir);
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

        if (Files.isDirectory(path) && !path.getFileName().toString().startsWith("."))
            rootItem.getChildren().add(new TreeItem<>(new Item(path)));
        else if (matcher.matches(path))
            rootItem.getChildren().add(new TreeItem<>(new Item(path)));

    }

}

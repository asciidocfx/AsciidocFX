package com.kodcu.service;

import com.kodcu.controller.AsciiDocController;
import com.kodcu.other.Item;
import javafx.application.Platform;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.*;
import java.util.Objects;

/**
 * Created by usta on 12.07.2014.
 */
@Component
public class FileBrowseService {

    private TreeItem<Item> rootItem;
    private PathMatcher matcher = FileSystems.getDefault().getPathMatcher("glob:**.{asciidoc,adoc,asc,ad,txt}");

    public void browse(TreeView<Item> treeView, AsciiDocController controller, String browserPath) {

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

            treeView.setOnMouseClicked(event -> {
                TreeItem<Item> selectedItem = treeView.getSelectionModel().getSelectedItem();
                if (Objects.isNull(selectedItem))
                    return;
                Path selectedPath = selectedItem.getValue().getPath();
                if (Files.isDirectory(selectedPath)) {
                    try {
                        if (selectedItem.getChildren().size() == 0)
                            Files.newDirectoryStream(selectedPath).forEach(path -> {
                                selectedItem.getChildren().add(new TreeItem<>(new Item(path)));
                            });
                        selectedItem.setExpanded(!selectedItem.isExpanded());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else if (event.getClickCount() > 1) {
                    controller.addTab(selectedPath);
                }
            });
        });
    }

    private void addToTreeView(Path path) {

        if (Files.isDirectory(path))
            rootItem.getChildren().add(new TreeItem<>(new Item(path)));
        else if (matcher.matches(path))
            rootItem.getChildren().add(new TreeItem<>(new Item(path)));

    }

}

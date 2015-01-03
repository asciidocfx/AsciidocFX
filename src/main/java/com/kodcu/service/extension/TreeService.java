package com.kodcu.service.extension;

import com.kodcu.controller.ApplicationController;
import com.kodcu.other.Current;
import com.kodcu.other.IOHelper;
import com.kodcu.other.Tuple;
import com.kodcu.service.ThreadService;
import com.kodcu.service.ui.AwesomeService;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.WritableImage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.awt.image.BufferedImage;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Created by usta on 25.12.2014.
 */
@Component
public class TreeService {

    private Logger logger = LoggerFactory.getLogger(TreeService.class);

    @Autowired
    private Current current;

    @Autowired
    private ApplicationController controller;

    @Autowired
    private ThreadService threadService;

    @Autowired
    private AwesomeService awesomeService;


    public void createFileTree(String tree, String type, String fileName, String width, String height) {

        Objects.requireNonNull(fileName);

        if (!fileName.endsWith(".png") && !"ascii".equalsIgnoreCase(type))
            return;

        if ("ascii".equalsIgnoreCase(type)) {
            return;
        }
        // default: png
        else {

            Path path = current.currentPath().get().getParent();
            Path treePath = path.resolve("images/").resolve(fileName);

            if (!current.currentPath().isPresent())
                controller.saveDoc();

            Integer cacheHit = current.getCache().get(fileName);

            int hashCode = (fileName + type + tree + width + height).hashCode();
            if (Objects.isNull(cacheHit) || hashCode != cacheHit) {

                TreeView<Tuple<Integer, String>> fileView = new TreeView<>();

                fileView.getStyleClass().add("file-tree");
                fileView.setLayoutX(-99999);
                fileView.setLayoutY(-99999);

                try {
                    List<String> strings = Arrays.asList(tree.split("\n"));
                    List<TreeItem<Tuple<Integer, String>>> treeItems = strings.stream()
                            .map(s -> {
                                int level = StringUtils.countOccurrencesOf(s, "#");
                                String value = s.replace(" ", "").replace("#", "");
                                return new Tuple<Integer, String>(level, value);
                            })
                            .map(t -> {
                                Node icon = awesomeService.getIcon(Paths.get(t.getValue()));
                                TreeItem<Tuple<Integer, String>> treeItem = new TreeItem<>(t, icon);
                                treeItem.setExpanded(true);

                                return treeItem;
                            })
                            .collect(Collectors.toList());


                    for (int index = 0; index < treeItems.size(); index++) {

                        TreeItem<Tuple<Integer, String>> currentItem = treeItems.get(index);
                        Tuple<Integer, String> currentItemValue = currentItem.getValue();

                        if (Objects.isNull(fileView.getRoot())) {

                            fileView.setRoot(currentItem);

                            continue;
                        }

                        TreeItem<Tuple<Integer, String>> lastItem = treeItems.get(index - 1);
                        int lastPos = lastItem.getValue().getKey();

                        if (currentItemValue.getKey() > lastPos) {

                            lastItem.getChildren().add(currentItem);
                            continue;
                        }

                        if (currentItemValue.getKey() == lastPos) {

                            TreeItem<Tuple<Integer, String>> parent = lastItem.getParent();
                            if (Objects.isNull(parent))
                                parent = fileView.getRoot();
                            parent.getChildren().add(currentItem);
                            continue;
                        }

                        if (currentItemValue.getKey() < lastPos) {

                            List<TreeItem<Tuple<Integer, String>>> collect = treeItems.stream()
                                    .filter(t -> t.getValue().getKey() == currentItemValue.getKey())
                                    .collect(Collectors.toList());

                            if (collect.size() > 0) {

                                TreeItem<Tuple<Integer, String>> parent = fileView.getRoot();

                                try {
                                    TreeItem<Tuple<Integer, String>> treeItem = collect.get(collect.indexOf(currentItem) - 1);
                                    parent = treeItem.getParent();
                                } catch (RuntimeException e) {
                                    logger.info(e.getMessage(), e);
                                }

                                parent.getChildren().add(currentItem);
                            }
                            continue;
                        }

                    }
                    fileView.setMaxHeight(2500);
                    fileView.setPrefWidth(250);
                    fileView.setPrefHeight(treeItems.size() * 24);


                    try {
                        Double value = Double.valueOf(width);

                        if (width.contains("+") || width.contains("-"))
                            fileView.setPrefWidth(fileView.getPrefWidth() + value);
                        else
                            fileView.setPrefWidth(value);
                    } catch (Exception e) {
                        logger.debug(e.getMessage(), e);
                    }

                    try {
                        Double value = Double.valueOf(height);

                        if (height.contains("+") || height.contains("-"))
                            fileView.setPrefHeight(fileView.getPrefHeight() + value);
                        else
                            fileView.setPrefHeight(value);
                    } catch (Exception e) {
                        logger.debug(e.getMessage(), e);
                    }

                    threadService.runActionLater(run->{
                        controller.getRootAnchor().getChildren().add(fileView);
                        WritableImage writableImage = fileView.snapshot(new SnapshotParameters(), null);

                        BufferedImage bufferedImage = SwingFXUtils.fromFXImage(writableImage, null);

                        IOHelper.createDirectories(path.resolve("images"));
                        IOHelper.imageWrite(bufferedImage, "png", treePath.toFile());

                        controller.getLastRenderedChangeListener()
                                .changed(null, controller.getLastRendered().getValue(), controller.getLastRendered().getValue());

                        controller.getRootAnchor().getChildren().remove(fileView);
                    });

                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                }
            }

            current.getCache().put(fileName, hashCode);
        }
    }
}

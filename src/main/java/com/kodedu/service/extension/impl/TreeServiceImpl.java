package com.kodedu.service.extension.impl;

import com.kodedu.config.ExtensionConfigBean;
import com.kodedu.controller.ApplicationController;
import com.kodedu.helper.IOHelper;
import com.kodedu.helper.StyleHelper;
import com.kodedu.other.Current;
import com.kodedu.other.TrimWhite;
import com.kodedu.other.Tuple;
import com.kodedu.service.ThreadService;
import com.kodedu.service.cache.BinaryCacheService;
import com.kodedu.service.extension.TreeService;
import com.kodedu.service.ui.AwesomeService;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.WritableImage;
import javafx.scene.web.WebView;
import netscape.javascript.JSObject;
import org.kordamp.ikonli.fontawesome.FontAwesome;
import org.kordamp.ikonli.javafx.FontIcon;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;

import java.awt.image.BufferedImage;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Created by usta on 25.12.2014.
 */
@Component(TreeService.label)
public class TreeServiceImpl implements TreeService {
    private final Logger logger = LoggerFactory.getLogger(TreeService.class);

    private Current current;
    private ApplicationController controller;
    private final ExtensionConfigBean extensionConfigBean;
    @Autowired
    private ThreadService threadService;
    @Autowired
    private BinaryCacheService binaryCacheService;
    @Autowired
    private AwesomeService awesomeService;

    Pattern pattern = Pattern.compile("^(addw|minw|setw|addh|minh|seth|scale):\\s*(\\d+)$");

    @Value("${application.treeview.url}")
    private String treeviewUrl;

    @Autowired
    public TreeServiceImpl(final Current current, final ApplicationController controller, ExtensionConfigBean extensionConfigBean) {
        this.current = current;
        this.controller = controller;
        this.extensionConfigBean = extensionConfigBean;
    }


    @Override
    public void createFileTree(String tree, String type, String imagesDir,
                               String imageTarget, String nodename, CompletableFuture completed) {

        Objects.requireNonNull(imageTarget);

        boolean cachedResource = imageTarget.contains("/afx/cache");

        if (!imageTarget.contains(".png") && !cachedResource){
            completed.complete(null);
            return;
        }

        Integer cacheHit = current.getCache().get(imageTarget);

        int hashCode = (imageTarget + imagesDir + type + tree).hashCode();
        if (Objects.isNull(cacheHit) || hashCode != cacheHit) {

            logger.debug("Tree extension is started for {}", imageTarget);

            try {
                List<String> strings = Arrays.asList(tree.split("\\r?\\n"));

                Map<String, Integer> settings = newSettings();

                List<TreeItem<Tuple<Integer, String>>> treeItems = strings.stream()
                        .map(s -> {

                            if (s.isEmpty()) {
                                return null;
                            }

                            Matcher matcher = pattern.matcher(s);
                            if (matcher.matches() && matcher.groupCount() == 2) {
                                String command = matcher.group(1);
                                String value = matcher.group(2);

                                settings.put(command, Integer.valueOf(value));
                                return null;
                            }

                            if (!s.contains("#")) {
                                return null;
                            }

                            int level = StringUtils.countOccurrencesOf(s, "#");
                            String value = s.replace(" ", "").replace("#", "");
                            return new Tuple<Integer, String>(level, value);
                        })
                        .filter(Objects::nonNull)
                        .map(t -> {
                            Node icon = awesomeService.getIcon(IOHelper.getPath(t.getValue()));
                            TreeItem<Tuple<Integer, String>> treeItem = new TreeItem<>(t, icon);
                            treeItem.setExpanded(true);

                            return treeItem;
                        })
                        .collect(Collectors.toList());

                TreeView fileView = getSnaphotTreeView();
                Integer scale = settings.get("scale");

                if (Objects.isNull(scale)) {
                    scale = extensionConfigBean.getDefaultImageScale();
                }

                fileView.setScaleX(scale);
                fileView.setScaleY(scale);

                for (int index = 0; index < treeItems.size(); index++) {

                    TreeItem<Tuple<Integer, String>> currentItem = treeItems.get(index);
                    Tuple<Integer, String> currentItemValue = currentItem.getValue();

                    if (index == 0) {
                        fileView.setRoot(currentItem);
                        continue;
                    }

                    TreeItem<Tuple<Integer, String>> lastItem = treeItems.get(index - 1);
                    int lastPos = lastItem.getValue().getKey();

                    if (currentItemValue.getKey() > lastPos) {

                        applyFolderIcon(lastItem);
                        lastItem.getChildren().add(currentItem);
                        continue;
                    }

                    if (currentItemValue.getKey() == lastPos) {

                        TreeItem<Tuple<Integer, String>> parent = lastItem.getParent();
                        if (Objects.isNull(parent))
                            parent = fileView.getRoot();
                        applyFolderIcon(parent);
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

                            if (parent != null) {
                                applyFolderIcon(parent);
                                parent.getChildren().add(currentItem);
                            } else {
                                throw new IllegalStateException("Tree view can't have multiple root parent. Current item: " + currentItemValue.getValue());
                            }

                        }
                        continue;
                    }

                }

                Path currentDir = current.currentTab().getParentOrWorkdir();

                int changeWidth = (settings.get("addw") - settings.get("minw"));
                int changeHeight = (settings.get("addh") - settings.get("minh"));

                threadService.runActionLater(() -> {
                    controller.getRootAnchor().getChildren().add(fileView);

                    if (settings.get("setw") > 0) {
                        fileView.setPrefWidth(settings.get("setw"));
                    } else {
                        fileView.setPrefWidth(300 + changeWidth);
                    }

                    if (settings.get("seth") > 0) {
                        fileView.setPrefHeight(settings.get("seth"));
                    } else {
                        fileView.setPrefHeight((treeItems.size() * 26) + 15 + changeHeight);
                    }

                    WritableImage writableImage = fileView.snapshot(new SnapshotParameters(), null);
                    BufferedImage bufferedImage = SwingFXUtils.fromFXImage(writableImage, null);

                    if (!cachedResource) {
                        Path imagePath = Paths.get(imageTarget);
                        IOHelper.imageWrite(bufferedImage, "png", imagePath.toFile());
                        controller.clearImageCache(imagePath);

                    } else {
                        binaryCacheService.putBinary(imageTarget, bufferedImage);
                        controller.clearImageCache(imageTarget);
                    }

                    logger.debug("Tree extension is ended for {}", imageTarget);

                    controller.getRootAnchor().getChildren().remove(fileView);

                    completed.complete(null);

                });

            } catch (Exception e) {
                logger.error("Problem occured while generating Filesystem Tree", e);
            }
        } else {
            completed.complete(null);
        }

        current.getCache().put(imageTarget, hashCode);
    }

    private void applyFolderIcon(TreeItem<Tuple<Integer, String>> lastItem) {
        Node icon = new Label(null, new FontIcon(FontAwesome.FOLDER_O));
        lastItem.setGraphic(icon);
    }

    private TreeView getSnaphotTreeView() {
        TreeView fileView = new TreeView();
        StyleHelper.addClass(fileView, "tree-extension");
        fileView.setLayoutX(-14000);
        fileView.setLayoutY(-14000);
        fileView.setMinSize(0, 0);
        return fileView;
    }

    @Override
    public void createHighlightFileTree(String tree, String type, String imagesDir,
                                        String imageTarget, String nodename, CompletableFuture completed) {
        Objects.requireNonNull(imageTarget);

        boolean cachedResource = imageTarget.contains("/afx/cache");

        if (!imageTarget.contains(".png") && !cachedResource){
            completed.complete(null);
            return;
        }

        Integer cacheHit = current.getCache().get(imageTarget);

        int hashCode = (imageTarget + imagesDir + type + tree).hashCode();
        if (Objects.isNull(cacheHit) || hashCode != cacheHit) {

            Path path = current.currentTab().getParentOrWorkdir();

            threadService.runActionLater(() -> {

                WebView treeview = new WebView();
                treeview.setLayoutX(-21000);
                treeview.setLayoutY(-21000);
                treeview.setMinSize(0, 0);

                int zoom = extensionConfigBean.getDefaultImageZoom();

                treeview.setPrefSize(300, 600);
                treeview.setZoom(zoom);

                controller.getRootAnchor().getChildren().add(treeview);

                threadService.runActionLater(() -> {
                    treeview.getEngine().load(String.format(treeviewUrl, controller.getPort()));
                });

                treeview.getEngine().setOnAlert(event -> {
                    String data = event.getData();
                    if ("READY".equals(data)) {
                        JSObject window = (JSObject) treeview.getEngine().executeScript("window");
                        window.setMember("treeview", treeview);
                        window.call("executeTree", tree);
                    }
                    if ("RENDERED".equals(data)) {

                        WritableImage writableImage = treeview.snapshot(new SnapshotParameters(), null);
                        BufferedImage bufferedImage = SwingFXUtils.fromFXImage(writableImage, null);

                        threadService.runTaskLater(() -> {
                            TrimWhite trimWhite = new TrimWhite();
                            BufferedImage trimmed = trimWhite.trim(bufferedImage);

                            if (!cachedResource) {

                                Path imagePath = Paths.get(imageTarget);
                                IOHelper.imageWrite(trimmed, "png", imagePath.toFile());
                                controller.clearImageCache(imagePath);
                            } else {
                                binaryCacheService.putBinary(imageTarget, trimmed);
                                controller.clearImageCache(imageTarget);
                            }
                            completed.complete(null);

                            threadService.runActionLater(() -> {
                                controller.getRootAnchor().getChildren().remove(treeview);
                            });
                        });

                    }
                });
            });

        } else {
            completed.complete(null);
        }

        current.getCache().put(imageTarget, hashCode);
    }
}

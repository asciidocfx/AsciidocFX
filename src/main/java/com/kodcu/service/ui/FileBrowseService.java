package com.kodcu.service.ui;

import com.kodcu.controller.ApplicationController;
import com.kodcu.other.Item;
import com.kodcu.service.PathOrderService;
import com.kodcu.service.PathResolverService;
import com.kodcu.service.ThreadService;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Created by usta on 12.07.2014.
 */
@Component
public class FileBrowseService {

    private Logger logger = LoggerFactory.getLogger(FileBrowseService.class);

    private final PathOrderService pathOrder;
    private final ThreadService threadService;
    private final PathResolverService pathResolver;
    private final AwesomeService awesomeService;
    private final ApplicationController controller;

    private TreeItem<Item> rootItem;

    private Integer lastSelectedItem;

    @Autowired
    public FileBrowseService(final PathOrderService pathOrder, final ThreadService threadService, final PathResolverService pathResolver,
                             final AwesomeService awesomeService, ApplicationController controller) {
        this.pathOrder = pathOrder;
        this.threadService = threadService;
        this.pathResolver = pathResolver;
        this.awesomeService = awesomeService;
        this.controller = controller;
    }

    public void browse(final Path path) {

        threadService.runActionLater(() -> {

            controller.getWorkDirTabPane().getSelectionModel().selectFirst(); // fix

            TreeView<Item> treeView = controller.getFileSystemView();

            int selectedIndex = treeView.getSelectionModel().getSelectedIndex();
            if (selectedIndex != -1)
                lastSelectedItem = selectedIndex;

            rootItem = new TreeItem<>(new Item(path, String.format("Loading... (%s)", Optional.of(path).map(Path::getFileName).orElse(path))), awesomeService.getIcon(path));
            rootItem.setExpanded(true);

            treeView.setRoot(rootItem);

            threadService.runTaskLater(() -> {

                this.addPathToTree(path);

                if (Objects.nonNull(lastSelectedItem)) {
                    threadService.runActionLater(() -> {
                        treeView.getSelectionModel().select(lastSelectedItem);
                    });
                }

                logger.debug("Filesystem Tree relisted for {}", path);
            });

        }, true);
    }


    public void addPathToTree(Path path) {

        try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(path);) {

            List<TreeItem<Item>> subItemList = StreamSupport
                    .stream(directoryStream.spliterator(), false)
                    .filter(p -> !pathResolver.isHidden(p))
                    .filter(pathResolver::isViewable)
                    .sorted(pathOrder::comparePaths)
                    .map(p -> new TreeItem<>(new Item(p), awesomeService.getIcon(p)))
                    .collect(Collectors.toList());

            threadService.runActionLater(() -> {
                rootItem = new TreeItem<>(new Item(path, String.format("%s", Optional.of(path).map(Path::getFileName).orElse(path))), awesomeService.getIcon(path));
                rootItem.setExpanded(true);
                rootItem.getChildren().addAll(subItemList);
                controller.getFileSystemView().setRoot(rootItem);
            }, true);

        } catch (Exception e) {
            logger.error("Problem occured while updating WorkDir panel", e);
        }


    }

}

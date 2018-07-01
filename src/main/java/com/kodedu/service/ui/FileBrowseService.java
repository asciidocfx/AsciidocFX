package com.kodedu.service.ui;

import com.kodedu.controller.ApplicationController;
import com.kodedu.other.Current;
import com.kodedu.other.IOHelper;
import com.kodedu.other.Item;
import com.kodedu.service.FileWatchService;
import com.kodedu.service.PathOrderService;
import com.kodedu.service.PathResolverService;
import com.kodedu.service.ThreadService;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.nio.file.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.regex.PatternSyntaxException;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static com.kodedu.other.IOHelper.isHidden;

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
    private final Current current;

    @Autowired
    private FileWatchService fileWatchService;

    private final Map<Path, TreeItem<Item>> directoryItemMap = new ConcurrentHashMap();
    private final Map<Path, TreeItem<Item>> pathItemMap = new ConcurrentHashMap();
    private Set<Path> lastSelectedItems = new HashSet<>();
    private PathItem rootItem;
    private TreeView<Item> treeView;
    private ScrollState verticalScrollState;
    private ScrollState horizontalScrollState;
    private Path browsedPath;


    @Autowired
    public FileBrowseService(final PathOrderService pathOrder, final ThreadService threadService, final PathResolverService pathResolver,
                             final AwesomeService awesomeService, ApplicationController controller, Current current) {
        this.pathOrder = pathOrder;
        this.threadService = threadService;
        this.pathResolver = pathResolver;
        this.awesomeService = awesomeService;
        this.controller = controller;
        this.current = current;
    }

    public void refresh() {
        if (Objects.nonNull(browsedPath)) {
            browse(browsedPath);
        }
    }

    public void browse(final Path path) {

        if (path != browsedPath) {
            this.verticalScrollState = new ScrollState();
            this.horizontalScrollState = new ScrollState();
            initializeScrollListener();
        }

        this.browsedPath = path;

        threadService.runActionLater(() -> {

            current.currentEditor().updatePreviewUrl();

            this.treeView = controller.getFileSystemView();

            rootItem = new PathItem(new Item(path, String.format("%s", Optional.of(path).map(Path::getFileName).orElse(path))), awesomeService.getIcon(path));
            rootItem.getChildren().add(new PathItem(new Item(null, "Loading..")));

            treeView.setRoot(rootItem);
            rootItem.setExpanded(true);

            this.addPathToTree(path, rootItem, null);

            logger.info("File browser relisted for {}", path);

        }, true);
    }

    private void initializeScrollListener() {
        threadService.runActionLater(() -> {
            this.treeView = controller.getFileSystemView();

            Set<Node> nodes = this.treeView.lookupAll(".scroll-bar");
            for (Node node : nodes) {
                ScrollBar scrollBar = (ScrollBar) node;
                if (scrollBar.getOrientation() == Orientation.VERTICAL) {
                    verticalScrollState.updateState(scrollBar);
                    scrollBar.valueProperty().addListener((observable, oldValue, newValue) -> {
                        verticalScrollState.updateState(scrollBar, newValue);
                    });
                } else if (scrollBar.getOrientation() == Orientation.HORIZONTAL) {
                    horizontalScrollState.updateState(scrollBar);
                    scrollBar.valueProperty().addListener((observable, oldValue, newValue) -> {
                        horizontalScrollState.updateState(scrollBar, newValue);
                    });
                }
            }
        });
    }

    public void addPathToTree(Path path, final TreeItem<Item> treeItem, Path changedPath) {

        threadService.runTaskLater((() -> {

            if (Objects.isNull(path) || Objects.isNull(treeItem)) {
                return;
            }

            if (!Files.isDirectory(path)) {
                return;
            }

            if (!Files.exists(path)) {
                return;
            }

            if (treeItem == treeView.getRoot()) { // is root
                pathItemMap.clear();
                directoryItemMap.clear();
                fileWatchService.reCreateWatchService();
            }

            directoryItemMap.put(path, treeItem);
            pathItemMap.put(path, treeItem);

            try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(path);) {

                List<TreeItem<Item>> subItemList = StreamSupport
                        .stream(directoryStream.spliterator(), false)
                        .filter(p -> !(controller.isSkipHiddenFiles() && isHidden(p)))
                        .sorted(pathOrder::comparePaths)
                        .map(p -> {
                            TreeItem<Item> childItem = new PathItem(new Item(p), awesomeService.getIcon(p));
                            if (Files.isDirectory(p)) {
                                if (!IOHelper.isEmptyDir(p)) {
                                    childItem.getChildren().add(new PathItem(new Item(null, "Loading..")));
                                }
                                childItem.setExpanded(false);
                                childItem.expandedProperty().addListener((observable, oldValue, newValue) -> {
                                    if (newValue) {
                                        addPathToTree(childItem.getValue().getPath(), childItem, null);
                                    }

                                    // fixes not expand issue
                                    treeView.refresh();
                                });
                            }
                            pathItemMap.put(p, childItem);
                            return childItem;
                        })
                        .collect(Collectors.toList());

                threadService.runActionLater(() -> {

                    saveTreeSelectionState();
                    boolean treeViewFocused = treeView.isFocused();
                    treeItem.getChildren().clear();
                    treeView.getSelectionModel().clearSelection();

                    treeItem.getChildren().addAll(subItemList);

                    restoreTreeSelectionState();
                    restoreTreeScrollState();
                    if (treeViewFocused) {
                        treeView.requestFocus();
                    }

                    if (Objects.nonNull(changedPath)) {
                        TreeItem<Item> item = pathItemMap.get(changedPath);
                        if (Objects.nonNull(item)) {
                            treeView.getSelectionModel().clearSelection();
                            treeView.getSelectionModel().select(item);
                            treeView.scrollTo(findIndex(item));

                            TreeItem<Item> parent = item.getParent();
                            if (Objects.nonNull(parent)) {
                                if (!parent.isExpanded()) {
                                    parent.setExpanded(true);
                                }
                            }
                        }
                    }

                    fileWatchService.registerPathWatcher(path);
                });

            } catch (Exception e) {
                logger.warn("Problem occured while updating file browser", e);
            }

        }));

    }

    private void restoreTreeScrollState() {

        threadService.schedule(() -> { // run after some ms
            threadService.runActionLater(() -> { // run in ui thread

                Set<Node> nodes = this.treeView.lookupAll(".scroll-bar");
                for (Node node : nodes) {
                    ScrollBar scrollBar = (ScrollBar) node;
                    if (scrollBar.getOrientation() == Orientation.VERTICAL) {
                        verticalScrollState.restoreState(scrollBar);
                    } else if (scrollBar.getOrientation() == Orientation.HORIZONTAL) {
                        horizontalScrollState.restoreState(scrollBar);
                    }
                }
            });
        }, 50, TimeUnit.MILLISECONDS);
    }

    private void restoreTreeSelectionState() {
        for (Path lastSelectedPath : lastSelectedItems) {
            TreeItem<Item> item = pathItemMap.get(lastSelectedPath);
            if (Objects.nonNull(item)) {
                treeView.getSelectionModel().select(item);
            }
        }
    }

    private void saveTreeSelectionState() {
        try {
            lastSelectedItems = treeView.getSelectionModel()
                    .getSelectedItems()
                    .stream()
                    .map(TreeItem::getValue)
                    .filter(Objects::nonNull)
                    .map(Item::getPath)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet());

        } catch (Exception ex) {

        }
    }

    public void refreshPathToTree(Path path, Path changedPath) {

        TreeItem<Item> item = directoryItemMap.get(path);
        addPathToTree(path, item, changedPath);

    }

    TreeItem<Item> searchFoundItem;

    public void searchUpAndSelect(String text) {

        threadService.runTaskLater(() -> {
            List<TreeItem<Item>> foundItems = searchItems(text);

            if (foundItems.isEmpty()) {
                return;
            }

            ListIterator<TreeItem<Item>> listIterator = foundItems.listIterator();

            while (true) {

                if (Objects.isNull(searchFoundItem)) {
                    if (listIterator.hasNext()) {
                        searchFoundItem = listIterator.next();
                    }
                    break;
                }

                if (listIterator.hasNext()) {
                    TreeItem<Item> next = listIterator.next();
                    if (next.getValue().equals(searchFoundItem.getValue())) {
                        if (listIterator.hasNext()) {
                            TreeItem<Item> nexted = listIterator.next();

                            if (next == nexted) {
                                if (listIterator.hasNext()) {
                                    nexted = listIterator.next();
                                }
                            }

                            searchFoundItem = nexted;
                            break;
                        }
                    }
                } else {
                    break;
                }

            }

            focusFoundItem(searchFoundItem);
        });
    }

    public void searchDownAndSelect(String text) {

        threadService.runTaskLater(() -> {
            List<TreeItem<Item>> foundItems = searchItems(text);

            if (foundItems.isEmpty()) {
                return;
            }

            ListIterator<TreeItem<Item>> listIterator = foundItems.listIterator();

            while (true) {

                if (Objects.isNull(searchFoundItem)) {
                    if (listIterator.hasPrevious()) {
                        searchFoundItem = listIterator.previous();
                    }

                    break;
                }

                if (listIterator.hasNext()) {
                    TreeItem<Item> next = listIterator.next();
                    if (next.getValue().equals(searchFoundItem.getValue())) {
                        if (listIterator.hasPrevious()) {
                            TreeItem<Item> previous = listIterator.previous();
                            if (next == previous) {
                                if (listIterator.hasPrevious()) {
                                    previous = listIterator.previous();
                                }
                            }
                            searchFoundItem = previous;
                            break;
                        }
                    }
                } else {
                    break;
                }

            }

            focusFoundItem(searchFoundItem);
        });
    }

    private void focusFoundItem(TreeItem<Item> searchFoundItem) {
        if (Objects.nonNull(searchFoundItem)) {

            TreeView<Item> fileSystemView = controller.getFileSystemView();
            threadService.runActionLater(() -> {

                fileSystemView.getSelectionModel().clearSelection();

                int selectedIndex = findIndex(searchFoundItem);

                fileSystemView.getSelectionModel().select(searchFoundItem);

                fileSystemView.scrollTo(selectedIndex);

                TreeItem<Item> parent = searchFoundItem.getParent();
                if (Objects.nonNull(parent)) {
                    if (!parent.isExpanded()) {
                        parent.setExpanded(true);
                    }
                }
            }, true);
        }
    }

    private List<TreeItem<Item>> searchItems(String text) {

        PathMatcher pathMatcher = null;

        try {
            String syntaxAndPattern = String.format("glob:**%s**", text);
            pathMatcher = FileSystems.getDefault().getPathMatcher(syntaxAndPattern);
        } catch (PatternSyntaxException psex) {
            return new ArrayList<>();
        }

        final PathMatcher finalPathMatcher = pathMatcher;

        Optional.ofNullable(searchFoundItem)
                .map(TreeItem::getValue)
                .map(Item::getPath)
                .filter(p -> !finalPathMatcher.matches(p))
                .ifPresent(p -> searchFoundItem = null);

        if (Objects.nonNull(searchFoundItem)) {
            if (!pathItemMap.containsValue(searchFoundItem)) {
                searchFoundItem = null;
            }
        }

        return pathItemMap.values()
                .stream()
                .map(e -> Optional.ofNullable(e))
                .filter(o -> o
                        .map(TreeItem::getValue)
                        .map(Item::getPath)
                        .filter(p -> !p.equals(p.getRoot()))
                        .filter(p -> finalPathMatcher.matches(p))
                        .isPresent())
                .map(e -> e.get())
                .sorted((p1, p2) -> pathOrder.comparePaths(p1.getValue().getPath(), p2.getValue().getPath()))
                .collect(Collectors.toList());
    }

    public int findIndex(TreeItem<Item> changedItem) {

        TreeItem<Item> item = changedItem;
        int result = 0;
        while (true) {
            TreeItem<Item> parent = item.getParent();
            if (Objects.isNull(parent)) {
                break;
            }

            int index = parent.getChildren().indexOf(item);
            result += index;
            item = parent;
        }

        return result;
    }

    public void searchAndSelect(String text) {

        threadService.runTaskLater(() -> {

            List<TreeItem<Item>> foundItems = searchItems(text.trim());

            if (foundItems.isEmpty()) {
                return;
            }

            ListIterator<TreeItem<Item>> listIterator = foundItems.listIterator();


            if (Objects.isNull(searchFoundItem)) {
                if (listIterator.hasNext()) {
                    searchFoundItem = listIterator.next();
                }
            }

            focusFoundItem(searchFoundItem);
        });
    }
}

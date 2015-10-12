package com.kodcu.service.ui;

import com.kodcu.component.EditorPane;
import com.kodcu.component.ImageTab;
import com.kodcu.component.MenuItemBuilt;
import com.kodcu.component.MyTab;
import com.kodcu.config.StoredConfigBean;
import com.kodcu.controller.ApplicationController;
import com.kodcu.other.Current;
import com.kodcu.other.ExtensionFilters;
import com.kodcu.other.IOHelper;
import com.kodcu.other.Item;
import com.kodcu.service.DirectoryService;
import com.kodcu.service.ParserService;
import com.kodcu.service.PathResolverService;
import com.kodcu.service.ThreadService;
import com.kodcu.service.extension.AsciiTreeGenerator;
import com.kodcu.service.shortcut.ShortcutProvider;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.value.ObservableObjectValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Created by usta on 25.12.2014.
 */
@Component
public class TabService {

    private final Logger logger = LoggerFactory.getLogger(TabService.class);

    private final ApplicationController controller;
    private final EditorService editorService;
    private final PathResolverService pathResolver;
    private final ThreadService threadService;
    private final Current current;
    private final DirectoryService directoryService;
    private final StoredConfigBean storedConfigBean;
    private final ParserService parserService;
    private final ApplicationContext applicationContext;
    private final ShortcutProvider shortcutProvider;
    private final AsciiTreeGenerator asciiTreeGenerator;

    @Value("${application.editor.url}")
    private String editorUrl;

    @Value("${application.epub.url}")
    private String epubUrl;

    private ObservableList<Optional<Path>> closedPaths = FXCollections.observableArrayList();


    @Autowired
    public TabService(final ApplicationController controller, final EditorService editorService,
                      final PathResolverService pathResolver, final ThreadService threadService, final Current current,
                      final DirectoryService directoryService, StoredConfigBean storedConfigBean, ParserService parserService, ApplicationContext applicationContext, ShortcutProvider shortcutProvider, AsciiTreeGenerator asciiTreeGenerator) {
        this.controller = controller;
        this.editorService = editorService;
        this.pathResolver = pathResolver;
        this.threadService = threadService;
        this.current = current;
        this.directoryService = directoryService;
        this.storedConfigBean = storedConfigBean;
        this.parserService = parserService;
        this.applicationContext = applicationContext;
        this.shortcutProvider = shortcutProvider;
        this.asciiTreeGenerator = asciiTreeGenerator;
    }


    public void addTab(Path path, Runnable... runnables) {

        ObservableList<Item> recentFiles = storedConfigBean.getRecentFiles();
        if (Files.notExists(path)) {
            recentFiles.remove(path.toString());
            logger.debug("Path {} not found in the filesystem", path);
            return;
        }

        ObservableList<Tab> tabs = controller.getTabPane().getTabs();
        for (Tab tab : tabs) {
            MyTab myTab = (MyTab) tab;
            Path currentPath = myTab.getPath();
            if (Objects.nonNull(currentPath))
                if (currentPath.equals(path)) {
                    myTab.select(); // Select already added tab
                    return;
                }
        }

        AnchorPane anchorPane = new AnchorPane();

        MyTab tab = createTab();
        tab.setTabText(path.getFileName().toString());
        EditorPane editorPane = tab.getEditorPane();

        threadService.runActionLater(() -> {
            TabPane tabPane = controller.getTabPane();
            tabPane.getTabs().add(tab);
            tab.select();
        });

        Node editorVBox = editorService.createEditorVBox(editorPane, tab);
        controller.fitToParent(editorVBox);

        anchorPane.getChildren().add(editorVBox);
        tab.setContent(anchorPane);
        tab.setPath(path);

        Tooltip tip = new Tooltip(path.toString());
        Tooltip.install(tab.getGraphic(), tip);

        recentFiles.remove(new Item(path));
        recentFiles.add(0, new Item(path));

        editorPane.getHandleReadyTasks().clear();
        editorPane.getHandleReadyTasks().addAll(runnables);

        editorPane.load(String.format(editorUrl, controller.getPort()));
    }


    public void newDoc() {
        newDoc("");
    }

    public void newDoc(final String content) {

        MyTab tab = this.createTab();
        EditorPane editorPane = tab.getEditorPane();
        editorPane.setInitialEditorValue(content);

        AnchorPane anchorPane = new AnchorPane();

        Node editorVBox = editorService.createEditorVBox(editorPane, tab);
        controller.fitToParent(editorVBox);
        anchorPane.getChildren().add(editorVBox);

        tab.setContent(anchorPane);

        tab.setTabText("new *");
        TabPane tabPane = controller.getTabPane();
        tabPane.getTabs().add(tab);
        tab.select();

        editorPane.load(String.format(editorUrl, controller.getPort()));
    }

    public void openDoc() {
        FileChooser fileChooser = directoryService.newFileChooser("Open File");
        fileChooser.getExtensionFilters().add(ExtensionFilters.ASCIIDOC);
        fileChooser.getExtensionFilters().add(ExtensionFilters.MARKDOWN);
        fileChooser.getExtensionFilters().add(ExtensionFilters.ALL);
        List<File> chosenFiles = fileChooser.showOpenMultipleDialog(controller.getStage());
        if (chosenFiles != null) {
            chosenFiles.stream().map(File::toPath).forEach(this::previewDocument);
            ObservableList<Item> recentFiles = storedConfigBean.getRecentFiles();
            chosenFiles.stream()
                    .map(e -> new Item(e.toPath()))
                    .filter(file -> !recentFiles.contains(file)).forEach(recentFiles::addAll);
            directoryService.setInitialDirectory(Optional.ofNullable(chosenFiles.get(0)));
        }
    }


    public Path getSelectedTabPath() {
        TreeItem<Item> selectedItem = controller.getFileSystemView().getSelectionModel().getSelectedItem();
        Item value = selectedItem.getValue();
        Path path = value.getPath();
        return path;
    }

    public MyTab createTab() {

        final MyTab tab = applicationContext.getBean(MyTab.class);

        tab.setOnCloseRequest(event -> {
            event.consume();
            tab.close();
        });

        MenuItem menuItem0 = new MenuItem("Close");
        menuItem0.setOnAction(actionEvent -> {
            tab.close();
        });

        MenuItem menuItem1 = new MenuItem("Close All");
        menuItem1.setOnAction(controller::closeAllTabs);

        MenuItem menuItem2 = new MenuItem("Close Others");
        menuItem2.setOnAction(event -> {

            ObservableList<Tab> blackList = FXCollections.observableArrayList();
            blackList.addAll(tab.getTabPane().getTabs());
            blackList.remove(tab);

            blackList.stream().map(t -> (MyTab) t).sorted((mo1, mo2) -> {
                if (mo1.isNew() && !mo2.isNew())
                    return -1;
                else if (mo2.isNew() && !mo1.isNew()) {
                    return 1;
                }
                return 0;
            }).forEach(myTab -> {

                if (event.isConsumed())
                    return;

                ButtonType close = myTab.close();
                if (close == ButtonType.CANCEL)
                    event.consume();
            });
        });
//
//        MenuItem menuItem3 = new MenuItem("Close Unmodified");
//        menuItem3.setOnAction(actionEvent -> {
//
//            ObservableList<Tab> clonedTabs = FXCollections.observableArrayList();
//            clonedTabs.addAll(controller.getTabPane().getTabs());
//
//
//            for (Tab clonedTab : clonedTabs) {
//                MyTab myTab = (MyTab) clonedTab;
//                if (!myTab.getTabText().contains(" *"))
//                    threadService.runActionLater(()->{
//                        myTab.close();
//                    });
//            }
//        });

        MenuItem menuItem4 = new MenuItem("Select Next Tab");
        menuItem4.setOnAction(actionEvent -> {
            TabPane tabPane = tab.getTabPane();
            if (tabPane.getSelectionModel().isSelected(tabPane.getTabs().size() - 1))
                tabPane.getSelectionModel().selectFirst();
            else
                tabPane.getSelectionModel().selectNext();
        });

        MenuItem menuItem5 = new MenuItem("Select Previous Tab");
        menuItem5.setOnAction(actionEvent -> {
            SingleSelectionModel<Tab> selectionModel = tab.getTabPane().getSelectionModel();
            if (selectionModel.isSelected(0))
                selectionModel.selectLast();
            else
                selectionModel.selectPrevious();
        });

        MenuItem menuItem6 = new MenuItem("Reopen Closed Tab");
        menuItem6.setOnAction(actionEvent -> {
            if (closedPaths.size() > 0) {
                int index = closedPaths.size() - 1;
                closedPaths.get(index).filter(pathResolver::isAsciidoc).ifPresent(this::addTab);
                closedPaths.get(index).filter(pathResolver::isMarkdown).ifPresent(this::addTab);
                closedPaths.get(index).filter(pathResolver::isImage).ifPresent(this::addImageTab);
                closedPaths.remove(index);
            }
        });

        MenuItem menuItem7 = new MenuItem("Browse");

        menuItem7.setOnAction(event -> {
            current.currentPath().ifPresent(path -> {
                controller.getHostServices().showDocument(path.getParent().toUri().toASCIIString());
            });
        });

        MenuItem copyItem = MenuItemBuilt.item("Copy").click(event -> {
            Optional.ofNullable(tab.getPath())
                    .ifPresent(controller::copyFile);
        });

        MenuItem copyPathItem = MenuItemBuilt.item("Copy Path").click(event -> {
            Optional.ofNullable(tab.getPath())
                    .map(Path::toString)
                    .ifPresent(controller::cutCopy);
        });

        MenuItem menuItem8 = new MenuItem("New File");
        menuItem8.setOnAction(controller::newDoc);

        MenuItem reloadMenuItem = new MenuItem("Reload");
        reloadMenuItem.setOnAction(event -> {
            tab.load();
        });

        MenuItem gotoWorkdir = new MenuItem("Go to Workdir");
        gotoWorkdir.setOnAction(event -> {
            current.currentPath().map(Path::getParent).ifPresent(directoryService::changeWorkigDir);
        });

        ContextMenu contextMenu = new ContextMenu();
        contextMenu.getItems().addAll(menuItem0, menuItem1, menuItem2, new SeparatorMenuItem(),
                menuItem4, menuItem5, menuItem6, new SeparatorMenuItem(), reloadMenuItem,
                new SeparatorMenuItem(), gotoWorkdir, new SeparatorMenuItem(),
                menuItem7, copyItem, copyPathItem, menuItem8);

        tab.contextMenuProperty().setValue(contextMenu);
        Label label = tab.getLabel();

        label.setOnMouseClicked(mouseEvent -> {
            if (mouseEvent.getButton().equals(MouseButton.SECONDARY)) {
                tab.select();
            } else if (mouseEvent.getClickCount() > 1) {
                controller.adjustSplitPane();
            }
        });


        return tab;
    }

    public void previewDocument(Path path) {
        if (Objects.isNull(path)) {
            logger.error("Null path cannot be viewed");
            return;
        }

        if (Files.isDirectory(path)) {
            if (path.equals(directoryService.workingDirectory())) {
                directoryService.changeWorkigDir(path.getParent());
            } else {
                directoryService.changeWorkigDir(path);
            }
        } else if (pathResolver.isImage(path)) {
            addImageTab(path);
        } else if (pathResolver.isHTML(path) || pathResolver.isAsciidoc(path) || pathResolver.isMarkdown(path)) {
            addTab(path);
        } else if (pathResolver.isEpub(path)) {

            current.setCurrentEpubPath(path);
            controller.getHostServices()
                    .showDocument(String.format(epubUrl, controller.getPort()));
        } else {
            List<String> supportedModes = controller.getSupportedModes();
            String extension = FilenameUtils.getExtension(path.toString());

            if ("".equals(extension) || supportedModes.contains(extension)) {
                addTab(path);
//                controller.hidePreviewPanel();
            } else {
                controller.getHostServices()
                        .showDocument(path.toUri().toString());
            }
        }

    }

    public void addImageTab(Path imagePath) {

        TabPane previewTabPane = controller.getPreviewTabPane();

        ImageTab tab = new ImageTab(imagePath);

        if (previewTabPane.getTabs().contains(tab)) {
            previewTabPane.getSelectionModel().select(tab);
            return;
        }

        Image image = new Image(IOHelper.pathToUrl(imagePath));
        ImageView imageView = new ImageView(image);
        imageView.setPreserveRatio(true);

        imageView.setFitWidth(previewTabPane.getWidth());

        previewTabPane.widthProperty().addListener((observable, oldValue, newValue) -> {
            imageView.setFitWidth(previewTabPane.getWidth());
        });

        Tooltip tip = new Tooltip(imagePath.toString());
        Tooltip.install(tab.getGraphic(), tip);

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setContent(imageView);
        scrollPane.addEventFilter(ScrollEvent.SCROLL, e -> {
            if (e.isControlDown() && e.getDeltaY() > 0) {
                // zoom in
                imageView.setFitWidth(imageView.getFitWidth() + 16.0);
            } else if (e.isControlDown() && e.getDeltaY() < 0) {
                // zoom out
                imageView.setFitWidth(imageView.getFitWidth() - 16.0);
            }
        });

        tab.setContent(scrollPane);

        previewTabPane.getTabs().add(tab);
        previewTabPane.getSelectionModel().select(tab);
    }

    public void initializeTabChangeListener(TabPane tabPane) {

        ReadOnlyObjectProperty<Tab> itemProperty = tabPane.getSelectionModel().selectedItemProperty();

        tabPane.setOnMouseReleased(event -> {
            Optional.ofNullable(itemProperty)
                    .map(ObservableObjectValue::get)
                    .map(e -> (MyTab) e)
                    .map(MyTab::getEditorPane)
                    .ifPresent(EditorPane::focus);
        });

        itemProperty.addListener((observable, oldValue, selectedTab) -> {
            Optional.ofNullable(selectedTab)
                    .map(e -> (MyTab) e)
                    .map(MyTab::getEditorPane)
                    .filter(EditorPane::getReady)
                    .ifPresent(EditorPane::updatePreviewUrl);
        });
    }

    public ObservableList<Optional<Path>> getClosedPaths() {
        return closedPaths;
    }
}

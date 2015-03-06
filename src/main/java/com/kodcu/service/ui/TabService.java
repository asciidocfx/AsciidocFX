package com.kodcu.service.ui;

import com.kodcu.component.MyTab;
import com.kodcu.controller.ApplicationController;
import com.kodcu.other.Current;
import com.kodcu.other.IOHelper;
import com.kodcu.other.Item;
import com.kodcu.service.DirectoryService;
import com.kodcu.service.PathResolverService;
import com.kodcu.service.ThreadService;
import com.kodcu.service.convert.RenderService;
import javafx.collections.ObservableList;
import javafx.concurrent.Worker;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Popup;
import javafx.util.Callback;
import netscape.javascript.JSObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Created by usta on 25.12.2014.
 */
@Component
public class TabService {

    @Autowired
    private ApplicationController controller;

    @Autowired
    private WebviewService webviewService;

    @Autowired
    private EditorService editorService;

    @Autowired
    private PathResolverService pathResolver;

    @Autowired
    private ThreadService threadService;

    @Autowired
    private Current current;

    @Autowired
    private DirectoryService directoryService;

    @Autowired
    private RenderService renderService;

    private List<Optional<Path>> closedPaths = new ArrayList<>();

    private Logger logger = LoggerFactory.getLogger(TabService.class);


    public void addTab(Path path) {

        ObservableList<String> recentFiles = controller.getRecentFiles();
        if (Files.notExists(path)) {
            recentFiles.remove(path.toString());
            return;
        }

        AnchorPane anchorPane = new AnchorPane();
        WebView webView = webviewService.createWebView();
        WebEngine webEngine = webView.getEngine();
        webEngine.setConfirmHandler(param -> {
            if ("command:ready".equals(param)) {
                JSObject window = (JSObject) webEngine.executeScript("window");
                window.setMember("app", controller);
                window.call("updateOptions", new Object[]{});
                Map<String, String> shortCuts = controller.getShortCuts();
                Set<String> keySet = shortCuts.keySet();
                for (String key : keySet) {
                    window.call("addNewCommand", new Object[]{key, shortCuts.get(key)});
                }
                if (Objects.isNull(path))
                    return true;
                threadService.runTaskLater(() -> {
                    String content = IOHelper.readFile(path);
                    threadService.runActionLater(()->{
                        window.call("setEditorValue", new Object[]{content});
                    });
                });

            }
            return false;
        });

        Node editorVBox = editorService.createEditorVBox(webView);
        controller.fitToParent(editorVBox);

        anchorPane.getChildren().add(editorVBox);

        MyTab tab = createTab();
        tab.setTabText(path.getFileName().toString());
        tab.setContent(anchorPane);

        tab.setPath(path);
        tab.setWebView(webView);
        TabPane tabPane = controller.getTabPane();
        tabPane.getTabs().add(tab);

        Tooltip tip = new Tooltip(path.toString());
        Tooltip.install(tab.getGraphic(), tip);

        Tab lastTab = tabPane.getTabs().get(tabPane.getTabs().size() - 1);
        tabPane.getSelectionModel().select(lastTab);

        recentFiles.remove(path.toString());
        recentFiles.add(0, path.toString());

        webView.requestFocus();

    }


    public Path getSelectedTabPath() {
        TreeItem<Item> selectedItem = controller.getTreeView().getSelectionModel().getSelectedItem();
        Item value = selectedItem.getValue();
        Path path = value.getPath();
        return path;
    }

    public MyTab createTab() {
        MyTab tab = new MyTab();

        tab.setOnClosed(event -> {
            this.keepClosedTab(tab);
        });

        tab.selectedProperty().addListener((observableValue, before, after) -> {
            if (after) {
                if (Objects.nonNull(current.currentWebView())) {
                    WebEngine webEngine = current.currentEngine();
                    Worker.State state = webEngine.getLoadWorker().getState();
                    if (state == Worker.State.SUCCEEDED) {
                        controller.textListener(current.currentEditorValue());
                    }
                }
            }
        });

        MenuItem menuItem0 = new MenuItem("Close");
        menuItem0.setOnAction(actionEvent -> {
            this.keepClosedTab(tab);
            controller.getTabPane().getTabs().remove(tab);
        });

        MenuItem menuItem1 = new MenuItem("Close All");
        menuItem1.setOnAction(actionEvent -> {
            ObservableList<Tab> tabs = controller.getTabPane().getTabs();
            if (tabs.size() > 0)
                tabs.forEach(this::keepClosedTab);

            tabs.clear();
        });

        MenuItem menuItem2 = new MenuItem("Close Others");
        menuItem2.setOnAction(actionEvent -> {
            List<Tab> blackList = new ArrayList<>();
            blackList.addAll(controller.getTabPane().getTabs());
            blackList.remove(tab);
            controller.getTabPane().getTabs().removeAll(blackList);

            if (blackList.size() > 0)
                blackList.forEach(this::keepClosedTab);
        });

        MenuItem menuItem3 = new MenuItem("Close Unmodified");
        menuItem3.setOnAction(actionEvent -> {
            ObservableList<Tab> tabs = controller.getTabPane().getTabs();
            Predicate<Tab> filter = pTab -> !((MyTab) pTab).getText().contains(" *");

            List<Tab> collect = tabs.stream().filter(filter).collect(Collectors.toList());

            if (collect.size() > 0)
                collect.forEach(this::keepClosedTab);

            tabs.removeAll(collect);
        });

        MenuItem menuItem4 = new MenuItem("Select Next Tab");
        menuItem4.setOnAction(actionEvent -> {
            TabPane tabPane = controller.getTabPane();
            if (tabPane.getSelectionModel().isSelected(tabPane.getTabs().size() - 1))
                tabPane.getSelectionModel().selectFirst();
            else
                tabPane.getSelectionModel().selectNext();
        });

        MenuItem menuItem5 = new MenuItem("Select Previous Tab");
        menuItem5.setOnAction(actionEvent -> {
            SingleSelectionModel<Tab> selectionModel = controller.getTabPane().getSelectionModel();
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
                closedPaths.get(index).filter(pathResolver::isImage).ifPresent(this::addImageTab);
                closedPaths.remove(index);
            }
        });

        MenuItem menuItem7 = new MenuItem("Open File Location");

        menuItem7.setOnAction(event -> {
            current.currentPath().ifPresent(path -> {
                controller.getHostServices().showDocument(path.getParent().toUri().toASCIIString());
            });
        });

        MenuItem menuItem8 = new MenuItem("New File");
        menuItem8.setOnAction(controller::newDoc);

        MenuItem gotoWorkdir = new MenuItem("Go to Workdir");
        gotoWorkdir.setOnAction(event -> {
            current.currentPath().map(Path::getParent).ifPresent(directoryService::changeWorkigDir);
        });

        ContextMenu contextMenu = new ContextMenu();
        contextMenu.getItems().addAll(menuItem0, menuItem1, menuItem2, menuItem3, new SeparatorMenuItem(),
                menuItem4, menuItem5, menuItem6, new SeparatorMenuItem(),
                gotoWorkdir, new SeparatorMenuItem(),
                menuItem7, menuItem8);

        tab.contextMenuProperty().setValue(contextMenu);

        Label label = new Label();
        tab.setLabel(label);

        label.setOnMouseClicked(mouseEvent -> {
            if (mouseEvent.getButton().equals(MouseButton.SECONDARY)) {
                controller.getTabPane().getSelectionModel().select(tab);
            } else if (mouseEvent.getClickCount() > 1) {
                controller.adjustSplitPane();
            }
        });


        return tab;
    }

    public void addImageTab(Path imagePath) {
        MyTab tab = createTab();
        tab.setTabText(imagePath.getFileName().toString());
        ImageView imageView = new ImageView(new Image(IOHelper.pathToUrl(imagePath)));
        imageView.setPreserveRatio(true);
        imageView.setFitWidth(imageView.getImage().getWidth());

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
        tab.setWebView(null);
        tab.setPath(imagePath);

        TabPane tabPane = controller.getTabPane();
        tabPane.getTabs().add(tab);
        tabPane.getSelectionModel().select(tab);
    }

    public void keepClosedTab(Tab closedTab) {
        threadService.runTaskLater(() -> {
            threadService.runActionLater(() -> {
                MyTab tab = (MyTab) closedTab;
                if (!tab.getLabel().getText().equals("new *")) {
                    closedPaths.add(Optional.ofNullable(tab.getPath()));
                }

                tab.setPath(null);
                tab.setOnClosed(null);
                tab.setOnSelectionChanged(null);
                tab.setUserData(null);
                tab.getLabel().setOnMouseClicked(null);
                tab.setOnCloseRequest(null);
                tab.setWebView(null);
                tab.setContent(null);

            });
        });
    }
}

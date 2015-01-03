package com.kodcu.service.ui;

import com.kodcu.component.MyTab;
import com.kodcu.controller.ApplicationController;
import com.kodcu.other.Current;
import com.kodcu.other.IOHelper;
import com.kodcu.other.Item;
import com.kodcu.service.PathResolverService;
import com.kodcu.service.ThreadService;
import javafx.collections.ObservableList;
import javafx.concurrent.Worker;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.channels.CompletionHandler;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
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

    private List<Optional<Path>> closedPaths = new ArrayList<>();

    public void addTab(Path path) {

        ObservableList<String> recentFiles = controller.getRecentFiles();
        if (Files.notExists(path)) {
            recentFiles.remove(path.toString());
            return;
        }

        AnchorPane anchorPane = new AnchorPane();
        WebView webView = webviewService.createWebView();
        WebEngine webEngine = webView.getEngine();
        webEngine.getLoadWorker().stateProperty().addListener((observableValue1, state, state2) -> {
            if (state2 == Worker.State.SUCCEEDED) {
                threadService.runTaskLater(task ->{
                    String normalize = IOHelper.normalize(IOHelper.readFile(path));
                    threadService.runActionLater(run->{
                        webEngine.executeScript(String.format("setEditorValue('%s')",normalize));
                    });
                });
            }
        });

        Node editorVBox = editorService.createEditorVBox(webView);
        controller.fitToParent(editorVBox);

        anchorPane.getChildren().add(editorVBox);

        MyTab tab = createTab();
        ((Label) tab.getGraphic()).setText(path.getFileName().toString());
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
                    if (state == Worker.State.SUCCEEDED)
                        controller.textListener(current.currentEditorValue());
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
            Predicate<Tab> filter = pTab -> !((Label) pTab.getGraphic()).getText().contains(" *");

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
                controller.getHostServices().showDocument(path.getParent().toUri().toString());
            });
        });

        MenuItem menuItem8 = new MenuItem("New File");
        menuItem8.setOnAction(controller::newDoc);

        ContextMenu contextMenu = new ContextMenu();
        contextMenu.getItems().addAll(menuItem0, menuItem1, menuItem2, menuItem3, new SeparatorMenuItem(), menuItem4, menuItem5, menuItem6, new SeparatorMenuItem(), menuItem7, menuItem8);

        tab.contextMenuProperty().setValue(contextMenu);

        Label label = new Label();
        tab.setGraphic(label);

        label.setOnMouseClicked(mouseEvent -> {

            if (mouseEvent.getButton().equals(MouseButton.SECONDARY)) {
                controller.getTabPane().getSelectionModel().select(tab);
            } else if (mouseEvent.getClickCount() > 1) {
                SplitPane splitPane = controller.getSplitPane();
                if (splitPane.getDividerPositions()[0] > 0.1)
                    splitPane.setDividerPositions(0, 1);
                else
                    splitPane.setDividerPositions(0.18, 0.60);

            }
        });


        return tab;
    }

    public void addImageTab(Path imagePath) {
        MyTab tab = createTab();
        Label label = (Label) tab.getGraphic();
        label.setText(imagePath.getFileName().toString());
        ImageView imageView = new ImageView(new Image(IOHelper.pathToUrl(imagePath)));
        imageView.setPreserveRatio(true);
        TabPane tabPane = controller.getTabPane();
        imageView.fitWidthProperty().bind(tabPane.widthProperty());

        tab.setContent(imageView);
        tab.setWebView(null);
        tab.setPath(imagePath);
        tabPane.getTabs().add(tab);
        tabPane.getSelectionModel().select(tab);
    }

    public void keepClosedTab(Tab closedTab) {
        MyTab tab = (MyTab) closedTab;
        Label closedTabLabel = (Label) closedTab.getGraphic();
        if (!closedTabLabel.getText().equals("new *")) {
            closedPaths.add(Optional.ofNullable(tab.getPath()));
        }

        threadService.runTaskLater(task -> {
            threadService.runActionLater(run -> {
                tab.setOnClosed(null);
                tab.setOnSelectionChanged(null);
                tab.setPath(null);
                tab.setWebView(null);
                tab.setContent(null);
                tab.setUserData(null);
                tab.setOnCloseRequest(null);
                Label label = (Label) tab.getGraphic();
                label.setOnMouseClicked(null);
            });
        });
    }
}

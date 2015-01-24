package com.kodcu.service;

import com.kodcu.component.MyTab;
import com.kodcu.controller.ApplicationController;
import com.kodcu.other.Current;
import com.kodcu.other.IOHelper;
import com.kodcu.service.ui.EditorService;
import com.kodcu.service.ui.TabService;
import com.kodcu.service.ui.WebviewService;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TabPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;

/**
 * Created by usta on 26.12.2014.
 */
@Component
public class DocumentService {

    @Autowired
    private DirectoryService directoryService;

    @Autowired
    private ApplicationController controller;

    @Autowired
    private WebviewService webviewService;

    @Autowired
    private EditorService editorService;

    @Autowired
    private TabService tabService;

    @Autowired
    private Current current;

    public void saveDoc() {

        Label label = current.currentTabLabel();
        Path currentPath = directoryService.currentPath();

        if (Objects.isNull(currentPath))
            return;

        IOHelper.writeToFile(currentPath, (String) current.currentEngine().executeScript("editor.getValue();"), TRUNCATE_EXISTING, CREATE);
        current.setCurrentTabText(currentPath.getFileName().toString());
        ObservableList<String> recentFiles = controller.getRecentFiles();
        recentFiles.remove(currentPath.toString());
        recentFiles.add(0, currentPath.toString());

        label.setText(label.getText().replace(" *", ""));

        directoryService.setInitialDirectory(Optional.ofNullable(currentPath.toFile()));

        current.currentTab().setPath(currentPath);
    }

    public void newDoc() {
        WebView webView = webviewService.createWebView();
        AnchorPane anchorPane = new AnchorPane();
        Node editorVBox = editorService.createEditorVBox(webView);
        controller.fitToParent(editorVBox);
        anchorPane.getChildren().add(editorVBox);
        MyTab tab = tabService.createTab();
        tab.setWebView(webView);
        tab.setContent(anchorPane);

        ((Label) tab.getGraphic()).setText("new *");
        TabPane tabPane = controller.getTabPane();
        tabPane.getTabs().add(tab);
        tabPane.getSelectionModel().select(tab);

        webView.requestFocus();
    }

    public void openDoc() {
        FileChooser fileChooser = directoryService.newFileChooser("Open Asciidoc File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Asciidoc", "*.asc", "*.asciidoc", "*.adoc", "*.ad", "*.txt"));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("All", "*.*"));
        List<File> chosenFiles = fileChooser.showOpenMultipleDialog(controller.getStage());
        if (chosenFiles != null) {
            chosenFiles.stream().map(e -> e.toPath()).forEach(tabService::addTab);
            chosenFiles.stream()
                    .map(File::toString).filter(file -> !controller.getRecentFiles().contains(file))
                    .forEach(controller.getRecentFiles()::addAll);
            directoryService.setInitialDirectory(Optional.ofNullable(chosenFiles.get(0)));
        }
    }
}

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
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;
import netscape.javascript.JSObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.nio.file.Path;
import java.util.*;

import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;

/**
 * Created by usta on 26.12.2014.
 */
@Component
public class DocumentService {

    private final DirectoryService directoryService;
    private final ApplicationController controller;
    private final WebviewService webviewService;
    private final EditorService editorService;
    private final TabService tabService;
    private final Current current;

    
    
    @Autowired
    public DocumentService(DirectoryService directoryService, ApplicationController controller, WebviewService webviewService,
            EditorService editorService, TabService tabService, Current current) {
        this.directoryService = directoryService;
        this.controller = controller;
        this.webviewService = webviewService;
        this.editorService = editorService;
        this.tabService = tabService;
        this.current = current;
    }

    public void saveDoc() {

        Path currentPath = directoryService.currentPath();

        if (Objects.isNull(currentPath) || !current.getCurrentTabText().contains(" *"))
            return;

        IOHelper.writeToFile(currentPath, (String) current.currentEngine().executeScript("editor.getValue();"), TRUNCATE_EXISTING, CREATE);
        current.setCurrentTabText(currentPath.getFileName().toString());
        ObservableList<String> recentFiles = controller.getRecentFiles();
        recentFiles.remove(currentPath.toString());
        recentFiles.add(0, currentPath.toString());

        current.setCurrentTabText(current.getCurrentTabText().replace(" *", ""));

        directoryService.setInitialDirectory(Optional.ofNullable(currentPath.toFile()));

        current.currentTab().setPath(currentPath);
    }

    public void newDoc() {
        newDoc(null);
    }

    public void newDoc(String content) {
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
                if (Objects.isNull(content))
                    return true;
                window.call("setEditorValue", new Object[]{content});
            }
            return false;
        });

        AnchorPane anchorPane = new AnchorPane();
        MyTab tab = tabService.createTab();
        Node editorVBox = editorService.createEditorVBox(webView,tab);
        controller.fitToParent(editorVBox);
        anchorPane.getChildren().add(editorVBox);

        tab.setWebView(webView);
        tab.setContent(anchorPane);

        tab.setTabText("new *");
        TabPane tabPane = controller.getTabPane();
        tabPane.getTabs().add(tab);
        tab.select();

        webView.requestFocus();
    }

    public void openDoc() {
        FileChooser fileChooser = directoryService.newFileChooser("Open Asciidoc File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Asciidoc", "*.asc", "*.asciidoc", "*.adoc", "*.ad", "*.txt","*.*"));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Markdown", "*.md","*.markdown","*.txt","*.*"));
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

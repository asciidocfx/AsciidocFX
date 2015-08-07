package com.kodcu.service;

import com.kodcu.component.EditorPane;
import com.kodcu.component.MyTab;
import com.kodcu.config.StoredConfigBean;
import com.kodcu.controller.ApplicationController;
import com.kodcu.other.Current;
import com.kodcu.other.IOHelper;
import com.kodcu.service.ui.EditorService;
import com.kodcu.service.ui.TabService;
import com.kodcu.service.ui.WebviewService;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.TabPane;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import netscape.javascript.JSObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
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
    private final StoredConfigBean storedConfigBean;


    @Autowired
    public DocumentService(DirectoryService directoryService, ApplicationController controller, WebviewService webviewService,
                           EditorService editorService, TabService tabService, Current current, StoredConfigBean storedConfigBean) {
        this.directoryService = directoryService;
        this.controller = controller;
        this.webviewService = webviewService;
        this.editorService = editorService;
        this.tabService = tabService;
        this.current = current;
        this.storedConfigBean = storedConfigBean;

        webviewService.setDocumentService(this);
    }

    public void saveDoc() {

        Path currentPath = directoryService.currentPath();

        if (Objects.isNull(currentPath) || !current.getCurrentTabText().contains(" *"))
            return;

        Optional<IOException> exception =
                IOHelper.writeToFile(currentPath, current.currentEditorValue(), TRUNCATE_EXISTING, CREATE);

        if (exception.isPresent())
            return;

        current.setCurrentTabText(currentPath.getFileName().toString());
        ObservableList<String> recentFiles = storedConfigBean.getRecentFiles();
        recentFiles.remove(currentPath.toString());
        recentFiles.add(0, currentPath.toString());

        current.setCurrentTabText(current.getCurrentTabText().replace(" *", ""));

        directoryService.setInitialDirectory(Optional.ofNullable(currentPath.toFile()));

        current.currentTab().setPath(currentPath);
    }

    public void newDoc() {
        newDoc(null);
    }

    public void newDoc(final String content) {
        EditorPane editorPane = webviewService.createWebView();
        editorPane.confirmHandler(param -> {
            if ("command:ready".equals(param)) {
                JSObject window = editorPane.getWindow();
                window.setMember("afx", controller);
                window.call("updateOptions", new Object[]{});
                window.call("setInitialized");

                String finalContent = content;

                if (Objects.isNull(finalContent))
                    finalContent = "";

                window.call("setEditorValue", new Object[]{finalContent});

            }
            return false;
        });

        AnchorPane anchorPane = new AnchorPane();
        MyTab tab = tabService.createTab();
        Node editorVBox = editorService.createEditorVBox(editorPane, tab);
        controller.fitToParent(editorVBox);
        anchorPane.getChildren().add(editorVBox);

        tab.setEditorPane(editorPane);
        tab.setContent(anchorPane);

        tab.setTabText("new *");
        TabPane tabPane = controller.getTabPane();
        tabPane.getTabs().add(tab);
        tab.select();

        editorPane.focus();
    }

    public void openDoc() {
        FileChooser fileChooser = directoryService.newFileChooser("Open Asciidoc File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Asciidoc", "*.adoc", "*.asciidoc", "*.asc", "*.ad", "*.txt", "*.*"));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Markdown", "*.md", "*.markdown", "*.txt", "*.*"));
        List<File> chosenFiles = fileChooser.showOpenMultipleDialog(controller.getStage());
        if (chosenFiles != null) {
            chosenFiles.stream().map(e -> e.toPath()).forEach(directoryService.getOpenFileConsumer()::accept);
            chosenFiles.stream()
                    .map(File::toString).filter(file -> ! storedConfigBean.getRecentFiles().contains(file))
                    .forEach(storedConfigBean.getRecentFiles()::addAll);
            directoryService.setInitialDirectory(Optional.ofNullable(chosenFiles.get(0)));
        }
    }
}

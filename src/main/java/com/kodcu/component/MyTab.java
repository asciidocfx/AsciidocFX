package com.kodcu.component;

import com.kodcu.config.StoredConfigBean;
import com.kodcu.controller.ApplicationController;
import com.kodcu.other.ExtensionFilters;
import com.kodcu.other.IOHelper;
import com.kodcu.service.DirectoryService;
import com.kodcu.service.ThreadService;
import com.kodcu.service.shortcut.AsciidocShortcutService;
import com.kodcu.service.shortcut.MarkdownShortcutService;
import com.kodcu.service.shortcut.NoneShortcutService;
import com.kodcu.service.ui.TabService;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.stage.FileChooser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static java.nio.file.StandardOpenOption.*;

/**
 * Created by usta on 17.12.2014.
 */

@Component
@Scope("prototype")
public class MyTab extends Tab {

    private final EditorPane editorPane;
    private final StoredConfigBean storedConfigBean;
    private final DirectoryService directoryService;
    private final TabService tabService;
    private final ApplicationController controller;
    private final ThreadService threadService;

    private final Logger logger = LoggerFactory.getLogger(MyTab.class);

    @Autowired
    public MyTab(EditorPane editorPane, StoredConfigBean storedConfigBean, DirectoryService directoryService, TabService tabService, ApplicationController controller, ThreadService threadService) {
        this.editorPane = editorPane;
        this.storedConfigBean = storedConfigBean;
        this.directoryService = directoryService;
        this.tabService = tabService;
        this.controller = controller;
        this.threadService = threadService;
    }

    public Label getLabel() {
        if (Objects.isNull(this.getGraphic()))
            this.setGraphic(new Label());
        return (Label) this.getGraphic();
    }

    public String getTabText() {
        Label label = getLabel();
        return label.getText();
    }

    public void setTabText(String tabText) {
        Label label = getLabel();
        label.setText(tabText);
    }

    public Path getPath() {

        if (Objects.isNull(editorPane)) {
            return null;
        }

        return editorPane.getPath();
    }

    public void setPath(Path path) {
        if (Objects.nonNull(editorPane)) {
            editorPane.setPath(path);
        }
    }

    public boolean isSaved() {
        return !this.getTabText().contains(" *");
    }

    public ButtonType close() {

        if (Objects.nonNull(getPath()))
            tabService.getClosedPaths().add(Optional.ofNullable(getPath()));

        this.select();

        if (isSaved() || !isDirty()) {
            closeIt();
            return ButtonType.YES;
        }

        if (isNew()) {
            Optional<ButtonType> alert = AlertHelper.saveAlert();
            ButtonType type = alert.orElse(ButtonType.CANCEL);

            if (type == ButtonType.YES) {
                closeIt();
            }
            return type;
        } else {
            saveDoc();
            if (isSaved()) {
                closeIt();
                return ButtonType.YES;
            }
        }

        return ButtonType.CANCEL;
    }

    private boolean isDirty() {
        if (isNew()) {
            if (Objects.nonNull(editorPane)) {
                try {
                    String value = editorPane.getEditorValue();
                    if ("".equals(value))
                        return false;
                } catch (Exception e) {
                    // no-op
                }
            }
        }
        return true;
    }

    public boolean isChanged() {
        String tabText = getTabText();
        return tabText.contains(" *");
    }

    public synchronized void reload() {

        Optional.ofNullable(getPath())
                .filter(Files::exists)
                .ifPresent(path -> {
                    FileTime latestModifiedTime = IOHelper.getLastModifiedTime(path);
                    if (Objects.nonNull(latestModifiedTime) && Objects.nonNull(getLastModifiedTime())) {
                        if (latestModifiedTime.compareTo(getLastModifiedTime()) > 0) {

                            if (isChanged()) {
                                this.select();
                                ButtonType buttonType = AlertHelper.conflictAlert(getPath()).orElse(ButtonType.CANCEL);

                                if (buttonType != AlertHelper.LOAD_FILE_SYSTEM_CHANGES) {
                                    return;
                                }

                            }

                            load();
                        }
                    } else {
                        logger.warn("Will not reload");
                    }
                });
    }

    private void load() {
        FileTime latestModifiedTime = IOHelper.getLastModifiedTime(getPath());
        setLastModifiedTime(latestModifiedTime);
        String content = IOHelper.readFile(getPath());
        threadService.runActionLater(() -> {
            editorPane.setEditorValue(content);
            this.select();
            setTabText(getPath().getFileName().toString());
        });
    }

    public synchronized void saveDoc() {

        if (!isNew() && !isChanged()) {
            return;
        }

        if (!Platform.isFxApplicationThread()) {
            CompletableFuture completableFuture = new CompletableFuture();

            completableFuture.runAsync(() -> {
                Platform.runLater(() -> {
                    try {
                        saveDoc();
                        completableFuture.complete(null);
                    } catch (Exception e) {
                        completableFuture.completeExceptionally(e);
                    }
                });
            }, threadService.executor());

            completableFuture.join();
            return;
        }

        logger.warn("Entered Save");

        FileTime latestModifiedTime = IOHelper.getLastModifiedTime(getPath());

        if (Objects.nonNull(latestModifiedTime) && Objects.nonNull(getLastModifiedTime())) {
            if (latestModifiedTime.compareTo(getLastModifiedTime()) > 0) {

                this.select();
                ButtonType buttonType = AlertHelper.conflictAlert(getPath()).orElse(ButtonType.CANCEL);

                if (buttonType == ButtonType.CANCEL) {
                    return;
                }

                if (buttonType == AlertHelper.LOAD_FILE_SYSTEM_CHANGES) {
                    load();
                }
            }
        }

        if (Objects.isNull(getPath())) {
            final FileChooser fileChooser = directoryService.newFileChooser(String.format("Save file"));
            fileChooser.getExtensionFilters().addAll(ExtensionFilters.ASCIIDOC);
            fileChooser.getExtensionFilters().addAll(ExtensionFilters.MARKDOWN);
            fileChooser.getExtensionFilters().addAll(ExtensionFilters.ALL);
            File file = fileChooser.showSaveDialog(null);
            setPath(file.toPath());
            setTabText(file.toPath().getFileName().toString());
        }

        String editorValue = editorPane.getEditorValue();
        Optional<Exception> exception =
                IOHelper.writeToFile(getPath(), editorValue, TRUNCATE_EXISTING, CREATE, SYNC);

        setLastModifiedTime(IOHelper.getLastModifiedTime(getPath()));

        if (exception.isPresent()) {
            return;
        }

        setTabText(getPath().getFileName().toString());

        ObservableList<String> recentFiles = storedConfigBean.getRecentFiles();
        recentFiles.remove(getPath().toString());
        recentFiles.add(0, getPath().toString());

        directoryService.setInitialDirectory(Optional.ofNullable(getPath().toFile()));
    }


    public boolean isNew() {
        return "new *".equals(this.getTabText());
    }

    public void select() {
        this.getTabPane().getSelectionModel().select(this);
    }

    private void closeIt() {
        Platform.runLater(() -> {
            tabService.getClosedPaths().add(Optional.ofNullable(getPath()));
            this.getTabPane().getTabs().remove(this); // keep it here
            ObservableList<Tab> tabs = controller.getTabPane().getTabs();
            if (tabs.isEmpty()) {
                tabService.newDoc();
            }
        });
    }

    @Override
    public String toString() {
        return getTabText();
    }

    public Class getShortcutType() {
        if (isAsciidoc()) {
            return AsciidocShortcutService.class;
        } else if (isMarkdown()) {
            return MarkdownShortcutService.class;
        }

        return NoneShortcutService.class;
    }

    public boolean isMarkdown() {
        return editorPane.isMarkdown();
    }


    public boolean isAsciidoc() {
        return editorPane.isAsciidoc();
    }

    public String htmlToMarkupFunction() {
        return isAsciidoc() ? "toAsciidoc" : "toMarkdown";
    }

    public EditorPane getEditorPane() {
        return editorPane;
    }

    public FileTime getLastModifiedTime() {

        if (Objects.isNull(editorPane))
            return null;

        return editorPane.getLastModifiedTime();
    }

    public void setLastModifiedTime(FileTime lastModifiedTime) {

        if(Objects.nonNull(editorPane)){
            this.editorPane.setLastModifiedTime(lastModifiedTime);
        }
    }
}

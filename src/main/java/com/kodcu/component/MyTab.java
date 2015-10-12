package com.kodcu.component;

import com.kodcu.config.StoredConfigBean;
import com.kodcu.controller.ApplicationController;
import com.kodcu.other.ExtensionFilters;
import com.kodcu.other.IOHelper;
import com.kodcu.other.Item;
import com.kodcu.service.DirectoryService;
import com.kodcu.service.ThreadService;
import com.kodcu.service.shortcut.AsciidocShortcutService;
import com.kodcu.service.shortcut.HtmlShortcutService;
import com.kodcu.service.shortcut.MarkdownShortcutService;
import com.kodcu.service.shortcut.NoneShortcutService;
import com.kodcu.service.ui.TabService;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.util.Objects;
import java.util.Optional;

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
    private final BooleanProperty changedProperty = new SimpleBooleanProperty(false);

    private final Logger logger = LoggerFactory.getLogger(MyTab.class);

    @Autowired
    public MyTab(EditorPane editorPane, StoredConfigBean storedConfigBean, DirectoryService directoryService, TabService tabService, ApplicationController controller, ThreadService threadService) {
        this.editorPane = editorPane;
        this.storedConfigBean = storedConfigBean;
        this.directoryService = directoryService;
        this.tabService = tabService;
        this.controller = controller;
        this.threadService = threadService;
        this.changedProperty.addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                if (!getStyleClass().contains("mytab-changed"))
                    getStyleClass().add("mytab-changed");
            } else {
                getStyleClass().remove("mytab-changed");
            }
        });
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
//        return !this.getTabText().contains(" *");
        return !changedProperty.get();
    }

    public ButtonType close() {

        if (Objects.nonNull(getPath()))
            tabService.getClosedPaths().add(Optional.ofNullable(getPath()));

        this.select();

        if (isNew() && !isChanged()) { // if tab is not dirty
            closeIt();
            return ButtonType.YES;
        } else if (isNew()) { // else if is new
            ButtonType type = AlertHelper.saveAlert().orElse(ButtonType.CANCEL);

            if (type == ButtonType.YES) {
                closeIt();
            }
            return type;
        } else { // others should be save and close
            saveDoc();
            if (isSaved()) {
                closeIt();
                return ButtonType.YES;
            } else {
                ButtonType type = AlertHelper.saveAlert().orElse(ButtonType.CANCEL);

                if (type == ButtonType.YES) {
                    closeIt();
                }
                return type;
            }
        }
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
//        String tabText = getTabText();
//        return tabText.contains(" *");
        return changedProperty.get();
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
                    }
                });
    }

    public void load() {
        FileTime latestModifiedTime = IOHelper.getLastModifiedTime(getPath());
        setLastModifiedTime(latestModifiedTime);
        String content = IOHelper.readFile(getPath());
        threadService.runActionLater(() -> {
            editorPane.setEditorValue(content);
            this.select();
            setTabText(getPath().getFileName().toString());
        });
    }

    private synchronized void save() {

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
            } else {
                if (!isNew() && !isChanged()) {
                    return;
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

        IOHelper.createDirectories(getPath().getParent());

        Optional<Exception> exception =
                IOHelper.writeToFile(getPath(), editorValue, TRUNCATE_EXISTING, CREATE, SYNC);

        if (exception.isPresent()) {
            return;
        }

        setLastModifiedTime(IOHelper.getLastModifiedTime(getPath()));

        changedProperty.set(false);

        ObservableList<Item> recentFiles = storedConfigBean.getRecentFiles();
        recentFiles.remove(new Item(getPath()));
        recentFiles.add(0, new Item(getPath()));

        directoryService.setInitialDirectory(Optional.ofNullable(getPath().toFile()));
    }

    public void saveDoc() {
        threadService.runActionLater(this::save);
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
        } else if (isHTML()) {
            return HtmlShortcutService.class;
        }

        return NoneShortcutService.class;
    }

    public boolean isMarkdown() {
        return editorPane.isMarkdown();
    }

    public boolean isHTML() {
        return editorPane.isHTML();
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
        if (Objects.nonNull(editorPane)) {
            this.editorPane.setLastModifiedTime(lastModifiedTime);
        }
    }

    public boolean getChangedProperty() {
        return changedProperty.get();
    }

    public BooleanProperty changedPropertyProperty() {
        return changedProperty;
    }

    public void setChangedProperty(boolean changedProperty) {
        this.changedProperty.set(changedProperty);
    }

    public Path getParentOrWorkdir() {
        return Optional.ofNullable(getPath()).map(Path::getParent).orElse(directoryService.workingDirectory());
    }
}

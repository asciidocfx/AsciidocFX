package com.kodedu.component;

import com.kodedu.config.StoredConfigBean;
import com.kodedu.controller.ApplicationController;
import com.kodedu.helper.IOHelper;
import com.kodedu.other.ExtensionFilters;
import com.kodedu.other.Item;
import com.kodedu.service.DirectoryService;
import com.kodedu.service.ThreadService;
import com.kodedu.service.shortcut.AsciidocShortcutService;
import com.kodedu.service.shortcut.HtmlShortcutService;
import com.kodedu.service.shortcut.MarkdownShortcutService;
import com.kodedu.service.shortcut.NoneShortcutService;
import com.kodedu.service.ui.TabService;
import javafx.beans.property.BooleanProperty;
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

    private final Logger logger = LoggerFactory.getLogger(MyTab.class);

    @Autowired
    public MyTab(EditorPane editorPane, StoredConfigBean storedConfigBean, DirectoryService directoryService, TabService tabService, ApplicationController controller, ThreadService threadService) {
        this.editorPane = editorPane;
        this.editorPane.setTab(this);
        this.storedConfigBean = storedConfigBean;
        this.directoryService = directoryService;
        this.tabService = tabService;
        this.controller = controller;
        this.threadService = threadService;
        changedPropertyProperty().addListener((observable, oldValue, newValue) -> {

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
        return !this.editorPane.getChangedProperty();
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
        return this.editorPane.getChangedProperty();
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

    public synchronized void load() {
        FileTime latestModifiedTime = IOHelper.getLastModifiedTime(getPath());
        setLastModifiedTime(latestModifiedTime);
        try {
            String content = IOHelper.readFile(getPath());
            editorPane.setEditorValue(content);
            this.select();
            setTabText(getPath().getFileName().toString());
            setChangedProperty(false);
        } catch (Exception e) {
            closeIt();
        }

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

            if (Objects.isNull(file))
                return;

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

        setChangedProperty(false);

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

    public void closeIt() {
        threadService.runActionLater(() -> {
            tabService.getClosedPaths().add(Optional.ofNullable(getPath()));
            this.getTabPane().getTabs().remove(this); // keep it here
            ObservableList<Tab> tabs = controller.getTabPane().getTabs();
            if (tabs.isEmpty()) {
                tabService.newDoc();
            }
        }, true);
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
        return this.editorPane.getChangedProperty();
    }

    public BooleanProperty changedPropertyProperty() {
        return this.editorPane.changedPropertyProperty();
    }

    public void setChangedProperty(boolean changedProperty) {
        this.editorPane.setChangedProperty(changedProperty);
    }

    public Path getParentOrWorkdir() {
        return Optional.ofNullable(getPath()).map(Path::getParent).orElse(directoryService.workingDirectory());
    }
}

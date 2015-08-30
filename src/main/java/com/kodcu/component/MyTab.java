package com.kodcu.component;

import com.kodcu.config.StoredConfigBean;
import com.kodcu.controller.ApplicationController;
import com.kodcu.other.IOHelper;
import com.kodcu.service.DirectoryService;
import com.kodcu.service.shortcut.AsciidocShortcutService;
import com.kodcu.service.shortcut.MarkdownShortcutService;
import com.kodcu.service.shortcut.NoneShortcutService;
import com.kodcu.service.ui.TabService;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Optional;

import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;

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

    private final Logger logger = LoggerFactory.getLogger(MyTab.class);

    @Autowired
    public MyTab(EditorPane editorPane, StoredConfigBean storedConfigBean, DirectoryService directoryService, TabService tabService, ApplicationController controller) {
        this.editorPane = editorPane;
        this.storedConfigBean = storedConfigBean;
        this.directoryService = directoryService;
        this.tabService = tabService;
        this.controller = controller;
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

        Optional<ButtonType> alert = AlertHelper.saveAlert();
        ButtonType type = alert.orElse(ButtonType.CANCEL);

        if (type == ButtonType.YES) {
            closeIt();
        }
        return type;
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

    public void saveDoc() {

        if (Objects.isNull(getPath()))
            return;

        if (!isChanged())
            return;

        Optional<IOException> exception =
                IOHelper.writeToFile(getPath(), editorPane.getEditorValue(), TRUNCATE_EXISTING, CREATE);

        if (exception.isPresent())
            return;

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

    public void reloadDocument(String alertMessage) {

        if (Objects.nonNull(getPath())) {
            String content = IOHelper.readFile(getPath());

            if (!content.equals(editorPane.getEditorValue())) {
                if (isSaved()) {
                    editorPane.setEditorValue(content);
                } else {
                    Optional<ButtonType> reloadAlert = AlertHelper.showAlert(alertMessage);
                    reloadAlert.ifPresent(buttonType -> {
                        if (ButtonType.YES == buttonType) {
                            editorPane.setEditorValue(content);
                        }
                    });
                }
            }

        } else {
            logger.error("There is not path for this tab to reload");
        }

    }

    public void askReloadDocument(String alertMessage) {

        if (Objects.nonNull(getPath())) {
            String content = IOHelper.readFile(getPath());
            if (!content.equals(editorPane.getEditorValue())) {
                this.select();
                Optional<ButtonType> reloadAlert = AlertHelper.showAlert(alertMessage);
                reloadAlert.ifPresent(buttonType -> {
                    if (ButtonType.YES == buttonType) {
                        editorPane.setEditorValue(content);
                    }
                });
            }
        } else {
            logger.error("There is not path for this tab to reload");
        }

    }

    public EditorPane getEditorPane() {
        return editorPane;
    }
}

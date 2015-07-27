package com.kodcu.component;

import com.kodcu.other.IOHelper;
import com.kodcu.service.shortcut.AsciidocShortcutService;
import com.kodcu.service.shortcut.MarkdownShortcutService;
import com.kodcu.service.shortcut.NoneShortcutService;
import javafx.application.Platform;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.Objects;
import java.util.Optional;

/**
 * Created by usta on 17.12.2014.
 */

public class MyTab extends Tab {

    private EditorPane editorPane;
    private Path path;

    private final Logger logger = LoggerFactory.getLogger(MyTab.class);

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
        return path;
    }

    public void setPath(Path path) {
        this.path = path;
    }

    public boolean isSaved() {
        return !this.getTabText().contains(" *");
    }

    public ButtonType close() {
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

    public boolean isNew(){
        return "new *".equals(this.getTabText());
    }

    public void select() {
        this.getTabPane().getSelectionModel().select(this);
    }

    private void closeIt() {
        Platform.runLater(() -> {
            this.getTabPane().getTabs().remove(this); // keep it here
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
        return editorPane.is("markdown");
    }

    public void setEditorPane(EditorPane editorPane) {
        this.editorPane = editorPane;
    }

    public EditorPane getEditorPane() {
        return editorPane;
    }

    public boolean isAsciidoc() {
        return editorPane.is("asciidoc");
    }

    public String htmlToMarkupFunction() {
        return isAsciidoc() ? "toAsciidoc" : "toMarkdown";
    }

    public void reloadDocument(String alertMessage) {

        if (Objects.nonNull(path)) {
            String content = IOHelper.readFile(path);

            if(!content.equals(getEditorPane().getEditorValue())){
                if (isSaved()) {
                    getEditorPane().setEditorValue(content);
                } else {
                    Optional<ButtonType> reloadAlert = AlertHelper.showAlert(alertMessage);
                    reloadAlert.ifPresent(buttonType -> {
                        if (ButtonType.YES == buttonType) {
                            getEditorPane().setEditorValue(content);
                        }
                    });
                }
            }

        } else {
            logger.error("There is not path for this tab to reload");
        }

    }

    public void askReloadDocument(String alertMessage) {

        if (Objects.nonNull(path)) {
            String content = IOHelper.readFile(path);
            if(!content.equals(getEditorPane().getEditorValue())){
                this.select();
                Optional<ButtonType> reloadAlert = AlertHelper.showAlert(alertMessage);
                reloadAlert.ifPresent(buttonType -> {
                    if (ButtonType.YES == buttonType) {
                        getEditorPane().setEditorValue(content);
                    }
                });
            }
        } else {
            logger.error("There is not path for this tab to reload");
        }

    }

}

package com.kodcu.component;

import com.kodcu.service.shortcut.AsciidocShortcutService;
import com.kodcu.service.shortcut.MarkdownShortcutService;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.web.WebView;
import netscape.javascript.JSObject;

import java.nio.file.Path;
import java.util.Objects;
import java.util.Optional;

/**
 * Created by usta on 17.12.2014.
 */
public class MyTab extends Tab {

    private WebView webView;
    private Path path;
    private ChoiceBox<String> markup;

    private void updateMarkup() {
        String tabText = getTabText();
        if (Objects.isNull(tabText) || Objects.isNull(markup))
            return;

        if (tabText.contains(".md") || tabText.contains(".markdown"))
            markup.getSelectionModel().selectLast();
        else
            markup.getSelectionModel().selectFirst();
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

    public boolean isAsciidoc() {
        if (Objects.isNull(markup))
            return true;
        return markup.getSelectionModel().isSelected(0);
    }

    public boolean isMarkdown() {
        if (Objects.isNull(markup))
            return false;
        return markup.getSelectionModel().isSelected(1);
    }

    public void setTabText(String tabText) {
        Label label = getLabel();
        label.setText(tabText);
        updateMarkup();
    }

    public WebView getWebView() {
        return webView;
    }

    public void setWebView(WebView webView) {
        this.webView = webView;
    }

    public Path getPath() {
        return path;
    }

    public void setPath(Path path) {
        if (Objects.nonNull(markup)) {
            markup.setManaged(Objects.isNull(path));
            markup.setVisible(Objects.isNull(path));
        }
        this.path = path;
    }

    public String htmlToMarkupFunction() {
        return isAsciidoc() ? "toAsciidoc" : "toMarkdown";
    }

    public boolean isSaved() {
        return !this.getTabText().contains(" *");
    }

    public ButtonType close() {
        this.select();

        if (isSaved()) {
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

    private void cleanRemovedTabs() {

        this.setPath(null);
        this.setOnClosed(null);
        this.setOnSelectionChanged(null);
        this.setUserData(null);
        this.getLabel().setOnMouseClicked(null);
        this.setOnCloseRequest(null);
        this.setWebView(null);
        this.setGraphic(null);
        this.setContent(null);
    }

    public void select() {
        this.getTabPane().getSelectionModel().select(this);
    }

    private void closeIt() {
        Platform.runLater(() -> {
            this.getTabPane().getTabs().remove(this); // keep it here
        });
    }

    public void setMarkup(ChoiceBox markup) {
        this.markup = markup;
        ReadOnlyIntegerProperty indexProperty = this.markup.getSelectionModel().selectedIndexProperty();
        indexProperty.addListener((observable, oldValue, newValue) -> {
            if ((oldValue != newValue) && Objects.nonNull(webView)) {
                JSObject session = (JSObject) webView.getEngine().executeScript("window");
                session.call("switchMode", new Object[]{newValue});
                session.call("rerender", new Object[]{});
            }
        });
    }

    public ChoiceBox getMarkup() {
        return markup;
    }

    @Override
    public String toString() {
        return getTabText();
    }

    public Class getShortcutType() {
        if (isMarkdown())
            return MarkdownShortcutService.class;
        return AsciidocShortcutService.class;
    }
}

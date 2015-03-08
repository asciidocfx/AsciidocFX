package com.kodcu.component;

import com.kodcu.controller.ApplicationController;
import com.kodcu.service.ThreadService;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.web.WebView;
import netscape.javascript.JSObject;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Created by usta on 17.12.2014.
 */
public class MyTab extends Tab {

    private WebView webView;
    private Path path;
    private static List<Optional<Path>> closedPaths = new ArrayList<>();
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
        return markup.getSelectionModel().isSelected(0);
    }

    public boolean isMarkdown() {
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
        this.path = path;
    }

    public boolean isSaved() {
        return !this.getTabText().contains(" *");
    }

    public void close() {
        this.select();

        if (isSaved()) {
            closeIt();
            return;
        }

        Optional<ButtonType> alert = SaveAlert.alert();
        ButtonType type = alert.orElse(ButtonType.CANCEL);

        if (type == ButtonType.YES) {
            closeIt();
        }
    }

    private void select() {
        this.getTabPane().getSelectionModel().select(this);
    }

    private void closeIt() {

        ThreadService.runTaskLater(() -> {
            ThreadService.runActionLater(() -> {

                if (!this.getTabText().equals("new *")) {
                    closedPaths.add(Optional.ofNullable(this.getPath()));
                }

                this.getTabPane().getTabs().remove(this);

                this.setPath(null);
                this.setOnClosed(null);
                this.setOnSelectionChanged(null);
                this.setUserData(null);
                this.getLabel().setOnMouseClicked(null);
                this.setOnCloseRequest(null);
                this.setWebView(null);
                this.setContent(null);
//                this.setGraphic(null);

            });
        });


    }

    public static List<Optional<Path>> getClosedPaths() {
        return closedPaths;
    }

    public void setMarkup(ChoiceBox markup) {
        this.markup = markup;
        ReadOnlyIntegerProperty indexProperty = this.markup.getSelectionModel().selectedIndexProperty();
        indexProperty.addListener((observable, oldValue, newValue) -> {
            if (oldValue != newValue) {
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
        return "path=" + path;
    }
}

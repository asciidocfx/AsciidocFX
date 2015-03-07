package com.kodcu.component;

import com.kodcu.service.ThreadService;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.web.WebView;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.nio.file.Path;
import java.util.*;
import java.util.List;

/**
 * Created by usta on 17.12.2014.
 */
public class MyTab extends Tab {

    private WebView webView;
    private Path path;
    private static List<Optional<Path>> closedPaths = new ArrayList<>();

    public void setLabel(Label label) {
        this.setGraphic(label);
    }

    public Label getLabel() {
        return (Label) this.getGraphic();
    }

    public String getTabText() {
        return getLabel().getText();
    }

    public void setTabText(String tabText) {
        getLabel().setText(tabText);
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
        this.getTabPane().getTabs().remove(this);
        ThreadService.runTaskLater(() -> {
            ThreadService.runActionLater(() -> {

                if (!this.getTabText().equals("new *")) {
                    closedPaths.add(Optional.ofNullable(this.getPath()));
                }

                this.setPath(null);
                this.setOnClosed(null);
                this.setOnSelectionChanged(null);
                this.setUserData(null);
                this.getLabel().setOnMouseClicked(null);
                this.setOnCloseRequest(null);
                this.setWebView(null);
                this.setContent(null);
                this.setLabel(null);

            });
        });


    }

    public static List<Optional<Path>> getClosedPaths() {
        return closedPaths;
    }
}

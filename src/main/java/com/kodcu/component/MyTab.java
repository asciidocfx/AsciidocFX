package com.kodcu.component;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.web.WebView;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.nio.file.Path;

/**
 * Created by usta on 17.12.2014.
 */
public class MyTab extends Tab {

    private WebView webView;
    private Path path;

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

}

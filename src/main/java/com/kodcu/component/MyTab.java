package com.kodcu.component;

import javafx.scene.control.Tab;
import javafx.scene.web.WebView;

import java.nio.file.Path;

/**
 * Created by usta on 17.12.2014.
 */
public class MyTab extends Tab {

    private WebView webView;
    private Path path;

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

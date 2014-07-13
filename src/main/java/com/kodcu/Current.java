package com.kodcu;

import javafx.scene.control.Tab;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Created by usta on 18.05.2014.
 */
@Component
public class Current {

    private Tab currentTab;
    private Map<Tab, Path> newTabPaths = new HashMap<>();
    private Map<Tab, WebView> newTabWebViews = new HashMap<>();

    public Tab getCurrentTab() {
        return currentTab;
    }

    public void setCurrentTab(Tab currentTab) {
        this.currentTab = currentTab;
    }

    public Map<Tab, WebView> getNewTabWebViews() {
        return newTabWebViews;
    }

    public void setNewTabWebViews(Map<Tab, WebView> newTabWebViews) {
        this.newTabWebViews = newTabWebViews;
    }

    public Map<Tab, Path> getNewTabPaths() {
        return newTabPaths;
    }

    public void setNewTabPaths(Map<Tab, Path> newTabPaths) {
        this.newTabPaths = newTabPaths;
    }

    public void putTab(Tab tab, Path path, WebView webview) {
        setCurrentTab(tab);
        getNewTabPaths().put(tab, path);
        getNewTabWebViews().put(getCurrentTab(), webview);
    }

    public Path currentPath() {
        return getNewTabPaths().get(getCurrentTab());
    }

    public WebView currentView() {
        return getNewTabWebViews().get(getCurrentTab());
    }

    public WebEngine currentEngine() {
        WebView webView = getNewTabWebViews().get(getCurrentTab());
        return Objects.isNull(webView) ? null : webView.getEngine();
    }

    public Path currentParentRoot() {
        return currentPath().getParent();
    }
}

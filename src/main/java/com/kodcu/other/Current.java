package com.kodcu.other;

import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by usta on 18.05.2014.
 */
@Component
public class Current {

    private Tab currentTab;
    private Map<Tab, Optional<Path>> newTabPaths;
    private Map<Tab, WebView> newTabWebViews ;
    private Map<String, Integer> cache;

    public Tab getCurrentTab() {
        return currentTab;
    }

    public void setCurrentTab(Tab currentTab) {
        this.currentTab = currentTab;
    }

    public Map<Tab, WebView> getNewTabWebViews() {
        if(Objects.isNull(newTabWebViews))
            newTabWebViews = new ConcurrentHashMap<>();
        return newTabWebViews;
    }

    public void setNewTabWebViews(Map<Tab, WebView> newTabWebViews) {
        this.newTabWebViews = newTabWebViews;
    }

    public Map<Tab, Optional<Path>> getNewTabPaths() {
        if(Objects.isNull(newTabPaths))
            newTabPaths  = new ConcurrentHashMap<>();;
        return newTabPaths;
    }

    public void setNewTabPaths(Map<Tab, Optional<Path>> newTabPaths) {
        this.newTabPaths = newTabPaths;
    }

    public Map<String, Integer> getCache() {

        if (Objects.isNull(cache))
            cache = new ConcurrentHashMap<String, Integer>();
        return cache;
    }

    public void setCache(Map<String, Integer> cache) {
        this.cache = cache;
    }

    public void putTab(Tab tab, Path path, WebView webview) {
        putTab(tab, Optional.ofNullable(path), webview);
    }

    public void putTab(Tab tab, Optional<Path> path, WebView webview) {
        setCurrentTab(tab);
        if (Objects.nonNull(path))
            getNewTabPaths().put(tab, path);
        getNewTabWebViews().put(getCurrentTab(), webview);
    }

    public Optional<Path> currentPath() {
        Optional<Path> path = getNewTabPaths().get(getCurrentTab());
        return Objects.nonNull(path)?path:Optional.ofNullable(null);
    }

    public WebView currentView() {
        return getNewTabWebViews().get(getCurrentTab());
    }

    public WebEngine currentEngine() {
        WebView webView = getNewTabWebViews().get(getCurrentTab());
        return Objects.isNull(webView) ? null : webView.getEngine();
    }

    public Optional<Path> currentPathParent() {
        return currentPath().map(Path::getParent);
    }


    public void setCurrentTabText(String currentTabText) {
        Tab tab = getCurrentTab();
        Label label = (Label) tab.getGraphic();
        label.setText(currentTabText);
    }

    public String getCurrentTabText() {
        Tab tab = getCurrentTab();
        Label label = (Label) tab.getGraphic();

        return label.getText();
    }

    public String currentEditorValue() {
        String value = (String) currentEngine().executeScript("editor.getValue()");
        return value;
    }
}

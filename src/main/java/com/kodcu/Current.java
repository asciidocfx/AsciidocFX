package com.kodcu;

import javafx.scene.control.Tab;
import javafx.scene.control.TextArea;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by usta on 18.05.2014.
 */
@Component
public class Current {

    private Tab currentTab;
    private Map<Tab, Path> newTabPaths = new HashMap<>();
    private Map<Tab, TextArea> newTabTextAreas = new HashMap<>();

    public Tab getCurrentTab() {
        return currentTab;
    }

    public void setCurrentTab(Tab currentTab) {
        this.currentTab = currentTab;
    }

    public Map<Tab, TextArea> getNewTabTextAreas() {
        return newTabTextAreas;
    }

    public void setNewTabTextAreas(Map<Tab, TextArea> newTabTextAreas) {
        this.newTabTextAreas = newTabTextAreas;
    }

    public Map<Tab, Path> getNewTabPaths() {
        return newTabPaths;
    }

    public void setNewTabPaths(Map<Tab, Path> newTabPaths) {
        this.newTabPaths = newTabPaths;
    }

    public void putTab(Tab tab, Path path,TextArea textArea) {
        setCurrentTab(tab);
        getNewTabPaths().put(tab, path);
        getNewTabTextAreas().put(getCurrentTab(), textArea);
    }

    public Path currentPath() {
        return getNewTabPaths().get(getCurrentTab());
    }

    public TextArea currentTextArea() {
        return getNewTabTextAreas().get(getCurrentTab());
    }

    public Path currentRootPath() {
        return currentPath().getParent();
    }
}

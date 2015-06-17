package com.kodcu.component;

import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

import java.util.Objects;

/**
 * Created by usta on 17.06.2015.
 */
public class PreviewTab extends Tab {

    public PreviewTab(String text) {
        super(text);
    }

    public PreviewTab(String text, Node content) {

        super(text, content);
        initializeContextMenu();
    }

    private void initializeContextMenu() {
        this.setContextMenu(new ContextMenu(MenuItemBuilt.item("Close").click(e -> {
            this.getTabPane().getTabs().remove(this);
        })));
    }

    public PreviewTab() {
        initializeContextMenu();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PreviewTab that = (PreviewTab) o;
        return Objects.equals(getText(), that.getText());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getText());
    }
}

package com.kodedu.component;

import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.Tab;

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
    }

    public PreviewTab() {
    }

    public void setChild(Node node) {

        if (super.getContent() == node)
            return;

        if (!Platform.isFxApplicationThread()) {
            Platform.runLater(() -> {
                setChild(node);
            });
            return;
        }

        super.setContent(node);
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

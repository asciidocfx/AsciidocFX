package com.kodedu.component;

import javafx.scene.Node;
import javafx.scene.control.ScrollPane;

/**
 * Created by usta on 05.09.2015.
 */
public class ScrollPaneBuilt {

    private final ScrollPane scrollPane;

    public ScrollPaneBuilt(Node node) {
        this.scrollPane = new ScrollPane(node);
    }

    public static ScrollPaneBuilt content(Node node) {
        return new ScrollPaneBuilt(node);
    }

    public ScrollPane full() {
        scrollPane.setFitToWidth(true);
        return scrollPane;
    }
}

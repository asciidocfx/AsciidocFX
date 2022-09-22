package com.kodedu.helper;

import javafx.collections.ObservableList;
import javafx.css.Styleable;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;

public class FxHelper {

    public static void fitToParent(Node node) {
        AnchorPane.setTopAnchor(node, 0.0);
        AnchorPane.setBottomAnchor(node, 0.0);
        AnchorPane.setLeftAnchor(node, 0.0);
        AnchorPane.setRightAnchor(node, 0.0);
    }

    public static void addClass(Styleable styleable, String clazz) {
        ObservableList<String> styleClass = styleable.getStyleClass();
        if(!styleClass.contains(clazz)){
            styleClass.add(clazz);
        }
    }
}

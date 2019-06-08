package com.kodedu.helper;

import javafx.collections.ObservableList;
import javafx.css.Styleable;

public class StyleHelper {
    public static void addClass(Styleable styleable, String clazz) {
        ObservableList<String> styleClass = styleable.getStyleClass();
        if(!styleClass.contains(clazz)){
            styleClass.add(clazz);
        }
    }
}

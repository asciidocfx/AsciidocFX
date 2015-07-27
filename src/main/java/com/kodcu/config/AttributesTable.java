package com.kodcu.config;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Created by usta on 19.07.2015.
 */
public class AttributesTable {
    private StringProperty attribute = new SimpleStringProperty();
    private StringProperty value = new SimpleStringProperty();

    public String getAttribute() {
        return attribute.get();
    }

    public StringProperty attributeProperty() {
        return attribute;
    }

    public void setAttribute(String attribute) {
        this.attribute.set(attribute);
    }

    public String getValue() {
        return value.get();
    }

    public StringProperty valueProperty() {
        return value;
    }

    public void setValue(String value) {
        this.value.set(value);
    }
}

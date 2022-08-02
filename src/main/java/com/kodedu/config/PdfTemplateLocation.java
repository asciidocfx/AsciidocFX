package com.kodedu.config;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;


public class PdfTemplateLocation {
    private StringProperty name = new SimpleStringProperty();
    private StringProperty location = new SimpleStringProperty();

    public String getName() {
        return name.get();
    }

    public StringProperty nameProperty() {
        return name;
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public String getLocation() {
        return location.get();
    }

    public StringProperty locationProperty() {
        return location;
    }

    public void setLocation(String location) {
        this.location.set(location);
    }
}

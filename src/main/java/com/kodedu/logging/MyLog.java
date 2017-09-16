package com.kodedu.logging;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Created by usta on 04.06.2015.
 */
public class MyLog {

    private StringProperty level = new SimpleStringProperty();
    private StringProperty message = new SimpleStringProperty();

    public MyLog(String level, String message) {
        this.level.setValue(level);
        this.message.setValue(message);
    }

    public String getLevel() {
        return level.get();
    }

    public StringProperty levelProperty() {
        return level;
    }

    public void setLevel(String level) {
        this.level.set(level);
    }

    public String getMessage() {
        return message.get();
    }

    public StringProperty messageProperty() {
        return message;
    }

    public void setMessage(String message) {
        this.message.set(message);
    }
}

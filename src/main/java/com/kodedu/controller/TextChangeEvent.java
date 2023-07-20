package com.kodedu.controller;

import java.nio.file.Path;
import java.util.Objects;

public class TextChangeEvent {
    private String text;
    private String mode;
    private Path path;

    public TextChangeEvent(String text, String mode, Path path) {
        this.text = text;
        this.mode = mode;
        this.path = path;
    }

    public String getText() {
        return text;
    }

    public String getMode() {
        return mode;
    }

    public Path getPath() {
        return path;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public void setPath(Path path) {
        this.path = path;
    }

    public String getPathText() {
        return Objects.nonNull(path) ? path.toString() : null;
    }
}

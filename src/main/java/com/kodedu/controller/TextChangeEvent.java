package com.kodedu.controller;

import java.nio.file.Path;

public class TextChangeEvent {
    private final String text;
    private final String mode;
    private final Path path;

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
}

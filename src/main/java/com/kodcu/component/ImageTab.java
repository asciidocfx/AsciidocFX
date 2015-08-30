package com.kodcu.component;

import javafx.scene.control.Tab;

import java.nio.file.Path;
import java.util.Objects;

/**
 * Created by usta on 06.07.2015.
 */
public class ImageTab extends Tab {
    private Path path;

    public ImageTab(Path path) {
        this.path = path;
        setText(path.getFileName().toString());
    }

    public void setPath(Path path) {
        this.path = path;
        setText(path.getFileName().toString());
    }

    public Path getPath() {
        return path;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ImageTab imageTab = (ImageTab) o;
        return Objects.equals(path, imageTab.path);
    }

    @Override
    public int hashCode() {
        return Objects.hash(path);
    }
}

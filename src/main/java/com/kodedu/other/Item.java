package com.kodedu.other;

import java.nio.file.Path;
import java.util.Objects;

/**
 * Created by usta on 11.07.2014.
 */
public class Item {

    private String value;
    private Path path;

    public Item(Path path) {
        this.path = path;
    }

    public Item(Path path, String value) {
        this.value = value;
        this.path = path;
    }

    @Override
    public String toString() {
        if (Objects.nonNull(value))
            return value;
        return path.getFileName().toString();
    }

    public Path getPath() {
        return path;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Item item = (Item) o;

        if (Objects.isNull(path))
            return false;

        if (Objects.isNull(item.path))
            return false;

        if (!path.equals(item.path)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hash(path);
    }
}

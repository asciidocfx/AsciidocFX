package com.kodedu.service.ui;

import com.kodedu.other.Item;
import javafx.scene.Node;
import javafx.scene.control.TreeItem;

import java.util.Objects;

/**
 * Created by usta on 03.07.2016.
 */
public class PathItem extends TreeItem {

    public PathItem(Item item, Node graphic) {
        super(item, graphic);
    }

    private PathItem() {
        super();
    }

    public PathItem(Item item) {
        super(item);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PathItem pathItem = (PathItem) o;

        Item item = (Item) pathItem.getValue();

        if (item == null) {
            return false;
        }

        return Objects.equals(getValue(), item);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getValue());
    }
}

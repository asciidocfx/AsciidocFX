package com.kodedu.service.ui;

import com.kodedu.other.Item;
import javafx.scene.control.TreeItem;

import java.nio.file.Path;

/**
 * Created by usta on 12.07.2014.
 */
public interface FileBrowseService {

    public void cleanRefresh();

    public void refresh();

    public void browse(final Path path);

    public void addPathToTree(Path path, final TreeItem<Item> treeItem, Path changedPath);

    public void focusPath(Path path);

    public void refreshPathToTree(Path path, Path changedPath);

    public void searchUpAndSelect(String text);

    public void searchDownAndSelect(String text);

    public void searchAndSelect(String text);
}

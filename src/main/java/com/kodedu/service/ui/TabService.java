package com.kodedu.service.ui;

import javafx.collections.ObservableList;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import com.kodedu.component.MyTab;

/**
 * Created by usta on 25.12.2014.
 */
public interface TabService {

    public void closeFirstNewTab();

    public void addTab(Path path, Runnable... runnables);

    public void newDoc();

    public void newDoc(final String content);

    public void openDoc();

    public Path getSelectedTabPath();

    // TODO: It is not a right place for this helper
    public List<Path> getSelectedTabPaths();

    public MyTab createTab();

    public void previewDocument(Path path);

    public void addImageTab(Path imagePath);

    public void initializeTabChangeListener(TabPane tabPane);

    public ObservableList<Optional<Path>> getClosedPaths();

    public void applyForEachMyTab(Consumer<MyTab> consumer, List<? extends Tab> tabs);

    public void applyForEachMyTab(Consumer<MyTab> consumer);

}

package com.kodcu.service;

import com.kodcu.other.Item;
import javafx.scene.control.TreeView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;

/**
 * Created by usta on 31.12.2014.
 */
@Component
public class FileWatchService {

    private static Logger logger = LoggerFactory.getLogger(FileWatchService.class);

    private WatchService watcher = null;
    private Path lastWatchedPath;

    public void registerWatcher(TreeView<Item> treeView, Path path, BiConsumer<TreeView<Item>, Path> browseCallback) {
        try {
            if (watcher == null) {
                watcher = FileSystems.getDefault().newWatchService();
            }

            if (!path.equals(lastWatchedPath)) {
                if (Objects.nonNull(lastWatchedPath)) {
                    watcher.close();
                    watcher = FileSystems.getDefault().newWatchService();
                }
                lastWatchedPath = path;
                path.register(watcher, ENTRY_CREATE, ENTRY_DELETE);
            }

            WatchKey watckKey = watcher.take();
            List<WatchEvent<?>> events = watckKey.pollEvents();

            for (WatchEvent<?> event : events) {
            }
            watckKey.reset();
            browseCallback.accept(treeView, path);

        } catch (Exception e) {
            logger.error("Could not register watcher for path: {}", path, e);
        }
    }
}

package com.kodcu.service;

import com.kodcu.other.Item;
import javafx.scene.control.TreeView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.*;
import java.util.Objects;
import java.util.function.BiConsumer;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;

/**
 * Created by usta on 31.12.2014.
 */
@Component
public class FileWatchService {

    private final Logger logger = LoggerFactory.getLogger(FileWatchService.class);

    private WatchService watcher = null;
    private Path lastWatchedPath;
    private WatchKey watckKey;

    public void registerWatcher(TreeView<Item> treeView, Path path, BiConsumer<TreeView<Item>, Path> browseCallback) {
        try {
            if (Objects.isNull(watcher)) {
                watcher = FileSystems.getDefault().newWatchService();
            }

            if (!path.equals(lastWatchedPath)) {
                if (Objects.nonNull(lastWatchedPath)) {
                    invalidate();
                    watcher = FileSystems.getDefault().newWatchService();
                }
                lastWatchedPath = path;
                path.register(watcher, ENTRY_CREATE, ENTRY_DELETE);
            }

            logger.debug("Watchservice started for: {}", path);

            this.watckKey = watcher.take();
            if (watckKey.isValid()) {
                watckKey.pollEvents();
                watckKey.reset();
                browseCallback.accept(treeView, path);
            }

        }
        catch (ClosedWatchServiceException e){
            logger.debug("Watchservice closed for: {}", path, e);
        }
        catch (Exception e) {
            logger.debug("Could not register watcher for path: {}, but dont worry", path, e);
        }
    }

    public void invalidate() {
        if (Objects.nonNull(watcher)) {
            try {
                watcher.close();
            } catch (IOException e) {
                logger.debug("Problem occured while invalidating watcherservice", e);
            }
        }
    }
}

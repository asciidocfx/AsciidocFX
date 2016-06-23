package com.kodcu.service;

import com.kodcu.component.MyTab;
import com.kodcu.controller.ApplicationController;
import com.kodcu.service.ui.FileBrowseService;
import javafx.collections.ObservableList;
import javafx.scene.control.Tab;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.nio.file.*;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static java.nio.file.StandardWatchEventKinds.*;

/**
 * Created by usta on 31.12.2014.
 */
@Component
public class FileWatchService {

    private final Logger logger = LoggerFactory.getLogger(FileWatchService.class);

    private WatchService watcher = null;
    private Path lastWatchedPath;
    private WatchKey watckKey;
    private final ApplicationController controller;
    private final ThreadService threadService;
    private final FileBrowseService fileBrowseService;

    private WatchKey lastWatchKey;

    @Autowired
    public FileWatchService(ApplicationController controller, ThreadService threadService, FileBrowseService fileBrowseService) {
        this.controller = controller;
        this.threadService = threadService;
        this.fileBrowseService = fileBrowseService;
    }

    public void registerWatcher(Path path) {

        threadService.runTaskLater(() -> {
            try {
                if (Objects.isNull(watcher)) {
                    watcher = FileSystems.getDefault().newWatchService();
                }

                if (!path.equals(lastWatchedPath)) {
                    if (Objects.nonNull(lastWatchKey)) {
                        lastWatchKey.cancel();
                        watcher.close();
                        watcher = FileSystems.getDefault().newWatchService();
                    }
                    lastWatchedPath = path;
                    this.lastWatchKey = path.register(watcher, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);
                }

                logger.debug("Watchservice started for: {}", path);

                while (true) {

                    if (!lastWatchKey.isValid())
                        break;

                    this.watckKey = watcher.take();
                    List<WatchEvent<?>> watchEvents = watckKey.pollEvents();
                    boolean updateFsView = false;
                    for (WatchEvent<?> event : watchEvents) {
                        WatchEvent.Kind<?> kind = event.kind();
                        if (kind == ENTRY_MODIFY && event.count() == 1) {
                            WatchEvent<Path> ev = (WatchEvent<Path>) event;
                            Path modifiedPath = path.resolve(ev.context());
                            ObservableList<Tab> tabs = controller.getTabPane().getTabs();
                            for (Tab tab : tabs) {
                                if(tab instanceof MyTab){
                                    MyTab myTab = (MyTab) tab;
                                    if (modifiedPath.equals(myTab.getPath())) {
                                        myTab.reload();
                                        break;
                                    }
                                }
                            }
                            watckKey.reset();
                        } else if (kind == ENTRY_MODIFY && event.count() > 1) {
                            watckKey.reset();
                        } else {
                            updateFsView = true;
                            watckKey.reset();
                        }
                    }

                    if (updateFsView) {
                        threadService.buff("watchService").schedule(() -> {
                            fileBrowseService.browse(lastWatchedPath);
                        }, 500, TimeUnit.MILLISECONDS);
                    }

                }

            } catch (ClosedWatchServiceException e) {
                logger.debug("Watchservice closed for: {}", path, e);
            } catch (Exception e) {
                logger.debug("Could not register watcher for path: {}, but dont worry", path, e);
            }
        });
    }

}

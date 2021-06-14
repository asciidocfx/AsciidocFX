package com.kodedu.service.impl;

import com.kodedu.component.MyTab;
import com.kodedu.controller.ApplicationController;
import com.kodedu.helper.IOHelper;
import com.kodedu.service.FileWatchService;
import com.kodedu.service.PathMapper;
import com.kodedu.service.ThreadService;
import com.kodedu.service.ui.FileBrowseService;
import com.kodedu.service.ui.TabService;
import javafx.collections.ObservableList;
import javafx.scene.control.Tab;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.nio.file.*;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import static java.nio.file.StandardWatchEventKinds.*;

/**
 * Created by usta on 31.12.2014.
 */
@Component
public class FileWatchServiceImpl implements FileWatchService {

    private final Logger logger = LoggerFactory.getLogger(FileWatchService.class);

    private WatchService watcher = null;
    private final ApplicationController controller;
    private final ThreadService threadService;

    @Autowired
    private FileBrowseService fileBrowseService;

    @Autowired
    private ApplicationContext applicationContext;

    private Map<WatchKey, Path> watchKeys = new ConcurrentHashMap<>();
    private final PathMapper pathMapper;

    @Autowired
    public FileWatchServiceImpl(ApplicationController controller, ThreadService threadService, PathMapper pathMapper) {
        this.controller = controller;
        this.threadService = threadService;
        this.pathMapper = pathMapper;
    }

    @PostConstruct
    public void init() {
        threadService.runTaskLater(this::watchPathChanges);
    }

    @Override
    public void reCreateWatchService() {
        if (Objects.nonNull(watcher)) {
            try {
                if (watchKeys.keySet().size() > 10) {
                    unRegisterAllPath();
                    applicationContext.getBean(TabService.class)
                            .applyForEachMyTab(myTab -> {
                                registerPathWatcher(myTab.getPath());
                            });
                }
            } catch (Exception e) {
                logger.warn(e.getMessage());
            }
        } else {
            watcher = IOHelper.newWatchService();
        }

    }

    @Override
    public void unRegisterAllPath() {
        for (Map.Entry<WatchKey, Path> entry : watchKeys.entrySet()) {
            WatchKey watchKey = entry.getKey();
            watchKey.cancel();
            Path path = entry.getValue();
            logger.info("Watch service cancelled watching {}", path);
        }
        watchKeys.clear();
    }

    private void watchPathChanges() {

        if (Objects.isNull(watcher)) {
            reCreateWatchService();
        }

        while (true) {

            if (Objects.isNull(watcher)) {
                break;
            }

            WatchKey watchKey = null;
            Path path = null;

            try {
                watchKey = watcher.take();
                path = watchKeys.get(watchKey);

            } catch (ClosedWatchServiceException cws) {
                if (Objects.nonNull(path)) {
                    logger.info("Watch service closed for: {}", path);
                }
            } catch (Exception ex) {
                logger.warn(ex.getMessage());
                continue;
            }

            List<WatchEvent<?>> watchEvents = watchKey.pollEvents();

            boolean updateFsView = false;
            for (WatchEvent<?> event : watchEvents) {
                WatchEvent.Kind<?> kind = event.kind();
                if (kind == ENTRY_MODIFY && event.count() == 1) {
                    WatchEvent<Path> ev = (WatchEvent<Path>) event;
                    Path modifiedPath = path.resolve(ev.context());
                    ObservableList<Tab> tabs = controller.getTabPane().getTabs();
                    for (Tab tab : tabs) {
                        if (tab instanceof MyTab) {
                            MyTab myTab = (MyTab) tab;
                            if (modifiedPath.equals(myTab.getPath())) {
                                threadService.runActionLater(() -> {
                                    myTab.reload();
                                });
                                break;
                            }
                        }
                    }
                    watchKey.reset();
                } else if (kind == ENTRY_MODIFY && event.count() > 1) {
                    watchKey.reset();
                } else {
                    updateFsView = true;
                    watchKey.reset();
                }

            }

            if (updateFsView) {
                Path changedPath = null;

                if (watchEvents.size() == 1) {
                    WatchEvent<Path> ev = (WatchEvent<Path>) watchEvents.get(0);
                    changedPath = path.resolve(ev.context());
                    pathMapper.addPath(changedPath);
                } else {
                    pathMapper.addRootPath(path);
                }
                fileBrowseService.refreshPathToTree(path, changedPath);
            }

        }

    }

    @Override
    public void registerPathWatcher(final Path path) {

        threadService.runTaskLater(() -> {

            if (Objects.isNull(path)) {
                return;
            }

            Path finalPath = null;

            if (!Files.isDirectory(path)) {
                finalPath = path.getParent();
            } else {
                finalPath = path;
            }

            try {
                boolean isRegistered = isRegisteredPath(finalPath, watchKeys);

                if (!isRegistered) {
                    WatchKey watchKey = finalPath.register(watcher, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);
                    watchKeys.put(watchKey, finalPath);
                }
            } catch (Exception e) {
                logger.warn("Couldn't register watcher for: {}", finalPath);
            }
        });
    }

    @Override
    public boolean isRegisteredPath(Path finalPath, Map<WatchKey, Path> watchKeys) {
        boolean exist = false;
        for (Map.Entry<WatchKey, Path> entry : watchKeys.entrySet()) {
            WatchKey key = entry.getKey();
            Path value = entry.getValue();

            if (finalPath.equals(value)) {
                if (key.isValid()) {
                    exist = true;
                    break;
                }
            }
        }

        return exist;
    }

    @Override
    public void unRegisterPath(Path path) {
        try {
            for (Map.Entry<WatchKey, Path> entry : watchKeys.entrySet()) {
                Path registeredPath = entry.getValue();
                if (path.equals(registeredPath)) {
                    WatchKey watchKey = entry.getKey();
                    watchKey.cancel();
                    watchKeys.remove(watchKey);
                    break;
                }

            }
        } catch (Exception ex) {

        }
    }
}

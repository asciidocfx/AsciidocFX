package com.kodedu.service;

import java.nio.file.Path;
import java.nio.file.WatchKey;
import java.util.Map;

/**
 * Created by usta on 31.12.2014.
 */
public interface FileWatchService {

    public void reCreateWatchService();

    public void unRegisterAllPath();

    public void registerPathWatcher(final Path path);

    public boolean isRegisteredPath(Path finalPath, Map<WatchKey, Path> watchKeys);

    public void unRegisterPath(Path path);
}

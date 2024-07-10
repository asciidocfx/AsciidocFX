package com.kodedu.service.cache.impl;

import com.kodedu.helper.IOHelper;
import com.kodedu.other.Current;
import com.kodedu.service.ThreadService;
import com.kodedu.service.cache.BinaryCacheService;
import com.kodedu.service.cache.CacheData;
import com.kodedu.service.cache.InDiskData;
import com.kodedu.service.cache.InMemoryDAta;

import javafx.application.Platform;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by usta on 12.06.2016.
 */
@Component(BinaryCacheService.label)
public class BinaryCacheServiceImpl implements BinaryCacheService {

    private final long maximumSize = 50 * 1024 * 1024;
    private final AtomicLong totalSize = new AtomicLong(0);
    private final ConcurrentHashMap<String, CacheData> cache = new ConcurrentHashMap<>();
    private final Current current;
    @Autowired
    private ThreadService threadService;

    private Logger logger = LoggerFactory.getLogger(BinaryCacheService.class);

    @Autowired
    public BinaryCacheServiceImpl(Current current) {
        this.current = current;
    }

    @Override
    public String putBinary(String key, byte[] bytes) {
        key = alignHttpKey(key);
        if (Platform.isFxApplicationThread()) {
            String finalKey = key;
            threadService.runTaskLater(() -> {
                putBinary(finalKey, bytes);
            });
            return key;
        }

        synchronized (this) {

            if (hasCacheFor(bytes)) {
                saveInMemory(key, bytes);
            } else {
                saveInDisk(key, bytes);
            }

            return key;
        }
    }

    private void enLargeCache() {
        threadService.runTaskLater(() -> {
            Collection<CacheData> values = cache.values();
            logger.debug("Enlarge cache: {}", values.size());
            values
                    .stream()
                    .filter(e -> e.inDisk())
                    .sorted((o1, o2) -> {
                        Long l1 = o1.lastModified();
                        Long l2 = o2.lastModified();
                        return l1.compareTo(l2);
                    }).limit(5)
                    .forEach(cacheData -> {
                        logger.debug("Larged: {}", cacheData.key());
                        saveInMemory(cacheData.key(), cacheData.readBytes());
                    });
        });
    }

    private void shrinkCache() {
        threadService.runTaskLater(() -> {
            Collection<CacheData> values = cache.values();
            logger.debug("Shrinking cache, total entries: {}", values.size());
            values
                    .stream()
                    .filter(e -> e.inMemory())
                    .sorted((o1, o2) -> {
                        Long l1 = o1.lastModified();
                        Long l2 = o2.lastModified();
                        return l2.compareTo(l1);
                    }).limit(5)
                    .forEach(cacheData -> {
                        logger.debug("Shrunk cache entry: {}", cacheData.key());
                        byte[] bytes = cacheData.readBytes();
                        saveInDisk(cacheData.key(), bytes);
                        totalSize.addAndGet(-bytes.length);
                    });
        });
    }

    private void saveInDisk(String key, byte[] bytes) {
        threadService.runActionLater(() -> {
            Path tempFile = IOHelper.createTempFile(current.currentPath().get().getParent(), ".png");

//            System.out.println(tempFile.getParent());
            IOHelper.writeToFile(tempFile, bytes, StandardOpenOption.CREATE);

            Optional.ofNullable(cache.get(key))
                    .ifPresent(CacheData::removeFromDisk);

            cache.put(key, new InDiskData(key, tempFile));
        });
    }

    private void saveInMemory(String key, byte[] bytes) {

        Optional.ofNullable(cache.get(key))
                .ifPresent(CacheData::removeFromDisk);

        Optional.ofNullable(cache.get(key))
                .filter(e -> e.inMemory())
                .map(e -> e.length())
                .ifPresent(aLong -> totalSize.addAndGet(-aLong));

        cache.put(key, new InMemoryDAta(key, bytes));
        totalSize.addAndGet(bytes.length);
    }

    private boolean hasCacheFor(byte[] bytes) {
        return maximumSize > (getTotalSize() + bytes.length);
    }

    private long getTotalSize() {
        return totalSize.get();
    }

    @Override
    public CacheData getCacheData(String key) {
        key = alignHttpKey(key);
        return cache.get(key);
    }

    @Override
    public void putBinary(String key, BufferedImage trimmed) {
        key = alignHttpKey(key);
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();) {
            ImageIO.write(trimmed, "png", outputStream);
            byte[] bytes = outputStream.toByteArray();
            putBinary(key, bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String alignHttpKey(String key) {
        String cacheSeparator = "/afx/cache";
        if (Objects.nonNull(key) && key.contains(cacheSeparator) && key.startsWith("http")) {
            key = key.substring(key.indexOf(cacheSeparator));
        }
        return key;
    }

    @Override
    public boolean hasCache(String key) {
        return cache.containsKey(key);
    }
}

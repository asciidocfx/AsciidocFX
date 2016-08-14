package com.kodcu.service.cache;

import com.kodcu.other.Current;
import com.kodcu.other.IOHelper;
import com.kodcu.service.ThreadService;
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
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by usta on 12.06.2016.
 */
@Component
public class BinaryCacheService {

    private final long maximumSize = 50 * 1024 * 1024;
    private final AtomicLong totalSize = new AtomicLong(0);
    private final ConcurrentHashMap<String, CacheData> cache = new ConcurrentHashMap<>();
    private final ThreadService threadService;
    private final Current current;

    private Logger logger = LoggerFactory.getLogger(BinaryCacheService.class);

    @Autowired
    public BinaryCacheService(ThreadService threadService, Current current) {
        this.threadService = threadService;
        this.current = current;
    }

    public String putBinary(String key, byte[] bytes) {

        if (Platform.isFxApplicationThread()) {
            threadService.runTaskLater(() -> {
                putBinary(key, bytes);
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
            logger.debug("Shrink cache: {}", values.size());
            values
                    .stream()
                    .filter(e -> e.inMemory())
                    .sorted((o1, o2) -> {
                        Long l1 = o1.lastModified();
                        Long l2 = o2.lastModified();
                        return l2.compareTo(l1);
                    }).limit(5)
                    .forEach(cacheData -> {
                        logger.debug("Shrinked: {}", cacheData.key());
                        byte[] bytes = cacheData.readBytes();
                        saveInDisk(cacheData.key(), bytes);
                        totalSize.addAndGet(-bytes.length);
                    });
        });
    }

    private void saveInDisk(String key, byte[] bytes) {
        threadService.runActionLater(() -> {
            Path tempFile = IOHelper.createTempFile(current.currentPath().get().getParent(), ".png");

            System.out.println(tempFile.getParent());
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
                .filter(e->e.inMemory())
                .map(e->e.length())
                .ifPresent(aLong -> totalSize.addAndGet(-aLong));

        cache.put(key, new InMemoryDAta(key, bytes));
        long length = totalSize.addAndGet(bytes.length);
    }

    private boolean hasCacheFor(byte[] bytes) {
        return maximumSize > (getTotalSize() + bytes.length);
    }

    private long getTotalSize() {
        return totalSize.get();
    }

    public CacheData getCacheData(String key) {
        return cache.get(key);
    }

    public void putBinary(String key, BufferedImage trimmed) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();) {
            ImageIO.write(trimmed, "png", outputStream);
            byte[] bytes = outputStream.toByteArray();
            putBinary(key, bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean hasCache(String key) {
        return cache.containsKey(key);
    }
}

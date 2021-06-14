package com.kodedu.service.cache;

import java.awt.image.BufferedImage;

/**
 * Created by usta on 12.06.2016.
 */
public interface BinaryCacheService {
    public String putBinary(String key, byte[] bytes);

    public CacheData getCacheData(String key);

    public void putBinary(String key, BufferedImage trimmed);

    public boolean hasCache(String key);
}

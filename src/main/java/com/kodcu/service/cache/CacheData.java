package com.kodcu.service.cache;

import java.io.IOException;

/**
 * Created by usta on 12.06.2016.
 */
public interface CacheData {

    long length();

    void seek(long pos) throws IOException;

    byte[] readBytes();

    long lastModified();

    int read(byte[] buffer) throws IOException;

    void closeStream() throws IOException;

    boolean inMemory();

    boolean inDisk();

    String key();

    void removeFromDisk();
}

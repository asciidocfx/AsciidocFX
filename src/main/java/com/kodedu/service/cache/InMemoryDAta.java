package com.kodedu.service.cache;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.Objects;

/**
 * Created by usta on 12.06.2016.
 */
public class InMemoryDAta implements CacheData {

    private final byte[] bytes;
    private final ZonedDateTime lastModified;
    private final String key;
    private ByteArrayInputStream byteArrayStream;

    public InMemoryDAta(String key, byte[] bytes) {
        this.key = key;
        this.bytes = bytes;
        this.lastModified = ZonedDateTime.now();
    }

    @Override
    public byte[] readBytes() {
        return bytes;
    }

    @Override
    public long length() {
        return bytes.length;
    }

    @Override
    public void seek(long pos) throws IOException {
        if (Objects.isNull(byteArrayStream)) {
            this.byteArrayStream = new ByteArrayInputStream(bytes);
        }
        // 2 lines equal to seek
        byteArrayStream.reset();
        byteArrayStream.skip(pos);
    }

    @Override
    public long lastModified() {
        return lastModified.toEpochSecond();
    }

    @Override
    public int read(byte[] buffer) throws IOException {
        if (Objects.isNull(byteArrayStream)) {
            this.byteArrayStream = new ByteArrayInputStream(bytes);
        }
        return byteArrayStream.read(buffer);
    }

    @Override
    public void closeStream() throws IOException {
        ByteArrayInputStream currentByteArrayStream = byteArrayStream;
        if (Objects.nonNull(currentByteArrayStream)) {
            byteArrayStream = null;
            currentByteArrayStream.close();
        }
    }

    @Override
    public boolean inMemory() {
        return true;
    }

    @Override
    public boolean inDisk() {
        return false;
    }

    @Override
    public String key() {
        return key;
    }

    @Override
    public void removeFromDisk() {
// no-op
    }
}

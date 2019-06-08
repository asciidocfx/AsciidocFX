package com.kodedu.service.cache;

import com.kodedu.helper.IOHelper;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Path;
import java.util.Objects;

/**
 * Created by usta on 12.06.2016.
 */
public class InDiskData implements CacheData {
    private final Path path;
    private final String key;
    private RandomAccessFile randomAccessFile;

    public InDiskData(String key, Path path) {
        this.key = key;
        this.path = path;
    }

    @Override
    public byte[] readBytes() {
        return IOHelper.readAllBytes(path);
    }

    @Override
    public long length() {
        return path.toFile().length();
    }

    @Override
    public void seek(long pos) throws IOException {
        if (Objects.isNull(randomAccessFile)) {
            this.randomAccessFile = new RandomAccessFile(path.toFile(), "r");
        }

        randomAccessFile.seek(pos);

    }

    @Override
    public long lastModified() {
        return path.toFile().lastModified();
    }

    @Override
    public int read(byte[] buffer) throws IOException {
        if (Objects.isNull(randomAccessFile)) {
            this.randomAccessFile = new RandomAccessFile(path.toFile(), "r");
        }
        return randomAccessFile.read(buffer);
    }

    @Override
    public void closeStream() throws IOException {
        RandomAccessFile currentRandomAccessFile = randomAccessFile;
        if (Objects.nonNull(currentRandomAccessFile)) {
            this.randomAccessFile = null;
            currentRandomAccessFile.close();
        }
    }

    @Override
    public void removeFromDisk() {
        try {
            closeStream();
        } catch (IOException e) {
//            e.printStackTrace();
        }
        IOHelper.deleteIfExists(path);
    }

    @Override
    public boolean inMemory() {
        return false;
    }

    @Override
    public boolean inDisk() {
        return true;
    }

    @Override
    public String key() {
        return key;
    }
}

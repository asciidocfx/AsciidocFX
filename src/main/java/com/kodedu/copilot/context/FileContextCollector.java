package com.kodedu.copilot.context;

import com.kodedu.helper.IOHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Collects file content for Copilot context.
 */
@Component
public class FileContextCollector {

    private static final Logger logger = LoggerFactory.getLogger(FileContextCollector.class);
    private static final long MAX_FILE_SIZE = 100_000; // 100KB limit

    /**
     * Reads the content of a file, respecting size limits.
     */
    public String readFileContent(Path path) {
        try {
            if (path == null || !Files.exists(path) || !Files.isRegularFile(path)) {
                return null;
            }
            if (Files.size(path) > MAX_FILE_SIZE) {
                return readTruncated(path);
            }
            return Files.readString(path);
        } catch (IOException e) {
            logger.debug("Could not read file: {}", path, e);
            return null;
        }
    }

    private String readTruncated(Path path) {
        try {
            try (var is = Files.newInputStream(path)) {
                byte[] bytes = is.readNBytes((int) MAX_FILE_SIZE);
                return new String(bytes) + "\n... (truncated)";
            }
        } catch (IOException e) {
            return null;
        }
    }
}

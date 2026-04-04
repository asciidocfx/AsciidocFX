package com.kodedu.copilot.context;

import com.kodedu.service.DirectoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Collects workspace/project structure information for Copilot context.
 */
@Component
public class WorkspaceContextCollector {

    private static final Logger logger = LoggerFactory.getLogger(WorkspaceContextCollector.class);
    private static final int MAX_FILES = 50;
    private static final int MAX_DEPTH = 3;

    private final DirectoryService directoryService;

    @Autowired
    public WorkspaceContextCollector(DirectoryService directoryService) {
        this.directoryService = directoryService;
    }

    /**
     * Collects a summary of the workspace structure.
     */
    public String collectContext() {
        try {
            Path workingDir = directoryService.workingDirectory();
            if (workingDir == null || !Files.isDirectory(workingDir)) {
                return "";
            }

            StringBuilder sb = new StringBuilder();
            sb.append("Working directory: ").append(workingDir).append("\n");
            sb.append("Files:\n");

            try (Stream<Path> walk = Files.walk(workingDir, MAX_DEPTH)) {
                String fileList = walk
                        .filter(p -> !p.toString().contains(".git"))
                        .filter(p -> !p.toString().contains("node_modules"))
                        .filter(Files::isRegularFile)
                        .limit(MAX_FILES)
                        .map(p -> "  " + workingDir.relativize(p))
                        .collect(Collectors.joining("\n"));
                sb.append(fileList);
            }

            return sb.toString();
        } catch (IOException e) {
            logger.debug("Could not collect workspace context", e);
            return "";
        }
    }
}

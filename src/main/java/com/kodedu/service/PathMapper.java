package com.kodedu.service;

import com.kodedu.helper.IOHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

@Component
public class PathMapper {

    private final ThreadService threadService;

    private Logger logger = LoggerFactory.getLogger(PathMapper.class);

    private final ConcurrentHashMap<Path, Set<Path>> pathMap = new ConcurrentHashMap<>();

    @Autowired
    public PathMapper(ThreadService threadService) {
        this.threadService = threadService;
    }

    public void addPath(Path path) {
        if (Objects.isNull(path)) {
            return;
        }
        Path fileName = path.getFileName();

        if (Objects.isNull(fileName)) {
            return;
        }

        pathMap.putIfAbsent(fileName, ConcurrentHashMap.newKeySet());

        pathMap.get(fileName).add(path.normalize());
    }

    public void addFileParent(Path path) {
        if (Objects.isNull(path)) {
            return;
        }
        if (Files.isDirectory(path)) {
            addRootPath(path);
        } else {
            addRootPath(path.getParent());
        }

    }

    public void addRootPath(Path path) {
        if (Objects.isNull(path)) {
            return;
        }

        Iterable<Path> rootDirs = FileSystems.getDefault().getRootDirectories();
        for (Path rootDir : rootDirs) {
            if (path.equals(rootDir)) {
                return;
            }
        }

        Path userHome = IOHelper.getPath(System.getProperty("user.home"));

        if (path.equals(userHome)) {
            return;
        }

        threadService.start(() -> {

            logger.info("Indexing.. {}", path);

            try (Stream<Path> pathStream = IOHelper.walk(path);) {
                pathStream
                        .filter(p -> !Files.isDirectory(p))
                        .forEach(p -> {
                            this.addPath(p);
                        });
                logger.info("Indexing completed.. {}", path);
            }
        });
    }

    public Optional<Path> lookUpFile(String file) {

        if (Objects.isNull(file)) {
            return Optional.empty();
        }

        Path fileName = IOHelper.getPath(file).getFileName();

        if (Objects.isNull(fileName)) {
            return Optional.empty();
        }

        return lookUpFile(fileName);

    }

    public Optional<Path> lookUpFile(Path file) {
        if (Objects.isNull(file)) {
            return Optional.empty();
        }
        Path fileName = file.getFileName();

        if (Objects.isNull(fileName)) {
            return Optional.empty();
        }

        Set<Path> pathList = pathMap.get(fileName);

        if (Objects.isNull(pathList)) {
            return Optional.empty();
        }

        pathList.removeIf(Files::notExists);

        if (pathList.size() > 1) {
            return Optional.empty();
        }

        for (Path path : pathList) {
            if (Files.exists(path)) {
                return Optional.ofNullable(path);
            }
        }

        return Optional.empty();
    }
}

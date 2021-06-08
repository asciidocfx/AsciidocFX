package com.kodedu.service;

import com.kodedu.other.Current;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.util.Objects;
import java.util.stream.IntStream;

/**
 * Created by usta on 20.05.2015.
 */
@Component("pathFinder")
@Scope("prototype")
public class PathFinderService {

    private final Current current;
    private final DirectoryService directoryService;

    @Autowired
    public PathFinderService(Current current, DirectoryService directoryService) {
        this.current = current;
        this.directoryService = directoryService;
    }

    public Path findPath(String uri, Integer parent) {

        if (Objects.isNull(parent))
            parent = 0;

        Path file;

        if (current.currentPath().isPresent()) {
            final Integer finalParent = parent;
            file = current.currentPath().map((Path path) -> {
                final Path[] parentPath = {path};
                IntStream.rangeClosed(0, finalParent).forEach(i -> {
                    if (Objects.nonNull(parentPath[0].getParent()))
                        parentPath[0] = parentPath[0].getParent();
                });
                return parentPath[0];
            }).get().resolve(uri);
        } else {
            final Integer finalParent1 = parent;
            file = directoryService.getWorkingDirectory().map((Path path) -> {
                final Path[] parentPath = {path};
                IntStream.range(0, finalParent1).forEach(i -> {
                    if (Objects.nonNull(parentPath[0].getParent()))
                        parentPath[0] = parentPath[0].getParent();
                });
                return parentPath[0];
            }).get().resolve(uri);
        }
        return file;
    }
}

package com.kodcu.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Function;

/**
 * Created by usta on 01.01.2015.
 */
@Component
public class PathOrderService {

    @Autowired
    private PathResolverService pathResolver;

    public int comparePaths(Path first, Path second) {

        if (comparePathsFunction(first, second, pathResolver::isBook)) return 1;
        if (comparePathsFunction(second, first, pathResolver::isBook)) return -1;
        if (comparePathsFunction(first, second, pathResolver::isAsciidoc)) return 1;
        if (comparePathsFunction(second, first, pathResolver::isAsciidoc)) return -1;
        if (comparePathsFunction(first, second, pathResolver::isPDF)) return 1;
        if (comparePathsFunction(second, first, pathResolver::isPDF)) return -1;
        if (comparePathsFunction(first, second, pathResolver::isHTML)) return 1;
        if (comparePathsFunction(second, first, pathResolver::isHTML)) return -1;
        if (comparePathsFunction(first, second, pathResolver::isEpub)) return 1;
        if (comparePathsFunction(second, first, pathResolver::isEpub)) return -1;
        if (comparePathsFunction(first, second, pathResolver::isMobi)) return 1;
        if (comparePathsFunction(second, first, pathResolver::isMobi)) return -1;
        if (comparePathsFunction(first, second, pathResolver::isDocbook)) return 1;
        if (comparePathsFunction(second, first, pathResolver::isDocbook)) return -1;
        if (comparePathsFunction(first, second, pathResolver::isImage)) return 1;
        if (comparePathsFunction(second, first, pathResolver::isImage)) return -1;
        if (comparePathsFunction(first, second, Files::isDirectory)) return 1;
        if (comparePathsFunction(second, first, Files::isDirectory)) return -1;

        return first.compareTo(second);
    }

    private boolean comparePathsFunction(Path first, Path second, Function<Path, Boolean> function) {
        return !function.apply(first) && function.apply(second);
    }
}

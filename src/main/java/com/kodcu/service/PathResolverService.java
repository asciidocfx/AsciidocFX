package com.kodcu.service;

import javafx.stage.FileChooser;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.util.*;

/**
 * Created by usta on 07.09.2014.
 */
@Component
public class PathResolverService {

    private static final List<String> rootList =
            Arrays.asList("book.asc", "book.txt", "book.asciidoc", "book.adoc", "book.ad");

    PathMatcher pdfMatcher = FileSystems.getDefault().getPathMatcher("glob:**.pdf");
    PathMatcher htmlMatcher = FileSystems.getDefault().getPathMatcher("glob:**.html");
    PathMatcher docBookMatcher = FileSystems.getDefault().getPathMatcher("glob:**.xml");
    PathMatcher ascMatcher = FileSystems.getDefault().getPathMatcher("glob:**.{asc,asciidoc,ad,adoc,txt}");

    private Map<String, Boolean> rootExists = new HashMap<>();

    public boolean isPDF(Path path) {
        return pdfMatcher.matches(path);
    }

    public boolean isHidden(Path path) {
        try {
            return Files.isHidden(path) || path.getFileName().toString().startsWith(".");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean isDocbook(Path path) {
        return docBookMatcher.matches(path);
    }

    public boolean isHTML(Path path) {
        return htmlMatcher.matches(path);
    }

    public boolean isAsciidoc(Path path) {
        return ascMatcher.matches(path);
    }

    public Path resolve(Path currentPath) {

        rootExists.clear();
        rootList.forEach(p -> {
            rootExists.put(p, false);
        });

        long bookFileCount = rootExists.keySet()
                .stream()
                .map(p -> currentPath.resolve(p))
                .filter(p -> {
                    String fileName = p.getFileName().toString();
                    boolean exists = Files.exists(p);
                    rootExists.put(fileName, exists);
                    return exists;
                })
                .count();

        if (bookFileCount != 1) {

            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Select book.* root file");
            fileChooser.setInitialDirectory(currentPath.toFile());
            File file = fileChooser.showOpenDialog(null);
            if (Objects.isNull(file))
                return null;
            return file.toPath();
        }

        for (String path : rootList) {
            Boolean exists = rootExists.get(path);
            if (exists)
                return currentPath.resolve(path);
        }

        return null;
    }
}

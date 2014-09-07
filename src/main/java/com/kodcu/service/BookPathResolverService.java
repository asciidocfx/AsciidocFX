package com.kodcu.service;

import javafx.stage.FileChooser;
import org.springframework.stereotype.Component;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

/**
 * Created by usta on 07.09.2014.
 */
@Component
public class BookPathResolverService {

    private static final List<String> rootList =
            Arrays.asList("book.asc", "book.txt", "book.asciidoc", "book.adoc", "book.ad");

    private Map<String, Boolean> rootExists = new HashMap<>();

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

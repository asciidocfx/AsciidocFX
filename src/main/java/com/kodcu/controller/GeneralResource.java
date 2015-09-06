package com.kodcu.controller;

import com.kodcu.other.Current;
import com.kodcu.service.DirectoryService;
import com.kodcu.service.ui.TabService;
import javafx.application.Platform;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletResponse;
import java.nio.file.Path;

/**
 * Created by usta on 05.09.2015.
 */
@Component
public class GeneralResource {

    private final Current current;
    private final TabService tabService;
    private final DirectoryService directoryService;
    private final FileService fileService;

    @Autowired
    public GeneralResource(Current current, TabService tabService, DirectoryService directoryService, FileService fileService) {
        this.current = current;
        this.tabService = tabService;
        this.directoryService = directoryService;
        this.fileService = fileService;
    }


    public void executeAfxResource(AllController.Payload payload) {

        String finalURI = payload.getFinalURI();
        if (finalURI.matches(".*\\.(asc|asciidoc|ad|adoc|md|markdown)$")) {

            current.currentPath().ifPresent(path -> {

                Path ascFile = path.getParent().resolve(finalURI);

                Platform.runLater(() -> {
                    tabService.addTab(ascFile);
                });

            });
            payload.setStatus(HttpStatus.NO_CONTENT);
        } else {
            Path path = directoryService.findPathInConfigOrCurrentOrWorkDir(finalURI);
            fileService.processFile(payload, path);
        }
    }
}

package com.kodcu.controller;

import com.kodcu.other.Current;
import com.kodcu.service.DirectoryService;
import com.kodcu.service.ThreadService;
import com.kodcu.service.ui.TabService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.util.Optional;

/**
 * Created by usta on 05.09.2015.
 */
@Component
public class WebWorkerResource {

    private final Current current;
    private final TabService tabService;
    private final DirectoryService directoryService;
    private final FileService fileService;
    private final ThreadService threadService;
    private final ApplicationController controller;
    private final DataUriController dataUriService;

    @Autowired
    public WebWorkerResource(Current current, TabService tabService, DirectoryService directoryService, FileService fileService, ThreadService threadService, ApplicationController controller, DataUriController dataUriService) {
        this.current = current;
        this.tabService = tabService;
        this.directoryService = directoryService;
        this.fileService = fileService;
        this.threadService = threadService;
        this.controller = controller;
        this.dataUriService = dataUriService;
    }


    public void executeWorkerResource(AllController.Payload payload) {

        Optional.ofNullable(payload.getRequestURI())
                .filter(e -> e.endsWith("resource.afx"))
                .ifPresent(e -> {
                    payload.setFinalURI(payload.param("path"));
                });

        String finalURI = payload.getFinalURI();
        if (finalURI.matches(".*\\.(asc|asciidoc|ad|adoc|md|markdown)$")) {

            if (controller.getIncludeAsciidocResource()) {
                payload.write(String.format("link:%s[]", finalURI));
                return;
            }

            Path path = directoryService.findPathInConfigOrCurrentOrWorkDir(finalURI);
            fileService.processFile(payload, path);

        } else if (payload.getRequestURI().endsWith("webworker.js")) {
            Path path = directoryService.findPathInConfigOrCurrentOrWorkDir("js/webworker.js");
            fileService.processFile(payload, path);
        } else if (payload.getRequestURI().endsWith("asciidoctor-default.css")) {
            final String stylesheet = controller.readDefaultStylesheet();
            payload.write(stylesheet);
        } else {
            Path path = directoryService.findPathInConfigOrCurrentOrWorkDir(finalURI);
            fileService.processFile(payload, path);
        }
    }
}

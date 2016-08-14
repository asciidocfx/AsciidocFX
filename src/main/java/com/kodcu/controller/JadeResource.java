package com.kodcu.controller;

import com.kodcu.other.Current;
import com.kodcu.service.DirectoryService;
import com.kodcu.service.ThreadService;
import com.kodcu.service.ui.TabService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.nio.file.Path;

/**
 * Created by usta on 05.09.2015.
 */
@Component
public class JadeResource {

    private final Current current;
    private final TabService tabService;
    private final DirectoryService directoryService;
    private final FileService fileService;
    private final ThreadService threadService;
    private final ApplicationController controller;

    @Autowired
    public JadeResource(Current current, TabService tabService, DirectoryService directoryService, FileService fileService, ThreadService threadService, ApplicationController controller) {
        this.current = current;
        this.tabService = tabService;
        this.directoryService = directoryService;
        this.fileService = fileService;
        this.threadService = threadService;
        this.controller = controller;
    }

    public void executeJadeResource(AllController.Payload payload) {

        String finalURI = payload.getFinalURI();
        if (payload.getRequestURI().endsWith(".jade")) {
            final String template = controller.getTemplate(payload.getFinalURI());
            payload.write(template);
        } else {
            Path path = directoryService.findPathInConfigOrCurrentOrWorkDir(finalURI);
            fileService.processFile(payload, path);
        }
    }
}

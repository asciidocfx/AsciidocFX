package com.kodcu.controller;

import com.kodcu.other.Current;
import com.kodcu.service.DirectoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.nio.file.Path;

/**
 * Created by usta on 05.09.2015.
 */
@Component
public class EpubResource {

    private final Current current;
    private final FileService fileService;
    private final DirectoryService directoryService;

    @Autowired
    public EpubResource(Current current, FileService fileService, DirectoryService directoryService) {
        this.current = current;
        this.fileService = fileService;
        this.directoryService = directoryService;
    }

    public boolean executeEpubResource(AllController.Payload payload) {

        if (payload.getRequestURI().contains("booki.epub")) {
            fileService.processFile(payload, current.getCurrentEpubPath());
        } else {
            Path path = directoryService.findPathInConfigOrCurrentOrWorkDir(payload.getFinalURI());
            fileService.processFile(payload, path);
        }

        return true;
    }
}

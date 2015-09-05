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
public class LiveResource {

    private final Current current;
    private final DirectoryService directoryService;
    private final FileService fileService;

    @Autowired
    public LiveResource(Current current, DirectoryService directoryService, FileService fileService) {
        this.current = current;
        this.directoryService = directoryService;
        this.fileService = fileService;
    }


    public boolean executeLiveResource(AllController.Payload payload) {

        if (payload.getRequestURI().endsWith("live.html")) {
            payload.getDeferredResult().setResult(current.currentEditorValue());
        } else {
            Path path = directoryService.findPathInRoot(payload.getFinalURI());
            fileService.processFile(payload, path);
        }

        return true;
    }
}

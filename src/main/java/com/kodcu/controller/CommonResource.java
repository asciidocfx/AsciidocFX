package com.kodcu.controller;

import com.kodcu.service.DirectoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.nio.file.Path;
import java.util.Optional;

/**
 * Created by usta on 04.09.2016.
 */
@Controller
public class CommonResource {

    private final FileService fileService;
    private final DirectoryService directoryService;

    @Autowired
    public CommonResource(FileService fileService, DirectoryService directoryService) {
        this.fileService = fileService;
        this.directoryService = directoryService;
    }

    public void processPayload(Payload payload, String finalUri) {
        Optional<Path> resolvedUri = payload.resolveUri(finalUri);

        if (resolvedUri.isPresent()) {
            Path path = directoryService.findPathInWorkdirOrLookup(resolvedUri.get());
            fileService.processFile(payload, path);
        } else {
            Path path = directoryService.findPathInCurrentOrWorkDir(finalUri);
            fileService.processFile(payload, path);
        }
    }

    public void processPayload(Payload payload) {
        processPayload(payload, payload.getFinalURI());
    }
}

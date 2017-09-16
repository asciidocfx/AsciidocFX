package com.kodedu.controller;

import com.kodedu.service.DirectoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.nio.file.Path;
import java.util.Objects;
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


    public void processPayload(Payload payload) {

        String p = payload.param("p");
        String finalUri = payload.getFinalURI();

        if (Objects.nonNull(p)) {
            Path inPublic = directoryService.findPathInPublic(p);
            if (Objects.nonNull(inPublic)) {
                fileService.processFile(payload, inPublic);
                return;
            }
        }

        Optional<Path> resolvedUri = payload.resolveUri(finalUri);

        if (resolvedUri.isPresent()) {
            Path path = directoryService.findPathInWorkdirOrLookup(resolvedUri.get());
            if (Objects.nonNull(path)) {
                fileService.processFile(payload, path);
                return;
            }
        }

        Path path = directoryService.findPathInCurrentOrWorkDir(finalUri);
        if (Objects.nonNull(path)) {
            fileService.processFile(payload, path);
            return;
        }

        Path inPublic = directoryService.findPathInPublic(finalUri);
        fileService.processFile(payload, inPublic);
    }
}

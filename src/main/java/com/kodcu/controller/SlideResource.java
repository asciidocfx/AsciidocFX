package com.kodcu.controller;

import com.kodcu.service.DirectoryService;
import com.kodcu.service.convert.slide.SlideConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.nio.file.Path;

/**
 * Created by usta on 05.09.2015.
 */
@Component
public class SlideResource {

    private final SlideConverter slideConverter;
    private final DirectoryService directoryService;
    private final FileService fileService;

    @Autowired
    public SlideResource(SlideConverter slideConverter, DirectoryService directoryService, FileService fileService) {
        this.slideConverter = slideConverter;
        this.directoryService = directoryService;
        this.fileService = fileService;
    }


    public void executeSlideResource(AllController.Payload payload) {

        if (payload.getRequestURI().endsWith("slide.html")) {
            payload.write(slideConverter.getRendered());
        } else {
            Path path = directoryService.findPathInCurrentOrWorkDir(payload.getFinalURI());
            fileService.processFile(payload, path);
        }
    }
}

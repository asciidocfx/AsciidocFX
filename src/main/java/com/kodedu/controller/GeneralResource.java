package com.kodedu.controller;

import com.kodedu.other.Current;
import com.kodedu.service.DirectoryService;
import com.kodedu.service.ThreadService;
import com.kodedu.service.ui.TabService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.nio.file.Path;
import java.util.Objects;

import static org.springframework.web.bind.annotation.RequestMethod.*;

/**
 * Created by usta on 02.09.2015.
 */
@Controller
public class GeneralResource {

    private final FileService fileService;
    private final DirectoryService directoryService;
    private final Current current;
    private final ThreadService threadService;
    private final TabService tabService;

    private final CommonResource commonResource;

    private Logger logger = LoggerFactory.getLogger(GeneralResource.class);

    @Autowired
    public GeneralResource(FileService fileService, DirectoryService directoryService, Current current, ThreadService threadService, TabService tabService, CommonResource commonResource) {
        this.fileService = fileService;
        this.directoryService = directoryService;
        this.current = current;
        this.threadService = threadService;
        this.tabService = tabService;
        this.commonResource = commonResource;
    }

    @RequestMapping(value = {"/afx/resource", "/afx/resource/**", "/afx/resource/*.*"}, method = {GET, HEAD, OPTIONS, POST}, produces = "*/*", consumes = "*/*")
    @ResponseBody
    public void onrequest(HttpServletRequest request, HttpServletResponse response,
                          @RequestParam(value = "p", required = false) String p) {

        Payload payload = new Payload(request, response);
        payload.setPattern("/afx/resource/");

        String finalURI = payload.getFinalURI();

        if (Objects.nonNull(p)) {
            Path path = directoryService.findPathInPublic(p);
            fileService.processFile(request, response, path);
        } else if (finalURI.matches(".*\\.(asc|asciidoc|ad|adoc|md|markdown)$")) {

            current.currentPath().ifPresent(path -> {

                Path ascFile = path.getRoot().resolve(finalURI);

                threadService.runActionLater(() -> {
                    tabService.addTab(ascFile);
                });

            });
            payload.setStatus(HttpStatus.NO_CONTENT);
        } else {
            commonResource.processPayload(payload);
        }


    }

}

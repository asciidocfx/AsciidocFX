package com.kodcu.controller;

import com.kodcu.other.Current;
import com.kodcu.service.DirectoryService;
import com.kodcu.service.ThreadService;
import com.kodcu.service.ui.TabService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

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
    private final RestTemplate restTemplate;

    private Logger logger = LoggerFactory.getLogger(WebWorkerResource.class);

    @Autowired
    public WebWorkerResource(Current current, TabService tabService, DirectoryService directoryService, FileService fileService, ThreadService threadService, ApplicationController controller, DataUriController dataUriService, RestTemplate restTemplate) {
        this.current = current;
        this.tabService = tabService;
        this.directoryService = directoryService;
        this.fileService = fileService;
        this.threadService = threadService;
        this.controller = controller;
        this.dataUriService = dataUriService;
        this.restTemplate = restTemplate;
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

            if (finalURI.startsWith("//")) {
                finalURI = finalURI.replace("//", "http://");
            }

            if (finalURI.startsWith("http://") || finalURI.startsWith("https://")) {

                String data = "";

                try {
                    data = restTemplate.getForObject(finalURI, String.class);
                } catch (Exception ex) {
                    logger.warn("resource not found or not readable: {}", finalURI);
                }

                payload.write(data);

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

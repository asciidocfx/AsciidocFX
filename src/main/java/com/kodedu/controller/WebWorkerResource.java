package com.kodedu.controller;

import com.kodedu.helper.IOHelper;
import com.kodedu.other.Current;
import com.kodedu.service.DirectoryService;
import com.kodedu.service.ThreadService;
import com.kodedu.service.ui.TabService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Optional;

import static org.springframework.web.bind.annotation.RequestMethod.*;

/**
 * Created by usta on 05.09.2015.
 */
@Controller
public class WebWorkerResource {

    private final Current current;
    private final TabService tabService;
    private final DirectoryService directoryService;
    private final FileService fileService;
    private final ThreadService threadService;
    private final ApplicationController controller;
    private final DataUriController dataUriService;
    private final RestTemplate restTemplate;
    private final CommonResource commonResource;

    private Logger logger = LoggerFactory.getLogger(WebWorkerResource.class);

    @Autowired
    public WebWorkerResource(Current current, TabService tabService, DirectoryService directoryService, FileService fileService, ThreadService threadService, ApplicationController controller, DataUriController dataUriService, RestTemplate restTemplate, CommonResource commonResource) {
        this.current = current;
        this.tabService = tabService;
        this.directoryService = directoryService;
        this.fileService = fileService;
        this.threadService = threadService;
        this.controller = controller;
        this.dataUriService = dataUriService;
        this.restTemplate = restTemplate;
        this.commonResource = commonResource;
    }

    @RequestMapping(value = {"/afx/worker/", "/afx/worker/**", "/afx/worker/*.*"}, method = {GET, HEAD, OPTIONS, POST}, produces = "*/*", consumes = "*/*")
    @ResponseBody
    public void onrequest(HttpServletRequest request, HttpServletResponse response,
                          @RequestParam(value = "p", required = false) String p) {

        Payload payload = new Payload(request, response);
        payload.setPattern("/afx/worker/");

        String finalURI = payload.getFinalURI();

        Optional<String> optional = Optional.ofNullable(payload.getRequestURI())
                .filter(e -> e.endsWith("resource.afx"));

        if (optional.isPresent()) {
            finalURI = payload.param("path");
        }

        if (finalURI.matches(".*\\.(asc|asciidoc|ad|adoc|md|markdown)$")) {

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

        }

        if (optional.isPresent()) {
            Path found = directoryService.findPathInWorkdirOrLookup(IOHelper.getPath(finalURI));

            if (Objects.nonNull(found)) {
                fileService.processFile(request, response, found);
            } else {
                Path path = directoryService.findPathInPublic(finalURI);
                fileService.processFile(request, response, path);
            }

            return;
        }

        commonResource.processPayload(payload);

    }
}

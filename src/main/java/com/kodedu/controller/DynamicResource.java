package com.kodedu.controller;

import com.kodedu.config.LocationConfigBean;
import com.kodedu.other.Current;
import com.kodedu.service.DirectoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.Optional;

import static org.springframework.web.bind.annotation.RequestMethod.*;

/**
 * Created by usta on 05.09.2015.
 */
@Controller
public class DynamicResource {

    private final Current current;
    private final DirectoryService directoryService;
    private final FileService fileService;
    private final CommonResource commonResource;
    private final LocationConfigBean locationConfigBean;
    private final ApplicationController controller;

    @Autowired
    public DynamicResource(Current current, DirectoryService directoryService, FileService fileService, CommonResource commonResource, LocationConfigBean locationConfigBean, ApplicationController controller) {
        this.current = current;
        this.directoryService = directoryService;
        this.fileService = fileService;
        this.commonResource = commonResource;
        this.locationConfigBean = locationConfigBean;
        this.controller = controller;
    }

    @RequestMapping(value = {"/afx/dynamic", "/afx/dynamic/**", "/afx/dynamic/*.*"}, method = {GET, HEAD, OPTIONS, POST}, produces = "*/*", consumes = "*/*")
    @ResponseBody
    public void onrequest(HttpServletRequest request, HttpServletResponse response,
                          @RequestParam(value = "p", required = false) String p) {

        Payload payload = new Payload(request, response);
        payload.setPattern("/afx/dynamic/");

        if (Objects.nonNull(p)) {

            if (p.contains("asciidoctor-default.css")) {
                Optional<String> stylesheetDefault = Optional.ofNullable(locationConfigBean.getStylesheetDefault());
                processResource(payload, stylesheetDefault);
                return;
            } else if (p.contains("asciidoctor-default-overrides.css")) {
                Optional<String> stylesheetDefault = Optional.ofNullable(locationConfigBean.getStylesheetOverrides());
                processResource(payload, stylesheetDefault);
                return;
            }
        }

        if (payload.getFinalURI().contains("MathJax.js")) {
            Optional<String> mathjax = Optional.ofNullable(locationConfigBean.getMathjax());
            processResource(payload, mathjax);
        } else {
            Path path = directoryService.findPathInPublic(payload.getFinalURI());

            if (Files.exists(path)) {
                fileService.processFile(payload, path);
            } else {
                commonResource.processPayload(payload);
            }

        }
    }

    private void processResource(Payload payload, Optional<String> resourceOptional) {

        Optional<String> httpOptional = resourceOptional
                .map(String::trim)
                .filter(e -> e.startsWith("http"));

        Optional<Path> pathOptional = resourceOptional
                .filter(e -> !e.isEmpty())
                .filter(e -> !e.startsWith("http"))
                .map(String::trim)
                .map(Paths::get)
                .filter(Files::exists)
                .filter(e -> !Files.isDirectory(e));

        if (httpOptional.isPresent()) {
            payload.sendRedirect(httpOptional.get());
        } else if (pathOptional.isPresent()) {
            Path path = pathOptional.get();
            fileService.processFile(payload, path);
        } else {
            fileService.processFile(payload, controller.getConfigPath().resolve("public").resolve(payload.getFinalURI()));
        }
    }
}

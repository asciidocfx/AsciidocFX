package com.kodcu.controller;

import com.kodcu.config.LocationConfigBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.async.DeferredResult;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

/**
 * Created by usta on 05.09.2015.
 */
@Component
public class DynamicResource {

    private final LocationConfigBean locationConfigBean;
    private final FileService fileService;
    private final GeneralResource generalResource;
    private final ApplicationController controller;

    @Autowired
    public DynamicResource(LocationConfigBean locationConfigBean, FileService fileService, GeneralResource generalResource, ApplicationController controller) {
        this.locationConfigBean = locationConfigBean;
        this.fileService = fileService;
        this.generalResource = generalResource;
        this.controller = controller;
    }

    public boolean executeDynamicResource(AllController.Payload payload) {

        if (payload.getRequestURI().contains("asciidoctor-default.css")) {
            Optional<String> stylesheetDefault = Optional.ofNullable(locationConfigBean.getStylesheetDefault());
            processResource(payload, stylesheetDefault);
        } else if (payload.getRequestURI().contains("asciidoctor-default-overrides.css")) {
            Optional<String> stylesheetDefault = Optional.ofNullable(locationConfigBean.getStylesheetOverrides());
            processResource(payload, stylesheetDefault);
        } else if (payload.getRequestURI().contains("MathJax.js")) {
            Optional<String> mathjax = Optional.ofNullable(locationConfigBean.getMathjax());
            processResource(payload, mathjax);
        } else {
            generalResource.executeAfxResource(payload);
        }

        return true;
    }

    private void processResource(AllController.Payload payload, Optional<String> resourceOptional) {

        Optional<String> httpOptional = resourceOptional
                .map(String::trim)
                .filter(e -> e.startsWith("http"));

        Optional<Path> pathOptional = resourceOptional
                .filter(e -> !e.isEmpty())
                .filter(e -> !e.contains(":"))
                .map(String::trim)
                .map(Paths::get)
                .filter(Files::exists)
                .filter(e -> !Files.isDirectory(e));

        if (httpOptional.isPresent()) {
            payload.getDeferredResult().setResult(ResponseEntity
                    .status(HttpStatus.FOUND)
                    .header("Location", httpOptional.get())
                    .body("Redirecting.."));
        } else if (pathOptional.isPresent()) {
            Path path = pathOptional.get();
            fileService.processFile(payload, path);
        } else {
            fileService.processFile(payload, controller.getConfigPath().resolve("public").resolve(payload.getFinalURI()));
        }
    }
}

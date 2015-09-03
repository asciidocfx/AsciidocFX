package com.kodcu.controller;

import com.kodcu.other.Current;
import com.kodcu.service.DirectoryService;
import com.kodcu.service.ThreadService;
import com.kodcu.service.convert.slide.SlideConverter;
import com.kodcu.service.ui.TabService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.async.DeferredResult;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.HEAD;

/**
 * Created by usta on 02.09.2015.
 */
@Controller
@Scope("request")
public class AllController {

    private final FileService fileService;
    private final HttpServletRequest request;
    private final HttpServletResponse response;
    private final SlideConverter slideConverter;
    private final Current current;
    private final ThreadService threadService;
    private final TabService tabService;
    private final DirectoryService directoryService;

    private DeferredResult<String> result;


    @Autowired
    public AllController(FileService fileService, HttpServletRequest request, HttpServletResponse response, SlideConverter slideConverter, Current current, ThreadService threadService, TabService tabService, DirectoryService directoryService) {
        this.fileService = fileService;
        this.request = request;
        this.response = response;
        this.slideConverter = slideConverter;
        this.current = current;
        this.threadService = threadService;
        this.tabService = tabService;
        this.directoryService = directoryService;
    }

    @RequestMapping(value = {"/**/*.*", "*.*"}, method = {GET, HEAD}, produces = "*/*")
    @ResponseBody
    public DeferredResult all(DeferredResult result) {

        this.result = result;

        List<Supplier<Boolean>> chaines = Arrays
                .asList(this::executeAfxResource,
                        this::executeLiveResource,
                        this::executeSlideResource,
                        this::executeEpubResource);

        Optional<Boolean> executionResult = chaines
                .stream()
                .map(Supplier<Boolean>::get)
                .filter(Boolean::booleanValue)
                .findFirst();

        if (!executionResult.isPresent()) {
            ResponseEntity<String> responseEntity = new ResponseEntity<>(HttpStatus.NO_CONTENT);
            result.setResult(responseEntity);
        }
        return result;
    }

    private boolean executeEpubResource() {
        String requestURI = request.getRequestURI();
        String resourcePrefix = "/afx/epub/";

        if (requestURI.contains(resourcePrefix)) {

            if (requestURI.contains("booki.epub")) {
                fileService.processFile(request, response, current.getCurrentEpubPath());
            } else {
                String finalUri = requestURI.replace(resourcePrefix, "");
                Path path = directoryService.findPathInConfigOrCurrentOrWorkDir(finalUri);
                fileService.processFile(request, response, path);
            }

            return true;
        }

        return false;
    }

    private boolean executeSlideResource() {
        String requestURI = request.getRequestURI();
        String resourcePrefix = "/afx/slide/";

        if (requestURI.contains(resourcePrefix)) {

            if (requestURI.endsWith("slide.html")) {
                result.setResult(slideConverter.getRendered());
            } else {
                requestURI = requestURI.replace(resourcePrefix, "");
                Path path = directoryService.findPathInCurrentOrWorkDir(requestURI);
                fileService.processFile(request, response, path);
            }

            return true;
        }

        return false;
    }

    private boolean executeLiveResource() {
        String requestURI = request.getRequestURI();
        String resourcePrefix = "/afx/live/";

        if (requestURI.contains(resourcePrefix)) {

            if (requestURI.endsWith("live.html")) {
                result.setResult(current.currentEditorValue());
            } else {
                requestURI = requestURI.replace(resourcePrefix, "");

                Path path = directoryService.findPathInRoot(requestURI);
                fileService.processFile(request, response, path);
            }

            return true;
        }

        return false;
    }

    public boolean executeAfxResource() {

        String requestURI = request.getRequestURI();
        String resourcePrefix = "/afx/resource/";

        if (requestURI.contains(resourcePrefix)) {

            String finalUri = requestURI.replace(resourcePrefix, "");

            if (finalUri.matches(".*\\.(asc|asciidoc|ad|adoc|md|markdown)$")) {

                current.currentPath().ifPresent(path -> {

                    Path ascFile = path.getParent().resolve(finalUri);

                    threadService.runActionLater(() -> {
                        tabService.addTab(ascFile);
                    });

                });
            } else if (finalUri.endsWith("epub.html")) {

            } else {
                Path path = directoryService.findPathInConfigOrCurrentOrWorkDir(finalUri);
                fileService.processFile(request, response, path);
            }

            return true;
        }

        return false;

    }
}

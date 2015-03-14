package com.kodcu.controller;

import com.kodcu.other.Current;
import com.kodcu.service.ThreadService;
import com.kodcu.service.ui.TabService;
import javafx.application.Platform;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.nio.file.Path;

/**
 * Created by usta on 25.12.2014.
 */
@Controller
public class AsciidocController {

    private final Current current;
    private final TabService tabService;
    private final ThreadService threadService;
    
    @Autowired
    public AsciidocController(final Current current, final TabService tabService, final ThreadService threadService) {
        this.current = current;
        this.tabService = tabService;
        this.threadService = threadService;
    }

    @RequestMapping(value = {"/**/{extension:(?:\\w|\\W)+\\.(?:asc|asciidoc|ad|adoc|md|markdown)$}"}, method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<byte[]> asciidoc(HttpServletRequest request) {

        threadService.runTaskLater(()->{
            current.currentPath().ifPresent(path -> {
                String uri = request.getRequestURI();

                if (uri.startsWith("/"))
                    uri = uri.substring(1);

                Path ascFile = path.getParent().resolve(uri);

                threadService.runActionLater(() -> {
                    tabService.addTab(ascFile);
                });
            });
        });

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}

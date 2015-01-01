package com.kodcu.controller;

import com.kodcu.other.Current;
import com.kodcu.service.ui.TabService;
import javafx.application.Platform;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.async.DeferredResult;

import javax.servlet.http.HttpServletRequest;
import java.nio.file.Path;

/**
 * Created by usta on 25.12.2014.
 */
@Controller
public class AsciidocController {

    @Autowired
    private Current current;

    @Autowired
    private TabService tabService;

    @RequestMapping(value = {"**.asciidoc", "**.asc", "**.txt", "**.ad", "**.adoc"}, method = RequestMethod.GET)
    @ResponseBody
    public DeferredResult<String> asciidoc(HttpServletRequest request) {

        DeferredResult<String> deferredResult = new DeferredResult<String>();

        current.currentPath().ifPresent(path -> {
            String uri = request.getRequestURI();

            if (uri.startsWith("/"))
                uri = uri.substring(1);

            Path ascFile = path.getParent().resolve(uri);

            Platform.runLater(() -> {
                tabService.addTab(ascFile);
            });

            deferredResult.setResult("OK");
        });


        return deferredResult;
    }
}

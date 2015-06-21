package com.kodcu.controller;

import com.kodcu.other.Current;
import com.kodcu.other.IOHelper;
import com.kodcu.service.convert.slide.SlideConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Created by usta on 15.05.2015.
 */
@Controller
public class LiveReloadController {

    @Autowired
    private SlideConverter slideConverter;
    @Autowired
    private Current current;

    @RequestMapping(value = "/livereload/**", produces = "*/*", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity slide(HttpServletRequest request) {

        String requestURI = request.getRequestURI();
        if (requestURI.contains("index.reload"))
            return ResponseEntity.ok(current.currentEditorValue());


        Path parent = current.currentPath().get().getParent();

        requestURI = requestURI.replace("/livereload/", "");

        if (requestURI.startsWith("/"))
            requestURI = requestURI.substring(1);

        Path resolve = parent.resolve(requestURI);

        if (Files.exists(resolve))
            return ResponseEntity.ok(IOHelper.readAllBytes(resolve));

        return ResponseEntity.notFound().build();

    }
}

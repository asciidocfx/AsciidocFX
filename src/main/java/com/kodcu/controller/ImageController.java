package com.kodcu.controller;

import com.kodcu.other.Current;
import com.kodcu.other.IOHelper;
import com.kodcu.service.DirectoryService;
import com.kodcu.service.PathFinderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;

/**
 * Created by usta on 25.12.2014.
 */
//@Controller
public class ImageController {

    private final Current current;
    private final DirectoryService directoryService;
    private final ApplicationContext applicationContext;

    @Autowired
    public ImageController(final Current current, final DirectoryService directoryService, ApplicationContext applicationContext) {
        this.current = current;
        this.directoryService = directoryService;
        this.applicationContext = applicationContext;
    }

    @RequestMapping(value = {"/**/{extension:(?:\\w|\\W)+\\.(?:jpg|bmp|gif|jpeg|png|webp|svg)$}"}, method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<byte[]> images(HttpServletRequest request, HttpServletResponse response,
                                         @PathVariable("extension") String extension, @RequestParam(value = "parent", required = false, defaultValue = "0") Integer parent) {

        response.setDateHeader("Expires", System.currentTimeMillis() + Duration.ofSeconds(10).toMillis());

        Path imageFile;
        String uri = request.getRequestURI();

        if (uri.startsWith("/"))
            uri = uri.substring(1);

        byte[] temp = new byte[0];

        imageFile = applicationContext.getBean("pathFinder", PathFinderService.class).findPath(uri, parent);

        if (Files.exists(imageFile))
            temp = IOHelper.readAllBytes(imageFile);

        return new ResponseEntity<>(temp, HttpStatus.OK);
    }
}

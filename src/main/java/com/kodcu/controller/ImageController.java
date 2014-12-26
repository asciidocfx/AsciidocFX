package com.kodcu.controller;

import com.kodcu.other.Current;
import com.kodcu.service.DirectoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Enumeration;

/**
 * Created by usta on 25.12.2014.
 */
@Controller
public class ImageController {

    private Logger logger = LoggerFactory.getLogger(ImageController.class);

    @Autowired
    private Current current;

    @Autowired
    private DirectoryService directoryService;

    @Autowired
    private ApplicationController controller;

    @RequestMapping(value = {"/**/{extension:(?:\\w|\\W)+\\.(?:jpg|bmp|gif|jpeg|png|webp)$}"}, method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<byte[]> images(HttpServletRequest request, @PathVariable("extension") String extension) {

        Enumeration<String> headerNames = request.getHeaderNames();
        String uri = request.getRequestURI();
        byte[] temp = new byte[]{};
        if (uri.startsWith("/"))
            uri = uri.substring(1);

        Path imageFile = null;

        if (current.currentPath().isPresent()) {
            imageFile = current.currentPath().map(Path::getParent).get().resolve(uri);
        } else {
            imageFile = directoryService.getWorkingDirectory().get().resolve(uri);
        }

        try {
            temp = Files.readAllBytes(imageFile);
        } catch (Exception ex) {
            logger.debug(imageFile + " is not found");
        }


        return new ResponseEntity<>(temp, HttpStatus.OK);
    }
}

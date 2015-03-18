package com.kodcu.controller;

import com.kodcu.other.Current;
import com.kodcu.other.IOHelper;
import com.kodcu.service.DirectoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.Objects;
import java.util.stream.IntStream;

/**
 * Created by usta on 25.12.2014.
 */
@Controller
public class ImageController {

    private final Current current;
    private final DirectoryService directoryService;

    @Autowired
    public ImageController(final Current current, final DirectoryService directoryService) {
        this.current = current;
        this.directoryService = directoryService;
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

        if (current.currentPath().isPresent()) {
            imageFile = current.currentPath().map((Path path) -> {
                final Path[] parentPath = {path};
                IntStream.rangeClosed(0, parent).forEach(i -> {
                    if (Objects.nonNull(parentPath[0].getParent()))
                        parentPath[0] = parentPath[0].getParent();
                });
                return parentPath[0];
            }).get().resolve(uri);
        } else {
            imageFile = directoryService.getWorkingDirectory().map((Path path) -> {
                final Path[] parentPath = {path};
                IntStream.range(0, parent).forEach(i -> {
                    if (Objects.nonNull(parentPath[0].getParent()))
                        parentPath[0] = parentPath[0].getParent();
                });
                return parentPath[0];
            }).get().resolve(uri);
        }

        byte[] temp = new byte[0];

        if (Files.exists(imageFile))
            temp = IOHelper.readAllBytes(imageFile);

        return new ResponseEntity<>(temp, HttpStatus.OK);
    }
}

package com.kodcu.controller;

import com.kodcu.other.Current;
import com.kodcu.other.IOHelper;
import com.kodcu.service.DirectoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by usta on 31.12.2014.
 */
@Controller
public class EpubController {

    @Autowired
    private Current current;

    @Autowired
    private DirectoryService directoryService;

    private Path path;

    @RequestMapping(value = {"/epub/viewer"}, method = RequestMethod.GET)
    public String epubHtml(@RequestParam(value = "path") String path) {
        this.path = Paths.get(path);
        return "redirect:/epub.html";
    }

    @RequestMapping(value = {"**.epub", "**.epub3"}, method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<byte[]> epub3(HttpServletRequest request) {
        byte[] temp = IOHelper.readAllBytes(path);
        return new ResponseEntity<>(temp, HttpStatus.OK);
    }

}

package com.kodedu.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import static org.springframework.web.bind.annotation.RequestMethod.*;

/**
 * Created by usta on 02.09.2015.
 */
@Controller
public class AllController {

    private final CommonResource commonResource;

    private Logger logger = LoggerFactory.getLogger(AllController.class);

    @Autowired
    public AllController(CommonResource commonResource) {
        this.commonResource = commonResource;
    }

    @RequestMapping(value = {"/**/*.*", "*.*"}, method = {GET, HEAD, OPTIONS, POST}, produces = "*/*", consumes = "*/*")
    @ResponseBody
    public void all(HttpServletRequest request, HttpServletResponse response) {

        Payload payload = new Payload(request, response);

        commonResource.processPayload(payload);

    }

}

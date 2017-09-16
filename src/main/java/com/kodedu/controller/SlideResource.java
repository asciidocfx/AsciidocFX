package com.kodedu.controller;

import com.kodedu.other.Current;
import com.kodedu.service.DirectoryService;
import com.kodedu.service.convert.slide.SlideConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Objects;

import static org.springframework.web.bind.annotation.RequestMethod.*;

/**
 * Created by usta on 05.09.2015.
 */
@Controller
public class SlideResource {

    private final Current current;
    private final DirectoryService directoryService;
    private final FileService fileService;
    private final SlideConverter slideConverter;
    private final CommonResource commonResource;

    @Autowired
    public SlideResource(Current current, DirectoryService directoryService, FileService fileService, SlideConverter slideConverter, CommonResource commonResource) {
        this.current = current;
        this.directoryService = directoryService;
        this.fileService = fileService;
        this.slideConverter = slideConverter;
        this.commonResource = commonResource;
    }

    @RequestMapping(value = {"/afx/slide", "/afx/slide/**", "/afx/slide/*.*"}, method = {GET, HEAD, OPTIONS, POST}, produces = "*/*", consumes = "*/*")
    @ResponseBody
    public void onrequest(HttpServletRequest request, HttpServletResponse response,
                          @RequestParam(value = "p", required = false) String p, @RequestParam(value = "receiver", required = false) String receiver) {

        Payload payload = new Payload(request, response);
        payload.setPattern("/afx/slide/");

        if (Objects.nonNull(p)) {

            if (p.contains("slide.html")) {
                sendSlide(payload);
                return;
            }
        } else if (Objects.nonNull(receiver)) {
            sendSlide(payload);
            return;
        }

        commonResource.processPayload(payload);
    }

    private void sendSlide(Payload payload) {
        payload.getResponse().setContentType("text/html;charset=UTF-8");
        payload.write(slideConverter.getRendered());
    }
}

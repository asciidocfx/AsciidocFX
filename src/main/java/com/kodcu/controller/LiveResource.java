package com.kodcu.controller;

import com.kodcu.other.Current;
import com.kodcu.service.DirectoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.nio.file.Path;
import java.util.Objects;

import static org.springframework.web.bind.annotation.RequestMethod.*;

/**
 * Created by usta on 05.09.2015.
 */
@Controller
public class LiveResource {

    private final Current current;
    private final DirectoryService directoryService;
    private final FileService fileService;
    private final CommonResource commonResource;

    @Autowired
    public LiveResource(Current current, DirectoryService directoryService, FileService fileService, CommonResource commonResource) {
        this.current = current;
        this.directoryService = directoryService;
        this.fileService = fileService;
        this.commonResource = commonResource;
    }

    @RequestMapping(value = {"/afx/live", "/afx/live/**", "/afx/live/*.*"}, method = {GET, HEAD, OPTIONS, POST}, produces = "*/*", consumes = "*/*")
    @ResponseBody
    public void onrequest(HttpServletRequest request, HttpServletResponse response,
                          @RequestParam(value = "p", required = false) String p) {

        Payload payload = new Payload(request, response);
        payload.setPattern("/afx/live/");

        if (Objects.nonNull(p)) {

            if (p.contains("live.html")) {
                payload.getResponse().setContentType("text/html;charset=UTF-8");
                payload.write(current.currentEditorValue());
                return;
            }
        }

        commonResource.processPayload(payload);
    }
}

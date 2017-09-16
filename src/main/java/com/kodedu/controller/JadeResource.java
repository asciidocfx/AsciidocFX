package com.kodedu.controller;

import com.kodedu.other.Current;
import com.kodedu.service.DirectoryService;
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
public class JadeResource {

    private final Current current;
    private final DirectoryService directoryService;
    private final FileService fileService;
    private final CommonResource commonResource;
    private final ApplicationController controller;

    @Autowired
    public JadeResource(Current current, DirectoryService directoryService, FileService fileService, CommonResource commonResource, ApplicationController controller) {
        this.current = current;
        this.directoryService = directoryService;
        this.fileService = fileService;
        this.commonResource = commonResource;
        this.controller = controller;
    }

    @RequestMapping(value = {"/afx/jade", "/afx/jade/**", "/afx/jade/*.*"}, method = {GET, HEAD, OPTIONS, POST}, produces = "*/*", consumes = "*/*")
    @ResponseBody
    public void onrequest(HttpServletRequest request, HttpServletResponse response,
                          @RequestParam(value = "p", required = false) String p) {

        Payload payload = new Payload(request, response);
        payload.setPattern("/afx/jade/");

        if (Objects.nonNull(p)) {

            if (p.endsWith(".jade")) {
                final String template = controller.getTemplate(p);
                payload.write(template);
                return;
            }
        }

        commonResource.processPayload(payload);
    }
}

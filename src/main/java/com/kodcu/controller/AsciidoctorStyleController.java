package com.kodcu.controller;

import com.kodcu.config.EditorConfigBean;
import com.kodcu.other.IOHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

/**
 * Created by usta on 09.08.2015.
 */
@Controller
public class AsciidoctorStyleController {


    private final ApplicationController controller;
    private final EditorConfigBean editorConfigBean;

    private Logger logger = LoggerFactory.getLogger(AsciidoctorStyleController.class);

    @Autowired
    public AsciidoctorStyleController(ApplicationController controller, EditorConfigBean editorConfigBean) {
        this.controller = controller;
        this.editorConfigBean = editorConfigBean;
    }

    @RequestMapping(value = "/afxdata/asciidoctor-default.css", method = RequestMethod.GET)
    @ResponseBody
    public String readStyleSheet() {

        Path customStyleSheet = editorConfigBean.asciidoctorStyleSheetProperty().get();

        if (Objects.nonNull(customStyleSheet) && Files.exists(customStyleSheet)) {
           return IOHelper.readFile(customStyleSheet);
        } else {
            logger.error("Asciidoctor stylesheet is not correctly defined." +
                    " Default stylesheet will be used." +
                    " You can change stylesheet in Settings (Press F4).");
        }

        return IOHelper.readFile(AsciidoctorStyleController.class.getResourceAsStream("/public/css/asciidoctor-default.css"));
    }
}

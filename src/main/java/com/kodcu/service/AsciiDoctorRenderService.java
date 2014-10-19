package com.kodcu.service;

import com.kodcu.controller.AsciiDocController;
import com.kodcu.other.IOHelper;
import javafx.application.Platform;
import javafx.scene.web.WebEngine;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by usta on 19.07.2014.
 */
@Component
public class AsciiDoctorRenderService {

    @Autowired
    AsciiDocController controller;

    private static Logger logger = LoggerFactory.getLogger(AsciiDoctorRenderService.class);

    public String asciidocToHtml(WebEngine webEngine, String text) {

       return (String) webEngine.executeScript(String.format("renderToHtml('%s')", IOHelper.normalize(text)));
    }

    public String generateHtml(WebEngine webEngine, String text) {
        String rendered = (String) webEngine.executeScript(String.format("Opal.Asciidoctor.$render('%s',Opal.hash2(['attributes','header_footer'], {'attributes': ['backend=html', 'doctype=book'],'header_footer':true}));", IOHelper.normalize(text)));
        return rendered;
    }

    public String asciidocToDocbook(WebEngine webEngine, String text, boolean includeHeader) {
        if (includeHeader)
            webEngine.executeScript("var headfoot=true;");
        else
            webEngine.executeScript("var headfoot=false;");

        webEngine.executeScript("var docbookOpts = Opal.hash2(['attributes','header_footer'], {'attributes': ['backend=docbook5', 'doctype=book'],'header_footer':headfoot});");

        String rendered = (String) webEngine.executeScript(String.format("Opal.Asciidoctor.$render('%s',docbookOpts);", IOHelper.normalize(text)));

        return rendered;

    }
}

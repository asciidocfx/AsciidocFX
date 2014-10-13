package com.kodcu.service;

import com.kodcu.other.IOHelper;
import javafx.scene.web.WebEngine;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by usta on 19.07.2014.
 */
@Component
public class AsciiDoctorRenderService {

    private String htmlRenderer;
    private static Logger logger = LoggerFactory.getLogger(AsciiDoctorRenderService.class);

    @PostConstruct
    public void init() throws IOException {
        try (InputStream stream = getClass().getResourceAsStream("/htmlRenderer.js");) {
            htmlRenderer = IOUtils.toString(stream);
        }
    }

    public void asciidocToHtml(WebEngine webEngine, String text) {
        webEngine.executeScript(String.format(htmlRenderer, IOHelper.normalize(text)));
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

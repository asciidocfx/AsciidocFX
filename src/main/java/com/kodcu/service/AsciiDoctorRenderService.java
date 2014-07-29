package com.kodcu.service;

import com.kodcu.other.IOHelper;
import javafx.scene.web.WebEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Created by usta on 19.07.2014.
 */
@Component
public class AsciiDoctorRenderService {

    private static Logger logger = LoggerFactory.getLogger(DocoJarExtractorService.class);

    public String asciidocToHtml(WebEngine webEngine, String text) {
        return (String) webEngine.executeScript(String.format("Opal.Asciidoctor.$render('%s');", IOHelper.normalize(text)));
    }

    public String asciidocToDocbook(WebEngine webEngine, String text, boolean includeHeader) {
        if (includeHeader)
            webEngine.executeScript("var headfoot=true;");
        else
            webEngine.executeScript("var headfoot=false;");

        webEngine.executeScript("var docbookOpts = Opal.hash2(['attributes','header_footer'], {'attributes': ['backend=docbook5', 'doctype=book'],'header_footer':headfoot});");

        String rendered = (String) webEngine.executeScript(String.format("Opal.Asciidoctor.$render('%s',docbookOpts);",IOHelper.normalize(text)));

        return rendered;

    }
}

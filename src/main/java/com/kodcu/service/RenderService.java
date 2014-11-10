package com.kodcu.service;

import com.kodcu.controller.AsciiDocController;
import com.kodcu.other.Current;
import com.kodcu.other.IOHelper;
import javafx.scene.web.WebEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by usta on 19.07.2014.
 */
@Component
public class RenderService {

    @Autowired
    AsciiDocController controller;

    @Autowired
    Current current;

    private static Logger logger = LoggerFactory.getLogger(RenderService.class);

    public String convertBasicHtml(WebEngine webEngine, String text) {

        String rendered = (String) webEngine.executeScript(String.format("convertBasicHtml('%s')", IOHelper.normalize(text)));
        return rendered;
    }

    public String convertHtmlArticle(WebEngine webEngine, String text) {

        String rendered = (String) webEngine.executeScript(String.format("convertHtmlArticle('%s')", IOHelper.normalize(text)));
        return rendered;
    }

    public String convertHtmlBook(WebEngine webEngine, String text) {
        String rendered = (String) webEngine.executeScript(String.format("convertHtmlBook('%s')", IOHelper.normalize(text)));
        return rendered;
    }

    public String convertDocbook(WebEngine webEngine, String text, boolean includeHeader) {

        String rendered = (String) webEngine.executeScript(String.format("convertDocbook('%s',%b)", IOHelper.normalize(text),includeHeader));

        return rendered;

    }

    public String convertDocbookArticle(WebEngine webEngine) {

        String asciidoc = current.currentEditorValue();

        String rendered = (String) webEngine.executeScript(String.format("convertDocbookArticle('%s')", IOHelper.normalize(asciidoc)));

        return rendered;

    }
}

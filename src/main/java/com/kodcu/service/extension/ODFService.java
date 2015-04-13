package com.kodcu.service.extension;

import com.kodcu.component.HtmlPane;
import com.kodcu.controller.ApplicationController;
import com.kodcu.other.Current;
import netscape.javascript.JSObject;
import org.odftoolkit.simple.TextDocument;
import org.odftoolkit.simple.draw.Image;
import org.odftoolkit.simple.style.StyleTypeDefinitions;
import org.odftoolkit.simple.text.Paragraph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.function.Predicate;

/**
 * Created by Hakan on 4/13/2015.
 */
@Controller
public class ODFService {

    private final Logger logger = LoggerFactory.getLogger(TreeService.class);

    private final ApplicationController controller;
    private final Current current;
    private final HtmlPane htmlPane;
    private TextDocument document;

    @Autowired
    public ODFService(final ApplicationController controller, final Current current, final HtmlPane htmlPane) {
        this.controller = controller;
        this.current = current;
        this.htmlPane = htmlPane;
    }

    public void generateODFDocument(){
        try {
            document = TextDocument.newTextDocument();
            htmlPane.call("convertOdf", current.currentEditorValue());
            this.saveDocument();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    private void saveDocument() {
        Path currentTabPath = current.currentPath().get();
        Path currentTabPathDir = currentTabPath.getParent();
        Path file = currentTabPathDir.resolve(String.format("%s.odt",currentTabPath.getFileName()));

        try {
            document.save(file.toString());
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        finally {
            document.close();
        }
    }

    public void buildDocument(String name, JSObject jObj){

        System.out.println(name);

        if (exist.test(name))
            buildStructure(name, jObj);

    }

    Predicate<String> exist = (name) -> Arrays.asList("paragraph","image","section").stream().anyMatch(s -> s.equals(name));

    private void buildStructure(String name, JSObject jObj){
        document.addParagraph("");

        if(name.equals("paragraph")){
            document.addParagraph(jObj.getMember("content").toString());
        }
        else if(name.equals("image")){

            String imageUrl = (String) ((JSObject) jObj.getMember("attr")).getMember("target");
            Path currentTabPath = current.currentPath().get();
            Path currentTabPathDir = currentTabPath.getParent();
            currentTabPathDir = currentTabPathDir.resolve(imageUrl);

            Paragraph para = document.addParagraph("");
            Image image = Image.newImage(para,currentTabPathDir.toUri());
            image.setHorizontalPosition(StyleTypeDefinitions.FrameHorizontalPosition.CENTER);
            document.addParagraph("");
        }
        else if(name.equals("section")){
            document.addParagraph(jObj.getMember("title").toString());
        }
    }
}

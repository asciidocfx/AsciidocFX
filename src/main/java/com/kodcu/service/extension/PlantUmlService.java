package com.kodcu.service.extension;

import com.kodcu.controller.ApplicationController;
import com.kodcu.other.Current;
import com.kodcu.other.IOHelper;
import com.kodcu.service.ThreadService;
import net.sourceforge.plantuml.FileFormat;
import net.sourceforge.plantuml.FileFormatOption;
import net.sourceforge.plantuml.SourceStringReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

import static java.nio.file.StandardOpenOption.*;

/**
 * Created by usta on 25.12.2014.
 */
@Component
public class PlantUmlService {

    private final Logger logger = LoggerFactory.getLogger(PlantUmlService.class);

    private final Current current;
    private final ApplicationController controller;
    private final ThreadService threadService;

    @Autowired
    public PlantUmlService(final Current current, final ApplicationController controller, final ThreadService threadService) {
        this.current = current;
        this.controller = controller;
        this.threadService = threadService;
    }

    public void plantUml(String uml, String type, String imagesDir, String imageTarget, String nodename) {
        Objects.requireNonNull(imageTarget);

        if (!imageTarget.endsWith(".png") && !imageTarget.endsWith(".svg"))
            return;

        StringBuffer stringBuffer = new StringBuffer(uml);

//        if (nodename.contains("uml")) {
//            if (!uml.contains("skinparam") && !uml.contains("dpi") && !uml.contains("@start")) {
//                stringBuffer.insert(0, "\nskinparam dpi 300\n");
//            }
//        }

        appendHeaderNotExist(stringBuffer, nodename, "uml", "uml");
        appendHeaderNotExist(stringBuffer, nodename, "ditaa", "ditaa");
        appendHeaderNotExist(stringBuffer, nodename, "graphviz", "uml");

        uml = stringBuffer.toString();

        if (nodename.contains("uml")) {
            if (!uml.contains("skinparam") && !uml.contains("dpi")) {
                uml = uml.replaceFirst("@startuml", "@startuml\nskinparam dpi 300\n");
            }
        }

        Integer cacheHit = current.getCache().get(imageTarget);

        int hashCode = (imageTarget + imagesDir + type + uml + nodename).hashCode();

        if (Objects.nonNull(cacheHit))
            if (hashCode == cacheHit)
                return;

        logger.debug("UML extension is started for {}", imageTarget);

        SourceStringReader reader = new SourceStringReader(uml);

        try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {

            Path path = current.currentTab().getParentOrWorkdir();
            Path umlPath = path.resolve(imageTarget);

            FileFormat fileType = imageTarget.endsWith(".svg") ? FileFormat.SVG : FileFormat.PNG;

            threadService.runTaskLater(() -> {
                try {

                    reader.generateImage(os, new FileFormatOption(fileType));

                    Files.createDirectories(path.resolve(imagesDir));

                    IOHelper.writeToFile(umlPath, os.toByteArray(), CREATE, WRITE, TRUNCATE_EXISTING, SYNC);

                    logger.debug("UML extension is ended for {}", imageTarget);

                    threadService.runActionLater(() -> {
                        controller.clearImageCache(umlPath);
                    });
                } catch (Exception e) {
                    logger.error("Problem occured while generating UML diagram", e);
                }
            });


            current.getCache().put(imageTarget, hashCode);

        } catch (IOException e) {
            logger.error("Problem occured while generating UML diagram", e);
        }
    }

    private void appendHeaderNotExist(StringBuffer stringBuffer, String nodename, String ifNode, String header) {

        if (nodename.contains(ifNode)) {
            if (stringBuffer.indexOf("@start") == -1) {
                stringBuffer.insert(0, "@start" + header + "\n");
            }
            if (stringBuffer.indexOf("@end") == -1) {
                stringBuffer.append("\n@end" + header);
            }
        }


    }
}

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

    private Logger logger = LoggerFactory.getLogger(PlantUmlService.class);

    private final Current current;
    private final ApplicationController controller;
    private final ThreadService threadService;

    @Autowired
    public PlantUmlService(final Current current, final ApplicationController controller, final ThreadService threadService) {
        this.current = current;
        this.controller = controller;
        this.threadService = threadService;
    }

    public void plantUml(String uml, String type, String fileName) {
        Objects.requireNonNull(fileName);

        if (!fileName.endsWith(".png") && !fileName.endsWith(".svg"))
            return;

        String defaultScale = "\nskinparam dpi 300\n";

        if (!uml.contains("@startuml") && !uml.contains("@enduml")) {
            uml = defaultScale + uml;
            uml = "@startuml\n" + uml + "\n@enduml";
        } else {
            uml = uml.replaceFirst("@startuml", "@startuml" + defaultScale);
        }

        Integer cacheHit = current.getCache().get(fileName);

        int hashCode = (fileName + type + uml).hashCode();

        if (Objects.nonNull(cacheHit))
            if (hashCode == cacheHit)
                return;

        SourceStringReader reader = new SourceStringReader(uml);

        try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {

            if (!current.currentPath().isPresent())
                controller.saveDoc();

            Path path = current.currentPath().get().getParent();
            Path umlPath = path.resolve("images/").resolve(fileName);

            FileFormat fileType = fileName.endsWith(".svg") ? FileFormat.SVG : FileFormat.PNG;

            threadService.runTaskLater(() -> {
                try {
                    // FIXME: unused var, why?
                    String desc = reader.generateImage(os, new FileFormatOption(fileType));

                    Files.createDirectories(path.resolve("images"));

                    IOHelper.writeToFile(umlPath, os.toByteArray(), CREATE, WRITE, TRUNCATE_EXISTING);

                    threadService.runActionLater(() -> {
                        controller.clearImageCache();
                    });
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                }
            });


            current.getCache().put(fileName, hashCode);

        } catch (IOException e) {
            logger.info(e.getMessage(), e);
        }
    }
}

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

import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;
import static java.nio.file.StandardOpenOption.WRITE;

/**
 * Created by usta on 25.12.2014.
 */
@Component
public class PlantUmlService {

    private Logger logger = LoggerFactory.getLogger(PlantUmlService.class);

    @Autowired
    private Current current;

    @Autowired
    private ApplicationController controller ;

    @Autowired
    private ThreadService threadService;

    public void plantUml(String uml, String type, String fileName)  {
        Objects.requireNonNull(fileName);

        if (!fileName.endsWith(".png") && !"ascii".equalsIgnoreCase(type))
            return;

        String defaultScale = "\nskinparam dpi 300\n";

        if (!uml.contains("@startuml") && !uml.contains("@enduml")) {
            uml = defaultScale + uml;
            uml = "@startuml\n" + uml + "\n@enduml";
        } else {
            uml = uml.replaceFirst("@startuml", "@startuml" + defaultScale);
        }

        SourceStringReader reader = new SourceStringReader(uml);

        try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {

            if ("ascii".equalsIgnoreCase(type)) {
                String desc = reader.generateImage(os, new FileFormatOption(FileFormat.ATXT));

                return;
            }
            // default: png
            else {

                if (!current.currentPath().isPresent())
                    controller.saveDoc();

                Path path = current.currentPath().get().getParent();
                Path umlPath = path.resolve("images/").resolve(fileName);

                Integer cacheHit = current.getCache().get(fileName);

                int hashCode = (fileName + type + uml).hashCode();
                if (Objects.isNull(cacheHit) || hashCode != cacheHit) {

                    threadService.runTaskLater(() -> {
                        try {
                            String desc = reader.generateImage(os, new FileFormatOption(FileFormat.PNG));

                            Files.createDirectories(path.resolve("images"));

                            IOHelper.writeToFile(umlPath, os.toByteArray(), CREATE, WRITE, TRUNCATE_EXISTING);

                            controller.getLastRenderedChangeListener()
                                    .changed(null, controller.getLastRendered().getValue(), controller.getLastRendered().getValue());
                        } catch (Exception e) {
                            logger.error(e.getMessage(), e);
                        }
                    });
                }

                current.getCache().put(fileName, hashCode);
            }

        } catch (IOException e) {
            logger.info(e.getMessage(),e);
        }
    }
}

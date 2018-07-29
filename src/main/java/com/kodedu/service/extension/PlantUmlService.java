package com.kodedu.service.extension;

import com.kodedu.config.ExtensionConfigBean;
import com.kodedu.controller.ApplicationController;
import com.kodedu.other.Current;
import com.kodedu.other.IOHelper;
import com.kodedu.service.DirectoryService;
import com.kodedu.service.ThreadService;
import com.kodedu.service.cache.BinaryCacheService;
import net.sourceforge.plantuml.FileFormat;
import net.sourceforge.plantuml.FileFormatOption;
import net.sourceforge.plantuml.FileSystem;
import net.sourceforge.plantuml.SourceStringReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
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
    private final BinaryCacheService binaryCacheService;
    private final ExtensionConfigBean extensionConfigBean;

    private final DirectoryService directoryService;

    @Autowired
    public PlantUmlService(final Current current, final ApplicationController controller, final ThreadService threadService, BinaryCacheService binaryCacheService, ExtensionConfigBean extensionConfigBean, DirectoryService directoryService) {
        this.current = current;
        this.controller = controller;
        this.threadService = threadService;
        this.binaryCacheService = binaryCacheService;
        this.extensionConfigBean = extensionConfigBean;
        this.directoryService = directoryService;
    }

    public void plantUml(String uml, String type, String imagesDir, String imageTarget, String nodename) {
        Objects.requireNonNull(imageTarget);

        boolean cachedResource = imageTarget.contains("/afx/cache");

        if (!imageTarget.endsWith(".png") && !imageTarget.endsWith(".svg") && !cachedResource)
            return;

        StringBuffer stringBuffer = new StringBuffer(uml);

        appendHeaderNotExist(stringBuffer, nodename, "uml", "uml");
        appendHeaderNotExist(stringBuffer, nodename, "ditaa", "ditaa");
        appendHeaderNotExist(stringBuffer, nodename, "graphviz", "uml");

        uml = stringBuffer.toString();

        if (nodename.contains("uml")) {
            if (!uml.contains("skinparam") && !uml.contains("dpi")) {
                uml = uml.replaceFirst("@startuml", String.format("@startuml\nskinparam dpi %d\n", extensionConfigBean.getDefaultImageDpi()));
            }
        }

        if (uml.contains("@startdot")) {
            if (!uml.contains("dpi=")) {
                uml = uml.replaceFirst("\\{", String.format("{\ndpi=%d;\n", extensionConfigBean.getDefaultImageDpi()));
            }
        }

        if (nodename.contains("ditaa")) {
            if (!uml.contains("scale=")) {
                uml = uml.replaceFirst("@startditaa", String.format("@startditaa(scale=%d)", extensionConfigBean.getDefaultImageScale()));
            }
        }

        if (uml.contains("@startditaa")) {
            if (!uml.contains("scale=")) {
                uml = uml.replaceFirst("@startditaa", String.format("@startditaa(scale=%d)", extensionConfigBean.getDefaultImageScale()));
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

            FileSystem.getInstance().setCurrentDir(path.toFile());

            FileFormat fileType = imageTarget.endsWith(".svg") ? FileFormat.SVG : FileFormat.PNG;

            threadService.runTaskLater(() -> {
                try {

                    reader.outputImage(os, new FileFormatOption(fileType));

                    if (!cachedResource) {
                        IOHelper.createDirectories(path.resolve(imagesDir));
                        IOHelper.writeToFile(umlPath, os.toByteArray(), CREATE, WRITE, TRUNCATE_EXISTING, SYNC);
                    } else {
                        binaryCacheService.putBinary(imageTarget, os.toByteArray());
                    }

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

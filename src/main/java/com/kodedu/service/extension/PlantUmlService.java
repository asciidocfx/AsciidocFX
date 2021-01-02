package com.kodedu.service.extension;

import com.kodedu.config.ExtensionConfigBean;
import com.kodedu.controller.ApplicationController;
import com.kodedu.helper.IOHelper;
import com.kodedu.other.Current;
import com.kodedu.service.DirectoryService;
import com.kodedu.service.ThreadService;
import com.kodedu.service.cache.BinaryCacheService;
import net.sourceforge.plantuml.*;
import net.sourceforge.plantuml.security.SFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Objects;

import static java.lang.String.format;
import static java.nio.file.StandardOpenOption.*;
import static java.util.Objects.nonNull;

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

    public void plantUml(String uml, String type, String imagesDir, String imageTarget, String nodename, String options) {
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
                uml = uml.replaceFirst("@startuml", format("@startuml\nskinparam dpi %d\n", extensionConfigBean.getDefaultImageDpi()));
            }
        }

        if (uml.contains("@startdot")) {
            if (!uml.contains("dpi=")) {
                uml = uml.replaceFirst("\\{", format("{\ndpi=%d;\n", extensionConfigBean.getDefaultImageDpi()));
            }
        }

        if (uml.contains("@startditaa") && !uml.contains("@startditaa(")) {

            if (nonNull(options)) {

                options = replaceOptionsIfNecessary(options);

                if (!options.contains("scale=")) {
                    options = format("--scale=%d,", extensionConfigBean.getDefaultImageScale()) + options;
                }

            } else {
                options = format("--scale=%d", extensionConfigBean.getDefaultImageScale());
            }

            uml = uml.replaceFirst("@startditaa", format("@startditaa(%s)", options));
        }

        Integer cacheHit = current.getCache().get(imageTarget);

        int hashCode = (imageTarget + imagesDir + type + uml + nodename + options).hashCode();

        if (nonNull(cacheHit))
            if (hashCode == cacheHit)
                return;

        logger.debug("UML extension is started for {}", imageTarget);

        try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {

            Path path = current.currentTab().getParentOrWorkdir();
            Path umlPath = path.resolve(imageTarget);

            SourceStringReader reader = new SourceStringReader(uml, SFile.fromFile(path.toAbsolutePath().toFile()));

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

    private String replaceOptionsIfNecessary(String options) {
        options = options.replace("separation=false", "--no-separation");
        options = options.replace("antialias=false", "--no-antialias");
        options = options.replace("round-corners=true", "--round-corners");
        options = options.replace("shadows=false", "--no-shadows");
        options = options.replace("debug=true", "--debug");
        options = options.replace("fixed-slope=true", "--fixed-slope");
        options = options.replace("transparent=true", "--transparent");
        options = options.replace("tabs=", "--tabs=");
        options = options.replace("scale=", "--scale=");
        return options;
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

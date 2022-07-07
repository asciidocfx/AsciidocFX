package com.kodedu.service.extension.tree;

import com.kodedu.service.ThreadService;
import com.kodedu.service.extension.ImageInfo;
import com.kodedu.service.extension.impl.TreeServiceImpl;
import org.asciidoctor.ast.ContentNode;
import org.asciidoctor.extension.Reader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Component
@Scope("prototype")
public class FileTreeProcessor {

    private final Logger logger = LoggerFactory.getLogger(FileTreeProcessor.class);

    private final Environment environment;
    private final TreeServiceImpl treeService;
    private final ThreadService threadService;

    public FileTreeProcessor(Environment environment, TreeServiceImpl treeService, ThreadService threadService) {
        this.environment = environment;
        this.treeService = treeService;
        this.threadService = threadService;
    }

    public void process(ContentNode parent, Reader reader, Map<String, Object> attributes, ImageInfo imageInfo, String content, String name) {
        String type = (String) attributes.get("type");

        CompletableFuture completableFuture = new CompletableFuture();
        completableFuture.runAsync(() -> {
            threadService.runActionLater(() -> {
                try {
                    if (content.split("#").length > content.split("\\|-").length) {
                        treeService.createFileTree(content, type, imageInfo.imagesDir(), imageInfo.imagePath(), name, completableFuture);
                    } else {
                        treeService.createHighlightFileTree(content, type, imageInfo.imagesDir(), imageInfo.imagePath(), name, completableFuture);
                    }
                } catch (Exception e) {
                    completableFuture.completeExceptionally(e);
                }
            });
        }, threadService.executor());


        try {
            completableFuture.get(10, TimeUnit.SECONDS);
        } catch (Exception e) {
            logger.error("Error occured during tree generation. {}", content, e);
        }

    }


}

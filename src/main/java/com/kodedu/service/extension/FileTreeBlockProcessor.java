package com.kodedu.service.extension;

import com.kodedu.service.ThreadService;
import com.kodedu.service.extension.impl.TreeServiceImpl;
import org.asciidoctor.ast.ContentModel;
import org.asciidoctor.ast.StructuralNode;
import org.asciidoctor.extension.Contexts;
import org.asciidoctor.extension.Name;
import org.asciidoctor.extension.Reader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Name("tree")
@Contexts({Contexts.OPEN, Contexts.EXAMPLE, Contexts.SIDEBAR, Contexts.LITERAL, Contexts.LISTING})
@ContentModel(ContentModel.EMPTY)
@Component
@Scope("prototype")
public class FileTreeBlockProcessor extends CustomBlockProcessor {

    private final Logger logger = LoggerFactory.getLogger(FileTreeBlockProcessor.class);

    private final Environment environment;
    private final TreeServiceImpl treeService;
    private final ThreadService threadService;

    public FileTreeBlockProcessor(Environment environment, TreeServiceImpl treeService, ThreadService threadService) {
        super(environment);
        this.environment = environment;
        this.treeService = treeService;
        this.threadService = threadService;
    }

    @Override
    protected Object process(StructuralNode parent, Reader reader, Map<String, Object> attributes, ImageInfo imageInfo, String content) {
        String type = (String) attributes.get("type");

        CompletableFuture completableFuture = new CompletableFuture();
        completableFuture.runAsync(() -> {
            threadService.runActionLater(() -> {
                try {
                    if (content.split("#").length > content.split("\\|-").length) {
                        treeService.createFileTree(content, type, imageInfo.imagesDir(), imageInfo.imageTarget(), name, () -> {
                            completableFuture.complete(null);
                        });
                    } else {
                        treeService.createHighlightFileTree(content, type, imageInfo.imagesDir(), imageInfo.imageTarget(), name, () -> {
                            completableFuture.complete(null);
                        });
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

        return createBlock(parent, imageInfo.imageName());
    }

}

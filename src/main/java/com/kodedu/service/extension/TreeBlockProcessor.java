package com.kodedu.service.extension;

import com.kodedu.service.ThreadService;
import com.kodedu.service.extension.impl.TreeServiceImpl;
import org.asciidoctor.ast.Block;
import org.asciidoctor.ast.ContentModel;
import org.asciidoctor.ast.StructuralNode;
import org.asciidoctor.extension.BlockProcessor;
import org.asciidoctor.extension.Contexts;
import org.asciidoctor.extension.Name;
import org.asciidoctor.extension.Reader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;

import java.nio.charset.Charset;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Name("tree")
@Contexts({Contexts.OPEN, Contexts.EXAMPLE, Contexts.SIDEBAR, Contexts.LITERAL, Contexts.LISTING})
@ContentModel(ContentModel.EMPTY)
@Component
public class TreeBlockProcessor extends BlockProcessor {

    private final Logger logger = LoggerFactory.getLogger(TreeBlockProcessor.class);

    private final Environment environment;
    private final TreeServiceImpl treeService;
    private final ThreadService threadService;

    public TreeBlockProcessor(Environment environment, TreeServiceImpl treeService, ThreadService threadService) {
        this.environment = environment;
        this.treeService = treeService;
        this.threadService = threadService;
    }

    @Override
    public Object process(StructuralNode structuralNode, Reader reader, Map<String, Object> map) {
        String imagesDir = (String) structuralNode.getDocument().getAttribute("imagesdir");
        String imageName = (String) map.get("file");
        String type = (String) map.get("type");
        String imageTarget = null;
        String content = reader.read();
        String imageMd5 = cachedImageUri(content);
        if (Objects.isNull(imageName)) {
            int port = Integer.parseInt(environment.getProperty("local.server.port"));
            imageTarget = "/afx/cache/" + imageMd5 + ".png";
            imageName = "http://localhost:" + port + imageTarget;
        } else {
            imageTarget = structuralNode.imageUri(imageName);
        }

        imageName += "?" + imageMd5;

        CompletableFuture completableFuture = new CompletableFuture();
        String finalImageTarget = imageTarget;
        completableFuture.runAsync(() -> {
            threadService.runActionLater(() -> {
                try {
                    if (content.split("#").length > content.split("\\|-").length) {
                        treeService.createFileTree(content, type, imagesDir, finalImageTarget, name, () -> {
                            completableFuture.complete(null);
                        });
                    } else {
                        treeService.createHighlightFileTree(content, type, imagesDir, finalImageTarget, name, () -> {
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

        return createBlock(structuralNode, imageName);
    }

    private Block createBlock(StructuralNode structuralNode, String imageName) {
        Block block = createBlock(structuralNode, "image", Collections.emptyMap());
        block.setAttribute("target", imageName , true);
        return block;
    }

    public String cachedImageUri(String content) {
        String md5Digest = DigestUtils.md5DigestAsHex(content.getBytes(Charset.defaultCharset()));
        return md5Digest;
    }
}

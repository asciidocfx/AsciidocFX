package com.kodedu.service.extension;

import org.asciidoctor.ast.Block;
import org.asciidoctor.ast.StructuralNode;
import org.asciidoctor.extension.BlockProcessor;
import org.asciidoctor.extension.Reader;
import org.springframework.core.env.Environment;
import org.springframework.util.DigestUtils;

import java.nio.charset.Charset;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;

public abstract class CustomBlockProcessor extends BlockProcessor {

    private final Environment environment;

    protected CustomBlockProcessor(Environment environment) {
        this.environment = environment;
    }

    @Override
    public Object process(StructuralNode parent, Reader reader, Map<String, Object> attributes) {
        String content = preProcessContent(parent, reader, attributes, reader.read());
        ImageInfo imageInfo = getImageInfo(attributes, parent, content);
        return process(parent, reader, attributes, imageInfo, content);
    }

    private ImageInfo getImageInfo(Map<String, Object> attributes, StructuralNode parent, String content) {
        String imagesDir = getImagesDir(parent);
        String imageName = (String) attributes.get("file");
        String imageTarget = null;
        String imageMd5 = cachedImageUri(content);
        if (Objects.isNull(imageName)) {
            int port = Integer.parseInt(environment.getProperty("local.server.port"));
            imageTarget = "/afx/cache/" + imageMd5 + ".png";
            imageName = "http://localhost:" + port + imageTarget;
        } else {
            imageTarget = parent.imageUri(imageName);
        }

        imageName += "?" + imageMd5;
        return new ImageInfo(imagesDir, imageName, imageTarget);
    }

    private String getImagesDir(StructuralNode parent) {
        return (String) parent.getDocument().getAttribute("imagesdir");
    }

    private String cachedImageUri(String content) {
        String md5Digest = DigestUtils.md5DigestAsHex(content.getBytes(Charset.defaultCharset()));
        return md5Digest;
    }

    protected Block createBlock(StructuralNode structuralNode, String imageName) {
        Block block = createBlock(structuralNode, "image", Collections.emptyMap());
        block.setAttribute("target", imageName, true);
        return block;
    }

    protected abstract Object process(StructuralNode parent, Reader reader,
                                      Map<String, Object> attributes, ImageInfo imageInfo,
                                      String content);

    protected String preProcessContent(StructuralNode parent, Reader reader, Map<String, Object> attributes,
                                       String content) {
        return content;
    }

    ;
}

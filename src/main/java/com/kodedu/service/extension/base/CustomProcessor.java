package com.kodedu.service.extension.base;

import com.kodedu.helper.IOHelper;
import com.kodedu.service.extension.ImageInfo;
import org.asciidoctor.ast.Block;
import org.asciidoctor.ast.ContentNode;
import org.asciidoctor.ast.PhraseNode;
import org.asciidoctor.ast.StructuralNode;
import org.asciidoctor.extension.Reader;
import org.springframework.core.env.Environment;
import org.springframework.util.DigestUtils;

import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public interface CustomProcessor {

    default Path findPath(String normalizedPath) {
        List<String> extensions = List.of("", ".txt", ".csv");
        for (String extension : extensions) {
            Path path = Paths.get(normalizedPath + extension);
            if(Files.exists(path) && !Files.isDirectory(path)){
                return path;
            }
        }
        return Paths.get(normalizedPath);
    }

    default ImageInfo getImageInfo(Environment environment, Map<String, Object> attributes, ContentNode parent, String content) {
        String docdir = (String) parent.getDocument().getAttributes().get("docdir");
        String imagesDir = getImagesDir(parent);
        String imageTarget = (String) attributes.getOrDefault("file", attributes.get("target"));
        String format = (String) attributes.getOrDefault("format","png");
        if(Objects.nonNull(imageTarget) && Objects.nonNull(format)){
            imageTarget = String.format("%s.%s", imageTarget, format);
        }
        String imageMd5 = cachedImageUri(content);
        if (Objects.isNull(imageTarget)) {
            int port = Integer.parseInt(environment.getProperty("local.server.port"));
            imageTarget = "http://localhost:" + port + "/afx/cache/" + imageMd5 + ".png";
        } else {
            imageTarget = Paths.get(imagesDir).resolve(imageTarget).toString();
        }

        boolean isPreview = (boolean) parent.getDocument().getAttributes().getOrDefault("preview", false);
        boolean isDataUri = parent.getDocument().hasAttribute("data-uri");
        String imagePath = Paths.get(docdir).resolve(imageTarget).toString();
        if (isPreview && !isDataUri) {
            imageTarget += "?cache" + imageMd5; // for html cache
        }

        return new ImageInfo(imagesDir, imageTarget, imagePath);
    }

    default String getImagesDir(ContentNode parent) {
        return (String) parent.getDocument().getAttribute("imagesdir", "images");
    }

    default String cachedImageUri(String content) {
        String md5Digest = DigestUtils.md5DigestAsHex(content.getBytes(Charset.defaultCharset()));
        return md5Digest;
    }

    Object process(ContentNode parent, Reader reader,
                            Map<String, Object> attributes, ImageInfo imageInfo,
                            String content);

    default Object processMacro(ContentNode parent, String target, Map<String, Object> attributes,
                                Environment environment) {
        HashMap<String, Object> attributesCopy = new HashMap<>(attributes);
        attributesCopy.put("target", target);
        String docdir = (String) parent.getDocument().getAttributes().get("docdir");

        Reader reader = null; // reader is not available for block macro

        String normalizedPath = parent.normalizeWebPath(target, docdir, true);
        Path path = findPath(normalizedPath);

        String content = IOHelper.readFile(path);

        // TODO: Check if it is correct content to preprocess
        content = preProcessContent(parent, reader, attributesCopy, content);
        ImageInfo imageInfo = getImageInfo(environment, attributesCopy, parent, content);
        return process(parent, reader, attributesCopy, imageInfo, content);
    }

    default PhraseNode createInlineImage(ContentNode parent, Map<String, Object> attributes, ImageInfo imageInfo) {
        Map<String, Object> options = new HashMap<String, Object>();
        options.put("type", "image");
        options.put("target", imageInfo.imageTarget());
        HashMap<String, Object> attrCopy = new HashMap<>(attributes);
        attrCopy.put("target", imageInfo.imageTarget());
        Object alt = parent.getAttribute("alt", imageInfo.imageTarget());
        attrCopy.put("alt", alt);
        return createPhraseNode(parent, "image", null, attrCopy, options);
    }

    default Block createBlockImage(StructuralNode parent, Map<String, Object> attributes, ImageInfo imageInfo) {
        Block block = createBlock(parent, "image", Collections.emptyMap());
        for (Map.Entry<String, Object> entry : attributes.entrySet()) {
            block.setAttribute(entry.getKey(), entry.getValue(), true);
        }
        block.setAttribute("target", imageInfo.imageTarget(), true);
        return block;
    }

    PhraseNode createPhraseNode(ContentNode parent, String context, String text, Map<String, Object> attributes, Map<String, Object> options);
    Block createBlock(StructuralNode parent, String context, Map<Object, Object> options);

    default String preProcessContent(ContentNode parent, Reader reader, Map<String, Object> attributes,
                                     String content) {
        return content;
    }
}

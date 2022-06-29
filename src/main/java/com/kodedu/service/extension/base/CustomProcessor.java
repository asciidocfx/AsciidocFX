package com.kodedu.service.extension.base;

import com.kodedu.helper.IOHelper;
import com.kodedu.service.extension.ImageInfo;
import org.asciidoctor.ast.ContentNode;
import org.asciidoctor.extension.Reader;
import org.springframework.core.env.Environment;
import org.springframework.util.DigestUtils;

import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public interface CustomProcessor {

    default Path findPath(String docdir, String normalizedPath) {
        List<String> extensions = List.of("", ".txt", ".csv");
        for (String extension : extensions) {
            Path path = Paths.get(docdir).resolve(normalizedPath + extension);
            if(Files.exists(path) && !Files.isDirectory(path)){
                return path;
            }
        }
        return Paths.get(normalizedPath);
    }

    default ImageInfo getImageInfo(Environment environment, Map<String, Object> attributes, ContentNode parent, String content) {
        String imagesDir = getImagesDir(parent);
        String imageName = (String) attributes.getOrDefault("file", attributes.get("target"));
        String format = (String) attributes.getOrDefault("format","png");
        if(Objects.nonNull(imageName) && Objects.nonNull(format)){
            imageName = String.format("%s.%s", imageName, format);
        }
        String imageTarget = null;
        String imageMd5 = cachedImageUri(content);
        if (Objects.isNull(imageName)) {
            int port = Integer.parseInt(environment.getProperty("local.server.port"));
            imageTarget = "/afx/cache/" + imageMd5 + ".png";
            imageName = "http://localhost:" + port + imageTarget;
        } else {
            imageTarget = parent.imageUri(imageName);
        }

        imageName += "?cache" + imageMd5;
        return new ImageInfo(imagesDir, imageName, imageTarget);
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

        String normalizedPath = parent.normalizeWebPath(target, null, true);
        Path path = findPath(docdir, normalizedPath);

        String content = IOHelper.readFile(path);

        content = preProcessContent(parent, reader, attributesCopy, content);
        ImageInfo imageInfo = getImageInfo(environment, attributesCopy, parent, content);
        return process(parent, reader, attributesCopy, imageInfo, content);
    }

    default String preProcessContent(ContentNode parent, Reader reader, Map<String, Object> attributes,
                                     String content) {
        return content;
    }
}

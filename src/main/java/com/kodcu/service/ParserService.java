package com.kodcu.service;

import com.kodcu.controller.ApplicationController;
import com.kodcu.other.Constants;
import com.kodcu.other.Current;
import com.kodcu.other.IOHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.stream.Collectors;

/**
 * Created by usta on 16.12.2014.
 */
@Component
public class ParserService {

    private final ApplicationController asciiDocController;
    private final Current current;
    private final PathResolverService pathResolver;
    
    @Autowired
    public ParserService(final ApplicationController asciiDocController, final Current current, final PathResolverService pathResolver) {
        this.asciiDocController = asciiDocController;
        this.current = current;
        this.pathResolver = pathResolver;
    }

    public Optional<String> toIncludeBlock(List<File> dropFiles) {
        if (!current.currentPath().isPresent())
            asciiDocController.saveDoc();

        Path currentPath = current.currentPath().map(Path::getParent).get();

        List<Path> files = dropFiles.stream().map(File::toPath).filter(pathResolver::isAsciidoc).collect(Collectors.toList());

        List<String> buffer = new LinkedList<>();

        for (Path path : files) {
            Path targetPath = currentPath.resolve(path.getFileName());
            if (!path.equals(targetPath))
                IOHelper.copy(path, targetPath);
            buffer.add(String.format("include::%s[]", path.getFileName()));
        }

        if (buffer.size() > 0)
            return Optional.of(String.join("\n", buffer));

        return Optional.empty();
    }

    public Optional<String> toImageBlock(List<File> dropFiles) {

        if (!current.currentPath().isPresent())
            asciiDocController.saveDoc();

        Path currentPath = current.currentPath().map(Path::getParent).get();
        IOHelper.createDirectories(currentPath.resolve("images"));
        List<Path> paths = dropFiles.stream().map(File::toPath).filter(pathResolver::isImage).collect(Collectors.toList());

        List<String> buffer = new LinkedList<>();

        for (Path path : paths) {
            Path targetImage = currentPath.resolve("images").resolve(path.getFileName());
            if (!path.equals(targetImage))
                IOHelper.copy(path, targetImage);
            buffer.add(String.format("image::images/%s[]", path.getFileName()));
        }

        if (buffer.size() > 0)
            return Optional.of(String.join("\n", buffer));

        return Optional.empty();

    }

    public Optional<String> toWebImageBlock(String html) {

        Matcher matcher = Constants.IMAGE_URL_MATCH.matcher(html);

        List<String> buffer = new LinkedList<>();

        while (matcher.find()) {
            String imageUrl = matcher.group();
            buffer.add(String.format("image::%s[]", imageUrl));
        }

        if (buffer.size() > 0)
            return Optional.of(String.join("\n", buffer));

        return Optional.empty();
    }
}

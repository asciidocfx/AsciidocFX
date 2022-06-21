package com.kodedu.service.impl;

import com.kodedu.controller.ApplicationController;
import com.kodedu.helper.IOHelper;
import com.kodedu.other.Constants;
import com.kodedu.service.DirectoryService;
import com.kodedu.service.ParserService;
import com.kodedu.service.PathResolverService;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.stream.Collectors;

/**
 * Created by usta on 16.12.2014.
 */
@Component(ParserService.label)
public class ParserServiceImpl implements ParserService {
    @Autowired
    private DirectoryService directoryService;

    @Autowired
    private PathResolverService pathResolverService;

    @Autowired
    private ApplicationController asciiDocController;

    private Logger logger = LoggerFactory.getLogger(ParserService.class);

    @Override
    public Optional<String> toIncludeBlock(List<File> dropFiles) {

        List<Path> files = dropFiles.stream().map(File::toPath).filter(p -> !Files.isDirectory(p)).collect(Collectors.toList());

        List<String> buffer = new LinkedList<>();

        applyForEachInPath(files, includePath -> {
            buffer.add(String.format("include::%s[]", includePath));
        });

        if (buffer.size() > 0)
            return Optional.of(String.join("\n", buffer));

        return Optional.empty();
    }

    @Override
    public Optional<String> toImageBlock(Image image) {
        Path currentPath = directoryService.currentParentOrWorkdir();
        IOHelper.createDirectories(currentPath.resolve("images"));

        List<String> buffer = new LinkedList<>();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(asciiDocController.getClipboardImageFilePattern());
        Path path = IOHelper.getPath(dateTimeFormatter.format(LocalDateTime.now()));

        Path targetImage = currentPath.resolve("images").resolve(path.getFileName());

        try {
            BufferedImage fromFXImage = SwingFXUtils.fromFXImage(image, null);
            ImageIO.write(fromFXImage, "png", targetImage.toFile());

        } catch (Exception e) {
            logger.error("Problem occured while saving clipboard image {}", targetImage, e);
        }

        buffer.add(String.format("image::images/%s[]", path.getFileName()));

        if (buffer.size() > 0)
            return Optional.of(String.join("\n", buffer));

        return Optional.empty();

    }

    @Override
    public Optional<String> toImageBlock(List<File> dropFiles) {

        Path workDir = directoryService.workingDirectory();
        IOHelper.createDirectories(workDir.resolve("images"));
        List<Path> paths = dropFiles.stream().map(File::toPath).filter(pathResolverService::isImage).collect(Collectors.toList());

        List<String> buffer = new LinkedList<>();

        applyForEachInPath(paths, imagePath -> {
            buffer.add(String.format("image::%s[]", imagePath));
        });

        if (buffer.size() > 0)
            return Optional.of(String.join("\n", buffer));

        return Optional.empty();

    }

    @Override
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

    private void applyForEachInPath(List<Path> pathList, Consumer<Path> consumer) {
        Path workDir = directoryService.workingDirectory();
        applyForEachInPath(pathList, consumer, workDir);
    }

    private void applyForEachInPath(List<Path> pathList, Consumer<Path> consumer, Path root) {

        Path workDirRoot = root.getRoot();

        for (Path path : pathList) {

            Path includePath = null;

            if (workDirRoot.equals(path.getRoot())) {
                includePath = root.relativize(path);
            } else {
                includePath = path.toAbsolutePath();
            }

            if (Objects.nonNull(includePath)) {
                consumer.accept(includePath);
            }
        }
    }
}

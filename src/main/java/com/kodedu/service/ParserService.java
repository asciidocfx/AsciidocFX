package com.kodedu.service;

import javafx.scene.image.Image;
import java.io.File;
import java.util.List;
import java.util.Optional;

/**
 * Created by usta on 16.12.2014.
 */
public interface ParserService {
    public final static String label = "core::service::Parser";

    public Optional<String> toIncludeBlock(List<File> dropFiles);

    public Optional<String> toImageBlock(Image image);

    public Optional<String> toImageBlock(List<File> dropFiles);

    public Optional<String> toWebImageBlock(String html);
}

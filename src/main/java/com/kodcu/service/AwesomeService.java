package com.kodcu.service;

import de.jensd.fx.fontawesome.AwesomeDude;
import de.jensd.fx.fontawesome.AwesomeIcon;
import javafx.scene.Node;
import javafx.scene.control.Label;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Created by usta on 16.12.2014.
 */
@Component
public class AwesomeService {

    @Autowired
    private PathResolverService pathResolver;

    public Node getIcon(Path path) {
        AwesomeIcon awesomeIcon = null;
        if (Files.isDirectory(path))
            awesomeIcon = AwesomeIcon.FOLDER_ALT;
        else if (pathResolver.isAsciidoc(path))
            awesomeIcon = AwesomeIcon.FILE_TEXT_ALT;
        else if (pathResolver.isImage(path))
            awesomeIcon = AwesomeIcon.FILE_PICTURE_ALT;
        Label iconLabel = AwesomeDude.createIconLabel(awesomeIcon, "14.0");
        return iconLabel;
    }
}

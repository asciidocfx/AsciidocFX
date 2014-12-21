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
        AwesomeIcon awesomeIcon = AwesomeIcon.FILE;
        if (Files.isDirectory(path))
            awesomeIcon = AwesomeIcon.FOLDER_ALT;
        else if (pathResolver.isAsciidoc(path))
            awesomeIcon = AwesomeIcon.FILE_TEXT_ALT;
        else if (pathResolver.isImage(path))
            awesomeIcon = AwesomeIcon.FILE_PICTURE_ALT;
        else if (pathResolver.isPDF(path))
            awesomeIcon = AwesomeIcon.FILE_PDF_ALT;
        else if (pathResolver.isPPT(path))
            awesomeIcon = AwesomeIcon.FILE_POWERPOINT_ALT;
        else if (pathResolver.isDocx(path))
            awesomeIcon = AwesomeIcon.FILE_WORD_ALT;
        else if (pathResolver.isExcel(path))
            awesomeIcon = AwesomeIcon.FILE_EXCEL_ALT;
        else if (pathResolver.isArchive(path))
            awesomeIcon = AwesomeIcon.ARCHIVE;
        else if (pathResolver.isVideo(path))
            awesomeIcon = AwesomeIcon.FILE_VIDEO_ALT;
        else if (pathResolver.isHTML(path))
            awesomeIcon = AwesomeIcon.HTML5;
        else if (pathResolver.isCSS(path))
            awesomeIcon = AwesomeIcon.CSS3;
        else if (pathResolver.isBash(path))
            awesomeIcon = AwesomeIcon.TERMINAL;
        Label iconLabel = AwesomeDude.createIconLabel(awesomeIcon, "14.0");
        return iconLabel;
    }
}

package com.kodcu.service.ui;

import com.kodcu.service.PathResolverService;
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

    private final PathResolverService pathResolver;

    @Autowired
    public AwesomeService(final PathResolverService pathResolver) {
        this.pathResolver = pathResolver;
    }

    public Node getIcon(final Path path) {

        AwesomeIcon awesomeIcon = AwesomeIcon.FILE_ALT;

        if (Files.isDirectory(path)) {
            awesomeIcon = AwesomeIcon.FOLDER_ALT;
        } else {
            if (pathResolver.isAsciidoc(path) || pathResolver.isMarkdown(path))
                awesomeIcon = AwesomeIcon.FILE_TEXT_ALT;
            if (pathResolver.isXML(path) || pathResolver.isCode(path))
                awesomeIcon = AwesomeIcon.FILE_CODE_ALT;
            if (pathResolver.isImage(path))
                awesomeIcon = AwesomeIcon.FILE_PICTURE_ALT;
            if (pathResolver.isPDF(path))
                awesomeIcon = AwesomeIcon.FILE_PDF_ALT;
            if (pathResolver.isHTML(path))
                awesomeIcon = AwesomeIcon.HTML5;
            if (pathResolver.isArchive(path))
                awesomeIcon = AwesomeIcon.FILE_ZIP_ALT;
            if (pathResolver.isExcel(path))
                awesomeIcon = AwesomeIcon.FILE_EXCEL_ALT;
            if (pathResolver.isVideo(path))
                awesomeIcon = AwesomeIcon.FILE_VIDEO_ALT;
            if (pathResolver.isWord(path))
                awesomeIcon = AwesomeIcon.FILE_WORD_ALT;
            if (pathResolver.isPPT(path))
                awesomeIcon = AwesomeIcon.FILE_POWERPOINT_ALT;
            if (pathResolver.isVideo(path))
                awesomeIcon = AwesomeIcon.FILE_VIDEO_ALT;
            if (pathResolver.isBash(path))
                awesomeIcon = AwesomeIcon.TERMINAL;
        }

        final Label iconLabel = AwesomeDude.createIconLabel(awesomeIcon, "14.0");
        return iconLabel;
    }
}

package com.kodcu.service.ui;

import com.kodcu.service.PathResolverService;
import de.jensd.fx.fontawesome.AwesomeDude;
import de.jensd.fx.fontawesome.AwesomeIcon;
import javafx.scene.Node;
import javafx.scene.control.Label;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.nio.file.Path;

/**
 * Created by usta on 16.12.2014.
 */
@Component
public class AwesomeService {

    @Autowired
    private PathResolverService pathResolver;

    public Node getIcon(Path path) {

        AwesomeIcon awesomeIcon = AwesomeIcon.FOLDER_ALT;

        if (pathResolver.isAny(path))
            awesomeIcon = AwesomeIcon.FILE_ALT;
        if (pathResolver.isAsciidoc(path))
            awesomeIcon = AwesomeIcon.FILE_TEXT_ALT;
        if (pathResolver.isImage(path))
            awesomeIcon = AwesomeIcon.FILE_PICTURE_ALT;
        if (pathResolver.isPDF(path))
            awesomeIcon = AwesomeIcon.FILE_PDF_ALT;
        if (pathResolver.isPPT(path))
            awesomeIcon = AwesomeIcon.FILE_POWERPOINT_ALT;
        if (pathResolver.isDocx(path))
            awesomeIcon = AwesomeIcon.FILE_WORD_ALT;
        if (pathResolver.isExcel(path))
            awesomeIcon = AwesomeIcon.FILE_EXCEL_ALT;
        if (pathResolver.isArchive(path))
            awesomeIcon = AwesomeIcon.ARCHIVE;
        if (pathResolver.isVideo(path))
            awesomeIcon = AwesomeIcon.FILE_VIDEO_ALT;
        if (pathResolver.isHTML(path))
            awesomeIcon = AwesomeIcon.HTML5;
        if (pathResolver.isCSS(path))
            awesomeIcon = AwesomeIcon.CSS3;
        if (pathResolver.isBash(path))
            awesomeIcon = AwesomeIcon.TERMINAL;
        if (pathResolver.isCode(path))
            awesomeIcon = AwesomeIcon.CODE;
        if (pathResolver.isEpub(path) || pathResolver.isMobi(path))
            awesomeIcon = AwesomeIcon.FILE_TEXT;

        Label iconLabel = AwesomeDude.createIconLabel(awesomeIcon, "14.0");
        return iconLabel;
    }
}

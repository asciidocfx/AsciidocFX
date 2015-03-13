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

    private final PathResolverService pathResolver;

    @Autowired
    public AwesomeService(final PathResolverService pathResolver) {
        this.pathResolver = pathResolver;
    }

    public Node getIcon(final Path path) {

        AwesomeIcon awesomeIcon = AwesomeIcon.FOLDER_ALT;

        if (pathResolver.isAsciidoc(path) || pathResolver.isMarkdown(path))
            awesomeIcon = AwesomeIcon.FILE_TEXT_ALT;
        if (pathResolver.isXML(path))
            awesomeIcon = AwesomeIcon.FILE_CODE_ALT;
        if (pathResolver.isImage(path))
            awesomeIcon = AwesomeIcon.FILE_PICTURE_ALT;
        if (pathResolver.isPDF(path))
            awesomeIcon = AwesomeIcon.FILE_PDF_ALT;
        if (pathResolver.isHTML(path))
            awesomeIcon = AwesomeIcon.HTML5;
        if (pathResolver.isEpub(path) || pathResolver.isMobi(path))
            awesomeIcon = AwesomeIcon.FILE_TEXT;

        final Label iconLabel = AwesomeDude.createIconLabel(awesomeIcon, "14.0");
        return iconLabel;
    }
}

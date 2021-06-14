package com.kodedu.service.ui.impl;

import com.kodedu.service.PathResolverService;
import com.kodedu.service.ui.AwesomeService;

import javafx.scene.Node;
import javafx.scene.control.Label;
import org.kordamp.ikonli.fontawesome.FontAwesome;
import org.kordamp.ikonli.javafx.FontIcon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Created by usta on 16.12.2014.
 */
@Component
public class AwesomeServiceImpl implements AwesomeService {

    private final PathResolverService pathResolver;

    @Autowired
    public AwesomeServiceImpl(final PathResolverService pathResolver) {
        this.pathResolver = pathResolver;
    }

    @Override
    public Node getIcon(final Path path) {

        FontIcon fontIcon = new FontIcon(FontAwesome.FILE_O);

        if (Files.isDirectory(path)) {
            fontIcon.setIconCode(FontAwesome.FOLDER_O);
        } else {
            if (pathResolver.isAsciidoc(path) || pathResolver.isMarkdown(path))
                fontIcon.setIconCode(FontAwesome.FILE_TEXT_O);
            if (pathResolver.isXML(path) || pathResolver.isCode(path))
                fontIcon.setIconCode(FontAwesome.FILE_CODE_O);
            if (pathResolver.isImage(path))
                fontIcon.setIconCode(FontAwesome.FILE_PICTURE_O);
            if (pathResolver.isPDF(path))
                fontIcon.setIconCode(FontAwesome.FILE_PDF_O);
            if (pathResolver.isHTML(path))
                fontIcon.setIconCode(FontAwesome.HTML5);
            if (pathResolver.isArchive(path))
                fontIcon.setIconCode(FontAwesome.FILE_ZIP_O);
            if (pathResolver.isExcel(path))
                fontIcon.setIconCode(FontAwesome.FILE_EXCEL_O);
            if (pathResolver.isVideo(path))
                fontIcon.setIconCode(FontAwesome.FILE_VIDEO_O);
            if (pathResolver.isWord(path))
                fontIcon.setIconCode(FontAwesome.FILE_WORD_O);
            if (pathResolver.isPPT(path))
                fontIcon.setIconCode(FontAwesome.FILE_POWERPOINT_O);
            if (pathResolver.isBash(path))
                fontIcon.setIconCode(FontAwesome.TERMINAL);
        }

        return new Label(null, fontIcon);
    }
}

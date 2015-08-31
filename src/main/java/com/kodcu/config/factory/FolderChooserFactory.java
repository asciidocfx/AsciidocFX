package com.kodcu.config.factory;

import javafx.event.ActionEvent;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;

import java.io.File;
import java.util.Objects;

/**
 * Created by usta on 26.07.2015.
 */
public class FolderChooserFactory extends ChooserFactory {


    public FolderChooserFactory(String promptText) {
        super(promptText);
    }

    @Override
    public void chooser(ActionEvent actionEvent) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle(promptText);
        File openDialog = directoryChooser.showDialog(null);
        if (Objects.nonNull(openDialog)) {
            property.setValue(openDialog.toPath());
        }
    }
}

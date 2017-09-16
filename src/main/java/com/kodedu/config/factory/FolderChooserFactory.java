package com.kodedu.config.factory;

import javafx.event.ActionEvent;
import javafx.stage.DirectoryChooser;

import java.io.File;
import java.nio.file.Path;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * Created by usta on 26.07.2015.
 */
public class FolderChooserFactory extends ChooserFactory {


    public FolderChooserFactory(String promptText, Consumer<Path> browseConsumer) {
        super(promptText, browseConsumer);
    }

    @Override
    public void chooser(ActionEvent actionEvent) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle(promptText);
        File openDialog = directoryChooser.showDialog(null);
        if (Objects.nonNull(openDialog)) {
            property.setValue(openDialog.toPath().toString());
        }
    }
}

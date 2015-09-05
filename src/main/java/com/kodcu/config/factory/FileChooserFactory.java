package com.kodcu.config.factory;

import com.dooapp.fxform.view.FXFormNode;
import com.dooapp.fxform.view.FXFormNodeWrapper;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.util.Callback;

import java.io.File;
import java.nio.file.Path;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * Created by usta on 26.07.2015.
 */
public class FileChooserFactory extends ChooserFactory {

    public FileChooserFactory(String promptText, Consumer<Path> browseConsumer) {
        super(promptText, browseConsumer);
    }

    public FileChooserFactory(Consumer<Path> browseConsumer) {
        super("Enter local path or URL", browseConsumer);
    }

    @Override
    public void chooser(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(promptText);
        File openDialog = fileChooser.showOpenDialog(null);
        if (Objects.nonNull(openDialog)) {
            property.setValue(openDialog.toPath().toString());
        }
    }
}

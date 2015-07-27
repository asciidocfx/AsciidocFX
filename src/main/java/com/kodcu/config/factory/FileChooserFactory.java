package com.kodcu.config.factory;

import com.dooapp.fxform.view.FXFormNode;
import com.dooapp.fxform.view.FXFormNodeWrapper;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.util.Callback;

import java.io.File;
import java.nio.file.Path;
import java.util.Objects;

/**
 * Created by usta on 26.07.2015.
 */
public class FileChooserFactory implements Callback<Void, FXFormNode> {

    private final ObjectProperty<Path> property = new SimpleObjectProperty<>();
    private final TextField textField = new TextField();
    private final Button button = new Button();
    private final Tooltip tooltip = new Tooltip();

    @Override
    public FXFormNode call(Void param) {
        String promptText = "Select kindlegen executable file";
        textField.setPromptText(promptText);
        textField.setDisable(true);

        button.setText("Select");

        property.addListener((observable, oldValue, newValue) -> {
            if (Objects.nonNull(newValue)) {
                textField.setText(newValue.toString());
                tooltip.setText(newValue.toString());
            }
        });

        button.setOnAction(event -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle(promptText);
            File openDialog = fileChooser.showOpenDialog(null);
            if (Objects.nonNull(openDialog)) {
                property.setValue(openDialog.toPath());
            }
        });

        HBox hBox = new HBox(5);
        hBox.getChildren().addAll(textField, button);

        Tooltip.install(hBox, tooltip);

        return new FXFormNodeWrapper(hBox, property);
    }
}

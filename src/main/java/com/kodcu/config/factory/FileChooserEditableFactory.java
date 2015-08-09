package com.kodcu.config.factory;

import com.dooapp.fxform.view.FXFormNode;
import com.dooapp.fxform.view.FXFormNodeWrapper;
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
import java.util.function.Consumer;

/**
 * Created by usta on 26.07.2015.
 */
public class FileChooserEditableFactory implements Callback<Void, FXFormNode> {

    private final ObjectProperty<Path> property = new SimpleObjectProperty<>();
    private final TextField textField = new TextField();
    private final Button selectButton = new Button();
    private final Button editButton = new Button();
    private final Tooltip tooltip = new Tooltip();

    public void setOnEdit(Consumer<Path> pathConsumer) {
        editButton.setOnAction(event -> {
            pathConsumer.accept(property.get());
        });
    }

    @Override
    public FXFormNode call(Void param) {
        String promptText = "Select a stylesheet file";
        textField.setPromptText(promptText);
        tooltip.setText(promptText);
        textField.setDisable(true);

        selectButton.setText("Select");
        editButton.setText("Edit");

        property.addListener((observable, oldValue, newValue) -> {
            if (Objects.nonNull(newValue)) {
                textField.setText(newValue.toString());
                tooltip.setText(newValue.toString());
            }
        });

        selectButton.setOnAction(event -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle(promptText);
            File openDialog = fileChooser.showOpenDialog(null);
            if (Objects.nonNull(openDialog)) {
                property.setValue(openDialog.toPath());
            }
        });

        HBox hBox = new HBox(5);
        hBox.getChildren().addAll(textField, selectButton, editButton);

        Tooltip.install(hBox, tooltip);

        return new FXFormNodeWrapper(hBox, property);
    }
}

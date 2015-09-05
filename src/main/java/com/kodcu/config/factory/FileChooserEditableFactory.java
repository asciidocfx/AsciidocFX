package com.kodcu.config.factory;

import com.dooapp.fxform.view.FXFormNode;
import com.dooapp.fxform.view.FXFormNodeWrapper;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.stage.FileChooser;
import javafx.util.Callback;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * Created by usta on 26.07.2015.
 */
public class FileChooserEditableFactory implements Callback<Void, FXFormNode> {

    private final ObjectProperty<String> property = new SimpleObjectProperty<>();
    private final TextField textField = new TextField();
    private final Button selectButton = new Button();
    private final Button editButton = new Button();
    private final Button browseButton = new Button();
    private final Tooltip tooltip = new Tooltip();

    public FileChooserEditableFactory() {
    }

    public FileChooserEditableFactory(Consumer<Path> onEditConsumer, Consumer<Path> onBrowseConsumer) {
        setOnEdit(onEditConsumer);
        setOnBrowse(onBrowseConsumer);
    }

    public void setOnEdit(Consumer<Path> onEditConsumer) {
        editButton.setOnAction(event -> {
            String first = property.get();
            if (Objects.nonNull(first)) {
                onEditConsumer.accept(Paths.get(first));
            }
        });
    }

    public void setOnBrowse(Consumer<Path> onBrowseConsumer) {
        browseButton.setOnAction(event -> {
            Optional<Path> optional = Optional.ofNullable(property.get())
                    .map(Paths::get)
                    .filter(Files::exists);

            optional.filter(Files::isDirectory)
                    .ifPresent(onBrowseConsumer::accept);

            optional.filter(e -> !Files.isDirectory(e))
                    .map(Path::getParent)
                    .ifPresent(onBrowseConsumer::accept);
        });
    }

    @Override
    public FXFormNode call(Void param) {
        String promptText = "Enter local path or URL";
        textField.setPromptText(promptText);
        tooltip.setText(promptText);
        textField.setDisable(false);

        selectButton.setText("Change");
        editButton.setText("Edit");
        browseButton.setText("Browse");

        textField.setOnMouseEntered(event -> {
            Optional.of(textField).filter(e -> !e.isFocused())
                    .map(TextField::getText)
                    .ifPresent(text -> textField.positionCaret(text.length()));
        });

        textField.setOnMouseExited(event -> {
            if (!textField.isFocused())
                textField.positionCaret(0);
        });

        textField.textProperty().bindBidirectional(property);


        property.addListener((observable, oldValue, newValue) -> {
            if (Objects.nonNull(newValue)) {
                tooltip.setText(newValue);
                if (newValue.isEmpty()) {
                    property.set(null);
                }
            }
        });

        browseButton.visibleProperty().bind(property.isNotNull());
        browseButton.managedProperty().bind(property.isNotNull());
        editButton.visibleProperty().bind(property.isNotNull());
        editButton.managedProperty().bind(property.isNotNull());

        selectButton.setOnAction(event -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle(promptText);
            File openDialog = fileChooser.showOpenDialog(null);
            if (Objects.nonNull(openDialog)) {
                property.setValue(openDialog.toPath().toString());
            }
        });

        HBox hBox = new HBox(5);
        hBox.getChildren().addAll(textField, selectButton, editButton, browseButton);
        HBox.setHgrow(textField, Priority.ALWAYS);

        Tooltip.install(textField, tooltip);

        return new FXFormNodeWrapper(hBox, property);
    }
}

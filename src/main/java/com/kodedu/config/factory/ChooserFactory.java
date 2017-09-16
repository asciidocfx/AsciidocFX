package com.kodedu.config.factory;

import com.dooapp.fxform.view.FXFormNode;
import com.dooapp.fxform.view.FXFormNodeWrapper;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.util.Callback;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * Created by usta on 30.08.2015.
 */
public abstract class ChooserFactory implements Callback<Void, FXFormNode> {

    protected final ObjectProperty<String> property = new SimpleObjectProperty<>();
    protected final TextField textField = new TextField();
    protected final Button changeButton = new Button();
    protected final Button browseButton = new Button();
    protected final Tooltip tooltip = new Tooltip();
    protected final String promptText;

    public ChooserFactory(String promptText, Consumer<Path> browseConsumer) {
        this.promptText = promptText;
        setOnBrowse(browseConsumer);
    }

    @Override
    public FXFormNode call(Void param) {
        textField.setPromptText(promptText);
        tooltip.setText(promptText);
        textField.setDisable(false);

        changeButton.setText("Change");
        browseButton.setText("Browse");

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

        textField.setOnMouseEntered(event -> {
            Optional.of(textField).filter(e -> !e.isFocused())
                    .map(TextField::getText)
                    .ifPresent(text -> textField.positionCaret(text.length()));
        });

        textField.setOnMouseExited(event -> {
            if (!textField.isFocused())
                textField.positionCaret(0);
        });

        changeButton.setOnAction(this::chooser);

        HBox hBox = new HBox(5);
        hBox.getChildren().addAll(textField, changeButton, browseButton);
        HBox.setHgrow(textField, Priority.ALWAYS);

        Tooltip.install(textField, tooltip);

        return new FXFormNodeWrapper(hBox, property);
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

    public abstract void chooser(ActionEvent actionEvent);
}

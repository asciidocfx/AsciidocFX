package com.kodcu.config.factory;

import com.dooapp.fxform.view.FXFormNode;
import com.dooapp.fxform.view.FXFormNodeWrapper;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.util.Callback;

import java.nio.file.Path;
import java.util.Objects;

/**
 * Created by usta on 30.08.2015.
 */
public abstract class ChooserFactory implements Callback<Void, FXFormNode> {

    protected final ObjectProperty<Path> property = new SimpleObjectProperty<>();
    protected final TextField textField = new TextField();
    protected final Button button = new Button();
    protected final Tooltip tooltip = new Tooltip();
    protected final String promptText;

    public ChooserFactory(String promptText) {
        this.promptText = promptText;
    }

    @Override
    public FXFormNode call(Void param) {
        textField.setPromptText(promptText);
        tooltip.setText(promptText);
        textField.setDisable(true);

        button.setText("Select");

        property.addListener((observable, oldValue, newValue) -> {
            if (Objects.nonNull(newValue)) {
                textField.setText(newValue.toString());
                tooltip.setText(newValue.toString());
            }
        });

        button.setOnAction(this::chooser);

        HBox hBox = new HBox(5);
        hBox.getChildren().addAll(textField, button);

        Tooltip.install(hBox, tooltip);

        return new FXFormNodeWrapper(hBox, property);
    }

    public abstract void chooser(ActionEvent actionEvent);
}

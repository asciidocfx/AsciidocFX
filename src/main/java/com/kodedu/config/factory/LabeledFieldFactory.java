package com.kodedu.config.factory;

import com.dooapp.fxform.view.FXFormNode;
import com.dooapp.fxform.view.FXFormNodeWrapper;
import javafx.beans.property.StringProperty;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.util.Callback;

public class LabeledFieldFactory implements Callback<Void, FXFormNode> {

    private final Label label;

    public LabeledFieldFactory(String label) {
        this.label = new Label(label);
    }

    public FXFormNode call(Void aVoid) {
        final TextField textField = new TextField();
        StringProperty textProperty = textField.textProperty();
        label.setWrapText(true);
        label.setAlignment(Pos.CENTER);
        VBox box = new VBox(5, label, textField);
        return new FXFormNodeWrapper(box, textProperty);
    }

}

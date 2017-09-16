package com.kodedu.config.factory;

import com.dooapp.fxform.view.FXFormNode;
import com.dooapp.fxform.view.FXFormNodeWrapper;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.HBox;
import javafx.util.Callback;

/**
 * Created by usta on 17.07.2015.
 */
public class SliderFactory implements Callback<Void, FXFormNode> {

    private final Slider slider;

    public SliderFactory(Slider slider) {
        this.slider = slider;
    }

    @Override
    public FXFormNode call(Void param) {
        slider.setShowTickLabels(true);
        slider.setShowTickMarks(true);
        HBox hBox = new HBox();
        Label label = new Label();
        hBox.getChildren().addAll(slider, label);


        slider.valueProperty().addListener((observable, oldValue, newValue) -> {
            label.setText(String.format("%1.1f", newValue));
        });
        return new FXFormNodeWrapper(hBox, slider.valueProperty()) {

        };
    }
}

package com.kodcu.component;

import de.jensd.fx.fontawesome.AwesomeDude;
import de.jensd.fx.fontawesome.AwesomeIcon;
import javafx.event.EventHandler;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;

/**
 * Created by usta on 01.02.2015.
 */
public class LabelBuilt {

    private Label label;

    public LabelBuilt(Label label) {
        this.label = label;
    }

    public static LabelBuilt icon(AwesomeIcon awesomeIcon, String iconSize, double minSize) {
        Label iconLabel = AwesomeDude.createIconLabel(awesomeIcon, iconSize);
        iconLabel.setMinWidth(minSize);
        return new LabelBuilt(iconLabel);
    }

    public LabelBuilt tip(String text) {
        Tooltip tooltip = new Tooltip(text);
        Tooltip.install(label, tooltip);
        return this;
    }

    public LabelBuilt click(EventHandler eventHandler) {
        label.setOnMouseClicked(eventHandler);
        return this;
    }

    public Label build() {
        return label;
    }

    public LabelBuilt clazz(String clazz) {
        label.getStyleClass().add(clazz);
        return this;
    }
}

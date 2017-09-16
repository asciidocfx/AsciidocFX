package com.kodedu.component;


import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.Tooltip;

/**
 * Created by usta on 24.01.2015.
 */
public class ToggleButtonBuilt {

    private ToggleButton toggleButton;

    public ToggleButtonBuilt(ToggleButton toggleButton) {
        this.toggleButton = toggleButton;
    }

    public static ToggleButtonBuilt item(String name) {
        ToggleButton item = new ToggleButton();
        item.setText(name);
        return new ToggleButtonBuilt(item);
    }

    public ToggleButton click(EventHandler<ActionEvent> event) {
        toggleButton.setOnAction(event);
        return toggleButton;
    }

    public ToggleButtonBuilt tip(String tipText) {
        Tooltip tooltip = new Tooltip(tipText);
        Tooltip.install(toggleButton.getGraphic(), tooltip);
        return this;
    }
}

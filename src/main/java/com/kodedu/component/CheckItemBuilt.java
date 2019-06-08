package com.kodedu.component;


import com.kodedu.helper.StyleHelper;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.BooleanProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Tooltip;

/**
 * Created by usta on 24.01.2015.
 */
public class CheckItemBuilt {

    private RadioMenuItem menuItem;

    public CheckItemBuilt(RadioMenuItem menuItem) {
        this.menuItem = menuItem;
    }

    public CheckItemBuilt click(EventHandler<ActionEvent> event) {
        menuItem.setOnAction(event);
        return this;
    }

    public RadioMenuItem build() {
        return menuItem;
    }

    public CheckItemBuilt tip(String tipText) {
        Tooltip tooltip = new Tooltip(tipText);
        Tooltip.install(menuItem.getGraphic(), tooltip);
        return this;
    }

    public CheckItemBuilt clazz(String clazz) {
        StyleHelper.addClass(menuItem, clazz);
        return this;
    }

    public static CheckItemBuilt check(String name, boolean checked) {
        RadioMenuItem item = new RadioMenuItem() {
            @Override
            public boolean equals(Object o) {
                if (this == o) return true;
                if (o == null || getClass() != o.getClass()) return false;

                MenuItem i = (MenuItem) o;

                return !(getText() != null ? !getText().equals(i.getText()) : i.getText() != null);

            }

            @Override
            public int hashCode() {
                return getText() != null ? getText().hashCode() : 0;
            }
        };
        item.setMnemonicParsing(false);
        item.setSelected(checked);
        item.setText(name);
        final CheckItemBuilt checkItemBuilt = new CheckItemBuilt(item);
        return checkItemBuilt;
    }

    public CheckItemBuilt bindBi(BooleanProperty property) {
        menuItem.selectedProperty().bindBidirectional(property);
        return this;
    }

    public CheckItemBuilt bind(BooleanBinding binding) {
        menuItem.selectedProperty().bind(binding);
        return this;
    }

    public CheckItemBuilt visible(BooleanBinding binding) {
        menuItem.visibleProperty().bind(binding);
        return this;
    }

    public CheckItemBuilt group(ToggleGroup toggleGroup) {
        menuItem.setToggleGroup(toggleGroup);
        return this;
    }
}

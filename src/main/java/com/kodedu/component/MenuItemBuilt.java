package com.kodedu.component;


import com.kodedu.helper.StyleHelper;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tooltip;

/**
 * Created by usta on 24.01.2015.
 */
public class MenuItemBuilt {

    private MenuItem menuItem;

    public MenuItemBuilt(MenuItem menuItem) {
        this.menuItem = menuItem;
    }

    public static MenuItemBuilt item(String name) {
        MenuItem item = new MenuItem() {
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
        item.setText(name);
        return new MenuItemBuilt(item);
    }

    public MenuItem click(EventHandler<ActionEvent> event) {
        menuItem.setOnAction(event);
        return menuItem;
    }

    public MenuItemBuilt tip(String tipText) {
        Tooltip tooltip = new Tooltip(tipText);
        Tooltip.install(menuItem.getGraphic(), tooltip);
        return this;
    }

    public MenuItemBuilt clazz(String clazz) {
        StyleHelper.addClass(menuItem, clazz);
        return this;
    }
}

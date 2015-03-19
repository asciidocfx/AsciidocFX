package com.kodcu.component;


import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Label;
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
        MenuItem item = new MenuItem();
        item.setGraphic(new Label(name));
        return new MenuItemBuilt(item);
    }

    public MenuItem click(EventHandler<ActionEvent> event) {
        menuItem.setOnAction(event);
        return menuItem;
    }

    public MenuItemBuilt tip(String tipText) {
        Tooltip tooltip=new Tooltip(tipText);
        Tooltip.install(menuItem.getGraphic(),tooltip);
        return this;
    }
}

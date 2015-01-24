package com.kodcu.component;


import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.MenuItem;

/**
 * Created by usta on 24.01.2015.
 */
public class MenuItemBuilt {

    private MenuItem menuItem;

    public MenuItemBuilt(MenuItem menuItem) {
        this.menuItem = menuItem;
    }

    public static MenuItemBuilt item(String name) {
        return new MenuItemBuilt(new MenuItem(name));
    }

    public MenuItem onclick(EventHandler<ActionEvent> event) {
        menuItem.setOnAction(event);
        return menuItem;
    }
}

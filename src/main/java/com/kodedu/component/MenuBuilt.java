package com.kodedu.component;

import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;

/**
 * Created by usta on 24.01.2015.
 */
public class MenuBuilt {

    private Menu menu;

    public MenuBuilt(Menu menu) {
        this.menu = menu;
    }

    public static MenuBuilt name(String name) {
        return new MenuBuilt(new Menu(name));
    }

    public MenuBuilt add(MenuItem menuItem) {
        this.menu.getItems().add(menuItem);
        return this;
    }

    public Menu build() {
        return this.menu;
    }

}

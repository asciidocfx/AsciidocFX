package com.kodedu.commands;

import com.kodedu.helper.OSHelper;
import javafx.scene.input.KeyCombination;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static java.util.Objects.hash;
import static java.util.Objects.nonNull;

public class EditorCommand {
    private String name;
    private String description;
    private String win;
    private String mac;
    private String shortcut;
    private List<KeyCombination> keyCombination = Collections.emptyList();
    private boolean aNative;

    public boolean hasShortcut() {
        return nonNull(getShortcut());
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getWin() {
        return win;
    }

    public void setWin(String win) {
        clearShortcut();
        this.win = win;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        clearShortcut();
        this.mac = mac;
    }

    private void clearShortcut() {
        shortcut = null;
    }

    public String getShortcut() {

        if (Objects.nonNull(shortcut) && !shortcut.isEmpty()) {
            return shortcut;
        }

        boolean isMac = OSHelper.isMac();
        String shortcut = isMac ? mac : win;
        shortcut = nonNull(shortcut) ? KeyEventHelper.normalizeShortcut(shortcut) : null;

        if (Objects.nonNull(shortcut)) {
            String[] shortcuts = shortcut.split("\\|");
            List<KeyCombination> keyCombinations = KeyEventHelper.parseKeyCombinations(shortcuts);
            setKeyCombination(keyCombinations);
        }

        return this.shortcut = shortcut;
    }

    public void setNative(boolean aNative) {
        this.aNative = aNative;
    }

    public boolean isNative() {
        return aNative;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EditorCommand that = (EditorCommand) o;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return hash(name);
    }

    @Override
    public String toString() {
        return "EditorCommand{" +
                "name='" + name + '\'' +
                ", desc='" + description + '\'' +
                ", win='" + win + '\'' +
                ", mac='" + mac + '\'' +
                ", shortcut='" + shortcut + '\'' +
                '}';
    }

    public void setKeyCombination(List<KeyCombination> keyCombination) {
        this.keyCombination = keyCombination;
    }

    public List<KeyCombination> getKeyCombination() {
        getShortcut(); // Ensure key comb generated
        return keyCombination;
    }

}

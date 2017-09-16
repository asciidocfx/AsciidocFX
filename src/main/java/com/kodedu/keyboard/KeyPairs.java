package com.kodedu.keyboard;

import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;

import java.util.Arrays;
import java.util.Objects;

/**
 * Created by usta on 02.07.2016.
 */
public class KeyPairs {

    private final KeyCode[] keyCodes;
    private KeyCodeCombination combination;

    public KeyPairs(KeyCode... keyCodes) {
        this.keyCodes = keyCodes;
    }

    public KeyPairs(KeyCode keyCode, KeyCombination.Modifier... modifiers) {
        this.keyCodes = new KeyCode[]{keyCode};
        this.combination = new KeyCodeCombination(keyCode, modifiers);
    }

    public KeyPairs(KeyCode keyCode) {
        this.keyCodes = new KeyCode[]{keyCode};
    }

    public boolean match(KeyEvent event) {

        if (Objects.nonNull(combination)) {
            return combination.match(event);
        }

        return Arrays.stream(keyCodes).filter(k -> event.getCode() == k).findAny().isPresent();
    }
}
package com.kodedu.keyboard;

import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import static javafx.scene.input.KeyCombination.SHORTCUT_DOWN;

/**
 * Created by usta on 01.07.2016.
 */
public class KeyHelper {

    private static final KeyPairs UNDEFINED = new KeyPairs(KeyCode.UNDEFINED);
    private static final KeyPairs CONTEXT_MENU = new KeyPairs(KeyCode.CONTEXT_MENU);
    private static final KeyPairs F2 = new KeyPairs(KeyCode.F2);
    private static final KeyPairs COPY = new KeyPairs(KeyCode.C, SHORTCUT_DOWN);
    private static final KeyPairs CONTROL_G = new KeyPairs(KeyCode.G, SHORTCUT_DOWN);
    private static final KeyPairs ESC = new KeyPairs(KeyCode.ESCAPE);
    private static final KeyPairs DELETE = new KeyPairs(KeyCode.DELETE);
    private static final KeyPairs ENTER = new KeyPairs(KeyCode.ENTER);
    private static final KeyPairs UP = new KeyPairs(KeyCode.UP, KeyCode.KP_UP);
    private static final KeyPairs LEFT = new KeyPairs(KeyCode.LEFT, KeyCode.LEFT);
    private static final KeyPairs RIGHT = new KeyPairs(KeyCode.RIGHT, KeyCode.RIGHT);
    private static final KeyPairs DOWN = new KeyPairs(KeyCode.DOWN, KeyCode.KP_DOWN);
    private static final KeyPairs BACK_SPACE = new KeyPairs(KeyCode.BACK_SPACE);
    private static final KeyPairs FILE_LEGALS = new KeyPairs(KeyCode.SPACE, KeyCode.PERIOD, KeyCode.DECIMAL, KeyCode.MINUS, KeyCode.DEAD_TILDE, KeyCode.DOLLAR);

    public static boolean isBackSpace(KeyEvent event) {
        return BACK_SPACE.match(event);
    }

    public static boolean isUp(KeyEvent event) {
        return UP.match(event);
    }

    public static boolean isContextMenu(KeyEvent event) {
        return CONTEXT_MENU.match(event);
    }

    public static boolean isDirection(KeyEvent event) {
        return UP.match(event) || DOWN.match(event) || LEFT.match(event) || RIGHT.match(event);
    }

    public static boolean isDown(KeyEvent event) {
        return DOWN.match(event);
    }

    // TODO: review it
    public static boolean isWord(KeyEvent event) {
        KeyCode keyCode = event.getCode();

        return keyCode.isLetterKey() || keyCode.isDigitKey() || FILE_LEGALS.match(event) || keyCode == KeyCode.UNDEFINED;
    }

    public static boolean isEnter(KeyEvent event) {
        boolean enter = false;

        enter = ENTER.match(event);

        if (!enter) {
            if (isUndefined(event)) {
                String character = event.getCharacter();
                if (character.matches("\\R")) {
                    enter = true;
                }
            }
        }

        return enter;
    }

    public static boolean isUndefined(KeyEvent event) {
        return UNDEFINED.match(event);
    }

    public static boolean isDelete(KeyEvent event) {
        return DELETE.match(event);
    }

    // TODO: review it
    public static boolean isAnyDown(KeyEvent event) {
        return event.isShortcutDown() || event.isShiftDown() || event.isControlDown() || event.isAltDown();
    }

    public static boolean isCopy(KeyEvent event) {
        return COPY.match(event) || event.getCode() == KeyCode.COPY;
    }

    public static boolean isF2(KeyEvent event) {
        return F2.match(event);
    }

    public static boolean isControlG( KeyEvent event) {
        return CONTROL_G.match(event);
    }

    public static boolean isEsc(KeyEvent event) {
        return ESC.match(event);
    }
}

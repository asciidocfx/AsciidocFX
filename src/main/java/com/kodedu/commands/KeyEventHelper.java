package com.kodedu.commands;

import com.kodedu.component.EditorPane;
import com.kodedu.helper.OSHelper;
import javafx.event.EventType;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class KeyEventHelper {

    private static final Logger logger = LoggerFactory.getLogger(KeyEventHelper.class);

    private static String KEYCODE_DELIMETER = "(?<!-)-";

    public static String toString(KeyEvent e) {
        EventType<KeyEvent> eventType = e.getEventType();
        String text = e.getText();
        String character = e.getCharacter();
        String name = e.getCode().getName();
        String aChar = e.getCode().getChar();
        return String.format("Type='%s' Text='%s'    Character='%s'  Name='%s'   Char='%s'", eventType, text, character, name, aChar);
    }


    private static String getShortcutSubstitution(String shortcut) {
        return switch (shortcut) {
            case "Option" -> "Alt";
            default -> shortcut;
        };
    }

    public static String normalizeShortcut(String shortcut) {
        if (Objects.isNull(shortcut)) {
            return null;
        }
        return Arrays.stream(shortcut.split(KEYCODE_DELIMETER)).map(s -> getShortcutSubstitution(s)).collect(Collectors.joining("+"));
    }

    public static List<KeyCombination> parseKeyCombinations(String[] shortcuts) {
        boolean isMac = OSHelper.isMac();
        return Arrays.stream(shortcuts).map(s -> {
            if (isMac) {
                s = StringUtils.replaceIgnoreCase(s, "Command", "Meta");
                s = StringUtils.replaceIgnoreCase(s, "Cmd", "Meta");
                s = StringUtils.replaceIgnoreCase(s, "Option", "Alt");
                s = normalizeShortcut(s);
            }
            try {
                KeyCombination keyCombination = KeyCodeCombination.keyCombination(s);
                return keyCombination;
            } catch (Exception ee) {
                if (!s.contains("Input+")) {
                    logger.error("Skipped key code combination for {}", s, ee);
                }
                return KeyCodeCombination.NO_MATCH;
            }
        }).collect(Collectors.toList());
    }
}

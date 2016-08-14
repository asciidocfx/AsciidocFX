package com.kodcu.config;

/**
 * Created by usta on 22.06.2016.
 */
public enum FoldStyle {

    DEFAULT,
    MANUAL,
    MARKBEGIN,
    MARKBEGINEND;

    public static boolean contains(String test) {

        for (FoldStyle c : FoldStyle.values()) {
            if (c.name().equals(test)) {
                return true;
            }
        }

        return false;
    }
}

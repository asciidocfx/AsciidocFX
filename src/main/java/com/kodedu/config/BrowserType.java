package com.kodedu.config;

public enum BrowserType {

    CHROME,
    FIREFOX,
    EDGE,
    SAFARI,
    BRAVE,
    OPERA,
    DEFAULT;

    public static boolean contains(String test) {

        for (BrowserType c : BrowserType.values()) {
            if (c.name().equals(test)) {
                return true;
            }
        }

        return false;
    }
}

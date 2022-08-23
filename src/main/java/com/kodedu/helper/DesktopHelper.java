package com.kodedu.helper;

import java.awt.*;
import java.util.Objects;
import java.util.Optional;

public class DesktopHelper {

    private static Optional<Desktop> desktop = null;

    public static Optional<Desktop> getDesktop() {
        if (Objects.nonNull(desktop)) {
            return desktop;
        }
        if (Desktop.isDesktopSupported()) {
            return desktop = Optional.ofNullable(Desktop.getDesktop());
        }
        return desktop = Optional.empty();
    }
}

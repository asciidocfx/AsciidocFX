package com.kodedu.helper;

import java.awt.*;
import java.util.Objects;
import java.util.Optional;

public class TaskbarHelper {

    private static Optional<Taskbar> taskbar = null;

    public static Optional<Taskbar> getTaskBar() {
        if (Objects.nonNull(taskbar)) {
            return taskbar;
        }
        if (Taskbar.isTaskbarSupported()) {
            Taskbar tBar = Taskbar.getTaskbar();
            return taskbar = Optional.ofNullable(tBar);
        }
        return taskbar = Optional.empty();
    }
}

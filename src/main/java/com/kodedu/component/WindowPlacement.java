package com.kodedu.component;

import javafx.geometry.Rectangle2D;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.stage.Stage;

/**
 * Restore or initialize a JavaFX {@link Stage}'s position and size.
 * <p>
 * This class only depends on JavaFX. Copy it into another app as-is. Publishing
 * it to Maven Central is a separate process (owned {@code groupId}, GPG signing,
 * Central Portal) and is not done from this repository.
 */
public final class WindowPlacement {

    /**
     * First-run size relative to the display work area. {@code π/4 ≈ 0.785}
     * (about 78%). {@code 1/e ≈ 0.368} is a different constant.
     */
    public static final double FIRST_RUN_FRACTION = Math.PI / 4.0;

    static final double MIN_WIDTH = 400;
    static final double MIN_HEIGHT = 300;
    static final double FILL_SCREEN_FRACTION = 0.92;

    public record Geometry(double x, double y, double width, double height, boolean maximized) {
    }

    private WindowPlacement() {
    }

    /**
     * Apply last-known windowed bounds, or a first-run size, then the maximize
     * flag. Call before {@code show()}.
     */
    public static void apply(Stage stage, Double savedX, Double savedY,
                             Double savedWidth, Double savedHeight, boolean maximized) {
        Geometry geometry = resolve(savedX, savedY, savedWidth, savedHeight, maximized);
        stage.setWidth(geometry.width());
        stage.setHeight(geometry.height());
        stage.setX(geometry.x());
        stage.setY(geometry.y());
        stage.setMaximized(geometry.maximized());
    }

    /**
     * Some stage styles ignore pre-show size and come up filling the monitor.
     * If that happened without an explicit maximize, restore first-run size.
     */
    public static void ensureWindowedAfterShow(Stage stage) {
        if (stage.isMaximized()) {
            return;
        }
        Screen screen = screenFor(stage.getX(), stage.getY(), stage.getWidth(), stage.getHeight());
        Rectangle2D visual = screen.getVisualBounds();
        if (fillsWorkArea(stage.getWidth(), stage.getHeight(), visual)) {
            applyGeometry(stage, firstRun(visual, false));
        }
    }

    /**
     * Keep the window's top-left on some attached screen's work area.
     *
     * @return {@code true} if the stage was moved or resized
     */
    public static boolean ensureOnScreen(Stage stage) {
        Geometry before = new Geometry(stage.getX(), stage.getY(), stage.getWidth(), stage.getHeight(),
                stage.isMaximized());
        Screen screen = screenFor(before.x(), before.y(), before.width(), before.height());
        Geometry after = clamp(before, screen.getVisualBounds());
        if (after.x() == before.x() && after.y() == before.y()
                && after.width() == before.width() && after.height() == before.height()) {
            return false;
        }
        applyGeometry(stage, after);
        return true;
    }

    public static Geometry resolve(Double savedX, Double savedY,
                                   Double savedWidth, Double savedHeight, boolean maximized) {
        boolean hasSaved = usableSize(savedWidth, savedHeight);
        Screen screen = hasSaved
                ? screenFor(savedX, savedY, savedWidth, savedHeight)
                : Screen.getPrimary();
        Rectangle2D visual = screen.getVisualBounds();

        if (!hasSaved || (fillsWorkArea(savedWidth, savedHeight, visual) && !maximized)) {
            return firstRun(visual, maximized);
        }

        Geometry saved = new Geometry(
                savedX != null ? savedX : visual.getMinX(),
                savedY != null ? savedY : visual.getMinY(),
                savedWidth,
                savedHeight,
                maximized);
        return clamp(saved, visual);
    }

    public static Geometry firstRun(Rectangle2D visual, boolean maximized) {
        double width = fit(visual.getWidth() * FIRST_RUN_FRACTION, MIN_WIDTH, visual.getWidth());
        double height = fit(visual.getHeight() * FIRST_RUN_FRACTION, MIN_HEIGHT, visual.getHeight());
        double x = visual.getMinX() + (visual.getWidth() - width) / 2.0;
        double y = visual.getMinY() + (visual.getHeight() - height) / 2.0;
        return new Geometry(x, y, width, height, maximized);
    }

    public static Geometry clamp(Geometry geometry, Rectangle2D visual) {
        double width = fit(geometry.width(), MIN_WIDTH, visual.getWidth());
        double height = fit(geometry.height(), MIN_HEIGHT, visual.getHeight());
        double minX = visual.getMinX();
        double minY = visual.getMinY();
        double maxX = minX + visual.getWidth() - width;
        double maxY = minY + visual.getHeight() - height;
        double x = geometry.x();
        double y = geometry.y();
        if (maxX >= minX) {
            x = Math.min(Math.max(x, minX), maxX);
        } else {
            x = minX;
        }
        if (maxY >= minY) {
            y = Math.min(Math.max(y, minY), maxY);
        } else {
            y = minY;
        }
        return new Geometry(x, y, width, height, geometry.maximized());
    }

    private static void applyGeometry(Stage stage, Geometry geometry) {
        stage.setMaximized(false);
        stage.setWidth(geometry.width());
        stage.setHeight(geometry.height());
        stage.setX(geometry.x());
        stage.setY(geometry.y());
        stage.setMaximized(geometry.maximized());
    }

    private static boolean usableSize(Double width, Double height) {
        return width != null && height != null && width > 200 && height > 150;
    }

    private static boolean fillsWorkArea(double width, double height, Rectangle2D visual) {
        return width >= visual.getWidth() * FILL_SCREEN_FRACTION
                && height >= visual.getHeight() * FILL_SCREEN_FRACTION;
    }

    private static double fit(double value, double min, double max) {
        if (max < min) {
            return max;
        }
        return Math.min(Math.max(value, min), max);
    }

    private static Screen screenFor(Double x, Double y, Double width, Double height) {
        double px = x != null ? x : 0;
        double py = y != null ? y : 0;
        double pw = width != null && width > 0 ? width : 1;
        double ph = height != null && height > 0 ? height : 1;
        var hits = Screen.getScreensForRectangle(px, py, pw, ph);
        if (!hits.isEmpty()) {
            return hits.get(0);
        }
        var origin = Screen.getScreensForRectangle(px, py, 1, 1);
        if (!origin.isEmpty()) {
            return origin.get(0);
        }
        return Screen.getPrimary();
    }

    public static Color defaultSceneFill(boolean dark) {
        return Color.web(dark ? "#191A1B" : "#F3F3F3");
    }
}

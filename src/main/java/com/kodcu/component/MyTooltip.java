package com.kodcu.component;

import com.sun.javafx.css.StyleManager;
import com.sun.javafx.css.converters.BooleanConverter;
import com.sun.javafx.css.converters.EnumConverter;
import com.sun.javafx.css.converters.SizeConverter;
import com.sun.javafx.css.converters.StringConverter;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.value.WritableValue;
import javafx.css.*;
import javafx.event.EventHandler;
import javafx.geometry.NodeOrientation;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.OverrunStyle;
import javafx.scene.control.PopupControl;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.stage.Window;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * In order to decrease the delay of tooltips
 * we need to manipulate the class Tooltip
 * along its private members
 *
 * <br/>
 * Created by hakan on 25.01.2015.
 */
public class MyTooltip extends Tooltip {

    private static String TOOLTIP_PROP_KEY = "com.kodcu.component.MyTooltip";
    private static int TOOLTIP_XOFFSET = 10;
    private static int TOOLTIP_YOFFSET = 7;

    private static TooltipBehavior BEHAVIOR = new TooltipBehavior(
            new Duration(250), new Duration(3000), new Duration(200), false);

    public static void install(Node node, Tooltip t) {
        BEHAVIOR.install(node, t);
    }

    public static void uninstall(Node node, Tooltip t) {
        BEHAVIOR.uninstall(node);
    }

    public MyTooltip(String text) {
        super();
        if (text != null) setText(text);
        bridge = new CSSBridge();
        getContent().setAll(bridge);
        getStyleClass().setAll("tooltip");
    }

    private final ReadOnlyBooleanWrapper activated = new ReadOnlyBooleanWrapper(this, "activated");

    final void setActivated(boolean value) {
        activated.set(value);
    }

    private final ObjectProperty<Node> graphic = new StyleableObjectProperty<Node>() {
        @Override
        public CssMetaData getCssMetaData() {
            return GRAPHIC;
        }

        @Override
        public Object getBean() {
            return MyTooltip.this;
        }

        @Override
        public String getName() {
            return "graphic";
        }
    };

    private StyleableStringProperty imageUrl = null;

    private StyleableStringProperty imageUrlProperty() {
        if (imageUrl == null) {
            imageUrl = new StyleableStringProperty() {
                StyleOrigin origin = StyleOrigin.USER;

                @Override
                public void applyStyle(StyleOrigin origin, String v) {

                    this.origin = origin;

                    // Don't want applyStyle to throw an exception which would leave this.origin set to the wrong value
                    if (graphic == null || !graphic.isBound()) super.applyStyle(origin, v);

                    // Origin is only valid for this invocation of applyStyle, so reset it to USER in case someone calls set.
                    this.origin = StyleOrigin.USER;
                }

                @Override
                protected void invalidated() {

                    // need to call super.get() here since get() is overridden to return the graphicProperty's value
                    final String url = super.get();

                    if (url == null) {
                        ((StyleableProperty<Node>) (WritableValue<Node>) graphicProperty()).applyStyle(origin, null);
                    } else {
                        // RT-34466 - if graphic's url is the same as this property's value, then don't overwrite.
                        final Node graphicNode = MyTooltip.this.getGraphic();
                        if (graphicNode instanceof ImageView) {
                            final ImageView imageView = (ImageView) graphicNode;
                            final Image image = imageView.getImage();
                            if (image != null) {
                                final String imageViewUrl = image.impl_getUrl();
                                if (url.equals(imageViewUrl)) return;
                            }
                        }
                        final Image img = StyleManager.getInstance().getCachedImage(url);
                        if (img != null) {
                            ((StyleableProperty<Node>) (WritableValue<Node>) graphicProperty()).applyStyle(origin, new ImageView(img));
                        }
                    }
                }

                @Override
                public String get() {
                    final Node graphic = getGraphic();
                    if (graphic instanceof ImageView) {
                        final Image image = ((ImageView) graphic).getImage();
                        if (image != null) {
                            return image.impl_getUrl();
                        }
                    }
                    return null;
                }

                @Override
                public StyleOrigin getStyleOrigin() {
                    return graphic != null ? ((StyleableProperty<Node>) (WritableValue<Node>) graphic).getStyleOrigin() : null;
                }

                @Override
                public Object getBean() {
                    return MyTooltip.this;
                }

                @Override
                public String getName() {
                    return "imageUrl";
                }

                @Override
                public CssMetaData<MyTooltip.CSSBridge, String> getCssMetaData() {
                    return GRAPHIC;
                }
            };
        }
        return imageUrl;
    }

    private static final CssMetaData<MyTooltip.CSSBridge, Font> FONT =
            new FontCssMetaData<MyTooltip.CSSBridge>("-fx-font", Font.getDefault()) {

                @Override
                public boolean isSettable(MyTooltip.CSSBridge cssBridge) {
                    return !cssBridge.tooltip.fontProperty().isBound();
                }

                @Override
                public StyleableProperty<Font> getStyleableProperty(MyTooltip.CSSBridge cssBridge) {
                    return (StyleableProperty<Font>) (WritableValue<Font>) cssBridge.tooltip.fontProperty();
                }
            };

    private static final CssMetaData<MyTooltip.CSSBridge, TextAlignment> TEXT_ALIGNMENT =
            new CssMetaData<MyTooltip.CSSBridge, TextAlignment>("-fx-text-alignment",
                    new EnumConverter<>(TextAlignment.class),
                    TextAlignment.LEFT) {

                @Override
                public boolean isSettable(MyTooltip.CSSBridge cssBridge) {
                    return !cssBridge.tooltip.textAlignmentProperty().isBound();
                }

                @Override
                public StyleableProperty<TextAlignment> getStyleableProperty(MyTooltip.CSSBridge cssBridge) {
                    return (StyleableProperty<TextAlignment>) (WritableValue<TextAlignment>) cssBridge.tooltip.textAlignmentProperty();
                }
            };

    private static final CssMetaData<MyTooltip.CSSBridge, OverrunStyle> TEXT_OVERRUN =
            new CssMetaData<MyTooltip.CSSBridge, OverrunStyle>("-fx-text-overrun",
                    new EnumConverter<>(OverrunStyle.class),
                    OverrunStyle.ELLIPSIS) {

                @Override
                public boolean isSettable(MyTooltip.CSSBridge cssBridge) {
                    return !cssBridge.tooltip.textOverrunProperty().isBound();
                }

                @Override
                public StyleableProperty<OverrunStyle> getStyleableProperty(MyTooltip.CSSBridge cssBridge) {
                    return (StyleableProperty<OverrunStyle>) (WritableValue<OverrunStyle>) cssBridge.tooltip.textOverrunProperty();
                }
            };

    private static final CssMetaData<MyTooltip.CSSBridge, Boolean> WRAP_TEXT =
            new CssMetaData<MyTooltip.CSSBridge, Boolean>("-fx-wrap-text",
                    BooleanConverter.getInstance(), Boolean.FALSE) {

                @Override
                public boolean isSettable(MyTooltip.CSSBridge cssBridge) {
                    return !cssBridge.tooltip.wrapTextProperty().isBound();
                }

                @Override
                public StyleableProperty<Boolean> getStyleableProperty(MyTooltip.CSSBridge cssBridge) {
                    return (StyleableProperty<Boolean>) (WritableValue<Boolean>) cssBridge.tooltip.wrapTextProperty();
                }
            };

    private static final CssMetaData<MyTooltip.CSSBridge, ContentDisplay> CONTENT_DISPLAY =
            new CssMetaData<MyTooltip.CSSBridge, ContentDisplay>("-fx-content-display",
                    new EnumConverter<>(ContentDisplay.class),
                    ContentDisplay.LEFT) {

                @Override
                public boolean isSettable(MyTooltip.CSSBridge cssBridge) {
                    return !cssBridge.tooltip.contentDisplayProperty().isBound();
                }

                @Override
                public StyleableProperty<ContentDisplay> getStyleableProperty(MyTooltip.CSSBridge cssBridge) {
                    return (StyleableProperty<ContentDisplay>) (WritableValue<ContentDisplay>) cssBridge.tooltip.contentDisplayProperty();
                }
            };

    private static final CssMetaData<MyTooltip.CSSBridge, Number> GRAPHIC_TEXT_GAP =
            new CssMetaData<MyTooltip.CSSBridge, Number>("-fx-graphic-text-gap",
                    SizeConverter.getInstance(), 4.0) {

                @Override
                public boolean isSettable(MyTooltip.CSSBridge cssBridge) {
                    return !cssBridge.tooltip.graphicTextGapProperty().isBound();
                }

                @Override
                public StyleableProperty<Number> getStyleableProperty(MyTooltip.CSSBridge cssBridge) {
                    return (StyleableProperty<Number>) (WritableValue<Number>) cssBridge.tooltip.graphicTextGapProperty();
                }
            };

    private static final CssMetaData<MyTooltip.CSSBridge, String> GRAPHIC =
            new CssMetaData<MyTooltip.CSSBridge, String>("-fx-graphic",
                    StringConverter.getInstance()) {

                @Override
                public boolean isSettable(MyTooltip.CSSBridge cssBridge) {
                    return !cssBridge.tooltip.graphicProperty().isBound();
                }

                @Override
                public StyleableProperty<String> getStyleableProperty(MyTooltip.CSSBridge cssBridge) {
                    return (StyleableProperty<String>) cssBridge.tooltip.imageUrlProperty();
                }
            };

    private static final List<CssMetaData<? extends Styleable, ?>> STYLEABLES;

    static {
        final List<CssMetaData<? extends Styleable, ?>> styleables =
                new ArrayList<>(PopupControl.getClassCssMetaData());
        styleables.add(FONT);
        styleables.add(TEXT_ALIGNMENT);
        styleables.add(TEXT_OVERRUN);
        styleables.add(WRAP_TEXT);
        styleables.add(GRAPHIC);
        styleables.add(CONTENT_DISPLAY);
        styleables.add(GRAPHIC_TEXT_GAP);
        STYLEABLES = Collections.unmodifiableList(styleables);
    }

    public static List<CssMetaData<? extends Styleable, ?>> getClassCssMetaData() {
        return STYLEABLES;
    }

    @Override
    public List<CssMetaData<? extends Styleable, ?>> getCssMetaData() {
        return getClassCssMetaData();
    }

    @Override
    public Styleable getStyleableParent() {
        return BEHAVIOR.hoveredNode;
    }

    private final class CSSBridge extends PopupControl.CSSBridge {
        private MyTooltip tooltip = MyTooltip.this;
    }

    private static class TooltipBehavior {

        private Timeline activationTimer = new Timeline();
        private Timeline hideTimer = new Timeline();
        private Timeline leftTimer = new Timeline();

        /**
         * The Node with a tooltip over which the mouse is hovering. There can
         * only be one of these at a time.
         */
        private Node hoveredNode;

        /**
         * The tooltip that is currently activated. There can only be one
         * of these at a time.
         */
        private MyTooltip activatedTooltip;

        /**
         * The tooltip that is currently visible. There can only be one
         * of these at a time.
         */
        private MyTooltip visibleTooltip;

        /**
         * The last position of the mouse, in screen coordinates.
         */
        private double lastMouseX;
        private double lastMouseY;

        private boolean hideOnExit;

        TooltipBehavior(Duration openDelay, Duration visibleDuration, Duration closeDelay, final boolean hideOnExit) {
            this.hideOnExit = hideOnExit;

            activationTimer.getKeyFrames().add(new KeyFrame(openDelay));
            activationTimer.setOnFinished(event -> {
                assert activatedTooltip != null;
                final Window owner = getWindow(hoveredNode);
                final boolean treeVisible = isWindowHierarchyVisible(hoveredNode);
                if (owner != null && owner.isShowing() && treeVisible) {
                    double x = lastMouseX;
                    double y = lastMouseY;

                    NodeOrientation nodeOrientation = hoveredNode.getEffectiveNodeOrientation();
                    activatedTooltip.getScene().setNodeOrientation(nodeOrientation);
                    if (nodeOrientation == NodeOrientation.RIGHT_TO_LEFT) {
                        x -= activatedTooltip.getWidth();
                    }

                    activatedTooltip.show(owner, x + TOOLTIP_XOFFSET, y + TOOLTIP_YOFFSET);

                    if ((y + TOOLTIP_YOFFSET) > activatedTooltip.getAnchorY()) {
                        activatedTooltip.hide();

                        y -= activatedTooltip.getHeight();
                        activatedTooltip.show(owner, x + TOOLTIP_XOFFSET, y);
                    }

                    visibleTooltip = activatedTooltip;
                    hoveredNode = null;
                    hideTimer.playFromStart();
                }

                activatedTooltip.setActivated(false);
                activatedTooltip = null;
            });

            hideTimer.getKeyFrames().add(new KeyFrame(visibleDuration));
            hideTimer.setOnFinished(event -> {
                // Hide the currently visible tooltip.
                assert visibleTooltip != null;
                visibleTooltip.hide();
                visibleTooltip = null;
                hoveredNode = null;
            });

            leftTimer.getKeyFrames().add(new KeyFrame(closeDelay));
            leftTimer.setOnFinished(event -> {
                if (!hideOnExit) {
                    // Hide the currently visible tooltip.
                    assert visibleTooltip != null;
                    visibleTooltip.hide();
                    visibleTooltip = null;
                    hoveredNode = null;
                }
            });
        }

        private EventHandler<MouseEvent> MOVE_HANDLER = (MouseEvent event) -> {
            //Screen coordinates need to be actual for dynamic tooltip.
            //See Tooltip.setText

            lastMouseX = event.getScreenX();
            lastMouseY = event.getScreenY();

            // If the HIDE_TIMER is running, then we don't want this event
            // handler to do anything, or change any state at all.
            if (hideTimer.getStatus() == Timeline.Status.RUNNING) {
                return;
            }

            hoveredNode = (Node) event.getSource();
            MyTooltip t = (MyTooltip) hoveredNode.getProperties().get(TOOLTIP_PROP_KEY);
            if (t != null) {
                // In theory we should never get here with an invisible or
                // non-existant window hierarchy, but might in some cases where
                // people are feeding fake mouse events into the hierarchy. So
                // we'll guard against that case.
                final Window owner = getWindow(hoveredNode);
                final boolean treeVisible = isWindowHierarchyVisible(hoveredNode);
                if (owner != null && treeVisible) {
                    // Now we know that the currently HOVERED node has a tooltip
                    // and that it is part of a visible window Hierarchy.
                    // If LEFT_TIMER is running, then we make this tooltip
                    // visible immediately, stop the LEFT_TIMER, and start the
                    // HIDE_TIMER.
                    if (leftTimer.getStatus() == Timeline.Status.RUNNING) {
                        if (visibleTooltip != null) visibleTooltip.hide();
                        visibleTooltip = t;
                        t.show(owner, event.getScreenX() + TOOLTIP_XOFFSET,
                                event.getScreenY() + TOOLTIP_YOFFSET);
                        leftTimer.stop();
                        hideTimer.playFromStart();
                    } else {
                        // Start / restart the timer and make sure the tooltip
                        // is marked as activated.
                        t.setActivated(true);
                        activatedTooltip = t;
                        activationTimer.stop();
                        activationTimer.playFromStart();
                    }
                }
            }
        };

        private EventHandler<MouseEvent> LEAVING_HANDLER = (MouseEvent event) -> {
            // detect bogus mouse exit events, if it didn't really move then ignore it
            if (activationTimer.getStatus() == Timeline.Status.RUNNING) {
                activationTimer.stop();
            } else if (hideTimer.getStatus() == Timeline.Status.RUNNING) {
                assert visibleTooltip != null;
                hideTimer.stop();
                if (hideOnExit) visibleTooltip.hide();
                leftTimer.playFromStart();
            }

            hoveredNode = null;
            activatedTooltip = null;
            if (hideOnExit) visibleTooltip = null;
        };

        private EventHandler<MouseEvent> KILL_HANDLER = (MouseEvent event) -> {
            activationTimer.stop();
            hideTimer.stop();
            leftTimer.stop();
            if (visibleTooltip != null) visibleTooltip.hide();
            hoveredNode = null;
            activatedTooltip = null;
            visibleTooltip = null;
        };

        private void install(Node node, Tooltip t) {
            if (node == null) return;
            node.addEventHandler(MouseEvent.MOUSE_MOVED, MOVE_HANDLER);
            node.addEventHandler(MouseEvent.MOUSE_EXITED, LEAVING_HANDLER);
            node.addEventHandler(MouseEvent.MOUSE_PRESSED, KILL_HANDLER);
            node.getProperties().put(TOOLTIP_PROP_KEY, t);
        }

        private void uninstall(Node node) {
            if (node == null) return;
            node.removeEventHandler(MouseEvent.MOUSE_MOVED, MOVE_HANDLER);
            node.removeEventHandler(MouseEvent.MOUSE_EXITED, LEAVING_HANDLER);
            node.removeEventHandler(MouseEvent.MOUSE_PRESSED, KILL_HANDLER);
            Tooltip t = (Tooltip) node.getProperties().get(TOOLTIP_PROP_KEY);
            if (t != null) {
                node.getProperties().remove(TOOLTIP_PROP_KEY);
                if (t.equals(visibleTooltip) || t.equals(activatedTooltip)) {
                    KILL_HANDLER.handle(null);
                }
            }
        }

        private Window getWindow(final Node node) {
            final Scene scene = node == null ? null : node.getScene();
            return scene == null ? null : scene.getWindow();
        }

        private boolean isWindowHierarchyVisible(Node node) {
            boolean treeVisible = node != null;
            Parent parent = node == null ? null : node.getParent();
            while (parent != null && treeVisible) {
                treeVisible = parent.isVisible();
                parent = parent.getParent();
            }
            return treeVisible;
        }
    }
}

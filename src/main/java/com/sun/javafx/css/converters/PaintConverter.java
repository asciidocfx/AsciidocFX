package com.sun.javafx.css.converters;

import javafx.css.ParsedValue;
import javafx.css.Size;
import javafx.css.SizeUnits;
import javafx.css.StyleConverter;
import javafx.scene.image.Image;
import javafx.scene.paint.*;
import javafx.scene.text.Font;

/**
 * Created by usta on 27.02.2017.
 */
public class PaintConverter extends StyleConverter<ParsedValue<?, Paint>, Paint> {

    // lazy, thread-safe instantiation
    private static class Holder {
        static final PaintConverter INSTANCE = new PaintConverter();
        static final PaintConverter.SequenceConverter SEQUENCE_INSTANCE = new PaintConverter.SequenceConverter();
        static final PaintConverter.LinearGradientConverter LINEAR_GRADIENT_INSTANCE = new PaintConverter.LinearGradientConverter();
        static final PaintConverter.ImagePatternConverter IMAGE_PATTERN_INSTANCE = new PaintConverter.ImagePatternConverter();
        static final PaintConverter.RepeatingImagePatternConverter REPEATING_IMAGE_PATTERN_INSTANCE = new PaintConverter.RepeatingImagePatternConverter();
        static final PaintConverter.RadialGradientConverter RADIAL_GRADIENT_INSTANCE = new PaintConverter.RadialGradientConverter();
    }

    public static StyleConverter<ParsedValue<?, Paint>, Paint> getInstance() {
        return PaintConverter.Holder.INSTANCE;
    }

    private PaintConverter() {
        super();
    }

    @Override
    public Paint convert(ParsedValue<ParsedValue<?, Paint>, Paint> value, Font font) {
        Object obj = value.getValue();
        if (obj instanceof Paint) {
            return (Paint) obj;
        }
        return value.getValue().convert(font);
    }

    @Override
    public String toString() {
        return "PaintConverter";
    }

    /**
     * Convert [<paint]+ to Paint[]
     */
    public static final class SequenceConverter extends StyleConverter<ParsedValue<?, Paint>[], Paint[]> {

        public static PaintConverter.SequenceConverter getInstance() {
            return PaintConverter.Holder.SEQUENCE_INSTANCE;
        }

        private SequenceConverter() {
            super();
        }

        @Override
        public Paint[] convert(ParsedValue<ParsedValue<?, Paint>[], Paint[]> value, Font font) {
            ParsedValue<?, Paint>[] values = value.getValue();
            Paint[] paints = new Paint[values.length];
            for (int p = 0; p < values.length; p++) {
                paints[p] = values[p].convert(font);
            }
            return paints;
        }

        @Override
        public String toString() {
            return "Paint.SequenceConverter";
        }
    }

    public static final class LinearGradientConverter extends StyleConverter<ParsedValue[], Paint> {

        public static PaintConverter.LinearGradientConverter getInstance() {
            return Holder.LINEAR_GRADIENT_INSTANCE;
        }

        private LinearGradientConverter() {
            super();
        }

        @Override
        public Paint convert(ParsedValue<ParsedValue[], Paint> value, Font font) {

            Paint paint = super.getCachedValue(value);
            if (paint != null) return paint;

            ParsedValue[] values = value.getValue();
            int v = 0;
            final Size startX = (Size) values[v++].convert(font);
            final Size startY = (Size) values[v++].convert(font);
            final Size endX = (Size) values[v++].convert(font);
            final Size endY = (Size) values[v++].convert(font);
            boolean proportional = startX.getUnits() == SizeUnits.PERCENT && startX.getUnits() == startY.getUnits() && startX.getUnits() == endX.getUnits() && startX.getUnits() == endY.getUnits();
            final CycleMethod cycleMethod = (CycleMethod) values[v++].convert(font);
            final Stop[] stops = new Stop[values.length - v];
            for (int s = v; s < values.length; s++) {
                stops[s - v] = (Stop) values[s].convert(font);
            }
            paint = new LinearGradient(startX.pixels(font), startY.pixels(font), endX.pixels(font), endY.pixels(font), proportional, cycleMethod, stops);

            super.cacheValue(value, paint);
            return paint;
        }

        @Override
        public String toString() {
            return "LinearGradientConverter";
        }
    }

    public static final class ImagePatternConverter extends StyleConverter<ParsedValue[], Paint> {

        public static PaintConverter.ImagePatternConverter getInstance() {
            return PaintConverter.Holder.IMAGE_PATTERN_INSTANCE;
        }

        private ImagePatternConverter() {
            super();
        }

        @Override
        public Paint convert(ParsedValue<ParsedValue[], Paint> value, Font font) {

            Paint paint = super.getCachedValue(value);
            if (paint != null) return paint;

            ParsedValue[] values = value.getValue();
            ParsedValue<?, ?> urlParsedValue = values[0];
            String url = (String) urlParsedValue.convert(font);
            if (values.length == 1) {
                return new ImagePattern(new Image(url));
            }

            Size x = (Size) values[1].convert(font);
            Size y = (Size) values[2].convert(font);
            Size w = (Size) values[3].convert(font);
            Size h = (Size) values[4].convert(font);
            boolean p = values.length < 6 ? true : (Boolean) values[5].getValue();

            paint = new ImagePattern(
                    new Image(url),
                    x.getValue(),
                    y.getValue(),
                    w.getValue(),
                    h.getValue(), p);

            super.cacheValue(value, paint);
            return paint;
        }

        @Override
        public String toString() {
            return "ImagePatternConverter";
        }
    }

    public static final class RepeatingImagePatternConverter extends StyleConverter<ParsedValue[], Paint> {

        public static PaintConverter.RepeatingImagePatternConverter getInstance() {
            return PaintConverter.Holder.REPEATING_IMAGE_PATTERN_INSTANCE;
        }

        private RepeatingImagePatternConverter() {
            super();
        }

        @Override
        public Paint convert(ParsedValue<ParsedValue[], Paint> value, Font font) {

            Paint paint = super.getCachedValue(value);
            if (paint != null) return paint;

            ParsedValue[] values = value.getValue();
            ParsedValue<?, ?> url = values[0];
            String u = (String) url.convert(font);
            // If u is null, then we failed to locate the image associated with the url specified in the CSS file.
            if (u == null) return null;
            final Image image = new Image(u);
            paint = new ImagePattern(image, 0, 0, image.getWidth(), image.getHeight(), false);

            super.cacheValue(value, paint);
            return paint;
        }

        @Override
        public String toString() {
            return "RepeatingImagePatternConverter";
        }
    }

    public static final class RadialGradientConverter extends StyleConverter<ParsedValue[], Paint> {

        public static PaintConverter.RadialGradientConverter getInstance() {
            return PaintConverter.Holder.RADIAL_GRADIENT_INSTANCE;
        }

        private RadialGradientConverter() {
            super();
        }

        @Override
        public Paint convert(ParsedValue<ParsedValue[], Paint> value, Font font) {

            Paint paint = super.getCachedValue(value);
            if (paint != null) return paint;

            final ParsedValue[] values = value.getValue();
            int v = 0;
            // First four values are for startX, startY, endX, endY
            // and are type ParsedValue<Value<?,Size>,Double>. To figure out
            // proportional, we need to get to the Size. getValue() will
            // return ParsedValue<?,Size>, so getValue().convert(font) will
            // give us the size.
            final Size focusAngle = values[v++] != null ? (Size) values[v - 1].convert(font) : null;
            final Size focusDistance = values[v++] != null ? (Size) values[v - 1].convert(font) : null;
            final Size centerX = values[v++] != null ? (Size) values[v - 1].convert(font) : null;
            final Size centerY = values[v++] != null ? (Size) values[v - 1].convert(font) : null;
            final Size radius = (Size) values[v++].convert(font);
            boolean proportional = radius.getUnits().equals(SizeUnits.PERCENT);
            boolean unitsAgree = centerX != null ? proportional == centerX.getUnits().equals(SizeUnits.PERCENT) : true;
            unitsAgree = unitsAgree && centerY != null ? proportional == centerY.getUnits().equals(SizeUnits.PERCENT) : true;
            if (!unitsAgree) {
                throw new IllegalArgumentException("units do not agree");
            }
            final CycleMethod cycleMethod = (CycleMethod) values[v++].convert(font);
            final Stop[] stops = new Stop[values.length - v];
            for (int s = v; s < values.length; s++) {
                stops[s - v] = (Stop) values[s].convert(font);
            }
            //If the focus-angle is a percentage, the value is mutiplied
            // by 360, modulo 360.
            double fa = 0;
            if (focusAngle != null) {
                fa = focusAngle.pixels(font);
                if (focusAngle.getUnits().equals(SizeUnits.PERCENT)) {
                    fa = (fa * 360) % 360;
                }
            }
            paint = new RadialGradient(fa, focusDistance != null ? focusDistance.pixels() : 0, centerX != null ? centerX.pixels() : 0, centerY != null ? centerY.pixels() : 0, radius != null ? radius.pixels() : 1, proportional, cycleMethod, stops);

            super.cacheValue(value, paint);
            return paint;
        }

        @Override
        public String toString() {
            return "RadialGradientConverter";
        }
    }
}

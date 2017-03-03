package com.sun.javafx.css.converters;

import javafx.css.ParsedValue;
import javafx.css.Size;
import javafx.css.StyleConverter;
import javafx.scene.text.Font;

/**
 * Created by usta on 27.02.2017.
 */
public class SizeConverter extends StyleConverter<ParsedValue<?, Size>, Number> {
    // lazy, thread-safe instatiation
    private static class Holder {
        static final SizeConverter INSTANCE = new SizeConverter();
        static final SizeConverter.SequenceConverter SEQUENCE_INSTANCE = new SizeConverter.SequenceConverter();
    }

    public static StyleConverter<ParsedValue<?, Size>, Number> getInstance() {
        return SizeConverter.Holder.INSTANCE;
    }

    private SizeConverter() {
        super();
    }

    @Override
    public Number convert(ParsedValue<ParsedValue<?, Size>, Number> value, Font font) {
        ParsedValue<?, Size> size = value.getValue();
        return size.convert(font).pixels(font);
    }

    @Override
    public String toString() {
        return "SizeConverter";
    }

    /*
     * Convert [<size>]+ to an array of Number[].
     */
    public static final class SequenceConverter extends StyleConverter<ParsedValue[], Number[]> {

        public static SizeConverter.SequenceConverter getInstance() {
            return SizeConverter.Holder.SEQUENCE_INSTANCE;
        }

        private SequenceConverter() {
            super();
        }

        @Override
        public Number[] convert(ParsedValue<ParsedValue[], Number[]> value, Font font) {
            ParsedValue[] sizes = value.getValue();
            Number[] doubles = new Number[sizes.length];
            for (int i = 0; i < sizes.length; i++) {
                doubles[i] = ((Size)sizes[i].convert(font)).pixels(font);
            }
            return doubles;
        }

        @Override
        public String toString() {
            return "Size.SequenceConverter";
        }
    }
}

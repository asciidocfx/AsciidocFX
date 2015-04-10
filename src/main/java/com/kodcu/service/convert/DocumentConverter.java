package com.kodcu.service.convert;

import java.util.function.Consumer;

/**
 * Created by usta on 09.04.2015.
 */
public interface DocumentConverter<T> {

    public void convert(boolean askPath, Consumer<T>... nextStep);

}

package com.kodedu.component;

import javafx.scene.control.Slider;

/**
 * Created by usta on 19.07.2015.
 */
public class SliderBuilt {

    private Slider slider = new Slider();

    private SliderBuilt(double min, int max, double value) {
        slider.setMin(min);
        slider.setMax(max);
        slider.setValue(value);
    }

    public static SliderBuilt create(double min, int max, double value) {
        return new SliderBuilt(min, max, value);
    }

    public Slider step(double step) {
        slider.setBlockIncrement(step);
        return slider;
    }
}

package com.kodedu.service.ui;

import javafx.scene.control.ScrollBar;

import java.util.Objects;

/**
 * Created by usta on 30.06.2016.
 */
public class ScrollState {

    private double value;
    private double min;
    private double max;
    private double unitIncrement;
    private double blockIncrement;

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public void setMin(double min) {
        this.min = min;
    }

    public double getMin() {
        return min;
    }

    public void setMax(double max) {
        this.max = max;
    }

    public double getMax() {
        return max;
    }

    public void setUnitIncrement(double unitIncrement) {
        this.unitIncrement = unitIncrement;
    }

    public double getUnitIncrement() {
        return unitIncrement;
    }

    public void setBlockIncrement(double blockIncrement) {
        this.blockIncrement = blockIncrement;
    }

    public double getBlockIncrement() {
        return blockIncrement;
    }

    public void updateState(ScrollBar scrollBar) {
        this.setMin(scrollBar.getMin());
        this.setMax(scrollBar.getMax());
        this.setUnitIncrement(scrollBar.getUnitIncrement());
        this.setBlockIncrement(scrollBar.getBlockIncrement());
    }

    public void updateState(ScrollBar scrollBar, Number newValue) {
        if (Objects.nonNull(newValue)) {
            double value = newValue.doubleValue();
            if (value > 0) {
                updateState(scrollBar);
                this.setValue(value);
            }
        }
    }

    public void restoreState(ScrollBar scrollBar) {
        if (value > 0) {
            updateState(scrollBar);
            scrollBar.setMin(getMin());
            scrollBar.setMax(getMax());
            scrollBar.setUnitIncrement(getUnitIncrement());
            scrollBar.setBlockIncrement(getBlockIncrement());
            scrollBar.setValue(value);
        }
    }
}

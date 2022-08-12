package com.kodedu.service.extension.chart.impl;

import com.kodedu.config.ExtensionConfigBean;
import com.kodedu.controller.ApplicationController;
import com.kodedu.other.Current;
import com.kodedu.service.ThreadService;
import com.kodedu.service.cache.BinaryCacheService;
import javafx.scene.chart.BubbleChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import org.springframework.stereotype.Component;

/**
 * Created by usta on 31.03.2015.
 */
@Component("bubble-bean")
public class BubbleChartBuilderServiceImpl extends XYChartBuilderServiceImpl {

    public BubbleChartBuilderServiceImpl(ThreadService threadService, Current current, ApplicationController controller,
                                         ExtensionConfigBean extensionConfigBean, BinaryCacheService binaryCacheService) {
        super(threadService, current, controller, extensionConfigBean, binaryCacheService);
    }

    @Override
    protected XYChart<Number, Number> createXYChart() {
        final NumberAxis xAxis = new NumberAxis();
        final NumberAxis yAxis = new NumberAxis();
        BubbleChart<Number, Number> chart = new BubbleChart<>(xAxis, yAxis);
        chart.getStyleClass().add("chart-extension");
        return chart;
    }


}

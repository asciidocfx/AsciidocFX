package com.kodedu.service.extension.chart.impl;

import com.kodedu.config.ExtensionConfigBean;
import com.kodedu.controller.ApplicationController;
import com.kodedu.other.Current;
import com.kodedu.service.ThreadService;
import com.kodedu.service.cache.BinaryCacheService;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import org.springframework.stereotype.Component;

/**
 * Created by usta on 31.03.2015.
 */
@Component("line-bean")
public class LineChartBuilderServiceImpl extends XYChartBuilderServiceImpl {

    public LineChartBuilderServiceImpl(ThreadService threadService, Current current, ApplicationController controller,
                                       ExtensionConfigBean extensionConfigBean, BinaryCacheService binaryCacheService) {
        super(threadService, current, controller, extensionConfigBean, binaryCacheService);
    }

    @Override
    protected XYChart<String, Number> createXYChart() {
        final CategoryAxis xAxis = new CategoryAxis();
        final NumberAxis yAxis = new NumberAxis();
        final XYChart<String, Number> chart = new LineChart<String, Number>(xAxis, yAxis);
        chart.getStyleClass().add("chart-extension");
        return chart;
    }


}

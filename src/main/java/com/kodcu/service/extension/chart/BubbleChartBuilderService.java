package com.kodcu.service.extension.chart;

import com.kodcu.config.ExtensionConfigBean;
import com.kodcu.controller.ApplicationController;
import com.kodcu.other.Current;
import com.kodcu.service.ThreadService;
import javafx.scene.chart.BubbleChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by usta on 31.03.2015.
 */
@Component("bubble-bean")
public class BubbleChartBuilderService extends XYChartBuilderService {

    public BubbleChartBuilderService(ThreadService threadService, Current current, ApplicationController controller, ExtensionConfigBean extensionConfigBean) {
        super(threadService, current, controller, extensionConfigBean);
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

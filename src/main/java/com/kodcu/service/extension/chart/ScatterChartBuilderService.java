package com.kodcu.service.extension.chart;

import com.kodcu.controller.ApplicationController;
import com.kodcu.other.Current;
import com.kodcu.service.ThreadService;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.XYChart;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by usta on 31.03.2015.
 */
@Component("scatter-bean")
public class ScatterChartBuilderService extends XYChartBuilderService {

    private final ThreadService threadService;
    private final Current current;
    private final ApplicationController controller;

    @Autowired
    public ScatterChartBuilderService(ThreadService threadService, Current current, ApplicationController controller) {
        super(threadService, current, controller);
        this.threadService = threadService;
        this.current = current;
        this.controller = controller;
    }

    @Override
    protected XYChart<String, Number> createXYChart() {
        final CategoryAxis xAxis = new CategoryAxis();
        final NumberAxis yAxis = new NumberAxis();
        final XYChart<String, Number> lineChart = new ScatterChart<String, Number>(xAxis, yAxis);
        return lineChart;
    }


}

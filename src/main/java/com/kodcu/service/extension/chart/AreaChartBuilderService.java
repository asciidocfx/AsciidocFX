package com.kodcu.service.extension.chart;

import com.kodcu.controller.ApplicationController;
import com.kodcu.other.Current;
import com.kodcu.service.ThreadService;
import javafx.scene.chart.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by usta on 31.03.2015.
 */
@Component("area-bean")
public class AreaChartBuilderService extends XYChartBuilderService {

    private final ThreadService threadService;
    private final Current current;
    private final ApplicationController controller;

    @Autowired
    public AreaChartBuilderService(ThreadService threadService, Current current, ApplicationController controller) {
        super(threadService, current, controller);
        this.threadService = threadService;
        this.current = current;
        this.controller = controller;
    }

    @Override
    protected XYChart<Number, Number> createXYChart() {
        final NumberAxis xAxis = new NumberAxis();
        final NumberAxis yAxis = new NumberAxis();
        final XYChart<Number, Number> lineChart = new AreaChart<Number, Number>(xAxis, yAxis);
        return lineChart;
    }


}

package com.kodcu.service.extension.chart;

import com.kodcu.config.ExtensionConfigBean;
import com.kodcu.controller.ApplicationController;
import com.kodcu.other.Current;
import com.kodcu.service.ThreadService;
import javafx.collections.ObservableList;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by usta on 31.03.2015.
 */
@Component("bar-bean")
public class BarChartBuilderService extends XYChartBuilderService {

    public BarChartBuilderService(ThreadService threadService, Current current, ApplicationController controller, ExtensionConfigBean extensionConfigBean) {
        super(threadService, current, controller, extensionConfigBean);
    }

    @Override
    protected XYChart<String, Number> createXYChart() {
        final CategoryAxis xAxis = new CategoryAxis();
        final NumberAxis yAxis = new NumberAxis();
        final XYChart<String, Number> chart = new BarChart<String, Number>(xAxis, yAxis);
        chart.getStyleClass().add("chart-extension");
        return chart;
    }


}

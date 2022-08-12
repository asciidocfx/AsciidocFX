package com.kodedu.service.extension.chart.impl;

import com.kodedu.controller.ApplicationController;
import com.kodedu.other.Current;
import com.kodedu.service.ThreadService;
import com.kodedu.service.extension.ImageInfo;
import com.kodedu.service.extension.chart.ChartBuilderService;

import javafx.scene.chart.*;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

/**
 * Created by usta on 01.04.2015.
 */
public abstract class ChartBuilderServiceImpl implements ChartBuilderService {

    private final ThreadService threadService;
    private final Current current;
    private final ApplicationController controller;
    protected Path currentRoot;

    public ChartBuilderServiceImpl(ThreadService threadService, Current current, ApplicationController controller) {
        this.threadService = threadService;
        this.current = current;
        this.controller = controller;
    }

    @Override
    public boolean chartBuild(String chartContent, ImageInfo imageInfo, Map<String, String> optMap, CompletableFuture completableFuture) {

        if (!imageInfo.imageTarget().contains(".png")) {
            return false;
        }

        Integer cacheHit = current.getCache().get(imageInfo.imageTarget());
        int hashCode = (imageInfo.imageTarget() + imageInfo.imagePath() + imageInfo.imagesDir() + chartContent).hashCode() + optMap.hashCode();

        if (Objects.nonNull(cacheHit))
            if (hashCode == cacheHit) {
                return false;
            }

        current.getCache().put(imageInfo.imageTarget(), hashCode);

        currentRoot = current.currentTab().getParentOrWorkdir();

        return true;
    }

    protected XYChart<String, Number> createLineChart() {
        final CategoryAxis xAxis = new CategoryAxis();
        final NumberAxis yAxis = new NumberAxis();
        LineChart<String, Number> chart = new LineChart<>(xAxis, yAxis);
        chart.getStyleClass().add("chart-extension");
        return chart;
    }

    protected XYChart<String, Number> createAreaChart() {
        final CategoryAxis xAxis = new CategoryAxis();
        final NumberAxis yAxis = new NumberAxis();
        AreaChart<String, Number> chart = new AreaChart<>(xAxis, yAxis);
        chart.getStyleClass().add("chart-extension");
        return chart;
    }


}

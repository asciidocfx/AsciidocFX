package com.kodcu.service.extension.chart;

import com.kodcu.controller.ApplicationController;
import com.kodcu.other.Current;
import com.kodcu.service.ThreadService;
import javafx.scene.chart.*;

import java.nio.file.Path;
import java.util.Map;
import java.util.Objects;

/**
 * Created by usta on 01.04.2015.
 */
public abstract class ChartBuilderService {

    private final ThreadService threadService;
    private final Current current;
    private final ApplicationController controller;
    protected Path currentRoot;
    protected Path imagePath;

    public ChartBuilderService(ThreadService threadService, Current current, ApplicationController controller) {
        this.threadService = threadService;
        this.current = current;
        this.controller = controller;
    }

    public void chartBuild(String chartContent, String imagesDir, String imageTarget, Map<String, String> optMap) throws Exception {

        if (!imageTarget.endsWith(".png"))
            throw new InterruptedException();

        Integer cacheHit = current.getCache().get(imageTarget);
        int hashCode = (imageTarget + imagesDir + chartContent).hashCode() + optMap.hashCode();

        if (Objects.nonNull(cacheHit))
            if (hashCode == cacheHit) {
                throw new InterruptedException();
            }

        current.getCache().put(imageTarget, hashCode);

        if (!current.currentPath().isPresent())
            controller.saveDoc();

        currentRoot = current.currentPath().get().getParent();
        imagePath = currentRoot.resolve(imageTarget);

    }

    protected XYChart<String, Number> createLineChart() {
        final CategoryAxis xAxis = new CategoryAxis();
        final NumberAxis yAxis = new NumberAxis();
        return new LineChart<String, Number>(xAxis, yAxis);
    }

    protected XYChart<String, Number> createAreaChart() {
        final CategoryAxis xAxis = new CategoryAxis();
        final NumberAxis yAxis = new NumberAxis();
        return new AreaChart<String, Number>(xAxis, yAxis);
    }


}

package com.kodcu.service.extension.chart;

import com.kodcu.controller.ApplicationController;
import com.kodcu.other.Current;
import com.kodcu.service.ThreadService;
import javafx.application.Platform;
import javafx.scene.chart.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.nio.file.Path;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

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

    public void chartBuild(String chartContent, String fileName, Map<String, String> optMap) throws InterruptedException {

        currentRoot = current.currentPath().get().getParent();
        imagePath = currentRoot.resolve("images/").resolve(fileName);

        if (!fileName.endsWith(".png"))
            throw new InterruptedException();

        Integer cacheHit = current.getCache().get(fileName);
        StringBuffer builder=new StringBuffer();
        builder.append(fileName);
        builder.append(chartContent);
        builder.append(optMap.toString());
        int hashCode = builder.toString().hashCode();

        if (Objects.nonNull(cacheHit))
            if (hashCode == cacheHit) {
                throw new InterruptedException();
            }

        current.getCache().put(fileName, hashCode);

        if (!current.currentPath().isPresent())
            controller.saveDoc();

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

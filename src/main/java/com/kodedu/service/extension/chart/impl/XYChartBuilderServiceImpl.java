package com.kodedu.service.extension.chart.impl;

import com.kodedu.config.ExtensionConfigBean;
import com.kodedu.controller.ApplicationController;
import com.kodedu.helper.IOHelper;
import com.kodedu.other.Current;
import com.kodedu.service.ThreadService;
import com.kodedu.service.extension.chart.ChartBuilderService;

import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.chart.Axis;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.image.WritableImage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.image.BufferedImage;
import java.util.*;
import java.util.concurrent.CompletableFuture;

/**
 * Created by usta on 31.03.2015.
 */
public abstract class XYChartBuilderServiceImpl extends ChartBuilderServiceImpl {

    private final ThreadService threadService;
    private final Current current;
    private final ApplicationController controller;
    private final ExtensionConfigBean extensionConfigBean;
    private final Logger logger = LoggerFactory.getLogger(ChartBuilderService.class);

    public XYChartBuilderServiceImpl(ThreadService threadService, Current current, ApplicationController controller, ExtensionConfigBean extensionConfigBean) {
        super(threadService, current, controller);
        this.threadService = threadService;
        this.current = current;
        this.controller = controller;
        this.extensionConfigBean = extensionConfigBean;
    }

    @Override
    public boolean chartBuild(String chartContent, String imagesDir, String imageTarget, Map<String, String> optMap, CompletableFuture completableFuture) {

        boolean chartBuild = super.chartBuild(chartContent, imagesDir, imageTarget, optMap, completableFuture);

        if (!chartBuild) {
            completableFuture.complete(null);
            return chartBuild;
        }

        logger.debug("Chart extension is started for {}", imageTarget);

        String[] split = chartContent.split("\\r?\\n");
        List<String> lines = Arrays.asList(split);
        List colors = new ArrayList<>();

        XYChart xyChart = createXYChart();

        int scale = extensionConfigBean.getDefaultImageScale();

        xyChart.setScaleX(scale);
        xyChart.setScaleY(scale);
        xyChart.setScaleZ(scale);
        XYChart.Series series = new XYChart.Series();

        Axis xAxis = xyChart.getXAxis();
        Axis yAxis = xyChart.getYAxis();
        xAxis.setTickLabelsVisible(true);
        xAxis.setAnimated(false);
        xAxis.setTickLabelGap(10);
        yAxis.setTickLabelsVisible(true);
        yAxis.setAnimated(false);
        yAxis.setTickLabelGap(10);

        for (String line : lines) {

            if (line.trim().startsWith("//")) {
                series = new XYChart.Series();
                series.setName(line.trim().substring(2));
            }

            String[] parts = line.split(",");

            if (parts.length < 2)
                continue;

            Object name = null; // try first double
            Object value = null; // try first double
            Object color = null; // try first double

            if (parts.length == 3) {
                color = parts[2];
                colors.add(color);
                xyChart.setStyle("-fx-bar-fill: " + color + ";");
            }

            if (xAxis instanceof NumberAxis) {
                name = Double.valueOf(parts[0]);
            }

            if (yAxis instanceof NumberAxis) {
                value = Double.valueOf(parts[1]);
            }

            if (xAxis instanceof CategoryAxis) {
                name = String.valueOf(parts[0]);
            }

            if (yAxis instanceof CategoryAxis) {
                value = String.valueOf(parts[1]);
            }

            series.getData().add(new XYChart.Data(name, value));

            if (!xyChart.getData().contains(series))
                xyChart.getData().add(series);

        }

        for (int i = 0; i < colors.size(); i++) {
            Object color = colors.get(i);
            if (Objects.isNull(color))
                continue;
            Set<Node> nodes1 = xyChart.lookupAll(".default-color" + i + ".chart-bar");
            Set<Node> nodes2 = xyChart.lookupAll(".default-color" + i + ".chart-pie");

            for (Node node : nodes1) {
                node.setStyle("-fx-bar-fill: " + color + ";");
            }

            for (Node node : nodes2) {
                node.setStyle("-fx-bar-fill: " + color + ";");
            }

        }


        if (Objects.nonNull(optMap.get("legend"))) {
            try {
                xyChart.setLegendSide(Side.valueOf(optMap.get("legend").toUpperCase()));
            } catch (RuntimeException e) {
                xyChart.setLegendVisible(false);
            }
        }

        if (Objects.nonNull(optMap.get("title")))
            xyChart.setTitle(optMap.get("title"));

        if (Objects.nonNull(optMap.get("title-side"))) {
            xyChart.setTitleSide(Side.valueOf(optMap.get("title-side").toUpperCase()));
        }

        Set<Node> nodes = xyChart.lookupAll(".chart-title");
        for (Node node : nodes) {
            String titleColor = Objects.isNull(optMap.get("title-color")) ? "#000" : optMap.get("title-color");
            String titleSize = Objects.isNull(optMap.get("title-size")) ? "1.6em" : optMap.get("title-size");
            node.setStyle(String.format("-fx-text-fill: %s; -fx-font-size: %s;", titleColor, titleSize));
        }

        if (Objects.nonNull(optMap.get("x-label")))
            xAxis.setLabel(optMap.get("x-label"));

        if (Objects.nonNull(optMap.get("y-label")))
            yAxis.setLabel(optMap.get("y-label"));

        if (Objects.nonNull(optMap.get("x-label-rotation")))
            xAxis.setTickLabelRotation(Double.parseDouble(optMap.get("x-label-rotation")));

        if (Objects.nonNull(optMap.get("y-label-rotation")))
            yAxis.setTickLabelRotation(Double.parseDouble(optMap.get("y-label-rotation")));

        if (Objects.nonNull(optMap.get("show-labels"))) {
            Boolean value = Boolean.valueOf(optMap.get("show-labels"));
            xAxis.setTickLabelsVisible(value);
            yAxis.setTickLabelsVisible(value);
        }

        if (Objects.nonNull(optMap.get("show-x-label"))) {
            xAxis.setTickLabelsVisible(Boolean.valueOf(optMap.get("show-x-labels")));
        }

        if (Objects.nonNull(optMap.get("show-y-label"))) {
            yAxis.setTickLabelsVisible(Boolean.valueOf(optMap.get("show-y-labels")));
        }

        if (Objects.nonNull(optMap.get("label-gap"))) {
            xAxis.setTickLabelGap(Double.valueOf(optMap.get("label-gap")));
            yAxis.setTickLabelGap(Double.valueOf(optMap.get("label-gap")));
        }

        if (Objects.nonNull(optMap.get("x-label-gap"))) {
            xAxis.setTickLabelGap(Double.valueOf(optMap.get("x-label-gap")));
        }

        if (Objects.nonNull(optMap.get("y-label-gap"))) {
            yAxis.setTickLabelGap(Double.valueOf(optMap.get("y-label-gap")));
        }

        if (Objects.nonNull(optMap.get("x-side"))) {
            xAxis.setSide(Side.valueOf(optMap.get("x-side").toUpperCase()));
        }

        if (Objects.nonNull(optMap.get("y-side"))) {
            yAxis.setSide(Side.valueOf(optMap.get("y-side").toUpperCase()));
        }

        xyChart.setLayoutX(-7000);
        xyChart.setLayoutY(-7000);

        controller.getRootAnchor().getChildren().add(xyChart);
        WritableImage writableImage = xyChart.snapshot(new SnapshotParameters(), null);
        controller.getRootAnchor().getChildren().remove(xyChart);
        BufferedImage bufferedImage = SwingFXUtils.fromFXImage(writableImage, null);
        IOHelper.imageWrite(bufferedImage, "png", imagePath.toFile());
        completableFuture.complete(null);
        logger.debug("Chart extension is ended for {}", imageTarget);
        controller.clearImageCache(imagePath);

        return chartBuild;
    }

    protected abstract XYChart createXYChart();

}

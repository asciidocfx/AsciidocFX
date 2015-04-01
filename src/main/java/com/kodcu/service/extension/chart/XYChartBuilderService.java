package com.kodcu.service.extension.chart;

import com.kodcu.controller.ApplicationController;
import com.kodcu.other.Current;
import com.kodcu.other.IOHelper;
import com.kodcu.service.ThreadService;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Side;
import javafx.scene.SnapshotParameters;
import javafx.scene.chart.Axis;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.image.WritableImage;

import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Created by usta on 31.03.2015.
 */
public abstract class XYChartBuilderService extends ChartBuilderService {

    private final ThreadService threadService;
    private final Current current;
    private final ApplicationController controller;

    int layoyt = -78000;

//    static final AtomicBoolean completed = new AtomicBoolean(false);

    public XYChartBuilderService(ThreadService threadService, Current current, ApplicationController controller) {
        super(threadService, current, controller);
        this.threadService = threadService;
        this.current = current;
        this.controller = controller;
    }

    @Override
    public void chartBuild(String chartContent, String fileName, Map<String, String> optMap) throws InterruptedException {

        try {
            super.chartBuild(chartContent, fileName, optMap);
        } catch (InterruptedException e) {
            throw e;
        }

        String[] split = chartContent.split("\\r?\\n");
        List<String> lines = Arrays.asList(split);

        XYChart xyChart = createXYChart();
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


        if (Objects.nonNull(optMap.get("legend")))
            xyChart.setLegendSide(Side.valueOf(optMap.get("legend").toUpperCase()));

        if (Objects.nonNull(optMap.get("x-label")))
            xAxis.setLabel(optMap.get("x-label"));

        if (Objects.nonNull(optMap.get("y-label")))
            xAxis.setLabel(optMap.get("y-label"));

        if (Objects.nonNull(optMap.get("x-label-rotation")))
            xAxis.setTickLabelRotation(Double.parseDouble(optMap.get("x-label-rotation")));

        if (Objects.nonNull(optMap.get("y-label-rotation")))
            yAxis.setTickLabelRotation(Double.parseDouble(optMap.get("y-label-rotation")));

        if (Objects.nonNull(optMap.get("show-labels"))) {
            Boolean value = Boolean.valueOf(optMap.get("show-labels"));
            xAxis.setTickLabelsVisible(value);
            yAxis.setTickLabelsVisible(value);
        }

        if (Objects.nonNull(optMap.get("show-x-labels"))) {
            xAxis.setTickLabelsVisible(Boolean.valueOf(optMap.get("show-x-labels")));
        }

        if (Objects.nonNull(optMap.get("show-y-labels"))) {
            yAxis.setTickLabelsVisible(Boolean.valueOf(optMap.get("show-y-labels")));
        }

        if (Objects.nonNull(optMap.get("label-gap"))) {
            xAxis.setTickLabelGap(Double.valueOf(optMap.get("label-gap")));
            yAxis.setTickLabelGap(Double.valueOf(optMap.get("label-gap")));
        }

        if (Objects.nonNull(optMap.get("x-label-gap"))) {
            xAxis.setTickLabelGap(Double.valueOf(optMap.get("x-label-gap")));
        }

        if (Objects.nonNull(optMap.get("x-side"))) {
            xAxis.setSide(Side.valueOf(optMap.get("x-side").toUpperCase()));
        }


        if (Objects.nonNull(optMap.get("y-side"))) {
            yAxis.setSide(Side.valueOf(optMap.get("y-side").toUpperCase()));
        }


        if (Objects.nonNull(optMap.get("y-label-gap"))) {
            xAxis.setTickLabelGap(Double.valueOf(optMap.get("y-label-gap")));
        }

        layoyt -= 1000;
        xyChart.setLayoutX(layoyt);
        xyChart.setLayoutY(layoyt);

        controller.getRootAnchor().getChildren().add(xyChart);
        WritableImage writableImage = xyChart.snapshot(new SnapshotParameters(), null);
        controller.removeChildElement(xyChart);
        BufferedImage bufferedImage = SwingFXUtils.fromFXImage(writableImage, null);
        IOHelper.createDirectories(currentRoot.resolve("images"));
        IOHelper.imageWrite(bufferedImage, "png", imagePath.toFile());
        controller.clearImageCache();
    }

    protected abstract XYChart createXYChart();

}

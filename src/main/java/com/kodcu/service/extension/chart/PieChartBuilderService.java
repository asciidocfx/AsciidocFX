package com.kodcu.service.extension.chart;

import com.kodcu.controller.ApplicationController;
import com.kodcu.other.Current;
import com.kodcu.other.IOHelper;
import com.kodcu.service.ThreadService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.chart.PieChart;
import javafx.scene.image.WritableImage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.awt.image.BufferedImage;
import java.util.*;

/**
 * Created by usta on 31.03.2015.
 */
@Component("pie-bean")
public class PieChartBuilderService extends ChartBuilderService {

    private final ThreadService threadService;
    private final Current current;
    private final ApplicationController controller;

    @Autowired
    public PieChartBuilderService(ThreadService threadService, Current current, ApplicationController controller) {
        super(threadService, current, controller);
        this.threadService = threadService;
        this.current = current;
        this.controller = controller;
    }

    @Override
    public void chartBuild(String chartContent, String fileName, Map<String, String> optMap) throws Exception {

        try {
            super.chartBuild(chartContent, fileName, optMap);
        } catch (InterruptedException e) {
            throw e;
        }

        String[] split = chartContent.split("\\r?\\n");
        List<String> lines = Arrays.asList(split);

        ObservableList<PieChart.Data> datas = FXCollections.observableArrayList();
        ObservableList<String> colors = FXCollections.observableArrayList();

        for (String line : lines) {

            String[] parts = line.split(",");

            if (parts.length < 2)
                continue;

            String name = null;
            Double value = null;
            String color = null;

            try {
                name = parts[0];
                value = Double.valueOf(parts[1]);
                color = parts[2];
            } catch (Exception e) {
            }

            colors.add(color);
            datas.add(new PieChart.Data(name, value));
        }

        PieChart pieChart = new PieChart(datas);

        for (int i = 0; i < datas.size(); i++) {
            PieChart.Data data = datas.get(i);
            String color = colors.get(i);
            if (Objects.nonNull(color))
                data.getNode().setStyle("-fx-pie-color:" + color + ";");
        }

        if (Objects.nonNull(optMap.get("clockwise")))
            pieChart.setClockwise(Boolean.valueOf(optMap.get("clockwise")));
        if (Objects.nonNull(optMap.get("labels-visible")))
            pieChart.setLabelsVisible(Boolean.valueOf(optMap.get("labels-visible")));
        if (Objects.nonNull(optMap.get("line-length")))
            pieChart.setLabelLineLength(Double.parseDouble(optMap.get("line-length")));
        if (Objects.nonNull(optMap.get("start-angle")))
            pieChart.setStartAngle(Double.parseDouble(optMap.get("start-angle")));

        if (Objects.nonNull(optMap.get("legend"))) {
            try {
                pieChart.setLegendSide(Side.valueOf(optMap.get("legend").toUpperCase()));
            } catch (RuntimeException e) {
                pieChart.setLegendVisible(false);
            }
        }

        if (Objects.nonNull(optMap.get("title")))
            pieChart.setTitle(optMap.get("title"));

        if (Objects.nonNull(optMap.get("title-side")))
            pieChart.setTitleSide(Side.valueOf(optMap.get("title-side").toUpperCase()));

        Set<Node> nodes = pieChart.lookupAll(".chart-title");
        for (Node node : nodes) {
            String titleColor = Objects.isNull(optMap.get("title-color")) ? "#000" : optMap.get("title-color");
            String titleSize = Objects.isNull(optMap.get("title-size")) ? "1.6em" : optMap.get("title-size");
            node.setStyle(String.format("-fx-text-fill: %s; -fx-font-size: %s;", titleColor, titleSize));
        }

        pieChart.setLayoutX(-78000);
        pieChart.setLayoutY(-78000);

        threadService.runActionLater(() -> {
            controller.getRootAnchor().getChildren().add(pieChart);
            WritableImage writableImage = pieChart.snapshot(new SnapshotParameters(), null);
            controller.removeChildElement(pieChart);
            BufferedImage bufferedImage = SwingFXUtils.fromFXImage(writableImage, null);
            IOHelper.createDirectories(currentRoot.resolve("images"));
            IOHelper.imageWrite(bufferedImage, "png", imagePath.toFile());
            controller.clearImageCache();

        });
    }

}

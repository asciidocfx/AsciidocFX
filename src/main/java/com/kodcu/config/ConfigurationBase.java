package com.kodcu.config;

import com.kodcu.controller.ApplicationController;
import com.kodcu.service.ThreadService;
import javafx.animation.FadeTransition;
import javafx.beans.property.*;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.json.*;
import javax.json.stream.JsonGenerator;
import java.io.FileWriter;
import java.lang.reflect.Field;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by usta on 19.07.2015.
 */
public abstract class ConfigurationBase {

    private final ApplicationController controller;
    private final ThreadService threadService;

    private Logger logger = LoggerFactory.getLogger(ConfigurationBase.class);

    public ConfigurationBase(ApplicationController controller, ThreadService threadService) {
        this.controller = controller;
        this.threadService = threadService;
    }

    public abstract VBox createForm();

    public Path getConfigDirectory() {
        Path configPath = controller.getConfigPath();
        return configPath;
    }


    protected void fadeOut(Label label, String text) {
        threadService.runActionLater(() -> {
            label.setText(text);
            FadeTransition fadeTransition = new FadeTransition(Duration.millis(2000), label);
            fadeTransition.setFromValue(1);
            fadeTransition.setToValue(0);
            fadeTransition.playFromStart();
        });
    }

    protected void saveJson(JsonStructure jsonStructure){
        Map<String, Object> properties = new HashMap<>(1);
        properties.put(JsonGenerator.PRETTY_PRINTING, true);

        try (FileWriter fileWriter = new FileWriter(getConfigPath().toFile());
             JsonWriter jsonWriter = Json.createWriterFactory(properties).createWriter(fileWriter);) {
            jsonWriter.write(jsonStructure);
        } catch (Exception e) {
            logger.error("Problem occured while saving {}", this.getClass().getSimpleName(), e);
        }
    };

    public abstract Path getConfigPath();
    public abstract void load(ActionEvent... actionEvent);
    public abstract void save(ActionEvent... actionEvent);
    public abstract JsonObject getJSON();
}

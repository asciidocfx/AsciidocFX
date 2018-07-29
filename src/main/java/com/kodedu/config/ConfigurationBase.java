package com.kodedu.config;

import com.kodedu.controller.ApplicationController;
import com.kodedu.other.IOHelper;
import com.kodedu.service.ThreadService;
import javafx.animation.FadeTransition;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonStructure;
import javax.json.JsonWriter;
import javax.json.stream.JsonGenerator;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Created by usta on 19.07.2015.
 */
public abstract class ConfigurationBase {

    private final ApplicationController controller;
    private final ThreadService threadService;

    public abstract String formName();

    @Value("${application.config.folder}")
    private String userHomeConfigFolder;

    public static ObjectProperty<Path> configRootLocation = new SimpleObjectProperty<>();

    public Path getConfigRootLocation() {

        String userHome = System.getProperty("user.home");

        Path userHomeConfigPath = IOHelper.getPath(userHome).resolve(userHomeConfigFolder);

        IOHelper.createDirectories(userHomeConfigPath);

        setConfigRootLocation(userHomeConfigPath);

        return userHomeConfigPath;
    }

    public ObjectProperty<Path> configRootLocationProperty() {
        return configRootLocation;
    }

    public void setConfigRootLocation(Path configRootLocation) {
        this.configRootLocation.set(configRootLocation);
    }

    private Logger logger = LoggerFactory.getLogger(ConfigurationBase.class);

    public ConfigurationBase(ApplicationController controller, ThreadService threadService) {
        this.controller = controller;
        this.threadService = threadService;
    }

    public abstract VBox createForm();

    public Path resolveConfigPath(String fileName) {

        Path configRootLocation = getConfigRootLocation();

        Path configPath = null;

        configPath = configRootLocation.resolve(fileName);

        if (Files.notExists(configPath)) {
            Path defaultConfigPath = getConfigDirectory().resolve(fileName);
            IOHelper.copy(defaultConfigPath, configPath);
        }

        return configPath;
    }

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

    protected void saveJson(JsonStructure jsonStructure) {
        Map<String, Object> properties = new HashMap<>(1);
        properties.put(JsonGenerator.PRETTY_PRINTING, true);

        try (FileOutputStream fos = new FileOutputStream(getConfigPath().toFile());
             OutputStreamWriter fileWriter = new OutputStreamWriter(fos, "UTF-8");
             JsonWriter jsonWriter = Json.createWriterFactory(properties).createWriter(fileWriter);) {
            jsonWriter.write(jsonStructure);
        } catch (Exception e) {
            logger.error("Problem occured while saving {}", this.getClass().getSimpleName(), e);
        }
    }

    public abstract Path getConfigPath();

    public abstract void load(Path configPath, ActionEvent... actionEvent);

    public void load(ActionEvent... actionEvent) {
        load(getConfigPath(), actionEvent);
    }

    public abstract void save(ActionEvent... actionEvent);

    public abstract JsonObject getJSON();

    public void setOnConfigChanged(Runnable runnable) {
        Field[] fields = this.getClass().getDeclaredFields();
        for (Field field : fields) {
            try {
                field.setAccessible(true);
                Object value = field.get(this);

                if (value instanceof ObservableValue) {
                    ((ObservableValue) value).addListener((observable, oldValue, newValue) -> {
                        if (Objects.nonNull(newValue)) {
                            runnable.run();
                        }
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void loadPreviousConfiguration(String configDir) {
        Path defaultConfigPath = getConfigPath();
        Path fileName = defaultConfigPath.getFileName();

        String userHome = System.getProperty("user.home");
        Path resolvedConfigPath = Paths.get(userHome).resolve(configDir).resolve(fileName);

        load(resolvedConfigPath);
    }
}

package com.kodedu.config;

import com.kodedu.controller.ApplicationController;
import com.kodedu.other.IOHelper;
import com.kodedu.other.Item;
import com.kodedu.service.ThreadService;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.layout.VBox;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.json.*;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

/**
 * Created by usta on 07.08.2015.
 */
@Component
public class StoredConfigBean extends ConfigurationBase {

    private final ApplicationController controller;
    private final ThreadService threadService;

    private StringProperty workingDirectory = new SimpleStringProperty();
    private ObservableList<Item> recentFiles = FXCollections.observableArrayList();
    private ObservableList<String> favoriteDirectories = FXCollections.observableArrayList();


    @Override
    public String formName() {
        return "Stored Settings";
    }

    @Autowired
    public StoredConfigBean(ApplicationController controller, ThreadService threadService) {
        super(controller, threadService);
        this.controller = controller;
        this.threadService = threadService;
    }

    public String getWorkingDirectory() {
        return workingDirectory.get();
    }

    public StringProperty workingDirectoryProperty() {
        return workingDirectory;
    }

    public void setWorkingDirectory(String workingDirectory) {
        this.workingDirectory.set(workingDirectory);
    }

    public ObservableList<Item> getRecentFiles() {
        return recentFiles;
    }

    public void setRecentFiles(ObservableList<Item> recentFiles) {
        this.recentFiles = recentFiles;
    }

    public ObservableList<String> getFavoriteDirectories() {
        return favoriteDirectories;
    }

    public void setFavoriteDirectories(ObservableList<String> favoriteDirectories) {
        this.favoriteDirectories = favoriteDirectories;
    }

    @Override
    public VBox createForm() {
        return null;
    }

    @Override
    public Path getConfigPath() {
        return super.resolveConfigPath("stored_directories.json");
    }

    @Override
    public void load(Path configPath, ActionEvent... actionEvent) {

        Reader fileReader = IOHelper.fileReader(configPath);
        JsonReader jsonReader = Json.createReader(fileReader);

        JsonObject jsonObject = jsonReader.readObject();

        JsonArray recentFiles = jsonObject.getJsonArray("recentFiles");
        JsonArray favoriteDirectories = jsonObject.getJsonArray("favoriteDirectories");
        String workingDirectory = jsonObject.getString("workingDirectory", System.getProperty("user.home"));

        IOHelper.close(jsonReader, fileReader);

        threadService.runActionLater(() -> {

            if (Objects.nonNull(workingDirectory)) {
                this.workingDirectory.setValue(workingDirectory);
            }

            if (Objects.nonNull(recentFiles)) {
                recentFiles.stream().map(e -> (JsonString) e).map(e -> e.getString())
                        .map(e -> new Item(IOHelper.getPath(e)))
                        .forEach(this.recentFiles::add);
            }
            if (Objects.nonNull(favoriteDirectories)) {
                favoriteDirectories.stream().map(e -> (JsonString) e).map(e -> e.getString()).forEach(this.favoriteDirectories::add);
            }
        });
    }

    @Override
    public void save(ActionEvent... actionEvent) {
        saveJson(getJSON());
    }

    @Override
    public JsonObject getJSON() {
        JsonObjectBuilder objectBuilder = Json.createObjectBuilder();

        JsonArrayBuilder recentFilesArrayBuilder = Json.createArrayBuilder();
        JsonArrayBuilder favoriteDirectoriesArrayBuilder = Json.createArrayBuilder();

        recentFiles.stream()
                .map(Item::getPath)
                .filter(Files::exists)
                .map(e -> e.toString())
                .forEach(recentFilesArrayBuilder::add);

        favoriteDirectories.stream()
                .forEach(favoriteDirectoriesArrayBuilder::add);

        objectBuilder
                .add("workingDirectory", getWorkingDirectory())
                .add("recentFiles", recentFilesArrayBuilder)
                .add("favoriteDirectories", favoriteDirectoriesArrayBuilder);

        return objectBuilder.build();
    }
}

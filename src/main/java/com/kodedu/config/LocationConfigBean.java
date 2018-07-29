package com.kodedu.config;

//import com.dooapp.fxform.annotation.Accessor;

import com.dooapp.fxform.FXForm;
import com.dooapp.fxform.builder.FXFormBuilder;
import com.dooapp.fxform.handler.NamedFieldHandler;
import com.dooapp.fxform.view.factory.DefaultFactoryProvider;
import com.kodedu.config.factory.FileChooserEditableFactory;
import com.kodedu.config.factory.FileChooserFactory;
import com.kodedu.controller.ApplicationController;
import com.kodedu.other.IOHelper;
import com.kodedu.service.ThreadService;
import com.kodedu.service.ui.TabService;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonReader;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.ResourceBundle;

/**
 * Created by usta on 17.07.2015.
 */
@Component
public class LocationConfigBean extends ConfigurationBase {

    private Logger logger = LoggerFactory.getLogger(LocationConfigBean.class);

    private ObjectProperty<String> stylesheetDefault = new SimpleObjectProperty<>();
    private ObjectProperty<String> stylesheetOverrides = new SimpleObjectProperty<>();
    private ObjectProperty<String> mathjax = new SimpleObjectProperty<>();
    private ObjectProperty<String> kindlegen = new SimpleObjectProperty<>();

    public String getStylesheetDefault() {
        return stylesheetDefault.get();
    }

    public ObjectProperty<String> stylesheetDefaultProperty() {
        return stylesheetDefault;
    }

    public void setStylesheetDefault(String stylesheetDefault) {
        this.stylesheetDefault.set(stylesheetDefault);
    }

    public String getStylesheetOverrides() {
        return stylesheetOverrides.get();
    }

    public ObjectProperty<String> stylesheetOverridesProperty() {
        return stylesheetOverrides;
    }

    public void setStylesheetOverrides(String stylesheetOverrides) {
        this.stylesheetOverrides.set(stylesheetOverrides);
    }

    public String getMathjax() {
        return mathjax.get();
    }

    public ObjectProperty<String> mathjaxProperty() {
        return mathjax;
    }

    public void setMathjax(String mathjax) {
        this.mathjax.set(mathjax);
    }

    public String getKindlegen() {
        return kindlegen.get();
    }

    public ObjectProperty<String> kindlegenProperty() {
        return kindlegen;
    }

    public void setKindlegen(String kindlegen) {
        this.kindlegen.set(kindlegen);
    }

    private final ApplicationController controller;
    private final ThreadService threadService;
    private final TabService tabService;

    private final Button saveButton = new Button("Save");
    private final Button loadButton = new Button("Load");
    private final Label infoLabel = new Label();

    @Autowired
    public LocationConfigBean(ApplicationController controller, ThreadService threadService, TabService tabService) {
        super(controller, threadService);
        this.controller = controller;
        this.threadService = threadService;
        this.tabService = tabService;
    }


    @Override
    public String formName() {
        return "Location Settings";
    }

    @Override
    public VBox createForm() {

        FXForm locationConfigForm = new FXFormBuilder<>()
                .resourceBundle(ResourceBundle.getBundle("locationConfig"))
                .includeAndReorder("stylesheetDefault", "stylesheetOverrides", "mathjax", "kindlegen")
                .build();

        DefaultFactoryProvider locationConfigFormProvider = new DefaultFactoryProvider();


        locationConfigFormProvider.addFactory(new NamedFieldHandler("kindlegen"), new FileChooserFactory("Enter kindlegen path", controller::openInDesktop));
        locationConfigFormProvider.addFactory(new NamedFieldHandler("mathjax"), new FileChooserFactory(controller::openInDesktop));
        locationConfigFormProvider.addFactory(new NamedFieldHandler("stylesheetDefault"), new FileChooserEditableFactory(tabService::addTab, controller::openInDesktop));
        locationConfigFormProvider.addFactory(new NamedFieldHandler("stylesheetOverrides"), new FileChooserEditableFactory(tabService::addTab, controller::openInDesktop));
        locationConfigForm.setEditorFactoryProvider(locationConfigFormProvider);

        locationConfigForm.setSource(this);

        VBox vBox = new VBox();
        vBox.getChildren().add(locationConfigForm);

        saveButton.setOnAction(this::save);
        loadButton.setOnAction(this::load);
        HBox box = new HBox(5, saveButton, loadButton, infoLabel);
        box.setPadding(new Insets(0, 0, 15, 5));
        vBox.getChildren().add(box);

        return vBox;
    }

    @Override
    public Path getConfigPath() {
        return super.resolveConfigPath("location_config.json");
    }

    @Override
    public void load(Path configPath, ActionEvent... actionEvent) {

        fadeOut(infoLabel, "Loading...");

        loadPathDefaults();

        Reader fileReader = IOHelper.fileReader(configPath);
        JsonReader jsonReader = Json.createReader(fileReader);

        JsonObject jsonObject = jsonReader.readObject();

        String stylesheetDefault = jsonObject.getString("stylesheetDefault", null);
        String stylesheetOverrides = jsonObject.getString("stylesheetOverrides", null);
        String mathjax = jsonObject.getString("mathjax", null);
        String kindlegen = jsonObject.getString("kindlegen", null);

        IOHelper.close(jsonReader, fileReader);

        threadService.runActionLater(() -> {

            if (Objects.nonNull(stylesheetDefault)) {
                this.setStylesheetDefault(stylesheetDefault);
            }

            if (Objects.nonNull(stylesheetOverrides)) {
                this.setStylesheetOverrides(stylesheetOverrides);
            }

            if (Objects.nonNull(mathjax)) {
                this.setMathjax(mathjax);
            }

            if (Objects.nonNull(kindlegen)) {
                this.setKindlegen(kindlegen);
            }

            fadeOut(infoLabel, "Loaded...");

        });

    }

    private void loadPathDefaults() {
        Path configPath = controller.getConfigPath(); // installed /conf dir
        Path rootLocation = getConfigRootLocation(); // user.home root

        Path targetCssPath = rootLocation.resolve("css");
        Path backupCssPath = configPath.resolve("public/css");

        IOHelper.createDirectories(targetCssPath);

        if (Files.notExists(targetCssPath.resolve("asciidoctor-default.css"))) {
            IOHelper.copy(backupCssPath
                    .resolve("asciidoctor-default.css"), targetCssPath.resolve("asciidoctor-default.css"));
        }

        if (Files.notExists(targetCssPath.resolve("asciidoctor-default-overrides.css"))) {
            IOHelper.copy(backupCssPath
                    .resolve("asciidoctor-default-overrides.css"), targetCssPath.resolve("asciidoctor-default-overrides.css"));
        }

        this.setStylesheetDefault(targetCssPath.resolve("asciidoctor-default.css").toString());
        this.setStylesheetOverrides(targetCssPath.resolve("asciidoctor-default-overrides.css").toString());

        this.setMathjax(configPath
                .resolve("public/mathjax/MathJax.js").toString());
    }

    @Override
    public void save(ActionEvent... actionEvent) {

        infoLabel.setText("Saving...");
        saveJson(getJSON());
        fadeOut(infoLabel, "Saved...");
    }

    @Override
    public JsonObject getJSON() {
        JsonObjectBuilder objectBuilder = Json.createObjectBuilder();

        if (Objects.nonNull(getStylesheetDefault())) {
            objectBuilder.add("stylesheetDefault", getStylesheetDefault());
        }

        if (Objects.nonNull(getStylesheetOverrides())) {
            objectBuilder.add("stylesheetOverrides", getStylesheetOverrides());
        }

        if (Objects.nonNull(getMathjax())) {
            objectBuilder.add("mathjax", getMathjax());
        }

        if (Objects.nonNull(getKindlegen())) {
            objectBuilder.add("kindlegen", getKindlegen());
        }

        return objectBuilder.build();
    }
}

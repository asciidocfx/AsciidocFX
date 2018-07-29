package com.kodedu.config;

//import com.dooapp.fxform.annotation.Accessor;

import com.dooapp.fxform.FXForm;
import com.dooapp.fxform.builder.FXFormBuilder;
import com.dooapp.fxform.view.factory.DefaultFactoryProvider;
import com.kodedu.config.factory.FileChooserEditableFactory;
import com.kodedu.controller.ApplicationController;
import com.kodedu.other.IOHelper;
import com.kodedu.service.ThreadService;
import com.kodedu.service.ui.TabService;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
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
import java.nio.file.Path;
import java.util.ResourceBundle;

/**
 * Created by usta on 17.07.2015.
 */
@Component
public class ExtensionConfigBean extends ConfigurationBase {

    private IntegerProperty defaultImageScale = new SimpleIntegerProperty(2);
    private IntegerProperty defaultImageZoom = new SimpleIntegerProperty(2);
    private IntegerProperty defaultImageDpi = new SimpleIntegerProperty(200);

    public int getDefaultImageScale() {
        return defaultImageScale.get();
    }

    public IntegerProperty defaultImageScaleProperty() {
        return defaultImageScale;
    }

    public void setDefaultImageScale(int defaultImageScale) {
        this.defaultImageScale.set(defaultImageScale);
    }

    public int getDefaultImageZoom() {
        return defaultImageZoom.get();
    }

    public IntegerProperty defaultImageZoomProperty() {
        return defaultImageZoom;
    }

    public void setDefaultImageZoom(int defaultImageZoom) {
        this.defaultImageZoom.set(defaultImageZoom);
    }

    public int getDefaultImageDpi() {
        return defaultImageDpi.get();
    }

    public IntegerProperty defaultImageDpiProperty() {
        return defaultImageDpi;
    }

    public void setDefaultImageDpi(int defaultImageDpi) {
        this.defaultImageDpi.set(defaultImageDpi);
    }

    private Logger logger = LoggerFactory.getLogger(ExtensionConfigBean.class);

    private final ApplicationController controller;
    private final ThreadService threadService;
    private final TabService tabService;

    private final Button saveButton = new Button("Save");
    private final Button loadButton = new Button("Load");
    private final Label infoLabel = new Label();

    @Autowired
    public ExtensionConfigBean(ApplicationController controller, ThreadService threadService, TabService tabService) {
        super(controller, threadService);
        this.controller = controller;
        this.threadService = threadService;
        this.tabService = tabService;
    }


    @Override
    public String formName() {
        return "Extension Settings";
    }

    @Override
    public VBox createForm() {

        FXForm editorConfigForm = new FXFormBuilder<>()
                .resourceBundle(ResourceBundle.getBundle("terminalConfig"))
                .includeAndReorder(
                        "defaultImageScale",
                        "defaultImageZoom",
                        "defaultImageDpi")
                .build();

        DefaultFactoryProvider editorConfigFormProvider = new DefaultFactoryProvider();

        FileChooserEditableFactory fileChooserEditableFactory = new FileChooserEditableFactory();
        editorConfigForm.setEditorFactoryProvider(editorConfigFormProvider);

        fileChooserEditableFactory.setOnEdit(tabService::addTab);

        editorConfigForm.setSource(this);

        VBox vBox = new VBox();
        vBox.getChildren().add(editorConfigForm);

        saveButton.setOnAction(this::save);
        loadButton.setOnAction(this::load);
        HBox box = new HBox(5, saveButton, loadButton, infoLabel);
        box.setPadding(new Insets(0, 0, 15, 5));
        vBox.getChildren().add(box);

        return vBox;
    }

    @Override
    public Path getConfigPath() {
        return super.resolveConfigPath("extension_config.json");
    }

    @Override
    public void load(Path configPath, ActionEvent... actionEvent) {

        fadeOut(infoLabel, "Loading...");

        Reader fileReader = IOHelper.fileReader(configPath);
        JsonReader jsonReader = Json.createReader(fileReader);

        JsonObject jsonObject = jsonReader.readObject();

        Integer defaultImageScale = jsonObject.getInt("defaultImageScale", this.defaultImageScale.getValue());
        Integer defaultImageZoom = jsonObject.getInt("defaultImageZoom", this.defaultImageZoom.getValue());
        Integer defaultImageDpi = jsonObject.getInt("defaultImageDpi", this.defaultImageDpi.getValue());

        IOHelper.close(jsonReader, fileReader);

        threadService.runActionLater(() -> {

            this.setDefaultImageScale(defaultImageScale);
            this.setDefaultImageZoom(defaultImageZoom);
            this.setDefaultImageDpi(defaultImageDpi);

            fadeOut(infoLabel, "Loaded...");

        });
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

        objectBuilder
                .add("defaultImageScale", getDefaultImageScale())
                .add("defaultImageZoom", getDefaultImageZoom())
                .add("defaultImageDpi", getDefaultImageDpi());

        return objectBuilder.build();
    }
}

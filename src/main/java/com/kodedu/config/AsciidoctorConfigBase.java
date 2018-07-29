package com.kodedu.config;

//import com.dooapp.fxform.annotation.Accessor;

import com.dooapp.fxform.FXForm;
import com.dooapp.fxform.builder.FXFormBuilder;
import com.dooapp.fxform.handler.NamedFieldHandler;
import com.dooapp.fxform.view.factory.DefaultFactoryProvider;
import com.kodedu.config.factory.ListChoiceBoxFactory;
import com.kodedu.config.factory.TableFactory;
import com.kodedu.controller.ApplicationController;
import com.kodedu.other.IOHelper;
import com.kodedu.service.ThreadService;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.json.*;
import java.io.Reader;
import java.nio.file.Path;
import java.util.*;

/**
 * Created by usta on 17.07.2015.
 */
public abstract class AsciidoctorConfigBase extends ConfigurationBase {

    public ObjectProperty<JSPlatform> jsPlatform = new SimpleObjectProperty<>(JSPlatform.Webkit);

    private StringProperty safe = new SimpleStringProperty("safe");

    private BooleanProperty sourcemap = new SimpleBooleanProperty();

    private BooleanProperty header_footer = new SimpleBooleanProperty();

    private StringProperty backend = new SimpleStringProperty();

    private ListProperty<AttributesTable> attributes = new SimpleListProperty<>(FXCollections.observableArrayList());

    private final ApplicationController controller;

    private final ThreadService threadService;

    private Logger logger = LoggerFactory.getLogger(AsciidoctorConfigBase.class);

    private List<String> propertyList = Arrays.asList("attributes");

    private final Button saveButton = new Button("Save");
    private final Button loadButton = new Button("Load");
    private final Label infoLabel = new Label();

    public AsciidoctorConfigBase(ApplicationController controller, ThreadService threadService) {
        super(controller, threadService);
        this.controller = controller;
        this.threadService = threadService;
    }

    public String getBackend() {
        return backend.get();
    }

    public StringProperty backendProperty() {
        return backend;
    }

    public void setBackend(String backend) {
        this.backend.set(backend);
    }

    public String getSafe() {
        return safe.get();
    }

    public StringProperty safeProperty() {
        return safe;
    }

    public void setSafe(String safe) {
        this.safe.set(safe);
    }

    public boolean getSourcemap() {
        return sourcemap.get();
    }

    public BooleanProperty sourcemapProperty() {
        return sourcemap;
    }

    public void setSourcemap(boolean sourcemap) {
        this.sourcemap.set(sourcemap);
    }

    public ObservableList<AttributesTable> getAttributes() {
        return attributes.get();
    }

    public ListProperty<AttributesTable> attributesProperty() {
        return attributes;
    }

    public void setAttributes(ObservableList<AttributesTable> attributes) {
        this.attributes.set(attributes);
    }

    public boolean getHeader_footer() {
        return header_footer.get();
    }

    public BooleanProperty header_footerProperty() {
        return header_footer;
    }

    public void setHeader_footer(boolean header_footer) {
        this.header_footer.set(header_footer);
    }

    public JSPlatform getJsPlatform() {
        return jsPlatform.get();
    }

    public ObjectProperty<JSPlatform> jsPlatformProperty() {
        return jsPlatform;
    }

    public void setJsPlatform(JSPlatform jsPlatform) {
        this.jsPlatform.set(jsPlatform);
    }


    public FXForm getConfigForm() {
        FXForm configForm = new FXFormBuilder<>()
                .resourceBundle(ResourceBundle.getBundle("asciidoctorConfig"))
                .includeAndReorder(
//                        "jsPlatform",
                        "attributes").build();

        return configForm;
    }


    @Override
    public VBox createForm() {

        FXForm configForm = getConfigForm();

        DefaultFactoryProvider previewConfigFormProvider = new DefaultFactoryProvider();
        previewConfigFormProvider.addFactory(new NamedFieldHandler("safe"), new ListChoiceBoxFactory(new ChoiceBox()));
        previewConfigFormProvider.addFactory(new NamedFieldHandler("attributes"), new TableFactory(new TableView()));
        configForm.setEditorFactoryProvider(previewConfigFormProvider);

        configForm.setSource(this);

        VBox vBox = new VBox();
        vBox.getChildren().add(configForm);


        saveButton.setOnAction(this::save);

        loadButton.setOnAction(this::load);
        HBox box = new HBox(5, saveButton, loadButton, infoLabel);
        box.setPadding(new Insets(0, 0, 15, 5));
        vBox.getChildren().add(box);

        return vBox;
    }

    @Override
    public abstract Path getConfigPath();

    @Override
    public void load(Path configPath, ActionEvent... actionEvent) {

        infoLabel.setText("Loading...");

        Reader fileReader = IOHelper.fileReader(configPath);
        JsonReader jsonReader = Json.createReader(fileReader);

        JsonObject jsonObject = jsonReader.readObject();

        String safe = jsonObject.getString("safe", "safe");
        boolean sourcemap = jsonObject.getBoolean("sourcemap", false);
        String backend = jsonObject.getString("backend", null);
        boolean header_footer = jsonObject.getBoolean("header_footer", false);
        JsonObject attributes = jsonObject.getJsonObject("attributes");
        String jsPlatform = jsonObject.getString("jsPlatform", "Webkit");

        IOHelper.close(jsonReader, fileReader);

        threadService.runActionLater(() -> {
            this.setSafe(safe);
            this.sourcemap.set(sourcemap);
            this.setBackend(backend);
            this.setHeader_footer(header_footer);
            this.setJsPlatform(JSPlatform.valueOf(JSPlatform.class, jsPlatform));

            ObservableList<AttributesTable> attrList = FXCollections.observableArrayList();

            if (Objects.nonNull(attributes)) {
                for (Map.Entry<String, JsonValue> attr : attributes.entrySet()) {
                    AttributesTable attributesTable = new AttributesTable();
                    attributesTable.setAttribute(attr.getKey());
                    attributesTable.setValue(((JsonString) attr.getValue()).getString());
                    attrList.add(attributesTable);
                }
            }

            setAttributes(attrList);

            fadeOut(infoLabel, "Loaded...");
        });
    }

    @Override
    public JsonObject getJSON() {
        JsonObjectBuilder attributesObject = Json.createObjectBuilder();

        ObservableList<AttributesTable> attributes = getAttributes();

        for (AttributesTable attribute : attributes) {
            String value = attribute.getValue();
            if ("false".equalsIgnoreCase(value)) {
                continue;
            }
            String key = attribute.getAttribute();

            if (Objects.nonNull(key) || Objects.nonNull(value)) {
                attributesObject.add(key, value);
            }

        }

        JsonObjectBuilder objectBuilder = Json.createObjectBuilder();

        if (Objects.nonNull(getBackend())) {
            objectBuilder.add("backend", getBackend());
        }

        if (Objects.nonNull(getJsPlatform())) {
            objectBuilder.add("jsPlatform", getJsPlatform().name());
        }

        objectBuilder.add("safe", getSafe());

        if (getSourcemap()) {
            objectBuilder.add("sourcemap", getSourcemap());
        }

        if (getHeader_footer()) {
            objectBuilder.add("header_footer", getHeader_footer());
        }

        objectBuilder.add("attributes", attributesObject);

        return objectBuilder.build();
    }

    @Override
    public void save(ActionEvent... actionEvent) {
        infoLabel.setText("Saving...");
        saveJson(getJSON());
        fadeOut(infoLabel, "Saved...");
    }


}

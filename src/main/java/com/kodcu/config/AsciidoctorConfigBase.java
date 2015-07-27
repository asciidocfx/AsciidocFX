package com.kodcu.config;

//import com.dooapp.fxform.annotation.Accessor;

import com.dooapp.fxform.FXForm;
import com.dooapp.fxform.builder.FXFormBuilder;
import com.dooapp.fxform.handler.NamedFieldHandler;
import com.dooapp.fxform.view.factory.DefaultFactoryProvider;
import com.kodcu.config.factory.ListChoiceBoxFactory;
import com.kodcu.config.factory.TableFactory;
import com.kodcu.controller.ApplicationController;
import com.kodcu.other.IOHelper;
import com.kodcu.service.ThreadService;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.json.*;
import java.io.FileReader;
import java.nio.file.Path;
import java.util.*;

/**
 * Created by usta on 17.07.2015.
 */
public abstract class AsciidoctorConfigBase extends ConfigurationBase {

    private ObjectProperty<ObservableList<String>> safe = new SimpleObjectProperty<>(FXCollections.observableArrayList());

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

    public ObservableList<String> getSafe() {
        return safe.get();
    }

    public ObjectProperty<ObservableList<String>> safeProperty() {
        return safe;
    }

    public void setSafe(ObservableList<String> safe) {
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

    @Override
    public VBox createForm() {
        FXForm previewConfigForm = new FXFormBuilder<>()
                .resourceBundle(ResourceBundle.getBundle("asciidoctorConfig"))
                .includeAndReorder("attributes").build();

        DefaultFactoryProvider previewConfigFormProvider = new DefaultFactoryProvider();
        previewConfigFormProvider.addFactory(new NamedFieldHandler("safe"), new ListChoiceBoxFactory(new ChoiceBox()));
        previewConfigFormProvider.addFactory(new NamedFieldHandler("attributes"), new TableFactory(new TableView()));
        previewConfigForm.setEditorFactoryProvider(previewConfigFormProvider);

        previewConfigForm.setSource(this);

        VBox vBox = new VBox();
        vBox.getChildren().add(previewConfigForm);


        saveButton.setOnAction(this::save);

        loadButton.setOnAction(this::load);
        vBox.getChildren().add(new HBox(10, saveButton, loadButton, infoLabel));

        return vBox;
    }

    @Override
    public abstract Path getConfigPath();

    @Override
    public void load(ActionEvent... actionEvent) {

        infoLabel.setText("Loading...");

        setSafe(FXCollections.observableArrayList("safe", "secure", "server"));

        threadService.runTaskLater(() -> {

            FileReader fileReader = IOHelper.fileReader(getConfigPath());
            JsonReader jsonReader = Json.createReader(fileReader);

            JsonObject jsonObject = jsonReader.readObject();

            String safe = jsonObject.getString("safe", "safe");
            boolean sourcemap = jsonObject.getBoolean("sourcemap", false);
            String backend = jsonObject.getString("backend", null);
            boolean header_footer = jsonObject.getBoolean("header_footer", false);
            JsonObject attributes = jsonObject.getJsonObject("attributes");

            IOHelper.close(jsonReader, fileReader);

            threadService.runActionLater(() -> {
                this.getSafe().set(0, safe);
                this.sourcemap.set(sourcemap);
                this.setBackend(backend);
                this.setHeader_footer(header_footer);

                ObservableList<AttributesTable> attrList = FXCollections.observableArrayList();

                for (Map.Entry<String, JsonValue> attr : attributes.entrySet()) {
                    AttributesTable attributesTable = new AttributesTable();
                    attributesTable.setAttribute(attr.getKey());
                    attributesTable.setValue(((JsonString) attr.getValue()).getString());
                    attrList.add(attributesTable);
                }
                setAttributes(attrList);

                fadeOut(infoLabel, "Loaded...");
            });
        });


    }

    @Override
    public JsonObject getJSON() {
        JsonObjectBuilder attributesObject = Json.createObjectBuilder();

        ObservableList<AttributesTable> attributes = getAttributes();

        for (AttributesTable attribute : attributes) {
            if ("false".equalsIgnoreCase(attribute.getValue())) {
                continue;
            }
            attributesObject.add(attribute.getAttribute(), attribute.getValue());
        }

        JsonObjectBuilder objectBuilder = Json.createObjectBuilder();

        if (Objects.nonNull(getBackend())) {
            objectBuilder.add("backend", getBackend());
        }

        objectBuilder.add("safe", getSafe().get(0));

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

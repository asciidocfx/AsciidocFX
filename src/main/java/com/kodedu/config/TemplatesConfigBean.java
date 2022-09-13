package com.kodedu.config;

import com.kodedu.component.TemplateSubMenu;
import com.kodedu.config.factory.TableFactory;
import com.kodedu.controller.ApplicationController;
import com.kodedu.helper.IOHelper;
import com.kodedu.service.ThreadService;

import org.springframework.stereotype.Component;

import java.io.Reader;
import java.nio.file.Path;
import java.util.Map;
import java.util.Objects;
import java.util.ResourceBundle;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonReader;
import javax.json.JsonString;
import javax.json.JsonValue;

//import com.dooapp.fxform.annotation.Accessor;

import com.dooapp.fxform.FXForm;
import com.dooapp.fxform.builder.FXFormBuilder;
import com.dooapp.fxform.handler.NamedFieldHandler;
import com.dooapp.fxform.view.factory.DefaultFactoryProvider;

import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

@Component
public class TemplatesConfigBean extends ConfigurationBase {

    private final ListProperty<MetaAsciidocTemplate> templates;
    private final ThreadService threadService;
	private final ApplicationController controller;

    private final Button saveButton = new Button("Save");
    private final Button loadButton = new Button("Load");
    private final Label infoLabel = new Label();
	private final TemplateSubMenu templateSubMenu;

    public TemplatesConfigBean(ApplicationController controller,
    		                   ThreadService threadService,
    		                   TemplateSubMenu templateSubMenu) {
        super(controller, threadService);
        this.threadService = threadService;
        this.controller = controller;
        this.templateSubMenu = templateSubMenu;
        templates = new SimpleListProperty<>(FXCollections.observableArrayList());
    }

	@Override
	public String formName() {
		return "Template Settings";
	}

	@Override
	public Path getConfigPath() {
		return super.resolveConfigPath("templates_config.json");
	}

	public ObservableList<MetaAsciidocTemplate> getTemplates() {
		return templates.get();
	}

	public ListProperty<MetaAsciidocTemplate> templatesProperty() {
		return templates;
	}

	public void setTemplates(ObservableList<MetaAsciidocTemplate> templates) {
		this.templates.set(templates);
	}

    public FXForm getConfigForm() {
        FXForm configForm = new FXFormBuilder<>()
                .resourceBundle(ResourceBundle.getBundle("templatesConfig"))
                .includeAndReorder(
                        "templates").build();

        return configForm;
    }

    @Override
    public VBox createForm() {

        FXForm configForm = getConfigForm();

        DefaultFactoryProvider previewConfigFormProvider = getFxFormFactoryProvider();
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
    public void load(Path configPath, ActionEvent... actionEvent) {

        infoLabel.setText("Loading...");

        Reader fileReader = IOHelper.fileReader(configPath);
        JsonReader jsonReader = Json.createReader(fileReader);

        JsonObject jsonObject = jsonReader.readObject();

        JsonObject templates = jsonObject.getJsonObject("templates");
        
        IOHelper.close(jsonReader, fileReader);

        threadService.runActionLater(() -> {

            ObservableList<MetaAsciidocTemplate> templateList = FXCollections.observableArrayList();
            if (Objects.nonNull(templates)) {
                for (Map.Entry<String, JsonValue> template : templates.entrySet()) {
                    var templateLoc = new MetaAsciidocTemplate();
                    templateLoc.setName(template.getKey());
                    
                    var locDescTuple = template.getValue().asJsonObject();
                    var location = locDescTuple.getString("location");
                    var description = locDescTuple.getString("description", "");
                    
                    templateLoc.setLocation(location);
                    templateLoc.setDescription(description);
                    templateList.add(templateLoc);
                }
            }

            setTemplates(templateList);

            fadeOut(infoLabel, "Loaded...");
        });
    }

	@Override
	public JsonObject getJSON() {
		JsonObjectBuilder templatesObject = Json.createObjectBuilder();

		ObservableList<MetaAsciidocTemplate> templates = getTemplates();

		for (MetaAsciidocTemplate template : templates) {
			String key = template.getName();
			String location = template.getLocation();
			String description = template.getDescription();

			JsonObjectBuilder locDescObj = Json.createObjectBuilder();
			locDescObj.add("location", location);
			if (Objects.nonNull(description)) {
				locDescObj.add("description", description);
			}

			if (Objects.nonNull(key) || Objects.nonNull(location)) {
				templatesObject.add(key, locDescObj);
			}
		}

		JsonObjectBuilder objectBuilder = Json.createObjectBuilder();

		objectBuilder.add("templates", templatesObject);

		return objectBuilder.build();
	}

	@Override
    public void save(ActionEvent... actionEvent) {
        infoLabel.setText("Saving...");
        saveJson(getJSON());
    	templateSubMenu.setTemplateMenuItems(getTemplates());
        fadeOut(infoLabel, "Saved...");
    }

    @Override
	public void load(ActionEvent... actionEvent) {
		super.load(actionEvent);
		templateSubMenu.setTemplateMenuItems(getTemplates());
	}

	private DefaultFactoryProvider getFxFormFactoryProvider() {
		DefaultFactoryProvider previewConfigFormProvider = new DefaultFactoryProvider();
	    previewConfigFormProvider.addFactory(new NamedFieldHandler("templates"), new TableFactory(new TableView()));
		return previewConfigFormProvider;
	}

}

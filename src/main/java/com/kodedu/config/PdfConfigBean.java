package com.kodedu.config;

import com.kodedu.config.factory.TableFactory;
import com.kodedu.controller.ApplicationController;
import com.kodedu.service.ThreadService;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Objects;
import java.util.ResourceBundle;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

import org.asciidoctor.Asciidoctor;

import com.dooapp.fxform.FXForm;
import com.dooapp.fxform.builder.FXFormBuilder;
import com.dooapp.fxform.filter.ExcludeFilter;
import com.dooapp.fxform.handler.NamedFieldHandler;
import com.dooapp.fxform.view.factory.DefaultFactoryProvider;

import javafx.application.Platform;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ListProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.control.TableView;


@Component
public class PdfConfigBean extends AsciidoctorConfigBase<PdfConfigAttributes> {

    private final ApplicationController controller;
    private final ThreadService threadService;
	private final Asciidoctor asciidoctor;

    private ObjectProperty<PdfConverterType> converter = new SimpleObjectProperty<>(PdfConverterType.FOP);
    private final ListProperty<PdfTemplateLocation> templates;
	private final ObservableList<PdfTemplateLocation> observableTemplates;

    private final ExcludeFilter attributeExclusion = new ExcludeFilter("attributes");

	@Override
    public String formName() {
        return "PDF Settings";
    }

    public PdfConfigBean(ApplicationController controller, ThreadService threadService,
						 @Qualifier("plainDoctor") Asciidoctor asciidoctor) {
        super(controller, threadService, asciidoctor);
        this.controller = controller;
        this.threadService = threadService;
		this.asciidoctor = asciidoctor;
		
		
        templates = new SimpleListProperty<>(FXCollections.observableArrayList());
		observableTemplates = initObservableTemplates(controller);
	}

    @Override
    public Path getConfigPath() {
        return super.resolveConfigPath("asciidoctor_pdf.json");
    }

	@Override
	protected PdfConfigAttributes loadAdditionalAttributes(JsonObject jsonObject) {
		var attributes = new PdfConfigAttributes();
		String converterStr = jsonObject.getString("converter", PdfConverterType.FOP.name());
		if (PdfConverterType.contains(converterStr)) {
			attributes.converter = PdfConverterType.valueOf(converterStr);
		}
		System.out.println(jsonObject.getJsonArray("templates"));
        ObservableList<PdfTemplateLocation> attrList = FXCollections.observableArrayList();
		attributes.templates = attrList;
		return attributes;
	}
	
    @Override
	protected void fxSetAdditionalAttributes(PdfConfigAttributes childClassAttributes) {
		setPdfConverterType(childClassAttributes.converter);
		setTemplates(childClassAttributes.templates);
	}
    

	@Override
	protected void addAdditionalAttributesToJson(JsonObjectBuilder objectBuilder) {
		objectBuilder.add("converter", getPdfConverterType().name());
		
		JsonObjectBuilder attributesObject = Json.createObjectBuilder();
		ObservableList<PdfTemplateLocation> templates = getTemplates();
		for (PdfTemplateI template : templates) {
			String value = template.getName();
			if ("false".equalsIgnoreCase(value)) {
				continue;
			}
			String key = template.getLocation();

			if (Objects.nonNull(key) || Objects.nonNull(value)) {
				attributesObject.add(key, value);
			}

		}
        objectBuilder.add("templates", attributesObject);
	}

	@Override
	public FXForm getConfigForm() {
		FXForm configForm = new FXFormBuilder<>().resourceBundle(ResourceBundle.getBundle("asciidoctorConfig"))
		                                         .includeAndReorder("converter", "attributes", "templates")
		                                         .build();

		this.converter.addListener((obs, oldValue, newValue) -> {
			performFilters(configForm, newValue);
		});
		performFilters(configForm, getPdfConverterType());
		return configForm;
	}
	
	@Override
	DefaultFactoryProvider getFxFormFactoryProvider() {
		DefaultFactoryProvider factoryProvider = super.getFxFormFactoryProvider();
	    factoryProvider.addFactory(new NamedFieldHandler("templates"), new TableFactory(new TableView()));
		return factoryProvider;
	}

	public PdfConverterType getPdfConverterType() {
        return converter.get();
    }

    public ObjectProperty<PdfConverterType> pdfConverterTypeProperty() {
        return converter;
    }

	public void setPdfConverterType(PdfConverterType pdfConverterType) {
		if (pdfConverterType != null) {
			this.converter.set(pdfConverterType);
		}
	}

	public ObservableList<PdfTemplateLocation> getTemplates() {
		return templates.get();
	}

	public ListProperty<PdfTemplateLocation> templatesProperty() {
		return templates;
	}

	public void setTemplates(ObservableList<PdfTemplateLocation> templates) {
		this.templates.set(templates);
	}
    
    private void performFilters(FXForm configForm, PdfConverterType type) {
		if (PdfConverterType.ASCIIDOCTOR.equals(type)) {
			Platform.runLater(() -> configForm.getFilters().remove(attributeExclusion));
		} else {
			Platform.runLater(() -> configForm.addFilters(attributeExclusion));
		}
	}

	/**
	 * Receiving a change event for each modification of an ObservableList is a bit convoluted.
	 * Nevertheless this seems to be the JavaFx way: https://stackoverflow.com/a/28289021/2021763
	 * @param controller
	 * @return
	 */
	private ObservableList<PdfTemplateLocation> initObservableTemplates(ApplicationController controller) {
		var observableTemplates = FXCollections.observableList(new ArrayList<>(),
		                                                       (PdfTemplateLocation t) -> new Observable[] {
		                                                               t.nameProperty(),
		                                                               t.locationProperty() });
		Bindings.bindContent(observableTemplates, templates);
		observableTemplates.addListener((ListChangeListener<PdfTemplateLocation>) change -> {
			controller.setTemplateMenuItems(getTemplates());
		});
		return observableTemplates;
	}
    
    
}

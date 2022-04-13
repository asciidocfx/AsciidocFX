package com.kodedu.config;

import java.nio.file.Path;
import java.util.Objects;
import java.util.ResourceBundle;

import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

import org.asciidoctor.Attributes;
import org.asciidoctor.AttributesBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dooapp.fxform.FXForm;
import com.dooapp.fxform.builder.FXFormBuilder;
import com.dooapp.fxform.filter.ExcludeFilter;
import com.kodedu.config.AsciidoctorConfigBase.LoadedAttributes;
import com.kodedu.config.PdfConfigBean.PdfConfigAttributes;
import com.kodedu.controller.ApplicationController;
import com.kodedu.service.ThreadService;

import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;


@Component
public class PdfConfigBean extends AsciidoctorConfigBase<PdfConfigAttributes> {

    private final ApplicationController controller;
    private final ThreadService threadService;

    private ObjectProperty<PdfConverterType> converter = new SimpleObjectProperty<>(PdfConverterType.FOP);

    private final ExcludeFilter attributeExclusion = new ExcludeFilter("attributes");

	@Override
    public String formName() {
        return "PDF Settings";
    }

    @Autowired
    public PdfConfigBean(ApplicationController controller, ThreadService threadService) {
        super(controller, threadService);
        this.controller = controller;
        this.threadService = threadService;
    }

    @Override
    public Path getConfigPath() {
        return super.resolveConfigPath("asciidoctor_pdf.json");
    }

	public Attributes getAsciiDocAttributes() {
		AttributesBuilder attributesBuilder = Attributes.builder();

		ObservableList<AttributesTable> attributes = getAttributes();
		for (AttributesTable attribute : attributes) {
			String key = attribute.getAttribute();
			String value = attribute.getValue();

			if (Objects.nonNull(key) || Objects.nonNull(value)) {
				attributesBuilder.attribute(key, value);
			}
		}
		return attributesBuilder.build();
	}

	@Override
	protected PdfConfigAttributes loadAdditionalAttributes(JsonObject jsonObject) {
		var attributes = new PdfConfigAttributes();
		String converterStr = jsonObject.getString("converter", PdfConverterType.FOP.name());
		if (PdfConverterType.contains(converterStr)) {
			attributes.converter = PdfConverterType.valueOf(converterStr);
		}
		return attributes;
	}
	
    @Override
	protected void fxSetAdditionalAttributes(PdfConfigAttributes childClassAttributes) {
		setPdfConverterType(childClassAttributes.converter);
	}
    

	@Override
	protected void addAdditionalAttributesToJson(JsonObjectBuilder objectBuilder) {
		objectBuilder.add("converter", getPdfConverterType().name());
	}

	@Override
	public FXForm getConfigForm() {
		FXForm configForm = new FXFormBuilder<>().resourceBundle(ResourceBundle.getBundle("asciidoctorConfig"))
		                                         .includeAndReorder("converter", "attributes")
		                                         .build();

		this.converter.addListener((obs, oldValue, newValue) -> {
			performFilters(configForm, newValue);
		});
		performFilters(configForm, getPdfConverterType());
		return configForm;
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
    
    
    private void performFilters(FXForm configForm, PdfConverterType type) {
		if (PdfConverterType.ASCIIDOCTOR.equals(type)) {
			Platform.runLater(() -> configForm.getFilters().remove(attributeExclusion));
		} else {
			Platform.runLater(() -> configForm.addFilters(attributeExclusion));
		}
	}


	static class PdfConfigAttributes implements LoadedAttributes {
    	PdfConverterType converter;
    	
    	
    }
    
    
}

package com.kodedu.config;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;


public class PdfTemplateLocation implements PdfTemplateI {

	private final StringProperty name = new SimpleStringProperty();
	private final StringProperty location = new SimpleStringProperty();
	private final StringProperty description = new SimpleStringProperty();

	@Override
	public String getName() {
		return name.get();
	}

	public StringProperty nameProperty() {
		return name;
	}

	public void setName(String name) {
		this.name.set(name);
	}

	@Override
	public String getLocation() {
		return location.get();
	}

	public StringProperty locationProperty() {
		return location;
	}

	public void setLocation(String location) {
		this.location.set(location);
	}
	
	@Override
	public String getDescription() {
		return description.get();
	}

	public StringProperty descriptionProperty() {
		return description;
	}

	public void setDescription(String description) {
		this.description.set(description);
	}
}

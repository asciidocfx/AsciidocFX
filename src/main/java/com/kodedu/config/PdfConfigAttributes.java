package com.kodedu.config;

import com.kodedu.config.AsciidoctorConfigBase.LoadedAttributes;

import javafx.collections.ObservableList;

public class PdfConfigAttributes implements LoadedAttributes {
	PdfConverterType converter;
	ObservableList<PdfTemplateLocation> templates; 
}
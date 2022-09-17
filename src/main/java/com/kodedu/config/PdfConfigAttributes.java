package com.kodedu.config;

import com.kodedu.config.AsciidoctorConfigBase.LoadedAttributes;
import com.kodedu.config.templates.AsciidocTemplateFx;

import javafx.collections.ObservableList;

public class PdfConfigAttributes implements LoadedAttributes {
	PdfConverterType converter;
	ObservableList<AsciidocTemplateFx> templates; 
}
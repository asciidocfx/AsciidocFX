package com.kodedu.template;

import java.nio.file.Path;

public interface MetaAsciidocTemplateI {

	String getName();

	String getLocation();

	String getDescription();

	void furnish(Path directory);

}
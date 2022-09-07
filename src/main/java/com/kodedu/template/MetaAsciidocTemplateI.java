package com.kodedu.template;

import java.nio.file.Path;

public interface MetaAsciidocTemplateI {

	String getName();

	/**
	 * The location of the template.
	 * Supported protocols are: file, http, https
	 * @return the location, never null
	 */
	String getLocation();

	String getDescription();

	void furnish(Path targetDir) throws Exception;

}
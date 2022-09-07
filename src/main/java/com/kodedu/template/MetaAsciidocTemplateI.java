package com.kodedu.template;

import com.kodedu.other.ZipUtils;

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

	void furnish(Path targetDir, ZipUtils zipUtils) throws Exception;

}
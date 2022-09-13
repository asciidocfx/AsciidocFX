package com.kodedu.template;

import com.kodedu.service.UnzipService;

import java.nio.file.Path;

/**
 * An Asciidoc Template can be a predefined single file (e.g. an adoc-file) or a
 * zipped-folder (e.g containing a adoc-file referencing a pdf-theme-yaml).
 * 
 *
 */
public interface AsciidocTemplateI {

	/**
	 * @return the name of the template
	 */
	String getName();

	/**
	 * The location of the template. Supported protocols are: file, http, https
	 * 
	 * @return the location, never null
	 */
	String getLocation();

	/**
	 * @return the description of the template
	 */
	String getDescription();

	/**
	 * Depending on the location of the template downloads it or copies the file. If
	 * it is a zip-archive it should be extracted. During the process no existing
	 * files should be overwritten.
	 * 
	 * @param targetDir
	 * @param zipUtils
	 * @throws Exception
	 */
	void provide(Path targetDir, UnzipService zipUtils) throws Exception;

}
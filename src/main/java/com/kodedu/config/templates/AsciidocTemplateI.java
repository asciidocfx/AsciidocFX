package com.kodedu.config.templates;

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

}
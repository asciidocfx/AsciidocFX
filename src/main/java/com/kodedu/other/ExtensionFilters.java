package com.kodedu.other;

import javafx.stage.FileChooser;

/**
 * Created by usta on 23.08.2015.
 */
public class ExtensionFilters {

    public final static FileChooser.ExtensionFilter HTML = new FileChooser.ExtensionFilter("HTML", "*.html");
    public final static FileChooser.ExtensionFilter XML = new FileChooser.ExtensionFilter("XML", "*.xml");
    public final static FileChooser.ExtensionFilter ALL = new FileChooser.ExtensionFilter("All files", "*.*");
    public final static FileChooser.ExtensionFilter PDF = new FileChooser.ExtensionFilter("PDF", "*.pdf");
    public final static FileChooser.ExtensionFilter DOCBOOK = new FileChooser.ExtensionFilter("DOCBOOK", "*.xml");
    public final static FileChooser.ExtensionFilter EPUB = new FileChooser.ExtensionFilter("EPUB", "*.epub");
    public final static FileChooser.ExtensionFilter MOBI = new FileChooser.ExtensionFilter("MOBI", "*.mobi");
    public final static FileChooser.ExtensionFilter ODT = new FileChooser.ExtensionFilter("ODT", "*.odt");
    public final static FileChooser.ExtensionFilter ASCIIDOC = new FileChooser.ExtensionFilter("AsciiDoc", "*.adoc", "*.asciidoc", "*.asc", "*.ad");
    public final static FileChooser.ExtensionFilter MARKDOWN = new FileChooser.ExtensionFilter("Markdown", "*.md", "*.markdown");

}

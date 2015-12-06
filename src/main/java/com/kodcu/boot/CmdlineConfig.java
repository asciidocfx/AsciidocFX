package com.kodcu.boot;

import de.tototec.cmdoption.CmdOption;

import java.util.LinkedList;
import java.util.List;

public class CmdlineConfig {

    @CmdOption(names = {"--help", "-h"}, description = "Show this help")
    boolean help = false;

    @CmdOption(names = {"-v","--version"}, description = "Print program version number")
    boolean version = false;

    @CmdOption(args = {"FILE"}, description = "Asciidoc file. Specifying one or more asciidoc files with no other options will trigger AsciidocFX (e.g. $ asciidocfx file1 file2)", maxCount = -1)
    final List<String> files = new LinkedList<String>();

    @CmdOption(names = {"-b","--backend"}, description = "set output format backend: [html5, pdf, odt, docbook5, markdown, revealjs, deskjs]", args = {"BACKEND"})
    final List<String> backend = new LinkedList<String>();

    @CmdOption(names = {"-d", "--destination-dir"}, description = "destination output directory", args = {"DIR"})
    final List<String> destination = new LinkedList<String>();

    @CmdOption(names = {"-o", "--out-file"}, description = "output file (default: based on path of input file)", args = {"FILE"})
    final List<String> output = new LinkedList<String>();
}

package com.kodcu.boot;

import java.util.LinkedList;
import java.util.List;

import de.tototec.cmdoption.CmdOption;

public class CmdlineConfig {

    @CmdOption(names = { "--help", "-h" }, description = "Show this help")
    boolean help = false;

    @CmdOption(args = "FILE", description = "File to open", maxCount = -1)
    final List<String> files = new LinkedList<String>();

}

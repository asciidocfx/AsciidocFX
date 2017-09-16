package com.kodedu.boot;

import de.tototec.cmdoption.CmdOption;

import java.util.LinkedList;
import java.util.List;

public class CmdlineConfig {

    @CmdOption(names = {"--help", "-h"}, description = "Show this help")
    boolean help = false;

    @CmdOption(args = "FILE", description = "File to open", maxCount = -1)
    final List<String> files = new LinkedList<String>();

}

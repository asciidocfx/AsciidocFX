package com.kodedu.boot;

import de.tototec.cmdoption.CmdOption;

import java.util.LinkedList;
import java.util.List;

public class CmdlineConfig {

    @CmdOption(names = {"--help", "-h"}, description = "Show this help")
    boolean help = false;

    @CmdOption(args = "FILE", description = "File to open", maxCount = -1)
    final List<String> files = new LinkedList<>();

    @CmdOption(names = {"--workdir", "-w"}, args = "DIRECTORY", description = "Working directory to use when generating opened files views")
    String workingDirectory = null;

    @CmdOption(names = {"--backend", "-b"}, args = "BACKEND", description = "Defines output format: pdf, html, docbook")
    String backend = null;

    @CmdOption(names = {"--headless", "-H"}, description = "Selects to start AsciidocFX in headless mode")
    boolean headless = false;

    @CmdOption(names = {"--keep-after", "-K"}, description = "Keeps AsciidocFX running after conversion completed")
    boolean noQuitAfter = false;

    public boolean isCmdStart() {
        return !files.isEmpty();
    }

    @Override
    public String toString() {
        return "CmdlineConfig{" +
                "help=" + help +
                ", files=" + files +
                ", workingDirectory='" + workingDirectory + '\'' +
                ", backend='" + backend + '\'' +
                ", headless=" + headless +
                ", noQuitAfter=" + noQuitAfter +
                '}';
    }
}

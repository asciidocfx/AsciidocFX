package com.kodcu.boot;

import com.kodcu.commandline.AfxUsageFormatter;
import com.kodcu.commandline.CmdlineStarter;
import de.tototec.cmdoption.CmdlineParser;
import de.tototec.cmdoption.CmdlineParserException;
import javafx.application.Application;

/**
 * Created by Hakan on 12/6/2015.
 */
public class AppEntrance {

    public static void main(String[] args) {
        final CmdlineConfig config = new CmdlineConfig();
        final CmdlineParser cp = new CmdlineParser(config);

        try {
            cp.parse(args);
        } catch (final CmdlineParserException e) {
            System.err.println("Invalid commandline given: " + e.getMessage());
            System.exit(1);
        }

        if (controlFlags(config, cp))
            new CmdlineStarter(config, cp).start();
        else
            Application.launch(AppStarter.class, args);
    }

    private static boolean controlFlags(CmdlineConfig config, CmdlineParser cp) {
        cp.setProgramName("asciidocfx");
        cp.setAboutLine("\nAsciidocFX 1.4.2\nThe asciidocfx command line interface (CLI) converts the AsciiDoc source file to HTML5, DocBook 5, PDF, ODT, MARKDOWN, DESKJS, and REVEALJS.\n" +
                "By default, the output is written to a file with the basename of the source file and the appropriate extension.\n" +
                "This is the minimal command chain example to work with ACLI: asciidocfx -b html5 source.adoc\n" +
                "Or you can specify a destination path along with an appropriate extension and an out file\n" +
                "Example: asciidocfx -b pdf -d /path/to/dest -o pdfsource.pdf source.adoc");
        cp.setUsageFormatter(new AfxUsageFormatter(true, 160));

        if (config.help || config.version) {
            cp.usage();
            System.exit(0);
        }

        return (config.backend.size() != 0 && config.files.size() != 0);
    }

}

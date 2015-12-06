package com.kodcu.boot;

import com.kodcu.commandline.AfxUsageFormatter;
import com.kodcu.controller.ApplicationController;
import de.tototec.cmdoption.CmdlineParser;
import de.tototec.cmdoption.CmdlineParserException;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

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

        if (controlArgs(config, cp))
            AppStarter.launch(args);
        else
            new CmdLineStart(config,cp).start();
    }

    private static boolean controlArgs(CmdlineConfig config, CmdlineParser cp) {
        cp.setProgramName("asciidocfx");
        cp.setAboutLine("\nThe asciidocfx command line interface (CLI) converts the AsciiDoc source file to HTML5, DocBook 5, PDF, ODT, MARKDOWN, DESKJS, and REVEALJS.\n" +
                "By default, the output is written to a file with the basename of the source file and the appropriate extension.\n" +
                "Example: asciidocfx -b html5 source.adoc\n" +
                "Or you can specify a destination path along with an appropriate extension and an out file\n" +
                "Example: asciidocfx -b pdf -d /path/to/dest -o pdfsource.pdf source.adoc");
        cp.setUsageFormatter(new AfxUsageFormatter(true, 160));

        if (config.help) {
            cp.usage();
            return false;
        }

        if (config.version){
            cp.setAboutLine("AsciidocFX 1.4.2");
            return false;
        }

        return !((config.backend.size() != 0 && config.files.size() != 0 ) || config.destination.size() != 0 || config.output.size() != 0);
    }

    private static class CmdLineStart {

        private CmdlineConfig config;
        private CmdlineParser cp;

        public CmdLineStart(CmdlineConfig config, CmdlineParser cp) {
            this.config = config;
            this.cp = cp;
        }


        public void start() {
            final AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(SpringCoreConfig.class);
//            ctx.register(SpringAppConfig.class);
//            ctx.refresh();
//            ApplicationController pdf = ctx.getBean(ApplicationController.class);
//            controller = ctx.getBean(ApplicationController.class);
            System.out.println("gmmm");
        }
    }
}

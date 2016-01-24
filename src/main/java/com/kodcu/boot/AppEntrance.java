package com.kodcu.boot;

import com.kodcu.commandline.CmdConfig;
import com.kodcu.commandline.CmdlineStarter;
import de.tototec.cmdoption.CmdlineParser;
import de.tototec.cmdoption.CmdlineParserException;
import javafx.application.Application;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * Created by Hakan on 12/6/2015.
 */
public class AppEntrance {

    public static void main(String[] args) {
        final AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(CmdConfig.class);
        final CmdlineConfig config = ctx.getBean(CmdlineConfig.class);
        final CmdlineParser cp = ctx.getBean(CmdlineParser.class);

        try {
            cp.parse(args);
        } catch (final CmdlineParserException e) {
            System.err.println("Invalid commandline given: " + e.getMessage());
            System.exit(1);
        }

        if (controlFlags(config, cp)) {
            CmdlineStarter starter = ctx.getBean(CmdlineStarter.class);
            starter.start();
        }
        else {
            String[] files;
            if (!config.getFiles().isEmpty())
                files = config.getFiles().stream().toArray(String[]::new);
            else
                files = args;
            Application.launch(AppStarter.class, files);
        }
    }

    private static boolean controlFlags(CmdlineConfig config, CmdlineParser cp) {
        if (config.help || config.version) {
            cp.usage();
            System.exit(0);
        }
        // we are looking at a backend type and a file to be converted to
        return (config.backend.size() != 0 && config.files.size() != 0);
    }

}

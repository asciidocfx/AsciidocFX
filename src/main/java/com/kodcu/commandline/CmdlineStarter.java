package com.kodcu.commandline;

import com.kodcu.boot.CmdlineConfig;
import de.tototec.cmdoption.CmdlineParser;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * Created by Hakan on 12/6/2015.
 */
public class CmdlineStarter {

    private CmdlineConfig config;
    private CmdlineParser cp;

    public CmdlineStarter(CmdlineConfig config, CmdlineParser cp) {
        this.config = config;
        this.cp = cp;
    }


    public void start() {
        final AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
    }
}

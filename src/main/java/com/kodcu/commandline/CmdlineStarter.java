package com.kodcu.commandline;

import com.kodcu.boot.CmdlineConfig;
import com.kodcu.commandline.service.CmdPdfConverter;
import de.tototec.cmdoption.CmdlineParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by Hakan on 12/6/2015.
 */
@Component
public class CmdlineStarter {

    private CmdlineConfig config;
    private CmdlineParser cp;
    private CmdPdfConverter pdf;

    @Autowired
    public CmdlineStarter(CmdlineConfig config, CmdlineParser cp, CmdPdfConverter pdf) {
        this.config = config;
        this.cp = cp;
        this.pdf = pdf;
    }

    public void start() {
        String backend = config.getBackend().get(0).toLowerCase();
        switch (backend) {
            case "pdf":
                pdf.convert(config);
                break;
            default:
                System.out.println("Please specify a correct backend. For more type -h");
                break;
        }
    }
}

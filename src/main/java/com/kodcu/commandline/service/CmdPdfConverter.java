package com.kodcu.commandline.service;

import com.kodcu.boot.CmdlineConfig;
import org.asciidoctor.Asciidoctor;
import org.springframework.stereotype.Component;

import java.io.File;

import static org.asciidoctor.Asciidoctor.Factory.create;
import static org.asciidoctor.OptionsBuilder.options;

/**
 * Created by Hakan on 1/22/2016.
 */
@Component
public class CmdPdfConverter {

    public void convert(CmdlineConfig config) {
        // just try asciidoctor-pdf service
        Asciidoctor asciidoctor = create();
        File inputFile = new File(config.getFiles().get(0));
        asciidoctor.convertFile(inputFile, options().backend("pdf").get());
    }
}

package com.kodcu.commandline;

import com.kodcu.boot.CmdlineConfig;
import de.tototec.cmdoption.CmdlineParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

/**
 * Created by Hakan on 1/22/2016.
 */
@Configuration()
@ComponentScan(basePackages = "com.kodcu.commandline.**")
@PropertySource("classpath:application.properties")
public class CmdConfig {

    @Autowired
    Environment env;

    @Bean
    public CmdlineConfig cmdlineConfig() {
        final CmdlineConfig config = new CmdlineConfig();
        return config;
    }

    @Bean
    public CmdlineParser cmdlineParser() {
        final CmdlineParser cp = new CmdlineParser(cmdlineConfig());
        cp.setProgramName("asciidocfx");
        cp.setAboutLine("\n" +
                env.getProperty("application.name").concat(" ").concat(env.getProperty("application.version")).concat("\n").concat(env.getProperty("application.website")) +
                "\n" +
                "The AsciidocFX command line interface (CLI) converts the AsciiDoc source file to HTML5, DocBook 5, PDF, ODT, MARKDOWN, DESKJS, and REVEALJS.\n" +
                "By default, the output is written to a file with the basename of the source file and the appropriate extension.\n" +
                "This is the minimal command chain example to work with AFX CLI: \n" +
                "$ asciidocfx -b html5 source.adoc\n" +
                "Or you can specify a destination path along with an out file (name & extension)\n" +
                "Example: $ asciidocfx -b pdf -d /path/to/dest -o pdfsource.pdf source.adoc");
        cp.setUsageFormatter(new AfxUsageFormatter(true, 160));
        return cp;
    }


}

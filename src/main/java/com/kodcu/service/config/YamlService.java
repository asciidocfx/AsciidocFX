package com.kodcu.service.config;

import com.esotericsoftware.yamlbeans.YamlWriter;
import com.kodcu.bean.Config;
import com.kodcu.bean.RecentFiles;
import com.kodcu.controller.ApplicationController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;

/**
 * Created by usta on 26.12.2014.
 */
@Component
public class YamlService {

    private final ApplicationController controller;
    
    @Autowired
    public YamlService(final ApplicationController controller) {
        this.controller = controller;
    }

    public void persist() throws IOException {
        Path configPath = controller.getConfigPath();
        File recentFileYml = configPath.resolve("recentFiles.yml").toFile();
        YamlWriter yamlWriter = new YamlWriter(new FileWriter(recentFileYml));
        yamlWriter.getConfig().setClassTag("RecentFiles", RecentFiles.class);
        yamlWriter.write(new RecentFiles(controller.getRecentFiles()));
        yamlWriter.close();

        //

        File configYml = configPath.resolve("config.yml").toFile();
        yamlWriter = new YamlWriter(new FileWriter(configYml));
        yamlWriter.getConfig().setClassTag("Config", Config.class);
        yamlWriter.write(controller.getConfig());
        yamlWriter.close();
    }
}

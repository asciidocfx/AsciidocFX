package com.kodcu.service.config;

import com.kodcu.bean.RecentFiles;
import com.kodcu.controller.ApplicationController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.yaml.snakeyaml.Yaml;

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

        Yaml yaml=new Yaml();
        try(FileWriter writer = new FileWriter(recentFileYml);){
            controller.getRecentFiles().setFiles(controller.getRecentFilesList());
            yaml.dump(controller.getRecentFiles(), writer);
        }
    }
}

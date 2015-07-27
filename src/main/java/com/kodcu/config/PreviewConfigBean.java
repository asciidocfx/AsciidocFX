package com.kodcu.config;

import com.dooapp.fxform.annotation.NonVisual;
import com.kodcu.controller.ApplicationController;
import com.kodcu.service.ThreadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.nio.file.Path;

/**
 * Created by usta on 19.07.2015.
 */
@Component
public class PreviewConfigBean extends AsciidoctorConfigBase {

    private final ApplicationController controller;
    private final ThreadService threadService;

    @Autowired
    public PreviewConfigBean(ApplicationController controller, ThreadService threadService) {
        super(controller, threadService);
        this.controller = controller;
        this.threadService = threadService;
    }

    @Override
    public Path getConfigPath() {
        return getConfigDirectory().resolve("asciidoctor_preview.json");
    }
}

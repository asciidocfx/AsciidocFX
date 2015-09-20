package com.kodcu.config;

import com.dooapp.fxform.FXForm;
import com.dooapp.fxform.builder.FXFormBuilder;
import com.kodcu.controller.ApplicationController;
import com.kodcu.service.ThreadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.util.ResourceBundle;

/**
 * Created by usta on 19.07.2015.
 */
@Component
public class OdfConfigBean extends AsciidoctorConfigBase {

    private final ApplicationController controller;
    private final ThreadService threadService;

    @Override
    public String formName() {
        return "Odt Settings";
    }

    @Autowired
    public OdfConfigBean(ApplicationController controller, ThreadService threadService) {
        super(controller, threadService);
        this.controller = controller;
        this.threadService = threadService;
    }

    @Override
    public FXForm getConfigForm() {
        FXForm configForm = new FXFormBuilder<>()
                .resourceBundle(ResourceBundle.getBundle("asciidoctorConfig"))
                .includeAndReorder("attributes").build();

        return configForm;
    }

    @Override
    public Path getConfigPath() {
        return super.resolveConfigPath("asciidoctor_odf.json");
    }
}

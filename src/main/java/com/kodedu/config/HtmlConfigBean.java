package com.kodedu.config;

import com.kodedu.config.AsciidoctorConfigBase.NoAttributes;
import com.kodedu.controller.ApplicationController;
import com.kodedu.service.ThreadService;

import org.asciidoctor.Asciidoctor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.nio.file.Path;

/**
 * Created by usta on 19.07.2015.
 */
@Component
public class HtmlConfigBean extends AsciidoctorConfigBase<NoAttributes> {

    private final ApplicationController controller;
    private final ThreadService threadService;

    @Override
    public String formName() {
        return "HTML Config";
    }

    @Autowired
    public HtmlConfigBean(ApplicationController controller, ThreadService threadService) {
        super(controller, threadService);
        this.controller = controller;
        this.threadService = threadService;
    }

    @Override
    public Path getConfigPath() {
        return super.resolveConfigPath("asciidoctor_html.json");
    }
}

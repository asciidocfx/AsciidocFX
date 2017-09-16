package com.kodedu.engine;

import com.kodedu.config.AsciidoctorConfigBase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

/**
 * Created by usta on 22.08.2015.
 */
@Component
public class AsciidocConverterProvider {

    private final ApplicationContext applicationContext;

    @Autowired
    public AsciidocConverterProvider(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    public AsciidocConvertible get(AsciidoctorConfigBase configBase) {

        String name = configBase.getJsPlatform().name();

        AsciidocConvertible bean = applicationContext.getBean(name + "Engine", AsciidocConvertible.class);
        return bean;
    }

}

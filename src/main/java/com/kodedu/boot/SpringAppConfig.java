/*
 * Copyright 2012-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.kodedu.boot;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kodedu.controller.ApplicationController;
import com.kodedu.service.extension.chart.FxChartBlockProcessor;

import com.kodedu.service.extension.math.MathBlockMacroProcessor;
import com.kodedu.service.extension.math.MathBlockProcessor;
import com.kodedu.service.extension.math.MathInlineMacroProcessor;
import com.kodedu.service.extension.processor.CacheSuffixAppenderProcessor;
import com.kodedu.service.extension.processor.DataLineProcessor;
import com.kodedu.service.extension.processor.DocumentAttributeProcessor;
import com.kodedu.service.extension.processor.ExtensionPreprocessor;
import com.kodedu.service.extension.tree.FileTreeBlockMacroProcessor;
import com.kodedu.service.extension.tree.FileTreeBlockProcessor;
import com.kodedu.service.extension.tree.FileTreeInlineMacroProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import java.util.Base64;

import org.asciidoctor.Asciidoctor;


@Configuration
@EnableWebSocket
@ComponentScan(basePackages = "com.kodedu.**")
@EnableAutoConfiguration
public class SpringAppConfig extends SpringBootServletInitializer implements WebSocketConfigurer {

    @Autowired
    private ApplicationController applicationController;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(applicationController, "/ws", "/ws**", "/ws/**").withSockJS();
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {

        return application.sources(SpringAppConfig.class);
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public Base64.Encoder base64Encoder() {
        return Base64.getEncoder();
    }

    @Bean(destroyMethod = "shutdown")
    @Primary
    public Asciidoctor standardDoctor(FxChartBlockProcessor fxChartBlockProcessor,
                                      FileTreeBlockProcessor treeBlockProcessor,
                                      MathBlockProcessor mathBlockProcessor,
                                      ExtensionPreprocessor extensionPreprocessor,
                                      FileTreeBlockMacroProcessor fileTreeBlockMacroProcessor,
                                      FileTreeInlineMacroProcessor fileTreeInlineMacroProcessor,
                                      DataLineProcessor dataLineProcessor,
                                      MathBlockMacroProcessor mathBlockMacroProcessor,
                                      MathInlineMacroProcessor mathInlineMacroProcessor,
                                      CacheSuffixAppenderProcessor cacheSuffixAppenderProcessor) {
        Asciidoctor doctor = Asciidoctor.Factory.create();
        doctor.requireLibrary("asciidoctor-pdf");
        doctor.requireLibrary("asciidoctor-diagram");
        doctor.requireLibrary("asciidoctor-epub3");
        doctor.requireLibrary("asciidoctor-revealjs");
        doctor.javaExtensionRegistry()
                .treeprocessor(dataLineProcessor)
                .block(fxChartBlockProcessor)
                .block(treeBlockProcessor)
                .block(mathBlockProcessor)
                .preprocessor(extensionPreprocessor)
                .blockMacro(fileTreeBlockMacroProcessor)
                .blockMacro(mathBlockMacroProcessor)
                .inlineMacro(fileTreeInlineMacroProcessor)
                .inlineMacro(mathInlineMacroProcessor)
                .treeprocessor(cacheSuffixAppenderProcessor);

        return doctor;
    }

    @Bean(destroyMethod = "shutdown")
    public Asciidoctor plainDoctor(DocumentAttributeProcessor documentAttributeProcessor) {
        Asciidoctor doctor = Asciidoctor.Factory.create();
        doctor.requireLibrary("asciidoctor-pdf");
        doctor.requireLibrary("asciidoctor-diagram");
        doctor.requireLibrary("asciidoctor-epub3");
        doctor.requireLibrary("asciidoctor-revealjs");
        doctor.unregisterAllExtensions();
        doctor.javaExtensionRegistry()
                        .postprocessor(documentAttributeProcessor);
        return doctor;
    }

    @Bean
    public ObjectMapper objectMapper(){
        return new ObjectMapper();
    }

}

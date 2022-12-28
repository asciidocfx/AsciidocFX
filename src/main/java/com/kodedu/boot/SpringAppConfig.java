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
import com.kodedu.service.AsciidoctorFactory;
import com.kodedu.service.extension.chart.ChartBlockProcessor;
import com.kodedu.service.extension.math.block.MathBlockProcessor;
import com.kodedu.service.extension.math.blockmacro.MathBlockMacroProcessor;
import com.kodedu.service.extension.math.inlinemacro.MathInlineMacroProcessor;
import com.kodedu.service.extension.processor.CacheSuffixAppenderProcessor;
import com.kodedu.service.extension.processor.DataLineProcessor;
import com.kodedu.service.extension.processor.DocumentAttributeProcessor;
import com.kodedu.service.extension.processor.ExtensionPreprocessor;
import com.kodedu.service.extension.tree.FileTreeBlockMacroProcessor;
import com.kodedu.service.extension.tree.FileTreeBlockProcessor;
import com.kodedu.service.extension.tree.FileTreeInlineMacroProcessor;
import org.asciidoctor.Asciidoctor;
import org.asciidoctor.extension.JavaExtensionRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.task.TaskExecutionAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.core.task.support.TaskExecutorAdapter;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import java.util.Base64;
import java.util.concurrent.Executors;


@Configuration
@EnableWebSocket
@ComponentScan(basePackages = "com.kodedu.**")
@EnableAutoConfiguration
public class SpringAppConfig extends SpringBootServletInitializer implements WebSocketConfigurer {

    @Autowired
    private ApplicationController applicationController;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(applicationController, "/ws", "/ws*", "/ws/*").withSockJS();
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

    /*
        Used for standard html5 backend
    */
    @Bean(destroyMethod = "shutdown")
    @Lazy
    public Asciidoctor htmlDoctor(ChartBlockProcessor fxChartBlockProcessor,
                                  FileTreeBlockProcessor treeBlockProcessor,
                                  MathBlockProcessor[] mathBlockProcessor,
                                  ExtensionPreprocessor extensionPreprocessor,
                                  FileTreeBlockMacroProcessor fileTreeBlockMacroProcessor,
                                  FileTreeInlineMacroProcessor fileTreeInlineMacroProcessor,
                                  DataLineProcessor dataLineProcessor,
                                  MathBlockMacroProcessor[] mathBlockMacroProcessor,
                                  MathInlineMacroProcessor[] mathInlineMacroProcessor,
                                  CacheSuffixAppenderProcessor cacheSuffixAppenderProcessor,
                                  DocumentAttributeProcessor documentAttributeProcessor) {
        Asciidoctor asciidoctor = AsciidoctorFactory.getAsciidoctor();
        asciidoctor.requireLibrary("openssl", "asciidoctor-diagram");
        Thread.startVirtualThread(() -> {
            registerDefaultExtensions(asciidoctor,
                    fxChartBlockProcessor,
                    treeBlockProcessor,
                    mathBlockProcessor,
                    extensionPreprocessor,
                    fileTreeBlockMacroProcessor,
                    fileTreeInlineMacroProcessor,
                    dataLineProcessor,
                    mathBlockMacroProcessor,
                    mathInlineMacroProcessor,
                    cacheSuffixAppenderProcessor)
                    // Extra extensions
                    .postprocessor(documentAttributeProcessor);
        });
        return asciidoctor;
    }

    /*
        Used for reveal.js backend
    */
    @Bean(destroyMethod = "shutdown")
    @Lazy
    public Asciidoctor revealDoctor(ChartBlockProcessor fxChartBlockProcessor,
                                  FileTreeBlockProcessor treeBlockProcessor,
                                  MathBlockProcessor[] mathBlockProcessor,
                                  ExtensionPreprocessor extensionPreprocessor,
                                  FileTreeBlockMacroProcessor fileTreeBlockMacroProcessor,
                                  FileTreeInlineMacroProcessor fileTreeInlineMacroProcessor,
                                  DataLineProcessor dataLineProcessor,
                                  MathBlockMacroProcessor[] mathBlockMacroProcessor,
                                  MathInlineMacroProcessor[] mathInlineMacroProcessor,
                                  CacheSuffixAppenderProcessor cacheSuffixAppenderProcessor,
                                  DocumentAttributeProcessor documentAttributeProcessor) {
        Asciidoctor asciidoctor = AsciidoctorFactory.getAsciidoctor();
        asciidoctor.requireLibrary("openssl", "asciidoctor-diagram", "asciidoctor-revealjs");
        Thread.startVirtualThread(() -> {
            registerDefaultExtensions(asciidoctor,
                    fxChartBlockProcessor,
                    treeBlockProcessor,
                    mathBlockProcessor,
                    extensionPreprocessor,
                    fileTreeBlockMacroProcessor,
                    fileTreeInlineMacroProcessor,
                    dataLineProcessor,
                    mathBlockMacroProcessor,
                    mathInlineMacroProcessor,
                    cacheSuffixAppenderProcessor)
                    // Extra extensions
                    .postprocessor(documentAttributeProcessor);
        });
        return asciidoctor;
    }

    /*
        Used for non-html5 backend like pdf, epub3 etc.
    */
    @Bean(destroyMethod = "shutdown")
    @Lazy
    public Asciidoctor nonHtmlDoctor(ChartBlockProcessor fxChartBlockProcessor,
                                     FileTreeBlockProcessor treeBlockProcessor,
                                     MathBlockProcessor[] mathBlockProcessor,
                                     ExtensionPreprocessor extensionPreprocessor,
                                     FileTreeBlockMacroProcessor fileTreeBlockMacroProcessor,
                                     FileTreeInlineMacroProcessor fileTreeInlineMacroProcessor,
                                     DataLineProcessor dataLineProcessor,
                                     MathBlockMacroProcessor[] mathBlockMacroProcessor,
                                     MathInlineMacroProcessor[] mathInlineMacroProcessor,
                                     CacheSuffixAppenderProcessor cacheSuffixAppenderProcessor) {
        Asciidoctor asciidoctor = AsciidoctorFactory.getAsciidoctor();
        asciidoctor.requireLibrary("openssl", "asciidoctor-diagram", "asciidoctor-pdf", "asciidoctor-epub3");
        Thread.startVirtualThread(() -> {
            registerDefaultExtensions(asciidoctor,
                    fxChartBlockProcessor,
                    treeBlockProcessor,
                    mathBlockProcessor,
                    extensionPreprocessor,
                    fileTreeBlockMacroProcessor,
                    fileTreeInlineMacroProcessor,
                    dataLineProcessor,
                    mathBlockMacroProcessor,
                    mathInlineMacroProcessor,
                    cacheSuffixAppenderProcessor);
        });
        return asciidoctor;
    }

    /*
     Used for plan render of an Asciidoctor document,
     to read attributes, document etc.
     */
    @Bean(destroyMethod = "shutdown")
    @Lazy
    public Asciidoctor plainDoctor(DocumentAttributeProcessor documentAttributeProcessor) {
        Asciidoctor asciidoctor = AsciidoctorFactory.getAsciidoctor();
        asciidoctor.requireLibrary("openssl", "asciidoctor-revealjs");
        Thread.startVirtualThread(() -> {
            JavaExtensionRegistry registry = asciidoctor.javaExtensionRegistry();
            registry.postprocessor(documentAttributeProcessor);
        });
        return asciidoctor;
    }


    public JavaExtensionRegistry registerDefaultExtensions(Asciidoctor doctor,
                                                           ChartBlockProcessor fxChartBlockProcessor,
                                                           FileTreeBlockProcessor treeBlockProcessor,
                                                           MathBlockProcessor[] mathBlockProcessor,
                                                           ExtensionPreprocessor extensionPreprocessor,
                                                           FileTreeBlockMacroProcessor fileTreeBlockMacroProcessor,
                                                           FileTreeInlineMacroProcessor fileTreeInlineMacroProcessor,
                                                           DataLineProcessor dataLineProcessor,
                                                           MathBlockMacroProcessor[] mathBlockMacroProcessor,
                                                           MathInlineMacroProcessor[] mathInlineMacroProcessor,
                                                           CacheSuffixAppenderProcessor cacheSuffixAppenderProcessor) {

        JavaExtensionRegistry registry = doctor.javaExtensionRegistry();
        registry
                .treeprocessor(dataLineProcessor)
                .block(fxChartBlockProcessor)
                .block(treeBlockProcessor)
                .preprocessor(extensionPreprocessor)
                .blockMacro(fileTreeBlockMacroProcessor)
                .inlineMacro(fileTreeInlineMacroProcessor)
                .treeprocessor(cacheSuffixAppenderProcessor);

        for (MathInlineMacroProcessor inlineMacroProcessor : mathInlineMacroProcessor) {
            registry.inlineMacro(inlineMacroProcessor);
        }

        for (MathBlockProcessor blockProcessor : mathBlockProcessor) {
            registry.block(blockProcessor);
        }

        for (MathBlockMacroProcessor blockMacroProcessor : mathBlockMacroProcessor) {
            registry.blockMacro(blockMacroProcessor);
        }
        return registry;
    }

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

    @Bean(TaskExecutionAutoConfiguration.APPLICATION_TASK_EXECUTOR_BEAN_NAME)
    public AsyncTaskExecutor asyncTaskExecutor() {
        return new TaskExecutorAdapter(Executors.newVirtualThreadPerTaskExecutor());
    }

}

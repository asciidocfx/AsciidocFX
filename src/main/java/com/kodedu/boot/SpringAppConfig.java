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

import com.kodedu.controller.ApplicationController;
import com.kodedu.service.ThreadService;
import com.kodedu.service.extension.DataLineProcessor;
import com.kodedu.service.extension.MathBlockProcessor;
import com.kodedu.service.extension.TreeBlockProcessor;
import com.kodedu.service.extension.chart.ChartProvider;
import com.kodedu.service.extension.chart.FxChartBlockProcessor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
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
    
    @Bean
	public Asciidoctor previewDoctor(FxChartBlockProcessor fxChartBlockProcessor,
                                     DataLineProcessor dataLineProcessor,
                                     TreeBlockProcessor treeBlockProcessor,
                                     MathBlockProcessor mathBlockProcessor) {
		Asciidoctor doctor = Asciidoctor.Factory.create();
		doctor.requireLibrary("asciidoctor-diagram");
		doctor.javaExtensionRegistry()
		      .block(fxChartBlockProcessor)
		      .block(treeBlockProcessor)
		      .block(mathBlockProcessor)
		      .treeprocessor(dataLineProcessor);
		return doctor;
	}

    @Bean
    @Primary
    public Asciidoctor standardDoctor(FxChartBlockProcessor fxChartBlockProcessor,
                                      TreeBlockProcessor treeBlockProcessor,
                                      MathBlockProcessor mathBlockProcessor) {
        Asciidoctor doctor = Asciidoctor.Factory.create();
        doctor.requireLibrary("asciidoctor-diagram");
        doctor.javaExtensionRegistry()
                .block(treeBlockProcessor)
                .block(fxChartBlockProcessor)
                .block(mathBlockProcessor);
        return doctor;
    }


}

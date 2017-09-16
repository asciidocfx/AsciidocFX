package com.kodedu.service.extension.chart;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

/**
 * Created by usta on 01.04.2015.
 */
@Component
public class ChartProvider {

    @Autowired
    private ApplicationContext applicationContext;

    public ChartBuilderService getProvider(String type) {
        return (ChartBuilderService) applicationContext.getBean(type + "-bean");
    }
}

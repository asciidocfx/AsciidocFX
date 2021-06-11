package com.kodedu.service.extension.chart;

import java.util.Map;

/**
 * Created by usta on 01.04.2015.
 */
public interface ChartBuilderService {

    public boolean chartBuild(String chartContent, String imagesDir, String imageTarget, Map<String, String> optMap);

}

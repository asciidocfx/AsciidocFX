package com.kodcu.service.extension;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by usta on 06.09.2016.
 */
public interface DefaultSettings {
    public default Map<String, Integer> newSettings() {
        return new HashMap<String, Integer>() {
            {
                put("addw", 0);
                put("minw", 0);
                put("setw", 0);
                put("addh", 0);
                put("minh", 0);
                put("seth", 0);
                put("scale", 2);

            }
        };
    }
}

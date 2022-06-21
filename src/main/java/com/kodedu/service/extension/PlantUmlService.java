package com.kodedu.service.extension;

/**
 * Created by usta on 25.12.2014.
 */
public interface PlantUmlService {
    public final static String label = "core::service::extension::PlantUml";
    public void plantUml(String uml, String type, String imagesDir, String imageTarget, String nodename, String options);
}

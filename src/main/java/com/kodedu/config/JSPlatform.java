package com.kodedu.config;

/**
 * Created by usta on 24.08.2015.
 */
public enum JSPlatform {

    Webkit("webkit"),
    Nashorn("nashorn"),
    Asciidoctorj("asciidoctorj")
    ;

    private final String value;

    JSPlatform(String value) {
        this.value = value;
    }

}

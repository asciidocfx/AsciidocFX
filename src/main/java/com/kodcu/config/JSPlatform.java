package com.kodcu.config;

/**
 * Created by usta on 24.08.2015.
 */
public enum JSPlatform {

    Webkit("webkit"),
    Nashorn("nashorn"),
    AsciidoctorJ("AsciidoctorJ");

    private final String value;

    JSPlatform(String value) {
        this.value = value;
    }

}

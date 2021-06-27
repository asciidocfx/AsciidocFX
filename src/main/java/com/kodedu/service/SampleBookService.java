package com.kodedu.service;

import java.nio.file.Path;

/**
 * Created by usta on 02.09.2014.
 */
public interface SampleBookService {
    public final static String label = "core::service::SampleBook";

    public void produceSampleBook(Path configPath, Path outputPath);
}

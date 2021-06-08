package com.kodedu.service.impl;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.kodedu.service.SampleBookService;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

/**
 * Created by usta on 02.09.2014.
 */
@Component
public class SampleBookServiceImpl implements SampleBookService {

    private final Logger logger = LoggerFactory.getLogger(SampleBookService.class);

    public void produceSampleBook(Path configPath, Path outputPath) {
        File booksample = configPath.resolve("booksample").toFile();
        File destDir = outputPath.toFile();
        try {
            FileUtils.copyDirectory(booksample, destDir);
        } catch (IOException e) {
            logger.error("Problem occured while producing sample book", e);
        }

    }
}

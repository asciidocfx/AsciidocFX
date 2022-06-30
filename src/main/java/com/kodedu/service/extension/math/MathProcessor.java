package com.kodedu.service.extension.math;

import com.kodedu.service.ThreadService;
import com.kodedu.service.extension.ImageInfo;
import com.kodedu.service.extension.MathJaxService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Component
@Scope("prototype")
public class MathProcessor {

    private final Logger logger = LoggerFactory.getLogger(MathProcessor.class);

    private final MathJaxService mathJaxService;
    private final ThreadService threadService;

    public MathProcessor(MathJaxService mathJaxService, ThreadService threadService) {
        this.mathJaxService = mathJaxService;
        this.threadService = threadService;
    }

    public void process(ImageInfo imageInfo, String content) {

        CompletableFuture completableFuture = new CompletableFuture();

        completableFuture.runAsync(() -> {
            mathJaxService.processFormula(content, imageInfo.imagesDir(), imageInfo.imageTarget(), completableFuture);
        }, threadService.executor());

        try {
            completableFuture.get(10, TimeUnit.SECONDS);
        } catch (Exception e) {
            logger.error("Error occured during tree generation. {}", content, e);
        }
    }
}

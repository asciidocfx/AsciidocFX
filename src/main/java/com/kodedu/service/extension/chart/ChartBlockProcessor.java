package com.kodedu.service.extension.chart;

import static java.util.Objects.nonNull;
import static org.asciidoctor.extension.Contexts.*;
import static org.asciidoctor.extension.Contexts.PARAGRAPH;

import com.kodedu.service.ThreadService;

import com.kodedu.service.extension.base.CustomBlockProcessor;
import com.kodedu.service.extension.base.CustomProcessor;
import com.kodedu.service.extension.ImageInfo;
import org.asciidoctor.ast.ContentNode;
import org.springframework.context.annotation.Scope;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;

import org.asciidoctor.ast.ContentModel;
import org.asciidoctor.ast.StructuralNode;
import org.asciidoctor.extension.Contexts;
import org.asciidoctor.extension.Name;
import org.asciidoctor.extension.Reader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Name("chart")
@Contexts({OPEN, EXAMPLE, SIDEBAR, LITERAL, LISTING, QUOTE, PASS, PARAGRAPH})
@ContentModel(ContentModel.EMPTY)
@Component
@Scope("prototype")
public class ChartBlockProcessor extends CustomBlockProcessor implements CustomProcessor {

    private final Logger logger = LoggerFactory.getLogger(ChartBlockProcessor.class);

    private final ChartProvider chartProvider;
	private final ThreadService threadService;
    
    public ChartBlockProcessor(Environment environment, ChartProvider chartProvider, ThreadService threadService) {
		super(environment);
		this.chartProvider = chartProvider;
    	this.threadService = threadService;
	}

	@Override
	public Object process(ContentNode parent, Reader reader, Map<String, Object> attributes, ImageInfo imageInfo, String content) {
		String chartType = String.valueOf(attributes.get("2"));

		var optMap = parseChartOptions((String) attributes.get("opt"));

		CompletableFuture completableFuture = new CompletableFuture();

		completableFuture.runAsync(()->{
			threadService.runActionLater(() -> {
				chartProvider.getProvider(chartType)
						.chartBuild(content, imageInfo, optMap, completableFuture);
			});
		}, threadService.executor());

		try {
			completableFuture.get(10, TimeUnit.SECONDS);
		} catch (Exception e) {
			logger.error("Could not create FX chart. {}", content, e);
		}

		// Not sure why, but it seems that it only works if target is set afterwards
		// https://stackoverflow.com/questions/71595454/create-image-block-with-asciidocj/71849631#71849631
		return createBlockImage((StructuralNode) parent, attributes, imageInfo);
	}

	private Map<String, String> parseChartOptions(String options) {
        Map<String, String> optMap = new HashMap<>();
        if (nonNull(options)) {
            String[] optPart = options.split(",");

            for (String opt : optPart) {
                String[] keyVal = opt.split("=");
                if (keyVal.length != 2) {
                    continue;
                }
                optMap.put(keyVal[0], keyVal[1]);
            }
        }
        return optMap;
    }

}

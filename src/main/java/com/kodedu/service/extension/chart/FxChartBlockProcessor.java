package com.kodedu.service.extension.chart;

import static java.util.Objects.nonNull;

import com.kodedu.service.ThreadService;

import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.asciidoctor.ast.Block;
import org.asciidoctor.ast.ContentModel;
import org.asciidoctor.ast.StructuralNode;
import org.asciidoctor.extension.BlockProcessor;
import org.asciidoctor.extension.Contexts;
import org.asciidoctor.extension.Name;
import org.asciidoctor.extension.Reader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Name("chart")                                              
@Contexts({Contexts.OPEN})                            
@ContentModel(ContentModel.EMPTY)
@Component
public class FxChartBlockProcessor extends BlockProcessor {

    private final Logger logger = LoggerFactory.getLogger(FxChartBlockProcessor.class);

    private final ChartProvider chartProvider;
	private final ThreadService threadService;
    
    public FxChartBlockProcessor(ChartProvider chartProvider, ThreadService threadService) {
    	this.chartProvider = chartProvider;
    	this.threadService = threadService;
	}

	@Override
	public Object process(StructuralNode parent, Reader reader, Map<String, Object> attributes) {
		String imagesDir = String.valueOf(parent.getDocument().getAttribute("imagesdir"));
		String chartContent = reader.read();
		String chartType = String.valueOf(attributes.get("2"));
		
		String imageFile = String.valueOf(attributes.get("file"));
		String imageTarget = String.format("%s/%s", imagesDir, imageFile);

		var optMap = parseChartOptions(String.valueOf(attributes.get("opt")));
		
		
		// FX diagrams must be created in FX Thread
		final FutureTask<Boolean> chartBuilderTask = new FutureTask<Boolean>(
		        () -> chartProvider.getProvider(chartType).chartBuild(chartContent, imagesDir, imageTarget, optMap));
		threadService.runActionLater(chartBuilderTask);
		try {
			chartBuilderTask.get(1, TimeUnit.MINUTES);
		} catch (InterruptedException | ExecutionException | TimeoutException e) {
			logger.error("Could not create FX chart.", e);
			return null;
		}
		
		// Not sure why, but it seems that it only works if target is set afterwards
		// https://stackoverflow.com/questions/71595454/create-image-block-with-asciidocj/71849631#71849631
		Block block = createBlock(parent, "image", Collections.emptyMap());
		block.setAttribute("target", imageFile, true);
		return block;
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

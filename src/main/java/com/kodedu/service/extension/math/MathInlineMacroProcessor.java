package com.kodedu.service.extension.math;

import com.kodedu.service.ThreadService;
import com.kodedu.service.extension.ImageInfo;
import com.kodedu.service.extension.MathJaxService;
import com.kodedu.service.extension.base.CustomInlineMacroProcessor;
import org.asciidoctor.ast.ContentNode;
import org.asciidoctor.extension.Name;
import org.asciidoctor.extension.Reader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Name("math")
@Component
@Scope("prototype")
public class MathInlineMacroProcessor extends CustomInlineMacroProcessor implements MathStemPreProcessor {

    private final Logger logger = LoggerFactory.getLogger(MathInlineMacroProcessor.class);

    private final MathJaxService mathJaxService;
    private final ThreadService threadService;
    private final MathProcessor mathProcessor;

    public MathInlineMacroProcessor(Environment environment, MathJaxService mathJaxService, ThreadService threadService, MathProcessor mathProcessor) {
        super(environment);
        this.mathJaxService = mathJaxService;
        this.threadService = threadService;
        this.mathProcessor = mathProcessor;
    }

    @Override
    public String preProcessContent(ContentNode parent, Reader reader, Map<String, Object> attributes,
                                    String content) {
        return preProcessContent(parent, reader, attributes, content, name);
    }

    @Override
    public Object process(ContentNode parent, Reader reader, Map<String, Object> attributes, ImageInfo imageInfo, String content) {

        mathProcessor.process(imageInfo, content);
        Map<String, Object> options = new HashMap<String, Object>();
        options.put("type", "image");
        options.put("target", imageInfo.imageName());
        return createPhraseNode(parent, "image",null, attributes, options);
    }

}

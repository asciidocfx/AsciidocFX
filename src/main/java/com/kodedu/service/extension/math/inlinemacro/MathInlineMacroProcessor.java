package com.kodedu.service.extension.math.inlinemacro;

import com.kodedu.service.ThreadService;
import com.kodedu.service.extension.ImageInfo;
import com.kodedu.service.extension.MathJaxService;
import com.kodedu.service.extension.base.CustomInlineMacroProcessor;
import com.kodedu.service.extension.math.MathProcessor;
import com.kodedu.service.extension.math.MathStemPreProcessor;
import org.asciidoctor.ast.ContentNode;
import org.asciidoctor.extension.Reader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;

import java.util.Map;

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
        return createInlineImage(parent, attributes, imageInfo);
    }

}

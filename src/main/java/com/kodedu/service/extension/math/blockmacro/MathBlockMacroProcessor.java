package com.kodedu.service.extension.math.blockmacro;

import com.kodedu.service.ThreadService;
import com.kodedu.service.extension.ImageInfo;
import com.kodedu.service.extension.MathJaxService;
import com.kodedu.service.extension.base.CustomBlockMacroProcessor;
import com.kodedu.service.extension.math.MathProcessor;
import com.kodedu.service.extension.math.MathStemPreProcessor;
import org.asciidoctor.ast.ContentNode;
import org.asciidoctor.ast.StructuralNode;
import org.asciidoctor.extension.Reader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;

import java.util.Map;

public class MathBlockMacroProcessor extends CustomBlockMacroProcessor implements MathStemPreProcessor {

    private final Logger logger = LoggerFactory.getLogger(MathBlockMacroProcessor.class);

    private final MathJaxService mathJaxService;
    private final ThreadService threadService;
    private final MathProcessor mathProcessor;

    public MathBlockMacroProcessor(Environment environment, MathJaxService mathJaxService, ThreadService threadService, MathProcessor mathProcessor) {
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
        return createBlockImage((StructuralNode) parent, attributes, imageInfo);
    }

}

package com.kodedu.service.extension.math;

import com.kodedu.service.ThreadService;
import com.kodedu.service.extension.ImageInfo;
import com.kodedu.service.extension.MathJaxService;
import com.kodedu.service.extension.base.CustomBlockProcessor;
import org.asciidoctor.ast.Block;
import org.asciidoctor.ast.ContentModel;
import org.asciidoctor.ast.ContentNode;
import org.asciidoctor.ast.StructuralNode;
import org.asciidoctor.extension.Contexts;
import org.asciidoctor.extension.Name;
import org.asciidoctor.extension.Reader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Map;

@Name("math")
@Contexts({Contexts.OPEN, Contexts.EXAMPLE, Contexts.SIDEBAR, Contexts.LITERAL, Contexts.LISTING})
@ContentModel(ContentModel.EMPTY)
@Component
@Scope("prototype")
public class MathBlockProcessor extends CustomBlockProcessor implements MathStemPreProcessor {

    private final Logger logger = LoggerFactory.getLogger(MathBlockProcessor.class);

    private final MathJaxService mathJaxService;
    private final ThreadService threadService;
    private final MathProcessor mathProcessor;

    public MathBlockProcessor(Environment environment, MathJaxService mathJaxService, ThreadService threadService, MathProcessor mathProcessor) {
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

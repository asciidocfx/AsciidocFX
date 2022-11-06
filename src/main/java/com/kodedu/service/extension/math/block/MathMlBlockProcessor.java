package com.kodedu.service.extension.math.block;

import com.kodedu.service.ThreadService;
import com.kodedu.service.extension.MathJaxService;
import com.kodedu.service.extension.math.MathProcessor;
import org.asciidoctor.ast.ContentModel;
import org.asciidoctor.extension.Contexts;
import org.asciidoctor.extension.Name;
import org.springframework.context.annotation.Scope;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import static org.asciidoctor.extension.Contexts.*;
import static org.asciidoctor.extension.Contexts.PARAGRAPH;

@Name("mathml_")
@Contexts({OPEN, EXAMPLE, SIDEBAR, LITERAL, LISTING, QUOTE, PASS, PARAGRAPH})
@ContentModel(ContentModel.EMPTY)
@Component
@Scope("prototype")
public class MathMlBlockProcessor extends MathBlockProcessor {

    public MathMlBlockProcessor(Environment environment, MathJaxService mathJaxService, ThreadService threadService, MathProcessor mathProcessor) {
        super(environment, mathJaxService, threadService, mathProcessor);
    }

}

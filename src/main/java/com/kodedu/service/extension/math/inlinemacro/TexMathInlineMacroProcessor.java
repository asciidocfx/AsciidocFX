package com.kodedu.service.extension.math.inlinemacro;

import com.kodedu.service.ThreadService;
import com.kodedu.service.extension.MathJaxService;
import com.kodedu.service.extension.math.MathProcessor;
import org.asciidoctor.ast.ContentModel;
import org.asciidoctor.extension.Format;
import org.asciidoctor.extension.FormatType;
import org.asciidoctor.extension.Name;
import org.springframework.context.annotation.Scope;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Name("tex_") // _ replaced in preprocesser
@Format(FormatType.SHORT)
@ContentModel(ContentModel.RAW)
@Component
@Scope("prototype")
public class TexMathInlineMacroProcessor extends MathInlineMacroProcessor {
    public TexMathInlineMacroProcessor(Environment environment, MathJaxService mathJaxService, ThreadService threadService, MathProcessor mathProcessor) {
        super(environment, mathJaxService, threadService, mathProcessor);
    }
}

package com.kodedu.service.extension.math.blockmacro;

import com.kodedu.service.ThreadService;
import com.kodedu.service.extension.MathJaxService;
import com.kodedu.service.extension.math.MathProcessor;
import org.asciidoctor.extension.Name;
import org.springframework.context.annotation.Scope;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Name("latexmath_")
@Component
@Scope("prototype")
public class LatexMathBlockMacroProcessor extends MathBlockMacroProcessor{

    public LatexMathBlockMacroProcessor(Environment environment, MathJaxService mathJaxService, ThreadService threadService, MathProcessor mathProcessor) {
        super(environment, mathJaxService, threadService, mathProcessor);
    }

}

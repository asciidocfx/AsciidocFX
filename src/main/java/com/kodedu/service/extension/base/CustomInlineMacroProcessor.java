package com.kodedu.service.extension.base;

import org.asciidoctor.ast.ContentNode;
import org.asciidoctor.extension.InlineMacroProcessor;
import org.springframework.core.env.Environment;

import java.util.Map;

public abstract class CustomInlineMacroProcessor extends InlineMacroProcessor implements CustomProcessor {

    private final Environment environment;

    protected CustomInlineMacroProcessor(Environment environment) {
        this.environment = environment;
    }

    @Override
    public Object process(ContentNode parent, String target, Map<String, Object> attributes) {
        return processMacro(parent, target, attributes, environment);
    }

}

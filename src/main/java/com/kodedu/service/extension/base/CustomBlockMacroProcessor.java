package com.kodedu.service.extension.base;

import org.asciidoctor.ast.StructuralNode;
import org.asciidoctor.extension.BlockMacroProcessor;
import org.springframework.core.env.Environment;

import java.util.Map;

public abstract class CustomBlockMacroProcessor  extends BlockMacroProcessor implements CustomProcessor{

    private final Environment environment;

    protected CustomBlockMacroProcessor(Environment environment) {
        this.environment = environment;
    }

    @Override
    public Object process(StructuralNode parent, String target, Map<String, Object> attributes) {
        return processMacroLongFormat(parent, target, attributes, environment);
    }

}

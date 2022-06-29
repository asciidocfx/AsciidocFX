package com.kodedu.service.extension.base;

import com.kodedu.service.extension.ImageInfo;
import org.asciidoctor.ast.StructuralNode;
import org.asciidoctor.extension.BlockProcessor;
import org.asciidoctor.extension.Reader;
import org.springframework.core.env.Environment;

import java.util.Map;

public abstract class CustomBlockProcessor extends BlockProcessor implements CustomProcessor{

    private final Environment environment;

    protected CustomBlockProcessor(Environment environment) {
        this.environment = environment;
    }

    @Override
    public Object process(StructuralNode parent, Reader reader, Map<String, Object> attributes) {
        String content = preProcessContent(parent, reader, attributes, reader.read());
        ImageInfo imageInfo = getImageInfo(environment, attributes, parent, content);
        return process(parent, reader, attributes, imageInfo, content);
    }

}

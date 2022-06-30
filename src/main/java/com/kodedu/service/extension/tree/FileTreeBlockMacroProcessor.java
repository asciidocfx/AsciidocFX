package com.kodedu.service.extension.tree;

import com.kodedu.service.extension.base.CustomBlockMacroProcessor;
import com.kodedu.service.extension.base.CustomProcessor;
import com.kodedu.service.extension.ImageInfo;
import org.asciidoctor.ast.Block;
import org.asciidoctor.ast.ContentNode;
import org.asciidoctor.ast.StructuralNode;
import org.asciidoctor.extension.Name;
import org.asciidoctor.extension.Reader;
import org.springframework.context.annotation.Scope;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Map;

@Name("tree")
@Component
@Scope("prototype")
public class FileTreeBlockMacroProcessor extends CustomBlockMacroProcessor implements CustomProcessor {

    private final FileTreeProcessor fileTreeProcessor;

    protected FileTreeBlockMacroProcessor(Environment environment, FileTreeProcessor fileTreeProcessor) {
        super(environment);
        this.fileTreeProcessor = fileTreeProcessor;
    }

    @Override
    public Object process(ContentNode parent, Reader reader, Map<String, Object> attributes,
                          ImageInfo imageInfo, String content) {
        fileTreeProcessor.process(parent, reader, attributes, imageInfo, content, name);
        return createBlockImage((StructuralNode) parent, attributes, imageInfo);
    }
}

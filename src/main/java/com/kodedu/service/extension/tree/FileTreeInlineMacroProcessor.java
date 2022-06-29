package com.kodedu.service.extension.tree;

import com.kodedu.service.extension.base.CustomInlineMacroProcessor;
import com.kodedu.service.extension.base.CustomProcessor;
import com.kodedu.service.extension.ImageInfo;
import org.asciidoctor.ast.ContentNode;
import org.asciidoctor.extension.Name;
import org.asciidoctor.extension.Reader;
import org.springframework.context.annotation.Scope;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Name("tree")
@Component
@Scope("prototype")
public class FileTreeInlineMacroProcessor extends CustomInlineMacroProcessor implements CustomProcessor {

    private final FileTreeProcessor fileTreeProcessor;

    protected FileTreeInlineMacroProcessor(Environment environment, FileTreeProcessor fileTreeProcessor) {
        super(environment);
        this.fileTreeProcessor = fileTreeProcessor;
    }

    @Override
    public Object process(ContentNode parent, Reader reader, Map<String, Object> attributes,
                          ImageInfo imageInfo, String content) {
        fileTreeProcessor.process(parent, reader, attributes, imageInfo, content, name);
        Map<String, Object> options = new HashMap<String, Object>();
        options.put("type", "image");
        options.put("target", imageInfo.imageName());
        return createPhraseNode(parent, "image",null, attributes, options);
    }

}

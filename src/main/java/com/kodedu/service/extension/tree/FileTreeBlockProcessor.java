package com.kodedu.service.extension.tree;

import com.kodedu.service.ThreadService;
import com.kodedu.service.extension.base.CustomBlockProcessor;
import com.kodedu.service.extension.ImageInfo;
import com.kodedu.service.extension.impl.TreeServiceImpl;
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

import static org.asciidoctor.extension.Contexts.*;
import static org.asciidoctor.extension.Contexts.PARAGRAPH;

@Name("tree")
@Contexts({OPEN, EXAMPLE, SIDEBAR, LITERAL, LISTING, QUOTE, PASS, PARAGRAPH})
@ContentModel(ContentModel.EMPTY)
@Component
@Scope("prototype")
public class FileTreeBlockProcessor extends CustomBlockProcessor {

    private final Logger logger = LoggerFactory.getLogger(FileTreeBlockProcessor.class);

    private final Environment environment;
    private final TreeServiceImpl treeService;
    private final ThreadService threadService;

    private final FileTreeProcessor fileTreeProcessor;

    public FileTreeBlockProcessor(Environment environment, TreeServiceImpl treeService, ThreadService threadService, FileTreeProcessor fileTreeProcessor) {
        super(environment);
        this.environment = environment;
        this.treeService = treeService;
        this.threadService = threadService;
        this.fileTreeProcessor = fileTreeProcessor;
    }

    @Override
    public Object process(ContentNode parent, Reader reader, Map<String, Object> attributes, ImageInfo imageInfo, String content) {
        fileTreeProcessor.process(parent, reader, attributes, imageInfo, content, name);
        return createBlockImage((StructuralNode) parent, attributes, imageInfo);
    }

}

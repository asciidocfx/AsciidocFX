package com.kodedu.service.extension.math;

import com.kodedu.service.ThreadService;
import com.kodedu.service.extension.base.CustomBlockProcessor;
import com.kodedu.service.extension.ImageInfo;
import com.kodedu.service.extension.MathJaxService;
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
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Name("math")
@Contexts({Contexts.OPEN, Contexts.EXAMPLE, Contexts.SIDEBAR, Contexts.LITERAL, Contexts.LISTING})
@ContentModel(ContentModel.EMPTY)
@Component
@Scope("prototype")
public class MathBlockProcessor extends CustomBlockProcessor {

    private final Logger logger = LoggerFactory.getLogger(MathBlockProcessor.class);

    private final MathJaxService mathJaxService;
    private final ThreadService threadService;

    public MathBlockProcessor(Environment environment, MathJaxService mathJaxService, ThreadService threadService) {
        super(environment);
        this.mathJaxService = mathJaxService;
        this.threadService = threadService;
    }

    @Override
    public String preProcessContent(ContentNode parent, Reader reader, Map<String, Object> attributes,
                                    String content) {
        var finalContent = content;
        var stemAttr = (String) attributes.getOrDefault("stem", "no_stem");

        // :stem: -> ""
        if (Objects.isNull(stemAttr) || stemAttr.isBlank()) {
            stemAttr = "asciimath";
        }

        stemAttr = stemAttr.toLowerCase();

        if (stemAttr.equals("asciimath")) {
            // default is asciimath
            finalContent = asciimathWrap(content);
        }

        if (stemAttr.contains("tex")) { // latexmath
            finalContent = latexmathWrap(content);
        }

        if (stemAttr.equals("mathml")) {
            // mathml: nothing to change
            finalContent = content;
        }

        if ("latexmath".equals(name)) {
            finalContent = latexmathWrap(content);
        }

        if ("asciimath".equals(name)) {
            finalContent = asciimathWrap(content);
        }

        if ("mathml".equals(name)) {
            // mathml: nothing to change
            finalContent = content;
        }

        return finalContent;
    }

    @Override
    public Object process(ContentNode parent, Reader reader, Map<String, Object> attributes, ImageInfo imageInfo, String content) {

        CompletableFuture completableFuture = new CompletableFuture();

        completableFuture.runAsync(() -> {
            mathJaxService.processFormula(content, imageInfo.imagesDir(), imageInfo.imageTarget(), completableFuture);
        },threadService.executor());

        try {
            completableFuture.get(10, TimeUnit.SECONDS);
        } catch (Exception e) {
            logger.error("Error occured during tree generation. {}", content, e);
        }

        Block block = createBlock((StructuralNode) parent, "image", Collections.emptyMap());
        block.setAttribute("target", imageInfo.imageName(), true);
        return block;
    }

    public String latexmathWrap(String content){
        return "\\[\n" + content + "\n\\]";
    }

    public String asciimathWrap(String content) {
        return "\\$\n" + content + "\n\\$";
    }

}

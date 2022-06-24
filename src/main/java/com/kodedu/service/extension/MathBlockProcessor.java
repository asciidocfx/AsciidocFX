package com.kodedu.service.extension;

import com.kodedu.service.ThreadService;
import org.asciidoctor.ast.ContentModel;
import org.asciidoctor.ast.StructuralNode;
import org.asciidoctor.extension.Contexts;
import org.asciidoctor.extension.Name;
import org.asciidoctor.extension.Reader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Name("math")
@Contexts({Contexts.OPEN, Contexts.EXAMPLE, Contexts.SIDEBAR, Contexts.LITERAL, Contexts.LISTING})
@ContentModel(ContentModel.EMPTY)
@Component
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
    protected String preProcessContent(StructuralNode parent, Reader reader, Map<String, Object> attributes,
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
    protected Object process(StructuralNode parent, Reader reader, Map<String, Object> attributes, ImageInfo imageInfo, String content) {

        CompletableFuture completableFuture = new CompletableFuture();

        completableFuture.runAsync(() -> {
            mathJaxService.processFormula(content, imageInfo.imagesDir(), imageInfo.imageTarget(), completableFuture);
        },threadService.executor());

        try {
            completableFuture.get(10, TimeUnit.SECONDS);
        } catch (Exception e) {
            logger.error("Error occured during tree generation. {}", content, e);
        }

        return createBlock(parent, imageInfo.imageName());
    }

    public String latexmathWrap(String content){
        return "\\[\n" + content + "\n\\]";
    }

    public String asciimathWrap(String content) {
        return "\\$\n" + content + "\n\\$";
    }

}

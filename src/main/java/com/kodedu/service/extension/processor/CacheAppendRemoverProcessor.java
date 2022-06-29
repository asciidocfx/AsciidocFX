package com.kodedu.service.extension.processor;

import org.asciidoctor.ast.Document;
import org.asciidoctor.ast.StructuralNode;
import org.asciidoctor.extension.Treeprocessor;
import org.asciidoctor.jruby.ast.impl.BlockImpl;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Component
@Scope("prototype")
public class CacheAppendRemoverProcessor extends Treeprocessor {
    private final Map<Object, Object> SELECTOR = Map.of("traverse_documents", "true");

    @Override
    public Document process(Document document) {
        List<StructuralNode> nodes = document.findBy(SELECTOR);
        for (Object node : nodes) {
            if (node instanceof BlockImpl block) {
                if ("image".equals(block.getContext())) {
                    String target = (String) block.getAttribute("target");
                    if (Objects.nonNull(target)) {
                        String[] split = target.split("\\?cache");
                        if (split.length == 2) {
                            block.setAttribute("target", split[0], true);
                        }
                    }
                }
            }
        }
        return document;
    }

}

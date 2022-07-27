package com.kodedu.service.extension.processor;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.asciidoctor.ast.Document;
import org.asciidoctor.ast.StructuralNode;
import org.asciidoctor.extension.Treeprocessor;
import org.asciidoctor.jruby.ast.impl.BlockImpl;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Component
@Scope("prototype")
public class CacheSuffixAppenderProcessor extends Treeprocessor {
    private final Map<Object, Object> SELECTOR = Map.of("traverse_documents", "true");

    private final ObjectMapper objectMapper;

    public CacheSuffixAppenderProcessor(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public Document process(Document document) {

        String docdir = (String) document.getAttribute("docdir");
        boolean isPreview = (boolean) document.getAttribute("preview", false);
        if (!isPreview || Objects.isNull(docdir)) {
            return document;
        }

        List<StructuralNode> nodes = document.findBy(SELECTOR);
        for (Object node : nodes) {
            if (node instanceof BlockImpl block) {
                if ("image".equals(block.getContext())) {
                    String target = (String) block.getAttribute("target");
                    if (Objects.nonNull(target) && !target.contains("?cache")) {

                        String cachedir = (String) block.getAttribute("cachedir", ".asciidoctor/diagram");
                        Path cachePath = Paths.get(docdir).resolve(cachedir).resolve(target + ".cache");

                        if (Files.exists(cachePath)) {
                            try {
                                ObjectNode jsonNode = (ObjectNode) objectMapper.readTree(cachePath.toFile());
                                JsonNode checksumNode = jsonNode.get("checksum");
                                if (checksumNode.isTextual() && !checksumNode.isNull()) {
                                    String checksum = checksumNode.asText();
                                    block.setAttribute("target", target + "?cache=" + checksum, true);
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        }
        return document;
    }

}

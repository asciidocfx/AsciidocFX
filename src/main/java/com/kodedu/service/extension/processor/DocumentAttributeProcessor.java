package com.kodedu.service.extension.processor;

import org.asciidoctor.ast.Document;
import org.asciidoctor.extension.Postprocessor;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Scope("prototype")
public class DocumentAttributeProcessor extends Postprocessor {
    public static final String DOC_UUID = "uuid";
    public static final Map<String, Object> DOCUMENT_MAP = new ConcurrentHashMap<>();

    @Override
    public String process(Document document, String output) {
        String docUUID= (String) document.getAttribute(DOC_UUID);
        if (Objects.nonNull(docUUID)) {
            DOCUMENT_MAP.put(docUUID, document);
        }
        return output;
    }

}

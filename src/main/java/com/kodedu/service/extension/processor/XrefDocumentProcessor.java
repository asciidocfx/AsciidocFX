package com.kodedu.service.extension.processor;

import com.kodedu.other.RefProps;
import org.asciidoctor.ast.Cursor;
import org.asciidoctor.ast.Document;
import org.asciidoctor.ast.StructuralNode;
import org.springframework.stereotype.Component;

import java.util.*;


@Component
public class XrefDocumentProcessor {

    public Map<String, List<RefProps>> getCrossReferences(Document document, String content) {

        String docfile = (String) document.getAttribute("docfile");
        String docdir = (String) document.getAttribute("docdir");

        if (Objects.isNull(docdir) || Objects.isNull(docfile)) {
            return Collections.EMPTY_MAP;
        }

        Map<String, List<RefProps>> xrefMap = XrefHelper.parseXrefs(docfile, content);
        Map<String, List<RefProps>> xref = ProcessorThreadLocal.getXref();
        xref.putAll(xrefMap);

        return xref;
    }

    public Map<String, List<RefProps>> getReferences(Document document) {
        Map<String, Object> refs = document.getCatalog().getRefs();
        Map<String, List<RefProps>> result = new LinkedHashMap<>();
        for (Map.Entry<String, Object> entry : refs.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            if (value instanceof StructuralNode node) {
                Cursor location = node.getSourceLocation();
                int lineNumber = location.getLineNumber();
                String file = location.getFile();
                result.putIfAbsent(file, new ArrayList<>());
                result.get(file).add(new RefProps(file, lineNumber, key, false));
            }
        }

        return result;
    }
}

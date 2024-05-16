package com.kodedu.service.extension.processor;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.asciidoctor.ast.Cursor;
import org.asciidoctor.ast.Document;
import org.asciidoctor.ast.StructuralNode;
import org.asciidoctor.extension.Treeprocessor;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class DataLineProcessor extends Treeprocessor {
	
	private static final Map<Object, Object> SELECTOR = Map.of("traverse_documents", "true");

	@Override
	public Document process(Document document) {
//		Object foo = document.getAttribute("apply-data-line");
//		if (!Boolean.parseBoolean(String.valueOf(foo))) {
//			return document;
//		}

		Boolean isPreview = (Boolean) document.getAttribute("preview", false);
		if (!isPreview) {
			return document;
		}

		// The returned List is not type safe
		// StructuralNode.findBy() claims that only StructuralNodes are returned
		// But CellImpl were also returned which are no StructuralNode!
		// Therefore they must be treated as Object and instanceof check is needed
		List<StructuralNode> nodes = document.findBy(SELECTOR);
		for (Object node : nodes) {
			addDataLineInformation(document, node);
		}
		return document;
	}
	
	private void addDataLineInformation(Document document, Object obj) {
		if (obj instanceof StructuralNode node) {
			Cursor sourceLocation = node.getSourceLocation();
			if (sourceLocation == null) {
				return;
			}

			Cursor documentSourceLocation = document.getSourceLocation();
			if (Objects.nonNull(documentSourceLocation)) {
				String documentFile = documentSourceLocation.getFile();
				String nodeFile = sourceLocation.getFile();
				if (!Objects.equals(documentFile, nodeFile)) {
					return;
				}
			}

			int lineNo = sourceLocation.getLineNumber();
			node.addRole("data-line-" + lineNo);
			List<StructuralNode> blocks = node.getBlocks();
			for (StructuralNode block : blocks) {
				addDataLineInformation(document, block);
			}
		}
	}

}

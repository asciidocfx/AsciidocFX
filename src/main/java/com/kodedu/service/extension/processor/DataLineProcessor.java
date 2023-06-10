package com.kodedu.service.extension.processor;

import java.util.List;
import java.util.Map;

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
			addDataLineInformation(node);
		}
		return document;
	}
	
	private void addDataLineInformation(Object obj) {
		if (obj instanceof StructuralNode node) {
			var sourceLocation = node.getSourceLocation();
			if (sourceLocation == null) {
				return;
			}
			int lineNo = sourceLocation.getLineNumber();
			node.addRole("data-line-" + lineNo);
			List<StructuralNode> blocks = node.getBlocks();
			for (StructuralNode block : blocks) {
				addDataLineInformation(block);
			}
		}
	}

}

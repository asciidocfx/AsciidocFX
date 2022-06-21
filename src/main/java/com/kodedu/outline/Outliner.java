package com.kodedu.outline;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import org.asciidoctor.ast.Document;
import org.asciidoctor.ast.StructuralNode;

@Component
public class Outliner {

    private final List<Section> outlineList = new ArrayList<>();
    private Section lastParent = null;
    private Section lastSection = null;

	public List<Section> fillOutlines(Document asciiDoc) {
		List<StructuralNode> blocks = asciiDoc.getBlocks();
		for (StructuralNode node : blocks) {
			if (node instanceof org.asciidoctor.ast.Section section) {
				fillOutline(null, section.getLevel(), section.getTitle(), section.getSourceLocation().getLineNumber(),
				            section.getId());
				fillOutlinesSubSections(section);
			}
		}
		return outlineList;
	}

	private void fillOutline(String parentLineNo, int level, String title, int lineno, String id) {

		Section section = new Section();
		section.setLevel(Integer.valueOf(level));
		section.setTitle(title);
		section.setLineno(Integer.valueOf(lineno));
		section.setId(id);

		if (isNull(parentLineNo)) {
			outlineList.add(section);
			lastParent = section;
		} else if (nonNull(lastSection)) {
			if (nonNull(lastParent) && lastSection.getLevel().compareTo(section.getLevel()) > 0) {
				lastParent.getSubsections().add(section);
				section.setParent(lastParent);
			} else if (lastSection.getLevel().compareTo(section.getLevel()) < 0) {
				lastSection.getSubsections().add(section);
				section.setParent(lastSection);
			} else {
				lastSection.getParent().getSubsections().add(section);
				section.setParent(lastSection.getParent());
			}
		}

		lastSection = section;
	}
	
	private void fillOutlinesSubSections(org.asciidoctor.ast.Section section) {
		List<StructuralNode> blocks = section.getBlocks();
		for (StructuralNode node : blocks) {
			if (node instanceof org.asciidoctor.ast.Section subsection) {
				fillOutline(lastParent.getLineno().toString(), subsection.getLevel(), subsection.getTitle(), subsection.getSourceLocation().getLineNumber(),
				            subsection.getId());
         		fillOutlinesSubSections(subsection);
			}
		}
	}
}

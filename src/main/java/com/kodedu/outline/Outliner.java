package com.kodedu.outline;

import org.asciidoctor.ast.Document;
import org.asciidoctor.ast.StructuralNode;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Component
public class Outliner {

    public List<Section> getOutlineSections(Document asciiDoc) {
        List<StructuralNode> blocks = asciiDoc.getBlocks();
        List<Section> outlineList = new ArrayList<>();
        fillSections(blocks, outlineList, null);
        return outlineList;
    }

    private void fillSections(List<StructuralNode> blocks, List<Section> outlineList, Section parent) {
        for (StructuralNode node : blocks) {
            if (node instanceof org.asciidoctor.ast.Section section) {
                int level = section.getLevel();
                String title = section.getTitle(); // TODO: Get title returns <></> for html titles
                int lineNumber = section.getSourceLocation().getLineNumber();
                String id = section.getId();
                String file = section.getSourceLocation().getFile();
                Path path = null;
                if (Objects.nonNull(file)) {
                    path = Paths.get(file);
                }

                Section subSection = new Section(level, title, lineNumber, id, path, parent);
                if (Objects.isNull(parent)) {
                    outlineList.add(subSection);
                } else {
                    parent.getSubsections().add(subSection);
                }
                fillSections(section.getBlocks(), outlineList, subSection);
            }
        }
    }


}

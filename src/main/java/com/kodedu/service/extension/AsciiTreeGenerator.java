package com.kodedu.service.extension;

import com.kodedu.helper.IOHelper;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Created by usta on 16.03.2015.
 */
@Component
public class AsciiTreeGenerator {

    public String generate(Path path) {
        return this.printDirectoryTree(path);
    }

    private String printDirectoryTree(Path folder) {
        int indent = 0;
        Tree root = createInitialTree(folder, indent, new Tree());
        root.setSiblingAndParent();

        StringBuilder sb = new StringBuilder();
        root.generateTree(sb);

        return sb.toString();
    }

    private Tree createInitialTree(Path folder, int indent, Tree tree) {
        tree.setDepth(indent);
        tree.setName(folder.getFileName().toString());

        IOHelper.list(folder).forEach(p -> {
            if (IOHelper.isHidden(p))
                return;

            if (Files.isDirectory(p))
                tree.getChildren().add(createInitialTree(p, indent + 1, new Tree()));
            else
                createChild(p, indent + 1, tree);
        });
        return tree;
    }

    private void createChild(Path file, int indent, Tree parent) {
        final Tree child = new Tree();
        child.setName(file.getFileName().toString());
        child.setDepth(indent);
        parent.getChildren().add(child);
    }

    private class Tree {

        private int depth;
        private String name;
        private Tree parent;
        private Tree sibling;
        private boolean lastChild;
        private List<Tree> children;

        public boolean isLastChild() {
            return lastChild;
        }

        public void setLastChild(boolean lastChild) {
            this.lastChild = lastChild;
        }

        private Tree getNextSibling() {
            return sibling;
        }

        private void setNextSibling(Tree sibling) {
            this.sibling = sibling;
        }

        private List<Tree> getChildren() {
            if (Objects.isNull(children))
                children = new ArrayList<>();

            return children;
        }

        private int getDepth() {
            return depth;
        }

        private void setDepth(int depth) {
            this.depth = depth;
        }

        private String getName() {
            return name;
        }

        private void setName(String name) {
            this.name = name;
        }

        private Tree getParent() {
            return parent;
        }

        private void setParent(Tree next) {
            this.parent = next;
        }

        private void setSiblingAndParent() {
            this.setSiblingAndParent(this);
            this.findLastChild(this);
        }

        private void setSiblingAndParent(Tree item) {
            List<Tree> children = item.getChildren();
            for (int index = 0; index < children.size(); index++) {
                Tree currentChild = children.get(index);
                currentChild.setParent(item);
                if (index != children.size() - 1) {
                    currentChild.setNextSibling(children.get(index + 1));
                }
                setSiblingAndParent(currentChild);
            }
        }

        private void findLastChild(Tree currentChild) {
            if (currentChild.getChildren().size() == 0)
                currentChild.setLastChild(true);
            else {
                int lastIndex = currentChild.getChildren().size() - 1;
                findLastChild(currentChild.getChildren().get(lastIndex));
            }
        }

        private void generateTree(StringBuilder sb) {
            this.generateTree(this, sb);
        }

        private void generateTree(Tree tree, StringBuilder sb) {
            printFile(sb, tree);

            tree.getChildren().forEach(child -> {
                generateTree(child, sb);
            });
        }

        private void printFile(StringBuilder sb, Tree leaf) {
            sb.append(getIndentString(leaf));

            if (Objects.nonNull(leaf.getParent())) {
                if (Objects.isNull(leaf.getNextSibling()))
                    sb.append("`--");
                else
                    sb.append("|--");
            }

            sb.append(leaf.getName());
            if (!leaf.isLastChild())
                sb.append("\n");
        }

        private String getIndentString(Tree leaf) {
            StringBuilder sb = new StringBuilder();
            Optional<Tree> localTree = Optional.of(leaf);

            for (int index = 1; index < leaf.getDepth(); index++) {
                localTree = Optional.ofNullable(localTree.get().getParent());
                localTree.ifPresent(p -> {
                    if (Objects.nonNull(p.getNextSibling()))
                        sb.append("  |");
                    else
                        sb.append("   ");
                });
            }
            return sb.reverse().toString();
        }
    }
}

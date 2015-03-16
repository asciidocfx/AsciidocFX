package com.kodcu.service.extension;

import org.springframework.stereotype.Component;

import java.io.File;
import java.nio.file.Path;

/**
 * Created by usta on 16.03.2015.
 */
@Component
public class AsciiTreeGenerator {

    public String generate(Path path) {
        return this.printDirectoryTree(path.toFile());
    }

    private String printDirectoryTree(File folder) {

        int indent = 0;
        StringBuilder sb = new StringBuilder();
        printDirectoryTree(folder, indent, sb);
        return sb.toString();
    }

    private void printDirectoryTree(File folder, int indent,
                                    StringBuilder sb) {

        sb.append(getIndentString(indent));
        sb.append("|--");
        sb.append(folder.getName());
        sb.append("\n");
        for (File file : folder.listFiles()) {
            if (file.isDirectory()) {
                printDirectoryTree(file, indent + 1, sb);
            } else {
                printFile(file, indent + 1, sb);
            }
        }

    }

    private void printFile(File file, int indent, StringBuilder sb) {
        sb.append(getIndentString(indent));
        sb.append("|─");
        sb.append(file.getName());
        sb.append("\n");
    }

    private String getIndentString(int indent) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < indent; i++) {
            sb.append("|  ");
        }
        return sb.toString();
    }
}

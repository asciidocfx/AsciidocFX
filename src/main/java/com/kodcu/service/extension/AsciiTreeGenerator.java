package com.kodcu.service.extension;

import com.kodcu.other.IOHelper;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Path;

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
        StringBuilder sb = new StringBuilder();
        printDirectoryTree(folder, indent, sb);
        return sb.toString();
    }

    private void printDirectoryTree(Path folder, int indent,
                                    StringBuilder sb) {

        sb.append(getIndentString(indent));
        sb.append("|--");
        sb.append(folder.getFileName().toString());
        sb.append("\n");

        IOHelper.list(folder).forEach(p -> {
            if (IOHelper.isHidden(p))
                return;

            if (Files.isDirectory(p))
                printDirectoryTree(p, indent + 1, sb);
            else
                printFile(p, indent + 1, sb);
        });

    }

    private void printFile(Path file, int indent, StringBuilder sb) {
        sb.append(getIndentString(indent));
        sb.append("|--");
        sb.append(file.getFileName().toString());
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

package com.kodcu.service.extension;

import com.kodcu.other.OSHelper;
import org.springframework.stereotype.Component;
import org.zeroturnaround.exec.ProcessExecutor;

import java.io.File;
import java.nio.file.Path;
import java.util.Objects;

/**
 * Created by usta on 16.03.2015.
 */
@Component
public class AsciiTreeGenerator {

    public String generate(Path path) {

        String tree = null;

        if (OSHelper.isWindows()) {
            try {
                tree = new ProcessExecutor().directory(path.toFile()).command("tree", "/f", "/h")
                        .readOutput(true).execute()
                        .outputUTF8();
            } catch (Exception e) {
                tree = null;
                e.printStackTrace();
            }
        }

        if (Objects.isNull(tree) || tree.contains("[error opening dir]"))
            tree = AsciiTreeFallbackGenerator.printDirectoryTree(path.toFile());

        return tree;
    }

    public static class AsciiTreeFallbackGenerator {

        /**
         * Pretty print the directory tree and its file names.
         *
         * @param folder must be a folder.
         * @return
         */
        public static String printDirectoryTree(File folder) {
            if (!folder.isDirectory()) {
                throw new IllegalArgumentException("folder is not a Directory");
            }
            int indent = 0;
            StringBuilder sb = new StringBuilder();
            printDirectoryTree(folder, indent, sb);
            return sb.toString();
        }

        private static void printDirectoryTree(File folder, int indent,
                                               StringBuilder sb) {
            if (!folder.isDirectory()) {
                throw new IllegalArgumentException("folder is not a Directory");
            }
            sb.append(getIndentString(indent));
            sb.append("+--");
            sb.append(folder.getName());
            sb.append("/");
            sb.append("\n");
            for (File file : folder.listFiles()) {
                if (file.isDirectory()) {
                    printDirectoryTree(file, indent + 1, sb);
                } else {
                    printFile(file, indent + 1, sb);
                }
            }

        }

        private static void printFile(File file, int indent, StringBuilder sb) {
            sb.append(getIndentString(indent));
            sb.append("+--");
            sb.append(file.getName());
            sb.append("\n");
        }

        private static String getIndentString(int indent) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < indent; i++) {
                sb.append("|  ");
            }
            return sb.toString();
        }
    }
}

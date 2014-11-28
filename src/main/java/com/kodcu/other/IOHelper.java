package com.kodcu.other;

import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

/**
 * Created by usta on 09.05.2014.
 */
public class IOHelper {

    public static String convert(InputStream is) {

        String content = "";

        try {
            content = IOUtils.toString(is);
            IOUtils.closeQuietly(is);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return content;
    }

    public static String normalize(String content) {
        content = content.replace("\\", "\\\\");
        content = content.replace("'", "\\'");
        content = content.replace("\\\\'", "\\'");
        content = content.replace("\r\n", "\\r\\n");
        content = content.replace("\n\r", "\\n\\r");
        content = content.replace("\n", "\\n");
        content = content.replace("\r", "\\r");
        return content;
    }

    public static void writeToFile(File file, String content, StandardOpenOption... openOption) {
        writeToFile(file.toPath(), content, openOption);
    }

    public static void writeToFile(Path path, String content, StandardOpenOption... openOption) {
        try {
            Files.write(path, content.getBytes(Charset.forName("UTF-8")), openOption);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String readFile(Path path) {
        String content = "";
        try {
            InputStream is = Files.newInputStream(path, StandardOpenOption.READ);
            content = IOUtils.toString(is, "UTF-8");
//            content = normalize(content);
            IOUtils.closeQuietly(is);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return content;
    }

    public static void createDirectories(FileSystem fileSystem, Path path) {
        if(Files.isDirectory(path)){
            System.out.println(path.relativize(fileSystem.getPath("/")));
        }

    }
}

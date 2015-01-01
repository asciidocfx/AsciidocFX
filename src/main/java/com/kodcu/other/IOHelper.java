package com.kodcu.other;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zeroturnaround.zip.commons.IOUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.charset.Charset;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.stream.Stream;

/**
 * Created by usta on 09.05.2014.
 */
public class IOHelper {

    private static Logger logger = LoggerFactory.getLogger(IOHelper.class);

    public static String normalize(String content) {
        content = content.replace("\\", "\\\\");
        content = content.replace("'", "\\'");
        content = content.replace("\\\\'", "\\'");
        content = content.replace("\r\n", "\\r\\n");
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
            logger.info(e.toString());
        }
    }

    public static void writeToFile(Path path, byte[] content, StandardOpenOption... openOption) {
        try {
            Files.write(path, content, openOption);
        } catch (IOException e) {
            logger.info(e.toString());
        }
    }

    public static String readFile(Path path) {
        String content = "";
        try(InputStream is = Files.newInputStream(path, StandardOpenOption.READ)) {
            content = IOUtils.toString(is, "UTF-8");
        } catch (IOException e) {
            logger.info(e.toString());
        }
        return content;
    }

    public static void createDirectories(Path path) {
        try {
            Files.createDirectories(path);
        } catch (IOException e) {
            logger.info(e.toString());
        }

    }

    public static Path createTempFile(Path path, String prefix, String suffix) {
        try {
            return Files.createTempFile(path, prefix, suffix);
        } catch (IOException e) {
            logger.info(e.toString());
        }

        return null;
    }

    public static void copy(Path source, Path target, CopyOption... copyOptions) {
        try {
            Files.copy(source, target, copyOptions);
        } catch (IOException e) {
            logger.info(e.getMessage(),e);
        }
    }

    public static String pathToUrl(Path path) {
        try {
            return path.toUri().toURL().toString();
        } catch (MalformedURLException e) {
            logger.info(e.getMessage(), e);
        }
        return null;
    }

    public static Stream<Path> list(Path path) {
        try {
            return Files.list(path);
        } catch (IOException e) {
            logger.info(e.getMessage(), e);
        }
        return Stream.empty();
    }

    public static void imageWrite(BufferedImage bufferedImage, String format, File output) {
        try {
            ImageIO.write(bufferedImage,format,output);
        } catch (IOException e) {
            logger.info(e.getMessage(), e);
        }
    }

    public static byte[] readAllBytes(Path path)  {
        try {
            return Files.readAllBytes(path);
        } catch (IOException e) {
            logger.info(e.getMessage(),e);
        }
        return new byte[]{};
    }
}

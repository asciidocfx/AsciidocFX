package com.kodcu.other;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.fop.apps.FopFactory;
import org.joox.JOOX;
import org.joox.Match;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.imageio.ImageIO;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.charset.Charset;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.function.BiPredicate;
import java.util.stream.Stream;

/**
 * Created by usta on 09.05.2014.
 */
public class IOHelper {

    private static final Logger logger = LoggerFactory.getLogger(IOHelper.class);

//    public static String normalize(String content) {
//        content = content.replace("\\", "\\\\");
//        content = content.replace("'", "\\'");
//        content = content.replace("\\\\'", "\\'");
//        content = content.replace("\r\n", "\\r\\n");
//        content = content.replace("\n", "\\n");
//        content = content.replace("\r", "\\r");
//        return content;
//    }

    public static void writeToFile(File file, String content, StandardOpenOption... openOption) {
        writeToFile(file.toPath(), content, openOption);
    }

    public static void writeToFile(Path path, String content, StandardOpenOption... openOption) {
        try {
            Files.write(path, content.getBytes(Charset.forName("UTF-8")), openOption);
        } catch (IOException e) {
            logger.info(e.getMessage(), e);
        }
    }

    public static void writeToFile(Path path, byte[] content, StandardOpenOption... openOption) {
        try {
            Files.write(path, content, openOption);
        } catch (IOException e) {
            logger.info(e.getMessage(), e);
        }
    }

    public static String readFile(Path path) {
        String content = "";
        try (InputStream is = Files.newInputStream(path, StandardOpenOption.READ)) {
            content = IOUtils.toString(is, "UTF-8");
        } catch (IOException e) {
            logger.info(e.getMessage(), e);
        }
        return content;
    }

    public static void createDirectories(Path path) {
        try {
            Files.createDirectories(path);
        } catch (IOException e) {
            logger.info(e.getMessage(), e);
        }

    }

    public static Path createTempFile(String suffix) {
        try {
            return Files.createTempFile("asciidoc-temp", suffix);
        } catch (IOException e) {
            logger.info(e.getMessage(), e);
        }

        return null;
    }

    public static Path createTempFile(Path path, String suffix) {
        try {
            return Files.createTempFile(path, "asciidoc-temp", suffix);
        } catch (IOException e) {
            logger.info(e.getMessage(), e);
        }

        return null;
    }

    public static void copy(Path source, Path target, CopyOption... copyOptions) {
        try {
            Files.copy(source, target, copyOptions);
        } catch (IOException e) {
            logger.info(e.getMessage(), e);
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
            ImageIO.write(bufferedImage, format, output);
        } catch (IOException e) {
            logger.info(e.getMessage(), e);
        }
    }

    public static byte[] readAllBytes(Path path) {
        try {
            return Files.readAllBytes(path);
        } catch (IOException e) {
            logger.info(e.getMessage(), e);
        }
        return new byte[]{};
    }

    public static void move(Path source, Path target, StandardCopyOption... option) {
        try {
            Files.move(source, target, option);
        } catch (IOException e) {
            logger.info(e.getMessage(), e);
        }
    }

    public static Match $(InputSource inputSource) {
        try {
            return JOOX.$(inputSource);
        } catch (SAXException e) {
            logger.info(e.getMessage(), e);
        } catch (IOException e) {
            logger.info(e.getMessage(), e);
        }
        return null;
    }

    public static Match $(File file) {
        try {
            return JOOX.$(file);
        } catch (SAXException e) {
            logger.info(e.getMessage(), e);
        } catch (IOException e) {
            logger.info(e.getMessage(), e);
        }
        return null;
    }

    public static void transform(Transformer transformer, StreamSource xmlSource, StreamResult streamResult) {
        try {
            transformer.transform(xmlSource, streamResult);
        } catch (TransformerException e) {
            logger.info(e.getMessage(), e);
        }
    }

    public static void matchWrite(Match root, File file) {
        try {
            root.write(file);
        } catch (IOException e) {
            logger.info(e.getMessage(), e);
        }
    }

    public static void copyDirectoryToDirectory(File source, File target) {
        try {
            FileUtils.copyDirectoryToDirectory(source, target);
        } catch (IOException e) {
            logger.info(e.getMessage(), e);
        }
    }

    public static void setUserConfig(FopFactory fopFactory, String s) {
        try {
            fopFactory.setUserConfig(s);
        } catch (SAXException e) {
            logger.info(e.getMessage(), e);
        } catch (IOException e) {
            logger.info(e.getMessage(), e);
        }
    }

    public static void deleteIfExists(Path path) {
        try {
            Files.deleteIfExists(path);
        } catch (IOException e) {
            logger.info(e.getMessage(), e);
        }
    }

    public static void copyDirectory(Path sourceDir, Path targetDir) {
        try {
            FileUtils.copyDirectory(sourceDir.toFile(), targetDir.toFile());
        } catch (IOException e) {
            logger.info(e.getMessage(), e);
        }
    }

    public static Stream<Path> find(Path start, int maxDepth, BiPredicate<Path, BasicFileAttributes> matcher, FileVisitOption... options) {
        try {
            return Files.find(start, Integer.MAX_VALUE, matcher, options);
        } catch (IOException e) {
            logger.info(e.getMessage(), e);
        }
        return Stream.empty();
    }

    public static boolean isHidden(Path path) {
        try {
            return Files.isHidden(path) || path.getFileName().toString().startsWith(".");
        } catch (IOException e) {
            logger.info(e.getMessage(), e);
        }
        return false;
    }

    public static void copyFileToDirectory(File file, File directory) {
        try {
            FileUtils.copyFileToDirectory(file,directory);
        } catch (IOException e) {
            logger.info(e.getMessage(), e);
        }
    }

    public static void copyFile(File file, File dest) {
        try {
            FileUtils.copyFile(file,dest);
        } catch (IOException e) {
            logger.info(e.getMessage(), e);
        }
    }
}

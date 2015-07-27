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
import javax.json.JsonReader;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.MalformedURLException;
import java.nio.charset.Charset;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiPredicate;
import java.util.stream.Stream;

/**
 * Created by usta on 09.05.2014.
 */
public class IOHelper {

    private static final Logger logger = LoggerFactory.getLogger(IOHelper.class);

    public static void writeToFile(File file, String content, StandardOpenOption... openOption) {
        writeToFile(file.toPath(), content, openOption);
    }

    public static Optional<IOException> writeToFile(Path path, String content, StandardOpenOption... openOption) {
        try {
            Files.write(path, content.getBytes(Charset.forName("UTF-8")), openOption);
        } catch (IOException e) {
            logger.error("Problem occured while writing to {}", path, e);
            return Optional.of(e);
        }
        return Optional.empty();
    }

    public static void writeToFile(Path path, byte[] content, StandardOpenOption... openOption) {
        try {
            Files.write(path, content, openOption);
        } catch (IOException e) {
            logger.error("Problem occured while writing {}", path, e);
        }
    }

    public static String readFile(InputStream inputStream) {
        String content = "";
        try {
            content = IOUtils.toString(inputStream, "UTF-8");
            IOUtils.closeQuietly(inputStream);
        } catch (IOException e) {
            logger.error("Problem occured while reading inputstream", e);
        }
        return content;
    }

    public static String readFile(Path path) {
        String content = "";
        try (InputStream is = Files.newInputStream(path, StandardOpenOption.READ)) {
            content = IOUtils.toString(is, "UTF-8");
        } catch (IOException e) {
            logger.error("Problem occured while reading file {}", path, e);
        }
        return content;
    }

    public static void createDirectories(Path path) {
        try {
            Files.createDirectories(path);
        } catch (IOException e) {
            logger.error("Problem occured while creating directories {}", path, e);
        }

    }

    public static Path createTempFile(String suffix) {
        try {
            return Files.createTempFile("asciidoc-temp", suffix);
        } catch (IOException e) {
            logger.error("Problem occured while creating temp file", e);
        }

        return null;
    }

    public static Path createTempFile(Path path, String suffix) {
        if (Objects.isNull(path)) {
            return createTempFile(suffix);
        }
        try {
            return Files.createTempFile(path, "asciidoc-temp", suffix);
        } catch (IOException e) {
            logger.error("Problem occured while creating temp file {}", path, e);
        }

        return null;
    }

    public static void copy(Path source, Path target, CopyOption... copyOptions) {
        try {
            Files.copy(source, target, copyOptions);
        } catch (IOException e) {
            logger.error("Problem occured while copying {} to {}", source, target, e);
        }
    }

    public static String pathToUrl(Path path) {
        try {
            return path.toUri().toURL().toString();
        } catch (MalformedURLException e) {
            logger.error("Problem occured while getting URL of {}", path, e);
        }
        return null;
    }

    public static Stream<Path> list(Path path) {
        try {
            return Files.list(path);
        } catch (IOException e) {
            logger.error("Problem occured while listing {}", path, e);
        }
        return Stream.empty();
    }

    public static void imageWrite(BufferedImage bufferedImage, String format, File output) {
        try {
            ImageIO.write(bufferedImage, format, output);
        } catch (IOException e) {
            logger.error("Problem occured while writing buff image to {}", output, e);
        }
    }

    public static byte[] readAllBytes(Path path) {
        try {
            return Files.readAllBytes(path);
        } catch (IOException e) {
            logger.error("Problem occured while reading {}", path, e);
        }
        return new byte[]{};
    }

    public static void move(Path source, Path target, StandardCopyOption... option) {
        try {
            Files.move(source, target, option);
        } catch (IOException e) {
            logger.error("Problem occured while moving {} to {}", source, target, e);
        }
    }

    public static Match $(InputSource inputSource) {
        try {
            return JOOX.$(inputSource);
        } catch (SAXException | IOException e) {
            logger.error("Problem occured while selecting Match", e);
        }
        return null;
    }

    public static Match $(File file) {
        try {
            return JOOX.$(file);
        } catch (SAXException | IOException e) {
            logger.error("Problem occured while selecting Match for {}", file, e);
        }
        return JOOX.$();
    }

    public static void transform(Transformer transformer, StreamSource xmlSource, StreamResult streamResult) {
        try {
            transformer.transform(xmlSource, streamResult);
        } catch (TransformerException e) {
            logger.error("Problem occured while transforming XML Source to Stream result", e);

        }
    }

    public static void matchWrite(Match root, File file) {
        try {
            root.write(file);
        } catch (IOException e) {
            logger.error("Problem occured while writing XML Match to {}", file, e);
        }
    }

    public static void copyDirectoryToDirectory(File source, File target) {
        try {
            FileUtils.copyDirectoryToDirectory(source, target);
        } catch (IOException e) {
            logger.error("Problem occured while copying {} to {}", source, target, e);
        }
    }

    public static void setUserConfig(FopFactory fopFactory, String configUri) {
        try {
            fopFactory.setUserConfig(configUri);
        } catch (SAXException | IOException e) {
            logger.error("Problem occured while setting {} as UserConfig", configUri, e);
        }
    }

    public static void deleteIfExists(Path path) {
        try {
            Files.deleteIfExists(path);
        } catch (IOException e) {
            logger.error("Problem occured while deleting {}", path, e);
        }
    }

    public static void copyDirectory(Path sourceDir, Path targetDir) {
        try {
            FileUtils.copyDirectory(sourceDir.toFile(), targetDir.toFile());
        } catch (IOException e) {
            logger.error("Problem occured while copying {} to {}", sourceDir, targetDir, e);
        }
    }

    public static Stream<Path> find(Path start, int maxDepth, BiPredicate<Path, BasicFileAttributes> matcher, FileVisitOption... options) {
        try {
            return Files.find(start, Integer.MAX_VALUE, matcher, options);
        } catch (IOException e) {
            logger.error("Problem occured while finding in path {}", start, e);
        }
        return Stream.empty();
    }

    public static boolean isHidden(Path path) {
        try {
            return Files.isHidden(path) || path.getFileName().toString().startsWith(".");
        } catch (IOException e) {
            logger.error("Problem occured while detecting hidden path {}", path, e);
        }
        return false;
    }

    public static void copyFileToDirectory(File file, File directory) {
        try {
            FileUtils.copyFileToDirectory(file, directory);
        } catch (IOException e) {
            logger.error("Problem occured while copying {} to {}", file, directory, e);
        }
    }

    public static void copyFile(File file, File dest) {
        try {
            FileUtils.copyFile(file, dest);
        } catch (IOException e) {
            logger.error("Problem occured while copying {} to {}", file, dest, e);
        }
    }

    public static void createDirectory(Path path) {
        try {
            Files.createDirectory(path);
        } catch (IOException e) {
            logger.error("Problem occured while creating {} path", path, e);
        }
    }

    public static void deleteDirectory(Path path) {
        try {
            FileUtils.deleteDirectory(path.toFile());
        } catch (IOException e) {
            logger.error("Problem occured while deleting {} path", path, e);
        }
    }

    public static List<String> readAllLines(Path path) {
        try {
            return Files.readAllLines(path);
        } catch (IOException e) {
            logger.error("Problem occured while reading {} path", path, e);
        }
        return new ArrayList<>();
    }

    public static FileReader fileReader(Path path) {
        try {
            return new FileReader(path.toFile());
        } catch (FileNotFoundException e) {
            logger.error("Problem occured while creating FileReader for {} path", path, e);
        }
        return null;
    }

    public static void close(Closeable... closeables) {

        for (Closeable closeable : closeables) {
            try {
                closeable.close();
            } catch (IOException e) {
                logger.error("Problem occured while closing resource");
            }
        }
    }
}

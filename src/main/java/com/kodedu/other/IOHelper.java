package com.kodedu.other;

import com.kodedu.service.ThreadService;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
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
import java.io.*;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.FileTime;
import java.util.*;
import java.util.function.BiPredicate;
import java.util.stream.Stream;

import static java.util.Objects.isNull;

/**
 * Created by usta on 09.05.2014.
 */
public class IOHelper {

    private static final Logger logger = LoggerFactory.getLogger(IOHelper.class);

    public static void writeToFile(File file, String content, StandardOpenOption... openOption) {
        writeToFile(file.toPath(), content, openOption);
    }

    public static Optional<Exception> writeToFile(Path path, String content, StandardOpenOption... openOption) {
        try {
            Files.write(path, content.getBytes(Charset.forName("UTF-8")), openOption);
        } catch (Exception e) {
            logger.error("Problem occured while writing to {}", path, e);
            return Optional.of(e);
        }
        return Optional.empty();
    }

    public static void writeToFile(Path path, byte[] content, StandardOpenOption... openOption) {
        try {
            Files.write(path, content, openOption);
        } catch (Exception e) {
            logger.error("Problem occured while writing {}", path, e);
        }
    }

    public static String readFile(InputStream inputStream) {
        String content = "";
        try {
            content = IOUtils.toString(inputStream, "UTF-8");
            IOUtils.closeQuietly(inputStream);
        } catch (Exception e) {
            logger.error("Problem occured while reading inputstream", e);
        }
        return content;
    }

    public static String readFile(Path path) {
        String content = "";
        try (InputStream is = Files.newInputStream(path, StandardOpenOption.READ, StandardOpenOption.SYNC)) {
            content = IOUtils.toString(is, "UTF-8");
        } catch (Exception e) {
            logger.error("Problem occured while reading file {}", path, e);
        }
        return content;
    }

    public static void createDirectories(Path path) {
        try {
            Files.createDirectories(path);
        } catch (Exception e) {
            logger.error("Problem occured while creating directories {}", path, e);
        }

    }

    public static Path createTempFile(String suffix) {
        try {
            return Files.createTempFile("asciidoc-temp", suffix);
        } catch (Exception e) {
            logger.error("Problem occured while creating temp file", e);
        }

        return null;
    }

    public static Path createTempFile(Path path, String prefix, String suffix) {

        if (isNull(path)) {
            return createTempFile(suffix);
        }
        try {
            return Files.createTempFile(path, prefix, suffix);
        } catch (Exception e) {
            logger.error("Problem occured while creating temp file {}", path, e);
        }

        return null;
    }

    public static Path createTempFile(Path path, String suffix) {
        if (isNull(path)) {
            return createTempFile(suffix);
        }

        return createTempFile(path, "asciidoc-temp", suffix);
    }

    public static void copy(Path source, Path target, CopyOption... copyOptions) {
        try {
            Files.copy(source, target, copyOptions);
        } catch (Exception e) {
            logger.error("Problem occured while copying {} to {}", source, target, e);
        }
    }

    public static String pathToUrl(Path path) {
        try {
            return path.toUri().toURL().toString();
        } catch (Exception e) {
            logger.error("Problem occured while getting URL of {}", path, e);
        }
        return null;
    }

    public static Stream<Path> list(Path path) {
        try {
            return Files.list(path);
        } catch (Exception e) {
            logger.error("Problem occured while listing {}", path, e);
        }
        return Stream.empty();
    }

    public static void imageWrite(BufferedImage bufferedImage, String format, File output) {
        try {
            ImageIO.write(bufferedImage, format, output);
        } catch (Exception e) {
            logger.error("Problem occured while writing buff image to {}", output, e);
        }
    }

    public static byte[] readAllBytes(Path path) {
        try {
            return Files.readAllBytes(path);
        } catch (Exception e) {
            logger.error("Problem occured while reading {}", path, e);
        }
        return new byte[]{};
    }

    public static void move(Path source, Path target, StandardCopyOption... option) {
        try {
            Files.move(source, target, option);
        } catch (Exception e) {
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
        } catch (Exception e) {
            logger.error("Problem occured while writing XML Match to {}", file, e);
        }
    }

    public static void copyDirectoryToDirectory(File source, File target) {
        try {
            FileUtils.copyDirectoryToDirectory(source, target);
        } catch (Exception e) {
            logger.error("Problem occured while copying {} to {}", source, target, e);
        }
    }

    public static Optional<Exception> deleteIfExists(Path path) {
        try {
            Files.deleteIfExists(path);
        } catch (Exception e) {
            logger.error("Problem occured while deleting {}", path, e);
            return Optional.ofNullable(e);
        }

        return Optional.empty();
    }

    public static void copyDirectory(Path sourceDir, Path targetDir) {
        try {
            FileUtils.copyDirectory(sourceDir.toFile(), targetDir.toFile());
        } catch (Exception e) {
            logger.error("Problem occured while copying {} to {}", sourceDir, targetDir, e);
        }
    }

    public static Stream<Path> find(Path start, Integer maxDepth, BiPredicate<Path, BasicFileAttributes> matcher, FileVisitOption... options) {

        if (isNull(maxDepth)) {
            maxDepth = Integer.MAX_VALUE;
        }

        try {
            return Files.find(start, maxDepth, matcher, options);
        } catch (Exception e) {
            logger.error("Problem occured while finding in path {}", start, e);
        }
        return Stream.empty();
    }

    public static boolean isHidden(Path path) {
        try {
            return Files.exists(path) && (Files.isHidden(path) || path.getFileName().toString().startsWith("."));
        } catch (Exception e) {
//            logger.error("Problem occured while detecting hidden path {}", path, e);
        }
        return false;
    }

    public static void copyFileToDirectory(File file, File directory) {
        try {
            FileUtils.copyFileToDirectory(file, directory);
        } catch (Exception e) {
            logger.error("Problem occured while copying {} to {}", file, directory, e);
        }
    }

    public static void copyFile(File file, File dest) {
        try {
            FileUtils.copyFile(file, dest);
        } catch (Exception e) {
            logger.error("Problem occured while copying {} to {}", file, dest, e);
        }
    }

    public static void createDirectory(Path path) {
        try {
            Files.createDirectory(path);
        } catch (Exception e) {
            logger.error("Problem occured while creating {} path", path, e);
        }
    }

    public static void deleteDirectory(Path path) {
        try {
            // Firstly try forced delete
            Optional<Exception> forceDelete = IOHelper.forceDelete(path);

            if (!forceDelete.isPresent()) {
                return;
            }

            // if forced delete failed, try recursively delete dir
            Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    Optional<Exception> exception = IOHelper
                            .forceDelete(file)
                            .flatMap(e -> {
                                ThreadService.sleep(100);
                                return IOHelper.forceDelete(file);
                            });

                    if (exception.isPresent()) {
                        throw new IOException(exception.get());
                    }

                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
                    Optional<Exception> exception = IOHelper
                            .forceDelete(file)
                            .flatMap(e -> {
                                ThreadService.sleep(100);
                                return IOHelper.forceDelete(file);
                            });

                    if (exception.isPresent()) {
                        throw new IOException(exception.get());
                    }

                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                    if (exc == null) {
                        Optional<Exception> exception = IOHelper
                                .forceDelete(dir)
                                .flatMap(e -> {
                                    ThreadService.sleep(100);
                                    return IOHelper.forceDelete(dir);
                                });

                        if (exception.isPresent()) {
                            throw new IOException(exception.get());
                        }
                        return FileVisitResult.CONTINUE;
                    } else {
                        throw exc;
                    }
                }
            });
        } catch (Exception e) {
            logger.error("Problem occured while deleting {} path", path, e);
        }
    }

    private static Optional<Exception> forceDelete(Path path) {

        try {
            Objects.requireNonNull(path);

            if (Files.notExists(path)) {
                return Optional.empty();
            }

            FileUtils.forceDelete(path.toFile());
        } catch (FileNotFoundException fnx) {
            return Optional.empty();
        } catch (Exception e) {
            logger.error("Problem occured while deleting {}", path, e);
            return Optional.ofNullable(e);
        }
        return Optional.empty();
    }

    public static List<String> readAllLines(Path path) {
        try {
            return Files.readAllLines(path);
        } catch (Exception e) {
            logger.error("Problem occured while reading {} path", path, e);
        }
        return new ArrayList<>();
    }

    public static Reader fileReader(Path path) {
        try {
            FileInputStream fileInputStream = new FileInputStream(path.toFile());
            return new InputStreamReader(fileInputStream, "UTF-8");
        } catch (Exception e) {
            logger.error("Problem occured while creating FileReader for {} path", path, e);
        }
        return null;
    }

    public static void close(Closeable... closeables) {

        for (Closeable closeable : closeables) {
            try {
                closeable.close();
            } catch (Exception e) {
//                logger.error("Problem occured while closing resource");
            }
        }
    }

    public static FileTime getLastModifiedTime(Path path) {
        try {
            return Files.getLastModifiedTime(path);
        } catch (Exception e) {
//            e.printStackTrace();
        }
        return null;
    }

    public static String decode(String uri, String encoding) {
        try {
            return URLDecoder.decode(uri, encoding);
        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
        }
        return uri;
    }

    public static Path createTempDirectory(Path path, String prefix, FileAttribute<?>... attrs) {
        try {
            return Files.createTempDirectory(path, prefix, attrs);
        } catch (Exception e) {
            logger.error("Problem occured while creating temp directory");
        }

        return null;
    }

    public static String getPathCleanName(Path object) {
        return object.getFileName().toString().replaceAll("\\..*", "");
    }

    public static <T> T readFile(Path path, Class<T> clazz) {

        if (clazz.isAssignableFrom(byte[].class)) {
            return clazz.cast(readAllBytes(path));
        } else {
            return clazz.cast(readFile(path));
        }

    }

    public static boolean isEmptyDir(Path path) {
        try (DirectoryStream<Path> dirStream = Files.newDirectoryStream(path)) {
            return !dirStream.iterator().hasNext();
        } catch (Exception e) {
//            logger.warn("Problem occured while checking is directory empty {}", path);
        }
        return false;
    }

    public static WatchService newWatchService() {
        try {
            return FileSystems.getDefault().newWatchService();
        } catch (Exception e) {
            logger.warn("Problem occured while creating new watch service");
        }
        return null;
    }

    public static Optional<Long> size(Path path) {
        try {
            long size = Files.size(path);
            return Optional.of(size);
        } catch (Exception e) {
            logger.warn("Problem occured while getting size info of {}", path);
        }
        return Optional.empty();
    }

    public static boolean contains(Path root, Path subPath) {

        if (root == null || subPath == null)
            return false;

        Iterator<Path> realPathIterator = root.normalize().iterator();
        Iterator<Path> subPathIterator = subPath.normalize().iterator();

        while (realPathIterator.hasNext()) {
            Path realPathSegment = realPathIterator.next();
            if (subPathIterator.hasNext()) {
                Path subPathSegment = subPathIterator.next();
                if (!Objects.equals(realPathSegment, subPathSegment)) {
                    subPathIterator = subPath.normalize().iterator();
                }
            } else {
                break;
            }
        }
        return !subPathIterator.hasNext();
    }

    public static Stream<Path> walk(Path path) {
        try {
            return Files.walk(path);
        } catch (Exception e) {
            logger.warn("Problem occured while walking path {}", path);
        }
        return Stream.empty();
    }

    public static Stream<Path> walk(Path path, int depth) {
        try {
            return Files.walk(path, depth);
        } catch (Exception e) {
            logger.warn("Problem occured while walking path {}", path);
        }
        return Stream.empty();
    }

    public static boolean isSameImage(BufferedImage firstImage, BufferedImage secondImage) {

        if (isNull(firstImage)) {
            return false;
        }

        if (isNull(secondImage)) {
            return false;
        }

        // The images must be the same size.
        if (firstImage.getWidth() == secondImage.getWidth() && firstImage.getHeight() == secondImage.getHeight()) {
            int width = firstImage.getWidth();
            int height = firstImage.getHeight();

            // Loop over every pixel.
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    // Compare the pixels for equality.
                    if (firstImage.getRGB(x, y) != secondImage.getRGB(x, y)) {
                        return false;
                    }
                }
            }
        } else {
            return false;
        }

        return true;
    }

    public static Path getPath(String path) {
        try {
            Path getPath = Paths.get(path);
            return getPath;
        } catch (NullPointerException nlp) {
            throw nlp;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}

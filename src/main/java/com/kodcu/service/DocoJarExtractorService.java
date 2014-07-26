package com.kodcu.service;

import javafx.concurrent.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.security.CodeSource;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;

/**
 * Created by usta on 25.07.2014.
 */
public class DocoJarExtractorService {

    private static Logger logger = LoggerFactory.getLogger(DocoJarExtractorService.class);

    public static void extract() {

        Path current = Paths.get("").toAbsolutePath();

        if (Files.exists(current.resolve("doco/")))
            return;

        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                logger.debug("***Doco extraction started***");

                CodeSource codeSource = DocoJarExtractorService.class.getProtectionDomain().getCodeSource();
                File jarFile = new File(codeSource.getLocation().toURI().getPath());
                String jarDir = jarFile.getPath();

                JarFile zipFile = new JarFile(new File(jarDir));

                Enumeration<JarEntry> entries = zipFile.entries();

                while (entries.hasMoreElements()) {
                    JarEntry ze = entries.nextElement();

                    if (!ze.getName().contains("doco/"))
                        continue;

                    Path path = current.resolve(ze.getName());

                    if (ze.isDirectory()) {
                        Files.createDirectories(path);
                        continue;
                    }

                    InputStream zin = zipFile.getInputStream(ze);

                    try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();) {

                        for (int c = zin.read(); c != -1; c = zin.read()) {
                            outputStream.write(c);
                        }

                        Files.write(path, outputStream.toByteArray(), StandardOpenOption.CREATE, TRUNCATE_EXISTING);
                    }

                }

                logger.debug("***Doco extraction completed***");

                return null;
            }
        };

        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }
}

package com.kodcu.service;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.net.URI;
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

        Path userDir = Paths.get(System.getProperty("user.dir"));

        if (Files.exists(userDir.resolve("doco/.doco.cache")))
            return;

        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                logger.debug("***Doco extraction started***");
//
//                CodeSource codeSource = DocoJarExtractorService.class.getProtectionDomain().getCodeSource();
//                File jarFile = new File(codeSource.getLocation().toURI().getPath());
//                String jarDir = jarFile.getPath();

                String jarDir = getClass().getResource("").getPath().split("!")[0];

                JarFile zipFile = new JarFile(new File(jarDir));

                Enumeration<JarEntry> entries = zipFile.entries();

                while (entries.hasMoreElements()) {
                    JarEntry ze = entries.nextElement();

                    if (!ze.getName().contains("doco/"))
                        continue;

                    Path path = userDir.resolve(ze.getName());

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

                Files.createFile(userDir.resolve("doco/.doco.cache"));

                logger.debug("***Doco extraction completed***");

                return null;
            }
        };

        task.exceptionProperty().addListener((observable, oldValue, newValue) -> {
            logger.debug(newValue.getMessage(),newValue);
        });

        Thread thread = new Thread(task);
        thread.setDaemon(true);

        thread.start();
    }
}

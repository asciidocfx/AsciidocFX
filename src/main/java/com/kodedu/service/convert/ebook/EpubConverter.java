package com.kodedu.service.convert.ebook;

import com.kodedu.controller.ApplicationController;
import com.kodedu.helper.IOHelper;
import com.kodedu.other.Current;
import com.kodedu.other.ExtensionFilters;
import com.kodedu.service.DirectoryService;
import com.kodedu.service.PathResolverService;
import com.kodedu.service.ThreadService;
import com.kodedu.service.convert.docbook.DocBookConverter;
import com.kodedu.service.ui.IndikatorService;
import org.asciidoctor.Asciidoctor;
import org.asciidoctor.Attributes;
import org.asciidoctor.Options;
import org.asciidoctor.SafeMode;
import org.joox.Match;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.*;
import java.nio.file.*;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.Deflater;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;
import static java.nio.file.StandardOpenOption.WRITE;
import static org.joox.JOOX.$;

/**
 * Created by usta on 30.08.2014.
 */
@Component
public class EpubConverter {

    private final Logger logger = LoggerFactory.getLogger(EpubConverter.class);

    private final ApplicationController asciiDocController;
    private final Current current;
    private final ThreadService threadService;
    private final DirectoryService directoryService;
    private final IndikatorService indikatorService;
    private final DocBookConverter docBookConverter;
    private final PathResolverService pathResolverService;
    private final Asciidoctor asciidoctor;

    private Path epubPath;

    @Autowired
    public EpubConverter(final ApplicationController asciiDocController, final Current current, final ThreadService threadService,
                         final DirectoryService directoryService, final IndikatorService indikatorService, final DocBookConverter docBookConverter, PathResolverService pathResolverService,
                         @Qualifier("standardDoctor") Asciidoctor asciidoctor) {
        this.asciiDocController = asciiDocController;
        this.current = current;
        this.threadService = threadService;
        this.directoryService = directoryService;
        this.indikatorService = indikatorService;
        this.docBookConverter = docBookConverter;
        this.pathResolverService = pathResolverService;
        this.asciidoctor = asciidoctor;
    }

    public Path produceEpub3Temp() {
        return produceEpub3(false, true);
    }

    public Path produceEpub3(boolean askPath) {
        return produceEpub3(askPath, false);
    }

    private Path produceEpub3(boolean askPath, boolean isTemp) {

        Path currentTabPath = current.currentPath().get();
        Path currentTabPathDir = currentTabPath.getParent();
        Path configPath = asciiDocController.getConfigPath();
        String tabText = current.getCurrentTabText().replace("*", "").trim();
        String asciidoc = current.currentEditorValue();

        threadService.runTaskLater(() -> {
            final Path epubPath = directoryService.getSaveOutputPath(ExtensionFilters.EPUB, askPath);
            File destFile = epubPath.toFile();

            indikatorService.startProgressBar();
            logger.debug("Epub conversion started");

            try {
                Attributes attributes = Attributes.builder().build();
                attributes.setExperimental(true);
                attributes.setIgnoreUndefinedAttributes(true);
                attributes.setAllowUriRead(false);

                Options options = Options.builder()
                        .baseDir(destFile.getParentFile())
                        .toFile(destFile)
                        .backend("epub3")
                        .safe(SafeMode.UNSAFE)
                        .attributes(attributes)
                        .build();

                asciidoctor.convert(asciidoc, options);

                indikatorService.stopProgressBar();
                logger.debug("Epub conversion ended");

                asciiDocController.addRemoveRecentList(epubPath);
            } catch (Exception e) {
                indikatorService.stopProgressBar();
                logger.error("Epub conversion has failed", e);
            }

        });

        return epubPath;
    }


    private void iterativelyPackDir(Path rootPath, Path realRoot, FileSystem zipfs) throws IOException {
        try (Stream<Path> stream = IOHelper.list(rootPath);) {
            List<Path> fileList = stream.collect(Collectors.toList());
            for (Path oebpsFile : fileList) {
                if (Files.isDirectory(oebpsFile)) {
                    iterativelyPackDir(oebpsFile, realRoot, zipfs);
                } else {
                    Path relativeFile = realRoot.relativize(oebpsFile);
                    Path relativeRoot = realRoot.relativize(oebpsFile.getParent());
                    Files.createDirectories(zipfs.getPath(relativeRoot.toString()));
                    Files.copy(oebpsFile, zipfs.getPath(relativeFile.toString()), StandardCopyOption.REPLACE_EXISTING);
                }
            }
        }
    }
}

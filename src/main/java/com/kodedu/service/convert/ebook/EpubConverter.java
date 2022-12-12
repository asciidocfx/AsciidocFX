package com.kodedu.service.convert.ebook;

import com.kodedu.config.Epub3ConfigBean;
import com.kodedu.controller.ApplicationController;
import com.kodedu.helper.IOHelper;
import com.kodedu.other.Current;
import com.kodedu.other.ExtensionFilters;
import com.kodedu.service.AsciidoctorFactory;
import com.kodedu.service.DirectoryService;
import com.kodedu.service.PathResolverService;
import com.kodedu.service.ThreadService;
import com.kodedu.service.convert.docbook.DocBookConverter;
import com.kodedu.service.ui.IndikatorService;
import org.asciidoctor.Attributes;
import org.asciidoctor.Options;
import org.asciidoctor.SafeMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.kodedu.service.AsciidoctorFactory.getStandardDoctor;

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
    private final Epub3ConfigBean epub3ConfigBean;

    @Autowired
    public EpubConverter(final ApplicationController asciiDocController, final Current current, final ThreadService threadService,
                         final DirectoryService directoryService, final IndikatorService indikatorService, final DocBookConverter docBookConverter, PathResolverService pathResolverService,
                         Epub3ConfigBean epub3ConfigBean) {
        this.asciiDocController = asciiDocController;
        this.current = current;
        this.threadService = threadService;
        this.directoryService = directoryService;
        this.indikatorService = indikatorService;
        this.docBookConverter = docBookConverter;
        this.pathResolverService = pathResolverService;
        this.epub3ConfigBean = epub3ConfigBean;
    }

    public void produceEpub3Temp() {
         produceEpub3(false, true);
    }

    public void produceEpub3(boolean askPath) {
         produceEpub3(askPath, false);
    }

    private void produceEpub3(boolean askPath, boolean isTemp) {

        Path currentTabPath = current.currentPath().get();
        Path currentTabPathDir = currentTabPath.getParent();
        Path configPath = asciiDocController.getConfigPath();
        String tabText = current.getCurrentTabText().replace("*", "").trim();
        String asciidoc = current.currentEditorValue();

        threadService.runTaskLater(() -> {
            final Path epubPath = directoryService.getSaveOutputPath(ExtensionFilters.EPUB, askPath);
            File destFile = epubPath.toFile();
            Path workdir = current.currentTab().getParentOrWorkdir();

            indikatorService.startProgressBar();
            logger.debug("Epub conversion started");

            try {
                Attributes attributes = epub3ConfigBean.getAsciiDocAttributes(asciidoc);
                attributes.setExperimental(true);

                Options options = Options.builder()
                        .baseDir(workdir.toFile())
                        .toFile(destFile)
                        .backend("epub3")
                        .safe(SafeMode.UNSAFE)
                        .attributes(attributes)
                        .build();

                getStandardDoctor().convert(asciidoc, options);

                indikatorService.stopProgressBar();
                logger.debug("Epub conversion ended");

                asciiDocController.addRemoveRecentList(epubPath);
            } catch (Exception e) {
                indikatorService.stopProgressBar();
                logger.error("Epub conversion has failed", e);
            }

        });

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

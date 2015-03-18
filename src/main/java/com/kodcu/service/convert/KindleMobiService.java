package com.kodcu.service.convert;

import com.kodcu.controller.ApplicationController;
import com.kodcu.other.Current;
import com.kodcu.other.IOHelper;
import com.kodcu.service.DirectoryService;
import com.kodcu.service.ThreadService;
import com.kodcu.service.ui.IndikatorService;
import javafx.stage.FileChooser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.zeroturnaround.exec.ProcessExecutor;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

/**
 * Created by usta on 02.09.2014.
 */
@Component
public class KindleMobiService {

    private final Logger logger = LoggerFactory.getLogger(KindleMobiService.class);

    private final ApplicationController asciiDocController;
    private final ThreadService threadService;
    private final Epub3Service epub3Service;
    private final Current current;
    private final DirectoryService directoryService;
    private final IndikatorService indikatorService;

    private Path mobiPath;

    @Autowired
    public KindleMobiService(final ApplicationController asciiDocController, final ThreadService threadService, final Epub3Service epub3Service, 
            final Current current, final DirectoryService directoryService, final IndikatorService indikatorService) {
        this.asciiDocController = asciiDocController;
        this.threadService = threadService;
        this.epub3Service = epub3Service;
        this.current = current;
        this.directoryService = directoryService;
        this.indikatorService = indikatorService;
    }

    public void produceMobi() {
        produceMobi(false);
    }

    public void produceMobi(boolean askPath) {

        try {

            indikatorService.startCycle();

            final Path epubPath = epub3Service.produceEpub3Temp().join();

            final Path currentTabPath = current.currentPath().get();
            final Path currentTabPathDir = currentTabPath.getParent();
            final String tabText = current.getCurrentTabText().replace("*", "").trim();

            threadService.runActionLater(() -> {
                if (askPath) {
                    final FileChooser fileChooser = directoryService.newFileChooser("Save Mobi file");
                    fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("MOBI", "*.mobi"));
                    mobiPath = fileChooser.showSaveDialog(null).toPath();
                } else
                    mobiPath = currentTabPathDir.resolve(tabText + ".mobi");

                threadService.runTaskLater(() -> {

                    final ProcessExecutor processExecutor = new ProcessExecutor();
                    processExecutor.readOutput(true);
                    Path kindleGenPath = Paths.get(asciiDocController.getConfig().getKindlegenDir());

                    try {
                        final String message = processExecutor
                                .command(kindleGenPath.resolve("kindlegen").toString(), "-o", mobiPath.getFileName().toString(), epubPath.toString())
                                .execute()
                                .outputUTF8();
                        logger.info(message);

                        IOHelper.move(epubPath.getParent().resolve(mobiPath.getFileName()), mobiPath, StandardCopyOption.REPLACE_EXISTING);

                        indikatorService.completeCycle();

                        threadService.runActionLater(() -> {
                            asciiDocController.getRecentFiles().remove(mobiPath.toString());
                            asciiDocController.getRecentFiles().add(0, mobiPath.toString());
                        });

                    } catch (Exception e) {
                        logger.error(e.getMessage(), e);
                    } finally {
                        indikatorService.completeCycle();
                    }

                });
            });

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            indikatorService.completeCycle();
        }
    }
}

package com.kodcu.service.convert;

import com.kodcu.controller.ApplicationController;
import com.kodcu.other.Current;
import com.kodcu.other.IOHelper;
import com.kodcu.service.DirectoryService;
import com.kodcu.service.ThreadService;
import com.kodcu.service.ui.IndikatorService;
import javafx.application.Platform;
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

    private static final Logger logger = LoggerFactory.getLogger(KindleMobiService.class);

    @Autowired
    private ApplicationController asciiDocController;

    @Autowired
    private ThreadService threadService;

    @Autowired
    private Epub3Service epub3Service;

    @Autowired
    private Current current;

    @Autowired
    private DirectoryService directoryService;

    @Autowired
    private IndikatorService indikatorService;
    private Path mobiPath;

    public void produceMobi() {
        produceMobi(false);
    }

    public void produceMobi(boolean askPath) {

        try {

            indikatorService.startCycle();

            Path epubPath = epub3Service.produceEpub3Temp().join();

            Path currentTabPath = current.currentPath().get();
            Path currentTabPathDir = currentTabPath.getParent();
            String tabText = current.getCurrentTabText().replace("*", "").trim();

            threadService.runActionLater(()->{
                if (askPath) {
                    FileChooser fileChooser = directoryService.newFileChooser("Save Mobi file");
                    fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("MOBI", "*.mobi"));
                    mobiPath = fileChooser.showSaveDialog(null).toPath();
                } else
                    mobiPath = currentTabPathDir.resolve(tabText + ".mobi");

                threadService.runTaskLater(() -> {

                    ProcessExecutor processExecutor = new ProcessExecutor();
                    processExecutor.readOutput(true);
                    Path kindleGenPath = Paths.get(asciiDocController.getConfig().getKindlegenDir());

                    try {
                        String message = processExecutor
                                .command(kindleGenPath.resolve("kindlegen").toString(),"-o",mobiPath.getFileName().toString(), epubPath.toString())
                                .execute()
                                .outputUTF8();
                        logger.info(message);

                        IOHelper.move(epubPath.getParent().resolve(mobiPath.getFileName()), mobiPath, StandardCopyOption.REPLACE_EXISTING);

                        indikatorService.completeCycle();

                        Platform.runLater(() -> {
                            asciiDocController.getRecentFiles().remove(mobiPath.toString());
                            asciiDocController.getRecentFiles().add(0, mobiPath.toString());
                        });

                    } catch (Exception e) {
                        logger.error(e.getMessage(), e);
                    } finally {
                        indikatorService.hideIndikator();
                    }

                });
            });

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            indikatorService.hideIndikator();
        }
    }
}

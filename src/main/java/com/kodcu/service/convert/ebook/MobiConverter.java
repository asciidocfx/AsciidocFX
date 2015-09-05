package com.kodcu.service.convert.ebook;

import com.kodcu.config.EditorConfigBean;
import com.kodcu.config.LocationConfigBean;
import com.kodcu.controller.ApplicationController;
import com.kodcu.other.Current;
import com.kodcu.other.ExtensionFilters;
import com.kodcu.other.IOHelper;
import com.kodcu.service.DirectoryService;
import com.kodcu.service.ThreadService;
import com.kodcu.service.convert.DocumentConverter;
import com.kodcu.service.ui.IndikatorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.zeroturnaround.exec.ProcessExecutor;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.function.Consumer;

/**
 * Created by usta on 02.09.2014.
 */
@Component
public class MobiConverter implements DocumentConverter<String> {

    private final Logger logger = LoggerFactory.getLogger(MobiConverter.class);

    private final ApplicationController controller;
    private final ThreadService threadService;
    private final EpubConverter epubConverter;
    private final Current current;
    private final DirectoryService directoryService;
    private final IndikatorService indikatorService;
    private final EditorConfigBean editorConfigBean;
    private final LocationConfigBean locationConfigBean;

    @Autowired
    public MobiConverter(final ApplicationController controller, final ThreadService threadService, final EpubConverter epubConverter,
                         final Current current, final DirectoryService directoryService, final IndikatorService indikatorService, EditorConfigBean editorConfigBean, LocationConfigBean locationConfigBean) {
        this.controller = controller;
        this.threadService = threadService;
        this.epubConverter = epubConverter;
        this.current = current;
        this.directoryService = directoryService;
        this.indikatorService = indikatorService;
        this.editorConfigBean = editorConfigBean;
        this.locationConfigBean = locationConfigBean;
    }

    @Override
    public void convert(boolean askPath, Consumer<String>... nextStep) {
        try {

            indikatorService.startProgressBar();
            logger.debug("Mobi conversion started");

            final Path epubPath = epubConverter.produceEpub3Temp();

            final Path currentTabPath = current.currentPath().get();
            final Path currentTabPathDir = currentTabPath.getParent();
            final String tabText = current.getCurrentTabText().replace("*", "").trim();

            Path mobiPath = directoryService.getSaveOutputPath(ExtensionFilters.MOBI, askPath);

            final ProcessExecutor processExecutor = new ProcessExecutor();
            processExecutor.readOutput(true);
            Path kindleGenPath = Paths.get(locationConfigBean.getKindlegen());

            try {
                final String message = processExecutor
                        .command(kindleGenPath.toString(), "-o", mobiPath.getFileName().toString(), epubPath.toString())
                        .execute()
                        .outputUTF8();
                logger.debug(message);

                IOHelper.move(epubPath.getParent().resolve(mobiPath.getFileName()), mobiPath, StandardCopyOption.REPLACE_EXISTING);

                indikatorService.stopProgressBar();
                logger.debug("Mobi conversion ended");

                controller.addRemoveRecentList(mobiPath);

            } catch (Exception e) {
                logger.error("Problem occured while converting to Mobi", e);
            } finally {
                indikatorService.stopProgressBar();
            }


        } catch (Exception e) {
            logger.error("Problem occured while converting to Mobi", e);
            indikatorService.stopProgressBar();
        }
    }

}

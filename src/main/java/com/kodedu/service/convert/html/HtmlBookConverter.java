package com.kodedu.service.convert.html;

import com.kodedu.config.HtmlConfigBean;
import com.kodedu.controller.ApplicationController;
import com.kodedu.engine.AsciidocConverterProvider;
import com.kodedu.helper.IOHelper;
import com.kodedu.other.Current;
import com.kodedu.other.ExtensionFilters;
import com.kodedu.service.DirectoryService;
import com.kodedu.service.ThreadService;
import com.kodedu.service.convert.DocumentConverter;
import com.kodedu.service.convert.Traversable;
import com.kodedu.service.ui.IndikatorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.util.function.Consumer;

import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;

/**
 * Created by usta on 30.08.2014.
 */
@Component
public class HtmlBookConverter implements Traversable, DocumentConverter<String> {

    private final Logger logger = LoggerFactory.getLogger(HtmlBookConverter.class);

    private final ApplicationController controller;
    private final ThreadService threadService;
    private final DirectoryService directoryService;
    private final Current current;
    private final IndikatorService indikatorService;
    private final HtmlConfigBean htmlConfigBean;

    private Path htmlBookPath;
    private final AsciidocConverterProvider converterProvider;

    @Autowired
    public HtmlBookConverter(final ApplicationController controller, final ThreadService threadService,
                             final DirectoryService directoryService, final Current current,
                             IndikatorService indikatorService, HtmlConfigBean htmlConfigBean, AsciidocConverterProvider converterProvider) {
        this.controller = controller;
        this.threadService = threadService;
        this.directoryService = directoryService;
        this.current = current;
        this.indikatorService = indikatorService;
        this.htmlConfigBean = htmlConfigBean;
        this.converterProvider = converterProvider;
    }

    @Override
    public void convert(boolean askPath, Consumer<String>... nextStep) {

        try {

            htmlBookPath = directoryService.getSaveOutputPath(ExtensionFilters.HTML, askPath);

            indikatorService.startProgressBar();
            logger.debug("HTML conversion started");

            final String asciidoc = current.currentEditorValue();

            String rendered = converterProvider.get(htmlConfigBean).convertHtml(asciidoc).getRendered();

            IOHelper.writeToFile(htmlBookPath, rendered, CREATE, TRUNCATE_EXISTING);

            controller.addRemoveRecentList(htmlBookPath);

            indikatorService.stopProgressBar();
            logger.debug("HTML conversion ended");
        } catch (Exception e) {
            logger.error("Problem occured while converting to HTML", e);
        } finally {
            indikatorService.stopProgressBar();
        }

    }
}

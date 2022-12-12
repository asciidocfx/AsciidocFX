package com.kodedu.service.convert.docbook;

import com.kodedu.config.DocbookConfigBean;
import com.kodedu.controller.ApplicationController;
import com.kodedu.engine.AsciidocConverterProvider;
import com.kodedu.helper.IOHelper;
import com.kodedu.other.Current;
import com.kodedu.other.ExtensionFilters;
import com.kodedu.service.AsciidoctorFactory;
import com.kodedu.service.DirectoryService;
import com.kodedu.service.ThreadService;
import com.kodedu.service.convert.DocumentConverter;
import com.kodedu.service.ui.IndikatorService;
import org.asciidoctor.Attributes;
import org.asciidoctor.Options;
import org.asciidoctor.SafeMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.util.function.Consumer;

import static com.kodedu.helper.AsciidoctorHelper.convertSafe;
import static com.kodedu.service.AsciidoctorFactory.getStandardDoctor;

/**
 * Created by usta on 19.07.2014.
 */
@Component
public class DocBookConverter implements DocbookTraversable, DocumentConverter<String> {

    private Logger logger = LoggerFactory.getLogger(DocBookConverter.class);

    private final Current current;
    private final AsciidocConverterProvider converterProvider;
    private final DocbookValidator docbookValidator;
    private final DocbookConfigBean docbookConfigBean;
    private final ThreadService threadService;
    private final IndikatorService indikatorService;
    private final DirectoryService directoryService;
    private final ApplicationController applicationController;

    @Autowired
    public DocBookConverter(Current current, AsciidocConverterProvider converterProvider,
                            DocbookValidator docbookValidator, DocbookConfigBean docbookConfigBean,
                            ThreadService threadService,
                            IndikatorService indikatorService, DirectoryService directoryService, ApplicationController applicationController) {
        this.current = current;
        this.converterProvider = converterProvider;
        this.docbookValidator = docbookValidator;
        this.docbookConfigBean = docbookConfigBean;
        this.threadService = threadService;
        this.indikatorService = indikatorService;
        this.directoryService = directoryService;
        this.applicationController = applicationController;
    }


    @Override
    public void convert(boolean askPath, Consumer<String>... nextStep) {

        Path currentTabPath = current.currentPath().get();
        Path workdir = current.currentTab().getParentOrWorkdir();

        String asciidoc = current.currentEditorValue();

        Path docbookPath = directoryService.getSaveOutputPath(ExtensionFilters.DOCBOOK, askPath);
        indikatorService.startProgressBar();

        threadService.runTaskLater(() -> {

            try {
                SafeMode safe = convertSafe(docbookConfigBean.getSafe());

                Attributes attributes = docbookConfigBean.getAsciiDocAttributes(asciidoc);

                Options options = Options.builder()
                        .baseDir(workdir.toFile())
                        .toFile(docbookPath.toFile())
                        .backend("docbook5")
                        .safe(safe)
                        .sourcemap(docbookConfigBean.getSourcemap())
                        .headerFooter(docbookConfigBean.getHeader_footer())
                        .attributes(attributes)
                        .build();

                getStandardDoctor().convert(asciidoc, options);
                String rendered = IOHelper.readFile(docbookPath);
                boolean validated = docbookValidator.validateDocbook(rendered);

                if (!validated)
                    return;

                for (Consumer<String> step : nextStep) {
                    step.accept(rendered);
                }
            } finally {
                indikatorService.stopProgressBar();
                logger.debug("Docbook5 conversion ended");

                applicationController.addRemoveRecentList(docbookPath);
            }
        });
    }


}

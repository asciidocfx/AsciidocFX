package com.kodedu.service.convert.docbook;

import com.kodedu.config.DocbookConfigBean;
import com.kodedu.controller.ApplicationController;
import com.kodedu.engine.AsciidocConverterProvider;
import com.kodedu.helper.IOHelper;
import com.kodedu.helper.XMLHelper;
import com.kodedu.other.Current;
import com.kodedu.other.ExtensionFilters;
import com.kodedu.service.DirectoryService;
import com.kodedu.service.ThreadService;
import com.kodedu.service.convert.DocumentConverter;
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
import org.xml.sax.InputSource;

import java.io.StringReader;
import java.nio.file.Path;
import java.util.function.Consumer;

import static com.kodedu.helper.AsciidoctorHelper.convertSafe;
import static org.joox.JOOX.$;

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

    private final Asciidoctor asciidoctor;

    @Autowired
    public DocBookConverter(Current current, AsciidocConverterProvider converterProvider,
                            DocbookValidator docbookValidator, DocbookConfigBean docbookConfigBean,
                            ThreadService threadService,
                            IndikatorService indikatorService, DirectoryService directoryService, ApplicationController applicationController, @Qualifier("standardDoctor") Asciidoctor asciidoctor) {
        this.current = current;
        this.converterProvider = converterProvider;
        this.docbookValidator = docbookValidator;
        this.docbookConfigBean = docbookConfigBean;
        this.threadService = threadService;
        this.indikatorService = indikatorService;
        this.directoryService = directoryService;
        this.applicationController = applicationController;
        this.asciidoctor = asciidoctor;
    }


    @Override
    public void convert(boolean askPath, Consumer<String>... nextStep) {

        Path currentTabPath = current.currentPath().get();
        Path currentTabPathDir = currentTabPath.getParent();

        String asciidoc = current.currentEditorValue();

        Path docbookPath = directoryService.getSaveOutputPath(ExtensionFilters.DOCBOOK, askPath);
        indikatorService.startProgressBar();

        threadService.runTaskLater(() -> {

            try {
                SafeMode safe = convertSafe(docbookConfigBean.getSafe());

                Attributes attributes = docbookConfigBean.getAsciiDocAttributes();
                attributes.setExperimental(true);
                attributes.setIgnoreUndefinedAttributes(true);
                attributes.setAllowUriRead(true);

                Options options = Options.builder()
                        .baseDir(docbookPath.getParent().toFile())
                        .toFile(docbookPath.toFile())
                        .backend("docbook5")
                        .safe(safe)
                        .sourcemap(docbookConfigBean.getSourcemap())
                        .headerFooter(docbookConfigBean.getHeader_footer())
                        .attributes(attributes)
                        .build();

                String rendered = asciidoctor.convert(asciidoc, options);
                boolean validated = docbookValidator.validateDocbook(rendered);

                if (!validated || rendered == null)
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

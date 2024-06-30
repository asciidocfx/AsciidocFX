package com.kodedu.service.convert.pdf;

import com.kodedu.config.PdfConfigBean;
import com.kodedu.controller.ApplicationController;
import com.kodedu.other.Current;
import com.kodedu.other.ExtensionFilters;
import com.kodedu.other.RenderResult;
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

import java.io.File;
import java.nio.file.Path;
import java.util.function.Consumer;

import static com.kodedu.helper.AsciidoctorHelper.convertSafe;
import static com.kodedu.service.AsciidoctorFactory.getNonHtmlDoctor;

/**
 * Created by usta on 09.04.2015.
 */
@Component
public class AsciidoctorPdfBookConverter implements DocumentConverter<RenderResult> {

    private final Logger logger = LoggerFactory.getLogger(AsciidoctorPdfBookConverter.class);

    private final ApplicationController asciiDocController;
    private final IndikatorService indikatorService;
    private final ThreadService threadService;
    private final DirectoryService directoryService;
    private final Current current;
	private final PdfConfigBean pdfConfigBean;

    @Autowired
    public AsciidoctorPdfBookConverter(final ApplicationController asciiDocController,
                            final IndikatorService indikatorService, final PdfConfigBean pdfConfigBean,
                            final ThreadService threadService, final DirectoryService directoryService,
                            final Current current) {
        this.asciiDocController = asciiDocController;
        this.indikatorService = indikatorService;
        this.threadService = threadService;
        this.directoryService = directoryService;
        this.current = current;
        this.pdfConfigBean = pdfConfigBean;
    }


    @Override
	public void convert(boolean askPath, Consumer<RenderResult>... nextStep) {

		String asciidoc = current.currentEditorValue();

		threadService.runTaskLater(() -> {

			final Path pdfPath = directoryService.getSaveOutputPath(ExtensionFilters.PDF, askPath);

			File destFile = pdfPath.toFile();

			Path workdir = current.currentTab().getParentOrWorkdir();

			indikatorService.startProgressBar();
			logger.debug("PDF conversion started");

			try {
				SafeMode safe = convertSafe(pdfConfigBean.getSafe());
				Attributes attributes = pdfConfigBean.getAsciiDocAttributes(asciidoc);
				Options options = Options.builder()
						.baseDir(workdir.toFile())
						.toFile(destFile)
						.backend("pdf")
						.safe(safe)
						.sourcemap(pdfConfigBean.getSourcemap())
						.headerFooter(pdfConfigBean.getHeader_footer())
						.attributes(attributes)
						.build();
				String content = ExtensionPreprocessor.correctExtensionBlocks(asciidoc);
				getNonHtmlDoctor().convert(content, options);
				asciiDocController.addRemoveRecentList(pdfPath);
				onSuccessfulConversation(nextStep, destFile);
			} catch (Exception e) {
				logger.error("Problem occured while converting to PDF", e);
				onFailedConversation(nextStep, e);
			} finally {
				indikatorService.stopProgressBar();
				logger.debug("PDF conversion ended");
			}

		});
	}

}

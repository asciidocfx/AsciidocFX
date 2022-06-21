package com.kodedu.service.convert.pdf;

import com.kodedu.config.AsciidoctorConfigBase;
import com.kodedu.config.PdfConfigBean.PdfConfigAttributes;
import com.kodedu.controller.ApplicationController;
import com.kodedu.other.Current;
import com.kodedu.other.ExtensionFilters;
import com.kodedu.service.DirectoryService;
import com.kodedu.service.ThreadService;
import com.kodedu.service.convert.DocumentConverter;
import com.kodedu.service.ui.IndikatorService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.nio.file.Path;
import java.util.function.Consumer;

import org.asciidoctor.Asciidoctor;
import org.asciidoctor.Attributes;
import org.asciidoctor.Options;
import org.asciidoctor.SafeMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by usta on 09.04.2015.
 */
@Component
public class AsciidoctorPdfBookConverter implements DocumentConverter<String> {

    private final Logger logger = LoggerFactory.getLogger(AsciidoctorPdfBookConverter.class);

    private final ApplicationController asciiDocController;
    private final IndikatorService indikatorService;
    private final ThreadService threadService;
    private final DirectoryService directoryService;
    private final Current current;
	private final AsciidoctorConfigBase<PdfConfigAttributes> pdfConfigBean;
	private final Asciidoctor doctor;

    @Autowired
    public AsciidoctorPdfBookConverter(final ApplicationController asciiDocController,
                            final IndikatorService indikatorService, final AsciidoctorConfigBase<PdfConfigAttributes> pdfConfigBean,
                            final ThreadService threadService, final DirectoryService directoryService,
                            final Current current, Asciidoctor doctor) {
        this.asciiDocController = asciiDocController;
        this.indikatorService = indikatorService;
        this.threadService = threadService;
        this.directoryService = directoryService;
        this.current = current;
        this.pdfConfigBean = pdfConfigBean;
        this.doctor = doctor;
    }


    @Override
	public void convert(boolean askPath, Consumer<String>... nextStep) {

		String asciidoc = current.currentEditorValue();

		threadService.runTaskLater(() -> {

			final Path pdfPath = directoryService.getSaveOutputPath(ExtensionFilters.PDF, askPath);

			File destFile = pdfPath.toFile();

			indikatorService.startProgressBar();
			logger.debug("PDF conversion started");
			
			SafeMode safe = convertSafe(pdfConfigBean.getSafe());
			
			Attributes attributes = pdfConfigBean.getAsciiDocAttributes();
			
			Options options = Options.builder()
			                         .baseDir(destFile.getParentFile())
			                         .toFile(destFile)
			                         .backend("pdf")
			                         .safe(safe)
			                         .sourcemap(pdfConfigBean.getSourcemap())
			                         .headerFooter(pdfConfigBean.getHeader_footer())
			                         .attributes(attributes)
			                         .build();
			doctor.convert(asciidoc,
			               options);

			indikatorService.stopProgressBar();
			logger.debug("PDF conversion ended");

			asciiDocController.addRemoveRecentList(pdfPath);

		});
	}

	private SafeMode convertSafe(String safeStr) {
		if (safeStr == null) {
			return SafeMode.SAFE;
		}
		try {
			return SafeMode.valueOf(safeStr.toUpperCase());
		} catch (IllegalArgumentException ex) {
			logger.error("Unkown safe mode! Will use SAFE.", ex);
			return SafeMode.SAFE;
		}
	}
}

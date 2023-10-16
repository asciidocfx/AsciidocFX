package com.kodedu.service.convert.pdf;

import com.kodedu.config.PdfConfigBean;
import com.kodedu.config.PdfConverterType;
import com.kodedu.other.RenderResult;
import com.kodedu.service.convert.DocumentConverter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class PdfBookConverter implements DocumentConverter<RenderResult> {

    private final Logger logger = LoggerFactory.getLogger(PdfBookConverter.class);

	private final PdfConfigBean pdfConfigBean;
	private final AsciidoctorPdfBookConverter asciidoctorPdfBookConverter;
	private final FopPdfBookConverter fopPdfBookConverter;

    @Autowired
	public PdfBookConverter(final PdfConfigBean pdfConfigBean,
	        final AsciidoctorPdfBookConverter asciidoctorPdfBookConverter,
	        final FopPdfBookConverter fopPdfBookConverter) {
		this.pdfConfigBean = pdfConfigBean;
		this.asciidoctorPdfBookConverter = asciidoctorPdfBookConverter;
		this.fopPdfBookConverter = fopPdfBookConverter;
	}


    @Override
	public void convert(boolean askPath, Consumer<RenderResult>... nextStep) {
		if (PdfConverterType.ASCIIDOCTOR.equals(pdfConfigBean.getPdfConverterType())) {
			asciidoctorPdfBookConverter.convert(askPath, nextStep);
		} else {
			fopPdfBookConverter.convert(askPath, nextStep);
		}
	}
}

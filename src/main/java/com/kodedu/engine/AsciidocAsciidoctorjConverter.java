package com.kodedu.engine;

import com.kodedu.component.ViewPanel;
import com.kodedu.config.*;
import com.kodedu.controller.ApplicationController;
import com.kodedu.controller.TextChangeEvent;
import com.kodedu.other.ConverterResult;
import com.kodedu.other.Current;
import com.kodedu.outline.Outliner;
import com.kodedu.outline.Section;
import com.kodedu.service.ThreadService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

import org.asciidoctor.Asciidoctor;
import org.asciidoctor.Attributes;
import org.asciidoctor.Options;
import org.asciidoctor.SafeMode;
import org.asciidoctor.ast.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.kodedu.helper.AsciidoctorHelper.convertSafe;

@Component("AsciidoctorjEngine")
public class AsciidocAsciidoctorjConverter extends ViewPanel implements AsciidocConvertible {

    private final Logger logger = LoggerFactory.getLogger(AsciidocAsciidoctorjConverter.class);
	
    private final PreviewConfigBean previewConfigBean;
    private final RevealjsConfigBean revealjsConfigBean;
	private final DocbookConfigBean docbookConfigBean;
	private final ThreadService threadService;

	private final Asciidoctor doctor;

    @Autowired
	public AsciidocAsciidoctorjConverter(ThreadService threadService, ApplicationController controller,
										 Current current, EditorConfigBean editorConfigBean,
										 PreviewConfigBean previewConfigBean, HtmlConfigBean htmlConfigBean,
										 RevealjsConfigBean revealjsConfigBean, DocbookConfigBean docbookConfigBean, Asciidoctor doctor) {
		super(threadService, controller, current, editorConfigBean);
		this.previewConfigBean = previewConfigBean;
		this.threadService = threadService;
		this.revealjsConfigBean = revealjsConfigBean;
		this.docbookConfigBean = docbookConfigBean;
		this.doctor = doctor;
	}

	@Override
	public ConverterResult convertDocbook(TextChangeEvent textChangeEvent) {
		throw new UnsupportedOperationException();
	}

	@Override
	public ConverterResult convertAsciidoc(TextChangeEvent textChangeEvent) {
		throw new UnsupportedOperationException();
	}

	@Override
	public ConverterResult convertHtml(TextChangeEvent textChangeEvent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void convertOdf(String asciidoc) {
		// TODO Auto-generated method stub

	}

	@Override
	public void fillOutlines(Object doc) {
		if (doc instanceof Document document) {
			threadService.runTaskLater(() -> {
				Outliner outliner = new Outliner();
				List<Section> sections = outliner.fillOutlines(document);
				controller.finishOutline(sections);
			});
		}

	}

	@Override
	public String applyReplacements(String asciidoc) {
		// TODO Auto-generated method stub
		return null;
	}

	public ConverterResult convert(Document document, String text) {
		String backend = (String) document.getAttribute("backend", "html5");
		return switch (backend) {
			case "html5" -> convert(document, text, previewConfigBean);
			case "revealjs" -> convert(document, text, revealjsConfigBean);
			default -> throw new RuntimeException("Backend not found: " + backend);
		};
	}
	
	private ConverterResult convert(Document document, String text, AsciidoctorConfigBase<?> configBean) {
		SafeMode safe = convertSafe(configBean.getSafe());
		String backend = (String) document.getAttribute("backend", "html5");
		Attributes attributes = configBean.getAsciiDocAttributes(document.getAttributes());
		attributes.setAttribute("preview", true);

		var workdir = controller.getCurrent().currentTab().getParentOrWorkdir();

		Options options = Options.builder()
		                         .backend(backend)
		                         .baseDir(workdir.toFile())
		                         .safe(safe)
		                         .sourcemap(configBean.getSourcemap())
		                         .headerFooter(configBean.getHeader_footer())
		                         .attributes(attributes)
		                         .build();

		// Load and then convert of the returned document does not work as expected
		// The generated plantuml images are in the wrong location
		// See also https://github.com/asciidoctor/asciidoctorj-diagram/issues/25
		// String converted = doc.convert();
		String converted = doctor.convert(text, options);
		logger.info("Converted Asciidoc to {}", backend.toUpperCase());

        final String taskId = UUID.randomUUID().toString();
		ConverterResult res = new ConverterResult(taskId, converted, backend);
		
		fillOutlines(document);
		return res;
	}

	@Override
	public void runScroller(String text) {
        // no-op
	}

	@Override
	public void scrollByPosition(String text) {
		// no-op
	}

	@Override
	public void scrollByLine(String text) {
		// no-op
	}

}

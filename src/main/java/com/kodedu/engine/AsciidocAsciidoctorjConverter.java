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
import org.springframework.beans.factory.annotation.Qualifier;
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
		logger.debug("Using AsciidocJEngine to convert Asciidoc");
		String text = textChangeEvent.getText();
		return convert("docbook5", text, docbookConfigBean);
	}

	@Override
	public ConverterResult convertAsciidoc(TextChangeEvent textChangeEvent) {
		logger.debug("Using AsciidocJEngine to convert Asciidoc");
        String text = textChangeEvent.getText();
		return convert("html5", text, previewConfigBean);
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

	public ConverterResult convert(String backend, String text) {
		return switch (backend) {
			case "html5" -> convert(backend, text, previewConfigBean);
			case "revealjs" -> convert(backend, text, revealjsConfigBean);
			default -> throw new RuntimeException("Backend not found: " + backend);
		};
	}
	
	private ConverterResult convert(String backend, String text, AsciidoctorConfigBase<?> configBean) {
		SafeMode safe = convertSafe(configBean.getSafe());

		Attributes attributes = configBean.getAsciiDocAttributes(text);
//		if (backend.equals("html5")) {
//			attributes.setAttribute("preview", true);
//		}

		var workdir = controller.getCurrent().currentTab().getParentOrWorkdir();

		Options options = Options.builder()
		                         .backend(backend)
		                         .baseDir(workdir.toFile())
		                         .safe(safe)
		                         .sourcemap(configBean.getSourcemap())
		                         .headerFooter(configBean.getHeader_footer())
		                         .attributes(attributes)
		                         .build();

		Document doc = doctor.load(text, options);
		// Load and then convert of the returned document does not work as expected
		// The generated plantuml images are in the wrong location
		// See also https://github.com/asciidoctor/asciidoctorj-diagram/issues/25
		// String converted = doc.convert();
		String converted = doctor.convert(text, options);

        final String taskId = UUID.randomUUID().toString();
		ConverterResult res = new ConverterResult(taskId, converted, backend, (String)doc.getAttribute("doctype"));
		
		fillOutlines(doc);
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

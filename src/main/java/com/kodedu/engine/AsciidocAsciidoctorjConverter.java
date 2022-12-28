package com.kodedu.engine;

import com.kodedu.component.ViewPanel;
import com.kodedu.config.*;
import com.kodedu.controller.ApplicationController;
import com.kodedu.controller.TextChangeEvent;
import com.kodedu.other.ConverterResult;
import com.kodedu.other.Current;
import com.kodedu.outline.Outliner;
import com.kodedu.outline.Section;
import com.kodedu.service.AsciidoctorFactory;
import com.kodedu.service.ThreadService;
import org.asciidoctor.Asciidoctor;
import org.asciidoctor.Attributes;
import org.asciidoctor.Options;
import org.asciidoctor.SafeMode;
import org.asciidoctor.ast.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import static com.kodedu.helper.AsciidoctorHelper.convertSafe;
import static com.kodedu.service.AsciidoctorFactory.getHtmlDoctor;
import static com.kodedu.service.AsciidoctorFactory.getRevealDoctor;
import static com.kodedu.service.extension.processor.DocumentAttributeProcessor.DOCUMENT_MAP;
import static com.kodedu.service.extension.processor.DocumentAttributeProcessor.DOC_UUID;

@Component("AsciidoctorjEngine")
public class AsciidocAsciidoctorjConverter extends ViewPanel implements AsciidocConvertible {

    private final Logger logger = LoggerFactory.getLogger(AsciidocAsciidoctorjConverter.class);
	
    private final PreviewConfigBean previewConfigBean;
    private final RevealjsConfigBean revealjsConfigBean;
	private final DocbookConfigBean docbookConfigBean;
	private final ThreadService threadService;

    @Autowired
	public AsciidocAsciidoctorjConverter(ThreadService threadService, ApplicationController controller,
										 Current current, EditorConfigBean editorConfigBean,
										 PreviewConfigBean previewConfigBean, HtmlConfigBean htmlConfigBean,
										 RevealjsConfigBean revealjsConfigBean, DocbookConfigBean docbookConfigBean) {
		super(threadService, controller, current, editorConfigBean);
		this.previewConfigBean = previewConfigBean;
		this.threadService = threadService;
		this.revealjsConfigBean = revealjsConfigBean;
		this.docbookConfigBean = docbookConfigBean;
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
				List<Section> sections = outliner.getOutlineSections(document);
				controller.finishOutline(sections);
			});
		}

	}

	@Override
	public String applyReplacements(String asciidoc) {
		// TODO Auto-generated method stub
		return null;
	}

	public ConverterResult convert(Document document, TextChangeEvent textChangeEvent) {
		String backend = (String) document.getAttribute("backend", "html5");
		Map<String, Object> attributes = document.getAttributes();
		current.currentEditor().updateAttributes(attributes);
		return switch (backend) {
			case "html5" -> convert(document, textChangeEvent, previewConfigBean);
			case "revealjs" -> convert(document, textChangeEvent, revealjsConfigBean);
			default -> throw new RuntimeException("Backend not found: " + backend);
		};
	}

	private ConverterResult convert(Document document, TextChangeEvent textChangeEvent, AsciidoctorConfigBase<?> configBean) {
		SafeMode safe = convertSafe(configBean.getSafe());
		String backend = (String) document.getAttribute("backend", "html5");
		Attributes attributes = configBean.getAsciiDocAttributes(document.getAttributes());
		attributes.setAttribute("preview", true);
		String docUUID = UUID.randomUUID().toString();
		attributes.setAttribute(DOC_UUID, docUUID);

		Path workdir = controller.getCurrent().currentTab().getParentOrWorkdir();

		Options options = Options.builder()
		                         .backend(backend)
		                         .baseDir(workdir.toFile())
		                         .safe(safe)
		                         .sourcemap(configBean.getSourcemap())
		                         .headerFooter(true)
		                         .attributes(attributes)
		                         .build();

		// Load and then convert of the returned document does not work as expected
		// The generated plantuml images are in the wrong location
		// See also https://github.com/asciidoctor/asciidoctorj-diagram/issues/25
		// String converted = doc.convert();
		Asciidoctor asciidoctor = Objects.equals(backend,"revealjs") ? getRevealDoctor() : getHtmlDoctor();
		String converted = asciidoctor.convert(textChangeEvent.getText(), options);
		Document finalDocument = (Document) DOCUMENT_MAP.get(docUUID);
		current.currentEditor().setLastDocument(finalDocument);
		DOCUMENT_MAP.remove(docUUID);
		logger.info("Converted Asciidoc to {}", backend.toUpperCase());

        final String taskId = UUID.randomUUID().toString();
		ConverterResult res = new ConverterResult(taskId, converted, backend, finalDocument);
		fillOutlines(document); // Somehow files are not generated with finalDocument
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

package com.kodedu.engine;

import com.kodedu.component.ViewPanel;
import com.kodedu.config.AsciidoctorConfigBase;
import com.kodedu.config.EditorConfigBean;
import com.kodedu.config.HtmlConfigBean;
import com.kodedu.config.PreviewConfigBean;
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

@Component("AsciidoctorjEngine")
public class AsciidocAsciidoctorjConverter extends ViewPanel implements AsciidocConvertible {

    private final Logger logger = LoggerFactory.getLogger(AsciidocAsciidoctorjConverter.class);
	
    private final PreviewConfigBean previewConfigBean;
	private final ThreadService threadService;

	private final Asciidoctor doctor;

    @Autowired
	public AsciidocAsciidoctorjConverter(ThreadService threadService, ApplicationController controller,
	        Current current, EditorConfigBean editorConfigBean,
	        PreviewConfigBean previewConfigBean, HtmlConfigBean htmlConfigBean,
										 @Qualifier("previewDoctor") Asciidoctor doctor) {
		super(threadService, controller, current, editorConfigBean);
		this.previewConfigBean = previewConfigBean;
		this.threadService = threadService;
		this.doctor = doctor;
	}

	@Override
	public ConverterResult convertDocbook(TextChangeEvent textChangeEvent) {
		// TODO Auto-generated method stub
		return null;
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
	
	private ConverterResult convert(String backend, String text, AsciidoctorConfigBase<?> configBean) {
		SafeMode safe = convertSafe(configBean.getSafe());

		Attributes attributes = configBean.getAsciiDocAttributes();
		
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
		ConverterResult res = new ConverterResult(taskId, converted, backend, doc.getAttribute("doctype").toString());
		
		fillOutlines(doc);
		return res;
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

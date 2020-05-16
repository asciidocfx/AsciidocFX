package com.kodedu.service.convert.docbook;

import com.kodedu.config.DocbookConfigBean;
import com.kodedu.controller.TextChangeEvent;
import com.kodedu.engine.AsciidocConverterProvider;
import com.kodedu.helper.IOHelper;
import com.kodedu.helper.XMLHelper;
import com.kodedu.other.Current;
import com.kodedu.service.ThreadService;
import com.kodedu.service.convert.DocumentConverter;
import org.joox.Match;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.xml.sax.InputSource;

import java.io.StringReader;
import java.nio.file.Path;
import java.util.function.Consumer;

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

    @Autowired
    public DocBookConverter(Current current, AsciidocConverterProvider converterProvider, DocbookValidator docbookValidator, DocbookConfigBean docbookConfigBean, ThreadService threadService) {
        this.current = current;
        this.converterProvider = converterProvider;
        this.docbookValidator = docbookValidator;
        this.docbookConfigBean = docbookConfigBean;
        this.threadService = threadService;
    }


    @Override
    public void convert(boolean askPath, Consumer<String>... nextStep) {

        Path currentTabPath = current.currentPath().get();
        Path currentTabPathDir = currentTabPath.getParent();

        String asciidoc = current.currentEditorValue();

        threadService.runTaskLater(() -> {

            TextChangeEvent event = new TextChangeEvent(asciidoc, null, currentTabPath);
            String rendered = converterProvider.get(docbookConfigBean).convertDocbook(event).getRendered();

            boolean validated = docbookValidator.validateDocbook(rendered);

            if (!validated)
                return;

            StringReader bookReader = new StringReader(rendered);
            Match rootDocument = IOHelper.$(new InputSource(bookReader));
            bookReader.close();

//            // makes figure centering
            rootDocument.find("figure").find("imagedata").attr("align", "center");

            // remove callout's duplicated refs and pick last
            rootDocument.find("callout").forEach(elem -> {
                String arearefs = $(elem).attr("arearefs");
                String[] cos = arearefs.split(" ");
                if (cos.length > 1)
                    $(elem).attr("arearefs", cos[cos.length - 1]);
            });

            String result = XMLHelper.nodeToString(rootDocument.document(), false);
//            result = result.replace("00HEADER00COLON00", ":");

            for (Consumer<String> step : nextStep) {
                step.accept(result);
            }
        });
    }


}

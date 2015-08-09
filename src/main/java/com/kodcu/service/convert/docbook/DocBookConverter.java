package com.kodcu.service.convert.docbook;

import com.kodcu.component.HtmlPane;
import com.kodcu.component.WorkerPane;
import com.kodcu.controller.ApplicationController;
import com.kodcu.other.Current;
import com.kodcu.other.IOHelper;
import com.kodcu.other.XMLHelper;
import com.kodcu.service.ThreadService;
import com.kodcu.service.convert.DocumentConverter;
import com.kodcu.service.convert.markdown.MarkdownService;
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
    private final ThreadService threadService;
    private final MarkdownService markdownService;
    private final ApplicationController controller;
    private final DocbookValidator docbookValidator;
    private final HtmlPane htmlPane;
    private final WorkerPane workerPane;

    @Autowired
    public DocBookConverter(final Current current, ThreadService threadService, MarkdownService markdownService, ApplicationController controller, DocbookValidator docbookValidator, HtmlPane htmlPane, WorkerPane workerPane) {
        this.current = current;
        this.threadService = threadService;
        this.markdownService = markdownService;
        this.controller = controller;
        this.docbookValidator = docbookValidator;
        this.htmlPane = htmlPane;
        this.workerPane = workerPane;
    }


    @Override
    public void convert(boolean askPath, Consumer<String>... nextStep) {

//        indikatorService.startProgressBar();

        Path currentTabPath = current.currentPath().get();
        Path currentTabPathDir = currentTabPath.getParent();

        String asciidoc = current.currentEditorValue();

        threadService.runActionLater(() -> {

            String rendered = workerPane.convertDocbook(asciidoc).getRendered();

            boolean validated = docbookValidator.validateDocbook(rendered);

            if (!validated)
                return;

//            rendered = rendered.replace("<?xml version=\"1.0\" encoding=\"UTF-8\"?>", "<?xml version=\"1.1\" encoding=\"UTF-8\"?>");

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
            result = result.replace("00HEADER00COLON00", ":");

            for (Consumer<String> step : nextStep) {
                step.accept(result);
            }

//                indikatorService.stopProgressBar();
        });
    }


}

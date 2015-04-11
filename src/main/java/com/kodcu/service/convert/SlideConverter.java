package com.kodcu.service.convert;

import com.kodcu.component.HtmlPane;
import com.kodcu.component.SlidePane;
import com.kodcu.controller.ApplicationController;
import com.kodcu.other.Current;
import com.kodcu.other.IOHelper;
import com.kodcu.service.MarkdownService;
import com.kodcu.service.ThreadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.util.Objects;
import java.util.function.Consumer;

import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;

/**
 * Created by usta on 09.04.2015.
 */
@Component
public class SlideConverter implements DocumentConverter<String> {

    private final ApplicationController controller;
    private final ThreadService threadService;
    private final MarkdownService markdownService;
    private final Current current;
    private final SlidePane slidePane;
    private final HtmlPane htmlPane;


    @Autowired
    public SlideConverter(final ApplicationController controller, final ThreadService threadService, final MarkdownService markdownService, final Current current, SlidePane slidePane, HtmlPane htmlPane) {
        this.controller = controller;
        this.threadService = threadService;
        this.markdownService = markdownService;
        this.current = current;
        this.slidePane = slidePane;
        this.htmlPane = htmlPane;
    }


    @Override
    public void convert(boolean askPath, Consumer<String>... nextStep) {
        threadService.runActionLater(() -> {

            String rendered = htmlPane.convertSlide(current.currentEditorValue());

            Path resolve = current.currentPath().map(Path::getParent).get().resolve(current.getCurrentTabText().replace("*", "").trim() + ".html");
            IOHelper.writeToFile(resolve, rendered, TRUNCATE_EXISTING, CREATE);

            String location = slidePane.getLocation();

            if (Objects.isNull(location))
                    try {
                        slidePane.load(resolve.toUri().toURL().toString());
                        slidePane.setOnSuccess(() -> {
                            slidePane.loadJs("js/jquery.js","js/reveal-extensions.js");
                        });
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }
            else {

                threadService.runActionLater(() -> {
                    if (slidePane.isReady()) {
                        slidePane.replaceSlides(rendered);
                    }
                });

            }

            for (Consumer<String> step : nextStep) {
                step.accept(rendered);
            }
        });
    }


}

package com.kodcu.service.convert.slide;

import com.kodcu.component.PreviewTab;
import com.kodcu.component.SlidePane;
import com.kodcu.controller.ApplicationController;
import com.kodcu.other.Current;
import com.kodcu.service.ThreadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by usta on 09.04.2015.
 */
@Component
public class SlideConverter {

    private final ApplicationController controller;
    private final ThreadService threadService;
    private final Current current;
    private final SlidePane slidePane;
    private final Pattern pattern = Pattern.compile(":slide-type:.*(deckjs|revealjs)", Pattern.MULTILINE);
    private String rendered;

    @Value("${application.slide.url}")
    private String slideUrl;


    @Autowired
    public SlideConverter(final ApplicationController controller, final ThreadService threadService, final Current current, SlidePane slidePane) {
        this.controller = controller;
        this.threadService = threadService;
        this.current = current;
        this.slidePane = slidePane;
    }

    public String currentType() {
        String input = current.currentEditorValue();
        Matcher matcher = pattern.matcher(input);

        String slideType = "revealjs";

        if (matcher.find()) {
            slideType = matcher.group(1);
        }
        return slideType;
    }

    public void convert(String rendered, Consumer<String>... nextStep) {
        threadService.runActionLater(() -> {

            this.rendered = rendered;

            PreviewTab previewTab = controller.getPreviewTab();

            if (previewTab.getContent() != slidePane) {
                slidePane.load(String.format(slideUrl, controller.getPort()));
            } else {
                threadService.runActionLater(() -> {
                    slidePane.replaceSlides(rendered);
                });
            }

            for (Consumer<String> step : nextStep) {
                step.accept(rendered);
            }
        });
    }

    public String getRendered() {
        return rendered;
    }
}

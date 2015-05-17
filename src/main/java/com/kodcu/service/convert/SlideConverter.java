package com.kodcu.service.convert;

import com.kodcu.component.DeckSlidePane;
import com.kodcu.component.HtmlPane;
import com.kodcu.component.SlidePane;
import com.kodcu.controller.ApplicationController;
import com.kodcu.other.Current;
import com.kodcu.other.IOHelper;
import com.kodcu.service.MarkdownService;
import com.kodcu.service.ThreadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
    private final ApplicationContext applicationContext;
    private final HtmlPane htmlPane;
    private final Pattern pattern = Pattern.compile(":slide-type:.*(deckjs|revealjs)", Pattern.MULTILINE);
    private String rendered;


    @Autowired
    public SlideConverter(final ApplicationController controller, final ThreadService threadService, final MarkdownService markdownService, final Current current, ApplicationContext applicationContext, HtmlPane htmlPane) {
        this.controller = controller;
        this.threadService = threadService;
        this.markdownService = markdownService;
        this.current = current;
        this.applicationContext = applicationContext;
        this.htmlPane = htmlPane;
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

    public SlidePane currentBean() {
        return applicationContext.getBean(currentType() + "-bean", SlidePane.class);
    }

    public void showCurrentHideOthers() {
        currentBean().show();
        String[] beanNames = applicationContext.getBeanNamesForType(SlidePane.class);
        for (String beanName : beanNames) {
            if(!beanName.equals(currentType()+"-bean"))
                applicationContext.getBean(beanName,SlidePane.class).hide();
        }
    }

    @Override
    public void convert(boolean askPath, Consumer<String>... nextStep) {
        threadService.runActionLater(() -> {

            this.showCurrentHideOthers();

            SlidePane slidePane = this.currentBean();

            this.rendered = htmlPane.convertSlide(current.currentEditorValue());

//            Path resolve = current.currentPath().map(Path::getParent).get().resolve(current.getCurrentTabText().replace("*", "").trim() + ".html");
//            String newLocation = resolve.toUri().toString();
//            IOHelper.writeToFile(resolve, rendered, TRUNCATE_EXISTING, CREATE);

            if (Objects.isNull(slidePane.getLocation())) {
                slidePane.load(String.format("http://localhost:%d/slide/index.slide",controller.getPort()));
            }  else {
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

    public String getRendered() {
        return rendered;
    }
}

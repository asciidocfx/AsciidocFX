package com.kodcu.service.convert;

import com.kodcu.controller.ApplicationController;
import com.kodcu.other.Current;
import com.kodcu.other.IOHelper;
import com.kodcu.service.ThreadService;
import javafx.scene.web.WebEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.concurrent.CompletableFuture;

/**
 * Created by usta on 19.07.2014.
 */
@Component
public class RenderService {

    @Autowired
    ApplicationController controller;

    @Autowired
    ThreadService threadService;

    @Autowired
    Current current;

    public String convertBasicHtml(WebEngine webEngine, String text) {

        CompletableFuture<String> completableFuture = new CompletableFuture();

        completableFuture.runAsync(() -> {
            threadService.runActionLater(run -> {
                String rendered = (String) webEngine.executeScript(String.format("convertBasicHtml('%s')", IOHelper.normalize(text)));
                completableFuture.complete(rendered);
            });
        });

        return completableFuture.join();

    }

    public String convertHtmlArticle(WebEngine webEngine) {

        CompletableFuture<String> completableFuture = new CompletableFuture();

        completableFuture.runAsync(() -> {
            threadService.runActionLater(run -> {
                String text = current.currentEditorValue();
                String rendered = (String) webEngine.executeScript(String.format("convertHtmlArticle('%s')", IOHelper.normalize(text)));
                completableFuture.complete(rendered);
            });
        });

        return completableFuture.join();
    }

    public String convertHtmlBook(WebEngine webEngine, String text) {

        CompletableFuture<String> completableFuture = new CompletableFuture();

        completableFuture.runAsync(() -> {
            threadService.runActionLater(run -> {
                String rendered = (String) webEngine.executeScript(String.format("convertHtmlBook('%s')", IOHelper.normalize(text)));
                completableFuture.complete(rendered);
            });
        });

        return completableFuture.join();
    }

    public String convertDocbook(WebEngine webEngine, String text, boolean includeHeader) {

        CompletableFuture<String> completableFuture = new CompletableFuture();

        completableFuture.runAsync(() -> {
            threadService.runActionLater(run -> {
                String rendered = (String) webEngine.executeScript(String.format("convertDocbook('%s',%b)", IOHelper.normalize(text), includeHeader));
                completableFuture.complete(rendered);
            });
        });

        return completableFuture.join();

    }

    public String convertDocbookArticle(WebEngine webEngine) {

        CompletableFuture<String> completableFuture = new CompletableFuture();

        completableFuture.runAsync(() -> {
            threadService.runActionLater(run -> {
                String text = current.currentEditorValue();
                String rendered = (String) webEngine.executeScript(String.format("convertDocbookArticle('%s')", IOHelper.normalize(text)));
                completableFuture.complete(rendered);
            });
        });

        return completableFuture.join();

    }
}

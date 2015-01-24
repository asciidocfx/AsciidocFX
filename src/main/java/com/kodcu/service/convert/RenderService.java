package com.kodcu.service.convert;

import com.kodcu.controller.ApplicationController;
import com.kodcu.other.Current;
import com.kodcu.other.IOHelper;
import com.kodcu.service.ThreadService;
import javafx.application.Platform;
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

    public String convertBasicHtml(String text) {

        if (Platform.isFxApplicationThread())
            return (String) controller.getPreviewView().getEngine().executeScript(String.format("convertBasicHtml('%s')", IOHelper.normalize(text)));

        CompletableFuture<String> completableFuture = new CompletableFuture();

        completableFuture.runAsync(() -> {
            threadService.runActionLater(() -> {
                String rendered = (String) controller.getPreviewView().getEngine().executeScript(String.format("convertBasicHtml('%s')", IOHelper.normalize(text)));
                completableFuture.complete(rendered);
            });
        });

        return completableFuture.join();

    }

    public String convertHtmlArticle() {

        if (Platform.isFxApplicationThread()) {
            String text = current.currentEditorValue();
            return (String) controller.getPreviewView().getEngine().executeScript(String.format("convertHtmlArticle('%s')", IOHelper.normalize(text)));
        }

        CompletableFuture<String> completableFuture = new CompletableFuture();

        completableFuture.runAsync(() -> {
            threadService.runActionLater(() -> {
                String text = current.currentEditorValue();
                String rendered = (String) controller.getPreviewView().getEngine().executeScript(String.format("convertHtmlArticle('%s')", IOHelper.normalize(text)));
                completableFuture.complete(rendered);
            });
        });

        return completableFuture.join();
    }

    public String convertHtmlBook(String text) {

        if (Platform.isFxApplicationThread())
            return (String) controller.getPreviewView().getEngine().executeScript(String.format("convertHtmlBook('%s')", IOHelper.normalize(text)));

        CompletableFuture<String> completableFuture = new CompletableFuture();

        completableFuture.runAsync(() -> {
            threadService.runActionLater(() -> {
                String rendered = (String) controller.getPreviewView().getEngine().executeScript(String.format("convertHtmlBook('%s')", IOHelper.normalize(text)));
                completableFuture.complete(rendered);
            });
        });

        return completableFuture.join();
    }

    public String convertDocbook(String text, boolean includeHeader) {

        if (Platform.isFxApplicationThread())
            return (String) controller.getPreviewView().getEngine().executeScript(String.format("convertDocbook('%s',%b)", IOHelper.normalize(text), includeHeader));

        CompletableFuture<String> completableFuture = new CompletableFuture();

        completableFuture.runAsync(() -> {
            threadService.runActionLater(() -> {
                String rendered = (String) controller.getPreviewView().getEngine().executeScript(String.format("convertDocbook('%s',%b)", IOHelper.normalize(text), includeHeader));
                completableFuture.complete(rendered);
            });
        });

        return completableFuture.join();

    }

    public String convertDocbookArticle() {

        if (Platform.isFxApplicationThread()) {
            String text = current.currentEditorValue();
            return (String) controller.getPreviewView().getEngine().executeScript(String.format("convertDocbookArticle('%s')", IOHelper.normalize(text)));
        }

        CompletableFuture<String> completableFuture = new CompletableFuture();

        completableFuture.runAsync(() -> {
            threadService.runActionLater(() -> {
                String text = current.currentEditorValue();
                String rendered = (String) controller.getPreviewView().getEngine().executeScript(String.format("convertDocbookArticle('%s')", IOHelper.normalize(text)));
                completableFuture.complete(rendered);
            });
        });

        return completableFuture.join();

    }
}

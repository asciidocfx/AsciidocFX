package com.kodcu.other;

import com.kodcu.component.MyTab;
import com.kodcu.controller.ApplicationController;
import com.kodcu.service.ThreadService;
import javafx.application.Platform;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import netscape.javascript.JSObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by usta on 18.05.2014.
 */
@Component
public class Current {

    private final Pattern bookPattern = Pattern.compile(":doctype:(.*?)book");

    private final ApplicationController controller;
    private final ThreadService threadService;


    private Map<String, Integer> cache;

    @Autowired
    public Current(final ApplicationController controller, final ThreadService threadService) {
        this.controller = controller;
        this.threadService = threadService;
    }

    public MyTab currentTab() {
        return (MyTab) controller.getTabPane().getSelectionModel().getSelectedItem();
    }

    public Optional<Path> currentPath() {
        return Optional.ofNullable(currentTab().getPath());
    }

    public WebView currentWebView() {
        return currentTab().getWebView();
    }

    public WebEngine currentEngine() {
        return currentWebView().getEngine();
    }

    public Map<String, Integer> getCache() {
        if (Objects.isNull(cache))
            cache = new ConcurrentHashMap<String, Integer>();
        return cache;
    }

    public void setCache(Map<String, Integer> cache) {
        this.cache = cache;
    }

    public void setCurrentTabText(String currentTabText) {
        currentTab().setTabText(currentTabText);
    }

    public String getCurrentTabText() {
        return currentTab().getTabText();
    }

    public String currentEditorValue() {
        if (Platform.isFxApplicationThread())
            return (String) currentEngine().executeScript("editor.getValue()");

        final CompletableFuture<String> completableFuture = new CompletableFuture<>();

        CompletableFuture.runAsync(() -> {
            threadService.runActionLater(() -> {
                completableFuture.complete((String) currentEngine().executeScript("editor.getValue()"));
            });
        });

        return completableFuture.join();

    }

    public boolean currentIsBook() {
        String editorValue = currentEditorValue();
        Matcher matcher = bookPattern.matcher(editorValue);
        return matcher.find();
    }

    public boolean currentIsArticle() {
        String editorValue = currentEditorValue();
        Matcher matcher = bookPattern.matcher(editorValue);
        return !matcher.find();
    }

    public String currentEditorSelection() {
        String value = (String) currentEngine().executeScript("editor.session.getTextRange(editor.getSelectionRange())");
        return value;
    }

    public void insertEditorValue(String content) {
        ((JSObject)currentEngine().executeScript("window")).setMember("insertValue", content);
        currentEngine().executeScript("editor.insert(insertValue)");
    }

    public void clearEditorValue() {
        currentEngine().executeScript(String.format("editor.setValue('')"));
    }

}

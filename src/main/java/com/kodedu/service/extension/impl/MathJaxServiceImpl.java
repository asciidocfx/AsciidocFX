package com.kodedu.service.extension.impl;

import com.kodedu.config.ExtensionConfigBean;
import com.kodedu.controller.ApplicationController;
import com.kodedu.helper.IOHelper;
import com.kodedu.other.Current;
import com.kodedu.other.TrimWhite;
import com.kodedu.service.ThreadService;
import com.kodedu.service.cache.BinaryCacheService;
import com.kodedu.service.extension.MathJaxService;

import javafx.concurrent.Worker;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.WritableImage;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import netscape.javascript.JSObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.awt.image.BufferedImage;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

/**
 * Created by usta on 25.12.2014.
 */
@Component(MathJaxService.label)
public class MathJaxServiceImpl implements MathJaxService {

    private final Logger logger = LoggerFactory.getLogger(MathJaxService.class);

    private final ApplicationController controller;
    private final Current current;
    private final ExtensionConfigBean extensionConfigBean;
    @Autowired
    private ThreadService threadService;
    @Autowired
    private BinaryCacheService binaryCacheService;

    private WebView webView;
    private boolean initialized;

    @Value("${application.mathjax.url}")
    private String mathjaxUrl;

    @Autowired
    public MathJaxServiceImpl(final ApplicationController controller, final Current current, ExtensionConfigBean extensionConfigBean) {
        this.controller = controller;
        this.current = current;
        this.extensionConfigBean = extensionConfigBean;
    }

    private void initialize(Runnable... runnable) {

        webEngine().getLoadWorker().stateProperty().addListener((observableValue1, state, state2) -> {
            if (state2 == Worker.State.SUCCEEDED) {

                if (!initialized) {
                    for (Runnable run : runnable) {
                        run.run();
                    }
                }

                initialized = true;
            }
        });

        this.load();
    }

    private void load() {
        threadService.runActionLater(() -> {
            webEngine().load(String.format(mathjaxUrl, controller.getPort()));
        });
    }

    @Override
    public void reload() {
        this.load();
    }

    private WebEngine webEngine() {
        return getWebView().getEngine();
    }

    @Override
    public void processFormula(String formula, String imagesDir, String imageTarget, CompletableFuture completableFuture) {

        threadService.runActionLater(() -> {

            if (initialized) {
                getWindow().setMember("afx", this);
                getWindow().call("processFormula", formula, imagesDir, imageTarget, completableFuture);
            } else {
                initialize(() -> {
                    getWindow().setMember("afx", this);
                    getWindow().call("processFormula", formula, imagesDir, imageTarget, completableFuture);
                });
            }

        });
    }

    @Override
    public JSObject getWindow() {
        return (JSObject) webEngine().executeScript("window");
    }

    @Override
    public WebView getWebView() {
        if (Objects.isNull(webView)) {
            webView = new WebView();
            webView.setMaxHeight(1000);
            webView.setPrefHeight(1000);
            webView.setMaxWidth(1000);
            webView.setPrefWidth(1000);
            webView.setLayoutX(-35000);
            webView.setLayoutY(-35000);
            webView.setZoom(extensionConfigBean.getDefaultImageZoom());
            controller.getRootAnchor().getChildren().add(webView);
        }
        return webView;
    }

    @Override
    public void snapshotFormula(String formula, String imagesDir, String imageTarget, CompletableFuture completableFuture) {

        try {
            Objects.requireNonNull(imageTarget);

            boolean cachedResource = imageTarget.contains("/afx/cache");

            if (!imageTarget.contains(".png") && !cachedResource){
                completeSnapShot(completableFuture);
                return;
            }

            Integer cacheHit = current.getCache().get(imageTarget);

            int hashCode = (imageTarget + imagesDir + formula).hashCode();

            if (Objects.isNull(cacheHit) || hashCode != cacheHit) {

                WritableImage writableImage = getWebView().snapshot(new SnapshotParameters(), null);
                BufferedImage bufferedImage = SwingFXUtils.fromFXImage(writableImage, null);

                Path path = current.currentTab().getParentOrWorkdir();

                threadService.runTaskLater(() -> {
                    try {
                        TrimWhite trimWhite = new TrimWhite();
                        BufferedImage trimmed = trimWhite.trim(bufferedImage);
                        if (!cachedResource) {
                            Path imagePath = Paths.get(imageTarget);
                            IOHelper.imageWrite(trimmed, "png", imagePath.toFile());
                            threadService.runActionLater(() -> {
                                controller.clearImageCache(imagePath);
                            });
                        } else {
                            binaryCacheService.putBinary(imageTarget, trimmed);
                            threadService.runActionLater(() -> {
                                controller.clearImageCache(imageTarget);
                            });
                        }

                        completeSnapShot(completableFuture);

                        current.getCache().put(imageTarget, hashCode);
                        logger.debug("MathJax extension is ended for {}", imageTarget);

                    } catch (Exception e) {
                        logger.error("Problem occured while generating MathJax png", e);
                        completeSnapShot(completableFuture, e);
                        throw new RuntimeException(e);
                    }
                });
            }else{
                completeSnapShot(completableFuture);
            }
        } catch (Exception e) {
            logger.error("Problem occured while generating MathJax png", e);
            completeSnapShot(completableFuture, e);
            throw e;
        }


    }

    private void completeSnapShot(CompletableFuture completableFuture, Exception e) {
        if(Objects.nonNull(completableFuture)){
            completableFuture.completeExceptionally(e);
        }
    }

    private void completeSnapShot(CompletableFuture completableFuture) {
        if(Objects.nonNull(completableFuture)){
            completableFuture.complete(null);
        }
    }
}

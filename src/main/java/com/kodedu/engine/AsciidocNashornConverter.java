package com.kodedu.engine;

import com.kodedu.config.*;
import com.kodedu.controller.ApplicationController;
import com.kodedu.other.ConverterResult;
import com.kodedu.service.ThreadService;
import jdk.nashorn.api.scripting.JSObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.json.JsonObject;
import javax.script.Invocable;
import javax.script.ScriptEngine;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Created by usta on 22.08.2015.
 */
//@Lazy
@Component("NashornEngine")
@Order(1)
public class AsciidocNashornConverter implements AsciidocConvertible {

    private final ScriptEngine scriptEngine;
    private final ApplicationController controller;
    private final DocbookConfigBean docbookConfigBean;
    private final ThreadService threadService;
    private final EditorConfigBean editorConfigBean;
    private final CompletableFuture completableFuture = new CompletableFuture();

    private Invocable invocable;

    private Logger logger = LoggerFactory.getLogger(AsciidocNashornConverter.class);
    private final PreviewConfigBean previewConfigBean;
    private final HtmlConfigBean htmlConfigBean;
    private final AsciidocConfigMerger configMerger;

    @Autowired
    public AsciidocNashornConverter(ScriptEngine scriptEngine, ApplicationController controller, DocbookConfigBean docbookConfigBean, ThreadService threadService, EditorConfigBean editorConfigBean, PreviewConfigBean previewConfigBean, HtmlConfigBean htmlConfigBean, AsciidocConfigMerger configMerger) {

        this.scriptEngine = scriptEngine;
        this.controller = controller;
        this.docbookConfigBean = docbookConfigBean;
        this.threadService = threadService;
        this.editorConfigBean = editorConfigBean;
        this.previewConfigBean = previewConfigBean;
        this.htmlConfigBean = htmlConfigBean;
        this.configMerger = configMerger;

    }

    private ConverterResult convert(String functionName, String asciidoc, JsonObject config) {

        try {
            completableFuture.join();

            JsonObject finalConfig = updateConfig(asciidoc, config);

            Object o = invocable.invokeFunction(functionName, asciidoc, finalConfig.toString());
            JSObject convertDocbook = (JSObject) o;
            ConverterResult converterResult = new ConverterResult(convertDocbook);
            return converterResult;
        } catch (Exception e) {
            logger.error("Problem occured while {}", functionName, e);
            throw new RuntimeException(e);
        }

    }


    @Override
    public ConverterResult convertDocbook(String asciidoc) {
        return convert("convertDocbook", asciidoc, docbookConfigBean.getJSON());
    }

    @Override
    public ConverterResult convertAsciidoc(String asciidoc) {
        return convert("convertAsciidoc", asciidoc, previewConfigBean.getJSON());
    }

    @Override
    public ConverterResult convertHtml(String asciidoc) {
        return convert("convertHtml", asciidoc, htmlConfigBean.getJSON());
    }

    @Override
    public void convertOdf(String asciidoc) {

    }

    @Override
    public void fillOutlines(Object doc) {
        threadService.runTaskLater(() -> {
            try {
                invocable.invokeFunction("fillOutlines", doc);
            } catch (Exception e) {
                logger.debug("Problem occured while filling outlines", e);
            }
        });
    }

    @Override
    public String applyReplacements(String asciidoc) {
        try {
            return (String) invocable.invokeFunction("apply_replacements", asciidoc);
        } catch (Exception e) {
            logger.debug("Problem occured while applying replacements", e);
        }
        return asciidoc;
    }

    private JsonObject updateConfig(String asciidoc, JsonObject config) {
        return configMerger.updateConfig(asciidoc, config);
    }

    public void initialize() {

        if (true) // Don't use nashorn until bugfix
            return;
        ;

        completableFuture.runAsync(() -> {
            try {

                scriptEngine.put("afx", this.controller);

                List<String> scripts = Arrays.asList("nashorn-shim.js", "jade.js", "asciidoctor-all.js", "asciidoctor-image-size-info.js",
                        "asciidoctor-data-line.js", "asciidoctor-block-extensions.js",
                        "asciidoctor-chart-block.js", "asciidoctor-docbook.js", "asciidoctor-reveal.js", "asciidoctor-deck.js",
                        "apply-replacements.js", "asciidoctor-odf.js", "buffhelper.js", "outliner.js", "converters.js");

                Path configPath = controller.getConfigPath();

                for (String script : scripts) {

                    Path resolve = configPath.resolve("public/js").resolve(script);
                    scriptEngine.eval(String.format("load(\"%s\")", "conf/public/js/" + script));
//                    try (FileInputStream fileInputStream = new FileInputStream(resolve.toFile());
//                         InputStreamReader is = new InputStreamReader(fileInputStream, "UTF-8");) {
//                        scriptEngine.eval(is);
//                    }

                }

                this.invocable = (Invocable) scriptEngine;

                completableFuture.complete(null);

            } catch (Exception e) {
                logger.error(e.getMessage(), e);
                completableFuture.completeExceptionally(e);
            }
        }, threadService.executor());
    }
}

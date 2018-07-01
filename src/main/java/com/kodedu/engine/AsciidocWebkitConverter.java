package com.kodedu.engine;

import com.kodedu.component.ViewPanel;
import com.kodedu.config.*;
import com.kodedu.controller.ApplicationController;
import com.kodedu.other.ConverterResult;
import com.kodedu.other.Current;
import com.kodedu.other.IOHelper;
import com.kodedu.service.DirectoryService;
import com.kodedu.service.ThreadService;
import javafx.application.Platform;
import netscape.javascript.JSObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.json.JsonObject;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * Created by usta on 09.04.2015.
 */
@Component("WebkitEngine")
public class AsciidocWebkitConverter extends ViewPanel implements AsciidocConvertible {

    private final PreviewConfigBean previewConfigBean;
    private final OdfConfigBean odfConfigBean;
    private final DocbookConfigBean docbookConfigBean;
    private final HtmlConfigBean htmlConfigBean;
    private final AsciidocConfigMerger configMerger;

    private final Map<String, CompletableFuture<ConverterResult>> webWorkerTasks = new ConcurrentHashMap();

    @Value("${application.index.url}")
    private String indexUrl;

    private Logger logger = LoggerFactory.getLogger(AsciidocWebkitConverter.class);
    private final DirectoryService directoryService;

    @Autowired
    public AsciidocWebkitConverter(ThreadService threadService, ApplicationController controller, Current current, EditorConfigBean editorConfigBean, PreviewConfigBean previewConfigBean, OdfConfigBean odfConfigBean, DocbookConfigBean docbookConfigBean, HtmlConfigBean htmlConfigBean, AsciidocConfigMerger configMerger, DirectoryService directoryService) {
        super(threadService, controller, current, editorConfigBean);
        this.previewConfigBean = previewConfigBean;
        this.odfConfigBean = odfConfigBean;
        this.docbookConfigBean = docbookConfigBean;
        this.htmlConfigBean = htmlConfigBean;
        this.configMerger = configMerger;
        this.directoryService = directoryService;
    }

    public String getTemplate(String templateDir) {

        Path path = controller.getConfigPath().resolve("slide/templates").resolve(templateDir);

        if (Files.notExists(path)) {
            logger.error("Template not found in {}", path);
            return "";
        }

        String template = IOHelper.readFile(path);
        return template;
    }

    public JSObject getWindow() {
        return (JSObject) webEngine().executeScript("window");
    }

    @Override
    public void runScroller(String text) {
        // no-op
    }

    @Override
    public void scrollByPosition(String text) {
        // no-op
    }

    @Override
    public void scrollByLine(String text) {
        // no-op
    }

    @Override
    public void browse() {
        controller.browseInDesktop(String.format(indexUrl, controller.getPort(), directoryService.interPath()));
    }

    @Override
    public void fillOutlines(Object doc) {
        threadService.runActionLater(() -> {
            try {
                getWindow().call("fillOutlines", doc);
            } catch (Exception e) {
                logger.debug("Problem occured while filling outlines", e);
            }
        });
    }

    @Override
    public String applyReplacements(String asciidoc) {

        if (!Platform.isFxApplicationThread()) {
            CompletableFuture<String> completableFuture = new CompletableFuture<>();
            completableFuture.runAsync(() -> {
                threadService.runActionLater(() -> {
                    try {
                        String replacements = applyReplacements(asciidoc);
                        completableFuture.complete(replacements);
                    } catch (Exception e) {
                        completableFuture.completeExceptionally(e);
                    }
                });
            });
            return completableFuture.join();
        }

        try {
            return (String) getWindow().call("apply_replacements", asciidoc);
        } catch (Exception e) {
            logger.debug("Problem occured while applying replacements", e);
        }

        return asciidoc;

    }

    public String findRenderedSelection(String content) {
        this.setMember("context", content);
        return (String) webEngine().executeScript("findRenderedSelection(context)");
    }

    protected ConverterResult convert(String functionName, String asciidoc, JsonObject config) {

        final CompletableFuture<ConverterResult> completableFuture = new CompletableFuture();
        final String taskId = UUID.randomUUID().toString();

        webWorkerTasks.put(taskId, completableFuture);
        final String conf = config.toString();
        threadService.runActionLater(() -> {
            this.setMember("taskId", taskId);
            this.setMember("editorValue", asciidoc);
            this.setMember("editorOptions", conf);
            try {
                webEngine().executeScript(String.format("if ((typeof %s)!== \"undefined\"){ %s(taskId,editorValue,editorOptions) }", functionName, functionName));
            } catch (Exception e) {
                completableFuture.completeExceptionally(e);
            }
        });

        try {
            return completableFuture.get(60, TimeUnit.SECONDS);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private JsonObject updateConfig(String asciidoc, JsonObject config) {
        return configMerger.updateConfig(asciidoc, config);
    }

    @Override
    public ConverterResult convertDocbook(String asciidoc) {
        return convert("convertDocbook", asciidoc, updateConfig(asciidoc, docbookConfigBean.getJSON()));
    }

    @Override
    public ConverterResult convertAsciidoc(String asciidoc) {
        return convert("convertAsciidoc", asciidoc, updateConfig(asciidoc, previewConfigBean.getJSON()));
    }

    @Override
    public ConverterResult convertHtml(String asciidoc) {
        return convert("convertHtml", asciidoc, updateConfig(asciidoc, htmlConfigBean.getJSON()));
    }

    @Override
    public void convertOdf(String asciidoc) {
        convert("convertOdf", asciidoc, updateConfig(asciidoc, odfConfigBean.getJSON()));
    }

    public Map<String, CompletableFuture<ConverterResult>> getWebWorkerTasks() {
        return webWorkerTasks;
    }

    public boolean isHtml(String text) {
        Object isHtml = this.call("isHtml", text);

        if (isHtml instanceof Boolean) {
            return (boolean) isHtml;
        }

        return false;
    }
}

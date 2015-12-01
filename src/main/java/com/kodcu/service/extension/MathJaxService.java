package com.kodcu.service.extension;

import com.kodcu.controller.ApplicationController;
import com.kodcu.other.Current;
import com.kodcu.other.IOHelper;
import com.kodcu.service.ThreadService;
import javafx.concurrent.Worker;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import netscape.javascript.JSObject;
import org.apache.batik.dom.svg.SAXSVGDocumentFactory;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.PNGTranscoder;
import org.apache.batik.util.XMLResourceDescriptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.w3c.dom.svg.SVGDocument;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

import static java.nio.file.StandardOpenOption.*;

/**
 * Created by usta on 25.12.2014.
 */
@Component
public class MathJaxService {

    private final Logger logger = LoggerFactory.getLogger(MathJaxService.class);

    private final ApplicationController controller;
    private final Current current;
    private final ThreadService threadService;
    private WebView webView;
    private boolean initialized;

    @Value("${application.mathjax.url}")
    private String mathjaxUrl;

    @Autowired
    public MathJaxService(final ApplicationController controller, final Current current, ThreadService threadService) {
        this.controller = controller;
        this.current = current;
        this.threadService = threadService;
    }

    private void initialize(Runnable... runnable) {

        webEngine().getLoadWorker().stateProperty().addListener((observableValue1, state, state2) -> {
            if (state2 == Worker.State.SUCCEEDED) {
                JSObject window = getWindow();
                if (window.getMember("afx").equals("undefined"))
                    window.setMember("afx", controller);

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

    public void reload() {
        this.load();
    }

    private WebEngine webEngine() {
        return getWebView().getEngine();
    }

    public void appendFormula(String formula, String imagesDir, String imageTarget) {
        threadService.runActionLater(() -> {

            if (initialized) {
                getWindow().call("appendFormula", formula, imagesDir, imageTarget);
            } else {
                initialize(() -> {
                    getWindow().call("appendFormula", formula, imagesDir, imageTarget);
                });
            }

        });
    }

    public JSObject getWindow() {
        return (JSObject) webEngine().executeScript("window");
    }

    public void svgToPng(String imagesDir, String imageTarget, String svg, String formula, float width, float height) {

        if (!svg.startsWith("<svg"))
            return;

        if (!imageTarget.endsWith(".png") && !imageTarget.endsWith(".svg"))
            return;

        Integer cacheHit = current.getCache().get(imageTarget);
        int hashCode = Objects.hash(imagesDir, imageTarget, formula, width, height);
        if (Objects.nonNull(cacheHit))
            if (hashCode == cacheHit)
                return;

        logger.debug("MatJax extension is started for {}", imageTarget);

        current.getCache().put(imageTarget, hashCode);

        if (imageTarget.endsWith(".png"))
            saveAsPng(imagesDir, imageTarget, svg, formula, width, height);
        else if (imageTarget.endsWith(".svg"))
            saveAsSvg(imagesDir, imageTarget, svg, formula, width, height);

    }

    private void saveAsSvg(String imagesDir, String imageTarget, String svg, String formula, float width, float height) {
        try {

            Path path = current.currentTab().getParentOrWorkdir();
            Files.createDirectories(path.resolve(imagesDir));

            Path imagePath = path.resolve(imageTarget);
            IOHelper.writeToFile(imagePath, svg.getBytes(Charset.forName("UTF-8")), CREATE, WRITE, TRUNCATE_EXISTING, SYNC);

            logger.debug("MathJax extension is ended for {}", imageTarget);
            threadService.runActionLater(() -> {
                controller.clearImageCache(imagePath);
            });
        } catch (IOException e) {
            logger.error("Problem occured while generating MathJax svg", e);
        }
    }

    private void saveAsPng(String imagesDir, String imageTarget, String svg, String formula, float width, float height) {
        try (StringReader reader = new StringReader(svg);
             ByteArrayOutputStream ostream = new ByteArrayOutputStream();) {

            String uri = "http://www.w3.org/2000/svg";
            SAXSVGDocumentFactory f = new SAXSVGDocumentFactory(XMLResourceDescriptor.getXMLParserClassName());
            SVGDocument doc = f.createSVGDocument(uri, reader);

            TranscoderInput transcoderInput = new TranscoderInput(doc);
            TranscoderOutput transcoderOutput = new TranscoderOutput(ostream);

            PNGTranscoder transcoder = new PNGTranscoder();
            transcoder.addTranscodingHint(PNGTranscoder.KEY_WIDTH, width);
            transcoder.addTranscodingHint(PNGTranscoder.KEY_HEIGHT, height);
            transcoder.transcode(transcoderInput, transcoderOutput);

            Path path = current.currentTab().getParentOrWorkdir();
            Files.createDirectories(path.resolve(imagesDir));

            Path imagePath = path.resolve(imageTarget);
            IOHelper.writeToFile(imagePath, ostream.toByteArray(), CREATE, WRITE, TRUNCATE_EXISTING, SYNC);

            logger.debug("MathJax extension is ended for {}", imageTarget);
            threadService.runActionLater(() -> {
                controller.clearImageCache(imagePath);
            });

        } catch (Exception e) {
            logger.error("Problem occured while generating MathJax png", e);
        }
    }

    public WebView getWebView() {
        if (Objects.isNull(webView)) {
            webView = new WebView();
            webView.setVisible(false);
        }
        return webView;
    }
}

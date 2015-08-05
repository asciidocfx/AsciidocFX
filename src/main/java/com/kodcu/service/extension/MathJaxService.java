package com.kodcu.service.extension;

import com.kodcu.controller.ApplicationController;
import com.kodcu.other.Current;
import com.kodcu.service.ThreadService;
import netscape.javascript.JSObject;
import org.apache.batik.dom.svg.SAXSVGDocumentFactory;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.PNGTranscoder;
import org.apache.batik.util.XMLResourceDescriptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    public MathJaxService(final ApplicationController controller, final Current current, ThreadService threadService) {
        this.controller = controller;
        this.current = current;
        this.threadService = threadService;
    }

    public void appendFormula(String formula, String imagesDir, String imageTarget) {
        getWindow().call("appendFormula", new Object[]{formula, imagesDir, imageTarget});
    }

    public JSObject getWindow() {
        JSObject window = (JSObject) controller.getMathjaxView().getEngine().executeScript("window");
        return window;
    }

    public void svgToPng(String imagesDir, String imageTarget, String svg, String formula, float width, float height) {

        if (!svg.startsWith("<svg"))
            return;

        if (!imageTarget.endsWith(".png") && !imageTarget.endsWith(".svg"))
            return;

        Integer cacheHit = current.getCache().get(imageTarget);
        int hashCode = (imagesDir + imageTarget + formula + width + height).hashCode();
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
            if (!current.currentPath().isPresent())
                controller.saveDoc();

            Path path = current.currentPath().get().getParent();
            Files.createDirectories(path.resolve(imagesDir));

            Files.write(path.resolve(imageTarget), svg.getBytes(Charset.forName("UTF-8")), CREATE, WRITE, TRUNCATE_EXISTING);

            logger.debug("MathJax extension is ended for {}", imageTarget);
            threadService.runActionLater(() -> {
                controller.clearImageCache();
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

            if (!current.currentPath().isPresent())
                controller.saveDoc();

            Path path = current.currentPath().get().getParent();
            Files.createDirectories(path.resolve(imagesDir));

            Files.write(path.resolve(imageTarget), ostream.toByteArray(), CREATE, WRITE, TRUNCATE_EXISTING);

            logger.debug("MathJax extension is ended for {}", imageTarget);
            threadService.runActionLater(() -> {
                controller.clearImageCache();
            });

        } catch (Exception e) {
            logger.error("Problem occured while generating MathJax png", e);
        }
    }
}

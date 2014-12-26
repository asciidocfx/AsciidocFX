package com.kodcu.service.extension;

import com.kodcu.controller.ApplicationController;
import com.kodcu.other.Current;
import com.kodcu.other.IOHelper;
import javafx.scene.web.WebEngine;
import org.apache.batik.dom.svg.SAXSVGDocumentFactory;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.PNGTranscoder;
import org.apache.batik.util.XMLResourceDescriptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.w3c.dom.svg.SVGDocument;

import java.io.ByteArrayOutputStream;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;
import static java.nio.file.StandardOpenOption.WRITE;

/**
 * Created by usta on 25.12.2014.
 */
@Component
public class MathJaxService {

    @Autowired
    private ApplicationController controller;

    @Autowired
    private Current current;

    public String appendFormula(String fileName, String formula) {
        if (fileName.endsWith(".png")) {
            WebEngine engine = controller.getMathjaxView().getEngine();
            engine.executeScript(String.format("appendFormula('%s','%s')", fileName, IOHelper.normalize(formula)));
            return "images/" + fileName;
        }

        return "";
    }

    public void svgToPng(String fileName, String svg, String formula, float width, float height) {

        if (!fileName.endsWith(".png") || !svg.startsWith("<svg"))
            return;

        Integer cacheHit = current.getCache().get(fileName);
        int hashCode = fileName.concat(formula).hashCode();
        if (Objects.nonNull(cacheHit))
            if (hashCode == cacheHit)
                return;

        current.getCache().put(fileName, hashCode);

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
            Files.createDirectories(path.resolve("images"));

            Files.write(path.resolve("images/").resolve(fileName), ostream.toByteArray(), CREATE, WRITE, TRUNCATE_EXISTING);

            controller.getLastRenderedChangeListener().changed(null, controller.getLastRendered().getValue(), controller.getLastRendered().getValue());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

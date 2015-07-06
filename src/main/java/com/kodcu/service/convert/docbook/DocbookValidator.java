package com.kodcu.service.convert.docbook;

import com.kodcu.controller.ApplicationController;
import com.kodcu.other.Current;
import com.kodcu.other.IOHelper;
import com.kodcu.service.ui.TabService;
import javafx.application.Platform;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.xml.sax.SAXParseException;

import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.ByteArrayInputStream;
import java.nio.charset.Charset;
import java.nio.file.Path;

/**
 * Created by usta on 21.06.2015.
 */
@Component
public class DocbookValidator {

    private final ApplicationController controller;
    private final TabService tabService;
    private final Current current;

    private Logger logger = LoggerFactory.getLogger(DocbookValidator.class);

    @Autowired
    public DocbookValidator(ApplicationController controller, TabService tabService, Current current) {
        this.controller = controller;
        this.tabService = tabService;
        this.current = current;
    }

    public boolean validateDocbook(String rendered) {
        try {
            SchemaFactory schemaFactory = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");

            Path xsdPath = controller.getConfigPath().resolve("docbook-config/xsd/docbook.xsd");
            Schema sch = schemaFactory.newSchema(new StreamSource(xsdPath.toFile()));
            Validator validator = sch.newValidator();

            validator.validate(new StreamSource(new ByteArrayInputStream(rendered.getBytes(Charset.forName("UTF-8")))));

            logger.debug("Docbook successfully validated");

            return true;

        } catch (Exception e) {

            if (e instanceof SAXParseException) {
                SAXParseException pe = (SAXParseException) e;
                int columnNumber = pe.getColumnNumber();
                int lineNumber = pe.getLineNumber();

                Path currentDir = current.currentPath().map(Path::getParent).get();
                Path xmlPath = IOHelper.createTempFile(currentDir, ".xml");
                IOHelper.writeToFile(xmlPath, rendered);
                Platform.runLater(() -> {
                    tabService.addTab(xmlPath, () -> {
                        current.currentEditor().call("addAnnotation", lineNumber, columnNumber, pe.getMessage(), "error");
                    });
                });
                logger.error("Please fix Docbook validation error. LineNumber: {}, Column: {}", lineNumber, columnNumber, pe);
            } else {
                logger.error("Problem occured while validating Docbook content", e);
            }


            return false;
        }
    }
}

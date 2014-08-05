package com.kodcu.other;

import org.apache.fop.apps.FOPException;
import org.apache.fop.cli.InputHandler;

import javax.xml.transform.*;
import javax.xml.transform.stream.StreamSource;
import java.io.File;
import java.util.Vector;

/**
 * Created by usta on 31.07.2014.
 */
public class InputHandlerExtended extends InputHandler {

    private Vector xsltParams;

    public InputHandlerExtended(File fofile) {
        super(fofile);
    }

    public InputHandlerExtended(File xmlfile, File xsltfile, Vector params) {
        super(xmlfile, xsltfile, params);
        this.xsltParams = params;

    }

    @Override
    protected void transformTo(Result result) throws FOPException {
        try {
            // Setup XSLT
            TransformerFactory factory = TransformerFactory.newInstance();
            Transformer transformer;

            Source xsltSource = createXSLTSource();
            if (xsltSource == null) {   // FO Input
                transformer = factory.newTransformer();
            } else {    // XML/XSLT input
                transformer = factory.newTransformer(xsltSource);

                // Set the value of parameters, if any, defined for stylesheet
                if (xsltParams != null) {
                    for (int i = 0; i < xsltParams.size(); i += 2) {
                        transformer.setParameter((String) xsltParams.elementAt(i),
                                (String) xsltParams.elementAt(i + 1));
                    }
                }
            }
            URIResolver temp = transformer.getURIResolver();
            transformer.setURIResolver((href, base) -> {
                System.out.format("hmm %s - %s %n", href, base);
                return new StreamSource(InputHandlerExtended.class.getResourceAsStream(base));
            });


            transformer.setErrorListener(this);

            // Create a SAXSource from the input Source file
            Source src = createMainSource();

            // Start XSLT transformation and FOP processing
            transformer.transform(src, result);

        } catch (Exception e) {
            throw new FOPException(e);
        }
    }
}

package com.kodcu.other;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringWriter;

/**
 * Created by usta on 19.03.2015.
 */
public class XMLHelper {

    private static Logger logger = LoggerFactory.getLogger(IOHelper.class);

    public static String nodeToString(Node node, boolean omitDeclaration) {
        try (StringWriter writer = new StringWriter();) {
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, omitDeclaration ? "yes" : "no");
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");

            transformer.transform(new DOMSource(node), new StreamResult(writer));
            return writer.toString();
        } catch (Exception ex) {
           logger.error(ex.getMessage(),ex);
        }
        return "";
    }
}

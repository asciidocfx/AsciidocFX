package com.kodcu.config;

import javafx.collections.ObservableList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.json.*;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by usta on 31.08.2015.
 */
@Component
public class AsciidocConfigMerger {

    private Logger logger = LoggerFactory.getLogger(AsciidocConfigMerger.class);

    private final EditorConfigBean editorConfigBean;

    public final Pattern attributePattern = Pattern.compile("^:(?<key>.*):(.*)$", Pattern.MULTILINE);

    @Autowired
    public AsciidocConfigMerger(EditorConfigBean editorConfigBean) {
        this.editorConfigBean = editorConfigBean;
    }

    public JsonObject updateConfig(String asciidoc, JsonObject config) {
        try {

            Matcher matcher = attributePattern.matcher(asciidoc);

            JsonObject currentAttributes = config.getJsonObject("attributes");

            JsonObjectBuilder finalBuilder = Json.createObjectBuilder();
            JsonArrayBuilder finalAttrBuilder = Json.createArrayBuilder();

            // add converter attributes
            for (Map.Entry<String, JsonValue> entry : config.entrySet()) {
                String key = entry.getKey();
                JsonValue value = entry.getValue();

                if (!"attributes".equals(key)) {
                    finalBuilder.add(key, value);
                }
            }

            // find document attributes
            List<String> foundKeys = new LinkedList<>();
            while (matcher.find()) {
                String key = matcher.group("key");
                foundKeys.add(key);
            }

            // add document attributes
            for (Map.Entry<String, JsonValue> entry : currentAttributes.entrySet()) {
                String key = entry.getKey();
                JsonValue value = entry.getValue();
                String finalValue = "";

                if (value.getValueType() == JsonValue.ValueType.STRING) {
                    finalValue = ((JsonString) value).getString();
                } else {
                    finalValue = value.toString().replace("\"", "");
                }

                if (!foundKeys.contains(key)) {
                    finalAttrBuilder.add(key + "=" + finalValue);
                }
            }


            if (!foundKeys.contains("lang") && !currentAttributes.containsKey("lang")) {
                ObservableList<String> defaultLanguage = editorConfigBean.getDefaultLanguage();

                if (defaultLanguage.size() > 0) {
                    finalAttrBuilder.add("lang=" + defaultLanguage.get(0));
                }

            }

            finalBuilder.add("attributes", finalAttrBuilder);

            return finalBuilder.build();
        } catch (Exception e) {
            logger.error("Problem occured while merging options", e);
        }
        return config;
    }
}

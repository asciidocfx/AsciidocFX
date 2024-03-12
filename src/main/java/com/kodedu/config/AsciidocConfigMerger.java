package com.kodedu.config;

import com.kodedu.controller.TextChangeEvent;
import javafx.collections.ObservableList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import jakarta.json.*;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.kodedu.other.JsonHelper.getJsonObjectOrEmpty;
import static org.apache.commons.io.FilenameUtils.getBaseName;
import static org.apache.commons.io.FilenameUtils.getExtension;

/**
 * Created by usta on 31.08.2015.
 */
@Component
public class AsciidocConfigMerger {

    private Logger logger = LoggerFactory.getLogger(AsciidocConfigMerger.class);

    private final EditorConfigBean editorConfigBean;

    public final Pattern attributePattern = Pattern.compile("^:(!*)(?<key>.*)(!*):(.*)$", Pattern.MULTILINE);

    @Autowired
    public AsciidocConfigMerger(EditorConfigBean editorConfigBean) {
        this.editorConfigBean = editorConfigBean;
    }

    public JsonObject updateConfig(TextChangeEvent event, JsonObject config) {
        try {

            String asciidoc = event.getText();
            Matcher matcher = attributePattern.matcher(asciidoc);

            JsonObject currentAttributes = getJsonObjectOrEmpty(config, "attributes");

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

            Path path = event.getPath();
            if (path != null) {
                if (!foundKeys.contains("docdir")) {
                    finalAttrBuilder.add("docdir=" + path.getParent());
                }
                if (!foundKeys.contains("docfile")) {
                    finalAttrBuilder.add("docfile=" + path);
                }
                String filename = path.getFileName().toString();
                if (!foundKeys.contains("docfilesuffix")) {
                    finalAttrBuilder.add("docfilesuffix=." + getExtension(filename));
                }
                if (!foundKeys.contains("docname")) {
                    finalAttrBuilder.add("docname=" + getBaseName(filename));
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

package com.kodcu.controller;

import com.kodcu.other.IOHelper;
import com.kodcu.service.DirectoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import java.io.StringReader;
import java.nio.file.Path;
import java.util.Base64;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

/**
 * Created by usta on 17.12.2015.
 */
@Controller
public class DataUriResource {

    private final RestTemplate restTemplate;
    private final Base64.Encoder base64Encoder;
    private final DirectoryService directoryService;

    @Autowired
    public DataUriResource(RestTemplate restTemplate, Base64.Encoder base64Encoder, DirectoryService directoryService) {
        this.restTemplate = restTemplate;
        this.base64Encoder = base64Encoder;
        this.directoryService = directoryService;
    }

    @RequestMapping(value = "/read-data-uri", method = POST, consumes = "application/json")
    @ResponseBody
    public String readUri(@RequestBody String data) {

        try (final StringReader stringReader = new StringReader(data);
             final JsonReader jsonReader = Json.createReader(stringReader);) {

            final JsonObject object = jsonReader.readObject();
            String imageUri = object.getString("imageUri");
            String dataFormat = "data:image/%s;%s,%s";

            if (imageUri.startsWith("//")) {
                imageUri = imageUri.replaceFirst("//", "http://");
            }

            dataFormat = String.format(dataFormat, "png", "base64", "%s");

            if (isExternalUri(imageUri)) {
                return String.format(dataFormat, getExternalContent(imageUri));
            } else {
                return String.format(dataFormat, getLocalContent(imageUri));
            }

        }
    }

    private Object getLocalContent(String uri) {
        final Path path = directoryService.findPathInCurrentOrWorkDir(uri);
        final byte[] bytes = IOHelper.readFile(path, byte[].class);
        return base64Encoder.encodeToString(bytes);
    }

    private Object getExternalContent(String uri) {
        final byte[] bytes = restTemplate.getForObject(uri, byte[].class);
        return base64Encoder.encodeToString(bytes);
    }

    private boolean isSvg(String uri) {
        return uri.endsWith(".svg");
    }

    private boolean isExternalUri(String uri) {
        return uri.startsWith("http");
    }
}

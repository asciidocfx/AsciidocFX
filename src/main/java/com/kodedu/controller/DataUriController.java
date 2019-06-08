package com.kodedu.controller;

import com.kodedu.helper.IOHelper;
import com.kodedu.service.DirectoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;

import java.nio.file.Path;
import java.util.Base64;
import java.util.Objects;

import static org.springframework.web.bind.annotation.RequestMethod.*;

/**
 * Created by usta on 17.12.2015.
 */
@Controller
public class DataUriController {

    private final RestTemplate restTemplate;
    private final Base64.Encoder base64Encoder;
    private final DirectoryService directoryService;

    private final Logger logger = LoggerFactory.getLogger(DataUriController.class);

    @Autowired
    public DataUriController(RestTemplate restTemplate, Base64.Encoder base64Encoder, DirectoryService directoryService) {
        this.restTemplate = restTemplate;
        this.base64Encoder = base64Encoder;
        this.directoryService = directoryService;
    }

    @RequestMapping(value = "/read-data-uri", method = {HEAD, OPTIONS, POST}, produces = "*/*", consumes = "*/*")
    @ResponseBody
    public String readUri(@RequestParam(value = "path", required = true) String imageUri,
                          @RequestParam(value = "mimetype", required = true, defaultValue = "image/png") String mimetype
    ) {

        String encoding = "base64";
        String dataUri = String.format("data:%s:%s,", mimetype, encoding);

        try {

            String dataFormat = "data:%s;%s,%s";

            if (imageUri.startsWith("//")) {
                imageUri = imageUri.replaceFirst("//", "http://");
            }

            dataUri = String.format(dataFormat, mimetype, encoding, getImageContent(imageUri));
        } catch (Exception e) {
            if (imageUri.startsWith("http")) {
                logger.warn("image to embed not found or not readable: {}", imageUri);
            }
        }

        return dataUri;
    }

    private Object getImageContent(String imageUri) {
        byte[] bytes = new byte[]{};

        if (isExternalUri(imageUri)) {
            bytes = restTemplate.getForObject(imageUri, byte[].class);
        } else {
            final Path path = directoryService.findPathInWorkdirOrLookup(IOHelper.getPath(imageUri));
            Objects.requireNonNull(path, "No such file or directory: " + imageUri);
            bytes = IOHelper.readFile(path, byte[].class);
        }

        return base64Encoder.encodeToString(bytes);
    }

    private boolean isSvg(String uri) {
        return uri.endsWith(".svg");
    }

    private boolean isExternalUri(String uri) {
        return uri.startsWith("http");
    }
}

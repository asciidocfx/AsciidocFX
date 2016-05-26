package com.kodcu.controller;

import com.kodcu.other.IOHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Objects;
import java.util.function.Consumer;

import static org.springframework.web.bind.annotation.RequestMethod.*;

/**
 * Created by usta on 02.09.2015.
 */
@Controller
public class AllController {

    private final DynamicResource dynamicResource;
    private final EpubResource epubResource;
    private final GeneralResource generalResource;
    private final LiveResource liveResource;
    private final SlideResource slideResource;
    private final JadeResource jadeResource;
    private final WebWorkerResource webWorkerResource;


    private Logger logger = LoggerFactory.getLogger(AllController.class);

    @Autowired
    public AllController(DynamicResource dynamicResource, EpubResource epubResource, GeneralResource generalResource, LiveResource liveResource, SlideResource slideResource, JadeResource jadeResource, WebWorkerResource webWorkerResource) {
        this.dynamicResource = dynamicResource;
        this.epubResource = epubResource;
        this.generalResource = generalResource;
        this.liveResource = liveResource;
        this.slideResource = slideResource;
        this.jadeResource = jadeResource;
        this.webWorkerResource = webWorkerResource;
    }


    @RequestMapping(value = {"/**/*.*", "*.*"}, method = {GET, HEAD, OPTIONS, POST}, produces = "*/*", consumes = "*/*")
    @ResponseBody
    public void all(HttpServletRequest request, HttpServletResponse response) {

        Payload payload = new Payload();
        payload.setRequest(request);
        payload.setResponse(response);
        payload.setRequestURI(request.getRequestURI());

        Router router = new Router(payload)
                .executeIf("/afx/resource/", generalResource::executeAfxResource)
                .executeIf("/afx/dynamic/", dynamicResource::executeDynamicResource)
                .executeIf("/afx/live/", liveResource::executeLiveResource)
                .executeIf("/afx/slide/", slideResource::executeSlideResource)
                .executeIf("/afx/worker/", webWorkerResource::executeWorkerResource)
                .executeIf("/afx/jade/", jadeResource::executeJadeResource)
                .executeIf("/afx/epub/", epubResource::executeEpubResource);

    }

    class Payload {
        private String pattern;
        private String requestURI;
        private String finalURI;
        private HttpServletRequest request;
        private HttpServletResponse response;

        public String getPattern() {
            return pattern;
        }

        public void setPattern(String pattern) {
            this.pattern = pattern;
        }

        public String getRequestURI() {
            return requestURI;
        }

        public void setRequestURI(String requestURI) {
            this.requestURI = requestURI;
        }

        public String getFinalURI() {
            return IOHelper.decode(finalURI, "UTF-8");
        }

        public void setFinalURI(String finalURI) {
            this.finalURI = finalURI;
        }

        public HttpServletRequest getRequest() {
            return request;
        }

        public void setRequest(HttpServletRequest request) {
            this.request = request;
        }

        public HttpServletResponse getResponse() {
            return response;
        }

        public void setResponse(HttpServletResponse response) {
            this.response = response;
        }

        public void sendRedirect(String url) {
            try {
                response.sendRedirect(url);
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
            }
        }

        public Payload write(String content) {
            try (PrintWriter writer = response.getWriter();) {
                writer.write(content);
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
            return this;
        }

        public void setStatus(HttpStatus status) {
            response.setStatus(status.value());
        }

        public String param(String param) {
            return getRequest().getParameter(param);
        }
    }


    class Router {

        private final Payload payload;

        public Router(Payload payload) {
            this.payload = payload;
        }

        public Router executeIf(String pattern, Consumer<Payload> consumer) {

            if (payload.getRequestURI().contains(pattern)) {

                payload.setPattern(pattern);
                payload.setFinalURI(payload.getRequestURI().replace(pattern, ""));

                try {
                    consumer.accept(payload);
                } catch (Exception e) {
                    logger.debug(e.getMessage(), e);
                }
            }
            return this;
        }
    }

}

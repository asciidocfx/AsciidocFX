package com.kodcu.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.async.DeferredResult;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.function.Consumer;
import java.util.function.Function;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.HEAD;

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


    private Logger logger = LoggerFactory.getLogger(AllController.class);

    @Autowired
    public AllController(DynamicResource dynamicResource, EpubResource epubResource, GeneralResource generalResource, LiveResource liveResource, SlideResource slideResource) {
        this.dynamicResource = dynamicResource;
        this.epubResource = epubResource;
        this.generalResource = generalResource;
        this.liveResource = liveResource;
        this.slideResource = slideResource;
    }


    @RequestMapping(value = {"/**/*.*", "*.*"}, method = {GET, HEAD}, produces = "*/*")
    @ResponseBody
    public DeferredResult all(DeferredResult defenderResult, HttpServletRequest request, HttpServletResponse response) {

        Payload payload = new Payload();
        payload.setDeferredResult(defenderResult);
        payload.setRequest(request);
        payload.setResponse(response);
        payload.setRequestURI(request.getRequestURI());

        Router router = new Router(payload)
                .executeIf("/afx/resource/", generalResource::executeAfxResource)
                .executeIf("/afx/dynamic/", dynamicResource::executeDynamicResource)
                .executeIf("/afx/live/", liveResource::executeLiveResource)
                .executeIf("/afx/slide/", slideResource::executeSlideResource)
                .executeIf("/afx/epub/", epubResource::executeEpubResource);


        if (!defenderResult.isSetOrExpired() && !response.isCommitted()) {
            defenderResult.setResult(ResponseEntity.notFound());
        }

        return defenderResult;
    }

    class Payload {
        private String pattern;
        private String requestURI;
        private String finalURI;
        private DeferredResult deferredResult;
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
            return finalURI;
        }

        public void setFinalURI(String finalURI) {
            this.finalURI = finalURI;
        }

        public DeferredResult getDeferredResult() {
            return deferredResult;
        }

        public void setDeferredResult(DeferredResult deferredResult) {
            this.deferredResult = deferredResult;
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
    }


    class Router {

        private final Payload payload;

        public Router(Payload payload) {
            this.payload = payload;
        }

        public Router executeIf(String pattern, Consumer<Payload> consumer) {

            payload.setPattern(pattern);
            payload.setFinalURI(payload.getRequestURI().replace(pattern, ""));

            if (payload.getRequestURI().contains(pattern)) {
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

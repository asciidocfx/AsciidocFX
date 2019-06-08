package com.kodedu.controller;

import com.kodedu.helper.IOHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.Optional;

/**
 * Created by usta on 04.09.2016.
 */
public class Payload {

    private Logger logger = LoggerFactory.getLogger(Payload.class);

    private String pattern;
    private String finalURI;
    private HttpServletRequest request;
    private HttpServletResponse response;

    public Payload() {
    }

    public Payload(HttpServletRequest request, HttpServletResponse response) {
        this.request = request;
        this.response = response;
    }

    public String getPattern() {
        return pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    public String getRequestURI() {
        return request.getRequestURI();
    }

    public String getFinalURI() {

        if (Objects.isNull(finalURI)) {
            String requestURI = getRequestURI();
            if (requestURI.contains(pattern)) {
                setFinalURI(requestURI.replace(pattern, ""));
            } else {
                setFinalURI(requestURI);
            }
        }

        return IOHelper.decode(finalURI, "UTF-8");
    }

    private void setFinalURI(String finalURI) {
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

    public boolean hasParam(String param) {
        return Objects.nonNull(param(param));
    }

    public Optional<String> getReferer() {
        return Optional.ofNullable(request.getHeader("referer"));
    }

    public Optional<String> getCleanReferer() {
        return getReferer()
                .map(e -> e.replaceFirst(String.format("^.*%s", pattern), ""))
                .map(e -> e.replaceAll("\\?.*", ""));
    }

    public Optional<Path> resolveUri(String requestURI) {
        return getCleanReferer()
                .map(Paths::get)
                .map(e -> e.relativize(IOHelper.getPath(requestURI)));
    }
}

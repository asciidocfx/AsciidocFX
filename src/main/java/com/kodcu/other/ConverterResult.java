package com.kodcu.other;

import netscape.javascript.JSObject;

import java.util.Objects;
import java.util.function.Consumer;

/**
 * Created by usta on 20.06.2015.
 */
public class ConverterResult {
    private String rendered;
    private String backend;
    private String doctype;
    private String taskId;

    public ConverterResult(JSObject jsObject) {
        String rendered = (String) jsObject.getMember("rendered");
        String backend = (String) jsObject.getMember("backend");
        String doctype = (String) jsObject.getMember("doctype");
        String taskId = (String) jsObject.getMember("taskId");

        setRendered(rendered);
        setBackend(backend);
        setDoctype(doctype);
        setTaskId(taskId);

    }

    public ConverterResult(jdk.nashorn.api.scripting.JSObject jsObject) {
        setRendered((String) jsObject.getMember("rendered"));
        setBackend((String) jsObject.getMember("backend"));
        setDoctype((String) jsObject.getMember("doctype"));
    }

    public void setRendered(String rendered) {
        this.rendered = rendered;
    }

    public String getRendered() {
        return rendered;
    }

    public void setBackend(String backend) {
        this.backend = backend;
    }

    public String getBackend() {
        return backend;
    }

    public void setDoctype(String doctype) {
        this.doctype = doctype;
    }

    public String getDoctype() {
        return doctype;
    }

    public boolean isBackend(String backend) {
        return backend.equals(this.backend);
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public void afterRender(Consumer<String> consumer, Runnable... runnable) {
        if (Objects.nonNull(rendered)) {
            consumer.accept(rendered);
            for (Runnable r : runnable) {
                r.run();
            }
        }
    }
}

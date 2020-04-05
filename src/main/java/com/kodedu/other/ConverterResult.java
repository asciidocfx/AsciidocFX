package com.kodedu.other;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.function.Consumer;

import static com.kodedu.other.ContentFixes.decodeExtensionNames;
import static com.kodedu.other.ContentFixes.fixLineEnding;

/**
 * Created by usta on 20.06.2015.
 */
public class ConverterResult {
    private LocalDateTime dateTime = LocalDateTime.now();
    private String taskId;
    private String rendered;
    private String backend;
    private String doctype;

    public ConverterResult(String taskId, String rendered, String backend, String doctype) {
        this.taskId = taskId;
        this.rendered = doFinalReplacements(rendered);
        this.backend = backend;
        this.doctype = doctype;
    }

    private String doFinalReplacements(String rendered) {
        rendered = fixLineEnding(rendered);
        rendered = decodeExtensionNames(rendered);
        return rendered;
    }

    public void setRendered(String rendered) {
        this.rendered = doFinalReplacements(rendered);
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

    public LocalDateTime getDateTime() {
        return dateTime;
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

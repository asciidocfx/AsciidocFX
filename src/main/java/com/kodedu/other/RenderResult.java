package com.kodedu.other;

import java.nio.file.Path;

public class RenderResult {
    private String content;
    private boolean successful;
    private Exception exception;
    private Path destination;

    public void setContent(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public void setSuccessful(boolean successful) {
        this.successful = successful;
    }

    public boolean isSuccessful() {
        return successful;
    }

    public void setException(Exception exception) {
        this.exception = exception;
    }

    public Exception getException() {
        return exception;
    }

    public void setDestination(Path destination) {
        this.destination = destination;
    }

    public Path getDestination() {
        return destination;
    }
}

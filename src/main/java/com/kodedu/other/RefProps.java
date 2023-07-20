package com.kodedu.other;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

public record RefProps(String file, int lineNumber, String refId, boolean isCross) {

    public RefProps(String fileName, int lineNumber, String refId) {
        this(fileName, lineNumber, refId, false);
    }

    public boolean hasFileName() {
        return Objects.nonNull(file()) && !hasDash();
    }

    public boolean hasDash() {
        return Objects.nonNull(file()) && file().startsWith("#");
    }

    public Path path() {
        return hasFileName() ? Paths.get(file()) : null;
    }

    public String origRefId() {
        if (Objects.isNull(refId)) {
            return null;
        }
        String[] split = refId.split("#");
        if (split.length == 2) {
            return split[1];
        }
        return refId;
    }

    public String origRefTarget() {
        if (Objects.isNull(refId)) {
            return null;
        }
        String[] split = refId.split("#");
        if (split.length == 2) {
            return split[0];
        }
        return null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RefProps props = (RefProps) o;
        return Objects.equals(file, props.file) && Objects.equals(refId, props.refId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(file, refId);
    }
}

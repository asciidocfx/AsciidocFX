package com.kodedu.controller;

import com.kodedu.other.RefProps;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CrossReferenceHistory {
    private Map<String, List<RefProps>> crossReferences = Collections.emptyMap();

    public Map<String, List<RefProps>> getCrossReferences() {
        return crossReferences;
    }

    public void setCrossReferences(Map<String, List<RefProps>> crossReferences) {
        this.crossReferences = crossReferences;
    }

    private Map<String, List<RefProps>> refs = Collections.emptyMap();

    public Map<String, List<RefProps>> getRefs() {
        return refs;
    }

    public void setRefs(Map<String, List<RefProps>> refs) {
        this.refs = refs;
    }

    private Map<String, List<RefProps>> refsHistoryMap = new LinkedHashMap<String, List<RefProps>>();

    public Map<String, List<RefProps>> getRefsHistoryMap() {
        return refsHistoryMap;
    }

    public CrossReferenceHistory() {
    }
}
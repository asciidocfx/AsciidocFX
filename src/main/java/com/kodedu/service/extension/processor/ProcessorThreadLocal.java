package com.kodedu.service.extension.processor;

import com.kodedu.other.RefProps;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ProcessorThreadLocal {
    private static final ThreadLocal<Map> xref = ThreadLocal.withInitial(() -> new LinkedHashMap());

    public static Map<String, List<RefProps>> getXref() {
        return xref.get();
    }
}

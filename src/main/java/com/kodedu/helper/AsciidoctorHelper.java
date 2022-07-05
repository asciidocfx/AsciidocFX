package com.kodedu.helper;

import org.asciidoctor.SafeMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AsciidoctorHelper {

    private static final Logger logger = LoggerFactory.getLogger(AsciidoctorHelper.class);

    public static SafeMode convertSafe(String safeStr) {
        if (safeStr == null) {
            return SafeMode.SAFE;
        }
        try {
            return SafeMode.valueOf(safeStr.toUpperCase());
        } catch (IllegalArgumentException ex) {
            logger.error("Unkown safe mode! Will use SAFE.", ex);
            return SafeMode.SAFE;
        }
    }
}

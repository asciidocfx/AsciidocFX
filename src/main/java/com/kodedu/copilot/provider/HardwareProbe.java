package com.kodedu.copilot.provider;

import com.sun.management.OperatingSystemMXBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.management.ManagementFactory;
import java.util.Locale;

/**
 * Snapshot of this machine so local models can be recommended with headroom
 * for the editor and the OS.
 */
public record HardwareProbe(long totalRamBytes, long identifiedVramBytes, String osName) {

    private static final Logger logger = LoggerFactory.getLogger(HardwareProbe.class);

    /** Keep about a third of RAM free for AsciidocFX + OS. */
    public static final double HEADROOM = 1.0 / 3.0;

    public static HardwareProbe detect() {
        long ram = Runtime.getRuntime().maxMemory();
        try {
            if (ManagementFactory.getOperatingSystemMXBean() instanceof OperatingSystemMXBean sunBean) {
                ram = sunBean.getTotalMemorySize();
            }
        } catch (Exception e) {
            logger.debug("Using JVM max memory as RAM estimate: {}", e.getMessage());
        }
        return new HardwareProbe(ram, 0, System.getProperty("os.name", ""));
    }

    public double totalRamGb() {
        return totalRamBytes / (1024.0 * 1024.0 * 1024.0);
    }

    public double usableRamGb() {
        return totalRamGb() * (1.0 - HEADROOM);
    }

    public boolean fits(ChatModelSpec model) {
        if (model.minRamGb() > 0 && usableRamGb() < model.minRamGb()) {
            return false;
        }
        return model.minVramGb() <= 0 || identifiedVramBytes <= 0
                || identifiedVramBytes / (1024.0 * 1024.0 * 1024.0) >= model.minVramGb();
    }

    public String summary() {
        return String.format(Locale.US, "%.0f GB RAM (%.0f GB usable with headroom) on %s",
                totalRamGb(), usableRamGb(), osName);
    }
}

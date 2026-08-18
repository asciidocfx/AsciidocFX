package com.kodedu.copilot.provider;

/**
 * One selectable model from a provider catalog file.
 */
public record ChatModelSpec(
        String id,
        String label,
        double minRamGb,
        double minVramGb,
        String notes,
        boolean stripped
) {
    public String displayName() {
        return label != null && !label.isBlank() ? label : id;
    }

    public String requirementText() {
        StringBuilder text = new StringBuilder();
        if (minRamGb > 0) {
            text.append(String.format("%.0f GB RAM", minRamGb));
        }
        if (minVramGb > 0) {
            if (!text.isEmpty()) {
                text.append(", ");
            }
            text.append(String.format("%.0f GB VRAM", minVramGb));
        }
        if (stripped) {
            if (!text.isEmpty()) {
                text.append("; ");
            }
            text.append("small/stripped variant");
        }
        if (notes != null && !notes.isBlank()) {
            if (!text.isEmpty()) {
                text.append(" — ");
            }
            text.append(notes);
        }
        return text.toString();
    }
}

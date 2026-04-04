package com.kodedu.copilot;

/**
 * Defines the available interaction modes for the Copilot integration.
 */
public enum CopilotMode {
    ASK("Ask", "Ask questions about your document"),
    PLAN("Plan", "Create a step-by-step plan for changes"),
    AGENT("Agent", "Autonomous agent that can read/write files and run commands");

    private final String displayName;
    private final String description;

    CopilotMode(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }
}

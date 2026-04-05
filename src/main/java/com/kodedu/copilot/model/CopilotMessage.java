package com.kodedu.copilot.model;

import java.time.LocalDateTime;

/**
 * Represents a single message in a Copilot conversation.
 */
public class CopilotMessage {

    public enum Role {
        SYSTEM("system"),
        USER("user"),
        ASSISTANT("assistant"),
        TOOL("tool");

        private final String value;

        Role(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    private Role role;
    private String content;
    private String toolCallId;
    private String name;
    private LocalDateTime timestamp;

    public CopilotMessage() {
        this.timestamp = LocalDateTime.now();
    }

    public CopilotMessage(Role role, String content) {
        this.role = role;
        this.content = content;
        this.timestamp = LocalDateTime.now();
    }

    public static CopilotMessage system(String content) {
        return new CopilotMessage(Role.SYSTEM, content);
    }

    public static CopilotMessage user(String content) {
        return new CopilotMessage(Role.USER, content);
    }

    public static CopilotMessage assistant(String content) {
        return new CopilotMessage(Role.ASSISTANT, content);
    }

    public static CopilotMessage tool(String toolCallId, String content) {
        CopilotMessage msg = new CopilotMessage(Role.TOOL, content);
        msg.setToolCallId(toolCallId);
        return msg;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getToolCallId() {
        return toolCallId;
    }

    public void setToolCallId(String toolCallId) {
        this.toolCallId = toolCallId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}

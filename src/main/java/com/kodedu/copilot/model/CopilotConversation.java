package com.kodedu.copilot.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * Represents a conversation session with Copilot, holding the message history.
 */
public class CopilotConversation {

    private final String id;
    private final List<CopilotMessage> messages;
    private String title;

    public CopilotConversation() {
        this.id = UUID.randomUUID().toString();
        this.messages = new ArrayList<>();
    }

    public CopilotConversation(String title) {
        this();
        this.title = title;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void addMessage(CopilotMessage message) {
        messages.add(message);
    }

    public List<CopilotMessage> getMessages() {
        return Collections.unmodifiableList(messages);
    }

    public void clear() {
        messages.clear();
    }

    /**
     * Returns messages suitable for API request (excludes tool messages that have been processed).
     */
    public List<CopilotMessage> getMessagesForApi() {
        return new ArrayList<>(messages);
    }
}

package com.kodedu.copilot.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Consumer;

/**
 * Handles parsing of Server-Sent Events (SSE) stream from chat APIs.
 */
public class CopilotStreamHandler {

    private static final Logger logger = LoggerFactory.getLogger(CopilotStreamHandler.class);

    private final Consumer<String> onContent;
    private final Consumer<String> onToolCall;
    private final Runnable onComplete;
    private final Consumer<String> onError;
    private boolean completed;

    public CopilotStreamHandler(Consumer<String> onContent, Consumer<String> onToolCall,
                                Runnable onComplete, Consumer<String> onError) {
        this.onContent = onContent;
        this.onToolCall = onToolCall;
        this.onComplete = onComplete;
        this.onError = onError;
    }

    /**
     * Process a single line from an OpenAI-style SSE stream.
     */
    public void processLine(String line) {
        if (line == null || line.isEmpty()) {
            return;
        }

        if (line.startsWith("data: ")) {
            String data = line.substring(6).trim();
            if ("[DONE]".equals(data)) {
                completeOnce();
                return;
            }

            try {
                processJsonData(data);
            } catch (Exception e) {
                logger.debug("Failed to parse SSE data: {}", data, e);
            }
        }
    }

    /**
     * Process a line from Anthropic's {@code /v1/messages} SSE stream.
     */
    public void processAnthropicLine(String line) {
        if (line == null || line.isEmpty()) {
            return;
        }
        if (line.startsWith("event: ") && line.contains("message_stop")) {
            completeOnce();
            return;
        }
        if (!line.startsWith("data: ")) {
            return;
        }
        String data = line.substring(6).trim();
        if (data.contains("\"message_stop\"")) {
            completeOnce();
            return;
        }
        if (!data.contains("text_delta") && !data.contains("\"type\":\"text\"")) {
            return;
        }
        extractJsonStringField(data, "text");
    }

    public void completeOnce() {
        if (completed) {
            return;
        }
        completed = true;
        onComplete.run();
    }

    public boolean isCompleted() {
        return completed;
    }

    private void processJsonData(String jsonData) {
        try {
            int choicesIdx = jsonData.indexOf("\"choices\"");
            if (choicesIdx == -1) return;

            int deltaIdx = jsonData.indexOf("\"delta\"", choicesIdx);
            if (deltaIdx == -1) return;

            int toolCallsIdx = jsonData.indexOf("\"tool_calls\"", deltaIdx);
            if (toolCallsIdx != -1) {
                onToolCall.accept(jsonData);
                return;
            }

            extractJsonStringField(jsonData.substring(deltaIdx), "content");
        } catch (Exception e) {
            logger.debug("Error parsing SSE JSON", e);
        }
    }

    private void extractJsonStringField(String json, String field) {
        String needle = "\"" + field + "\"";
        int contentIdx = json.indexOf(needle);
        if (contentIdx == -1) return;

        int colonIdx = json.indexOf(":", contentIdx + needle.length());
        if (colonIdx == -1) return;

        int startQuote = json.indexOf("\"", colonIdx + 1);
        if (startQuote == -1) {
            return;
        }

        StringBuilder content = new StringBuilder();
        int i = startQuote + 1;
        while (i < json.length()) {
            char c = json.charAt(i);
            if (c == '\\' && i + 1 < json.length()) {
                char next = json.charAt(i + 1);
                switch (next) {
                    case '"': content.append('"'); i += 2; break;
                    case '\\': content.append('\\'); i += 2; break;
                    case 'n': content.append('\n'); i += 2; break;
                    case 't': content.append('\t'); i += 2; break;
                    case 'r': content.append('\r'); i += 2; break;
                    default: content.append(c); i++; break;
                }
            } else if (c == '"') {
                break;
            } else {
                content.append(c);
                i++;
            }
        }

        if (!content.isEmpty()) {
            onContent.accept(content.toString());
        }
    }
}

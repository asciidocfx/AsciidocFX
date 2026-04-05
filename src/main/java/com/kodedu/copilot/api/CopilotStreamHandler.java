package com.kodedu.copilot.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Consumer;

/**
 * Handles parsing of Server-Sent Events (SSE) stream from the Copilot API.
 * Processes incoming text lines and extracts content deltas.
 */
public class CopilotStreamHandler {

    private static final Logger logger = LoggerFactory.getLogger(CopilotStreamHandler.class);

    private final Consumer<String> onContent;
    private final Consumer<String> onToolCall;
    private final Runnable onComplete;
    private final Consumer<String> onError;
    private final StringBuilder buffer = new StringBuilder();

    public CopilotStreamHandler(Consumer<String> onContent, Consumer<String> onToolCall,
                                Runnable onComplete, Consumer<String> onError) {
        this.onContent = onContent;
        this.onToolCall = onToolCall;
        this.onComplete = onComplete;
        this.onError = onError;
    }

    /**
     * Process a single line from the SSE stream.
     */
    public void processLine(String line) {
        if (line == null || line.isEmpty()) {
            return;
        }

        if (line.startsWith("data: ")) {
            String data = line.substring(6).trim();
            if ("[DONE]".equals(data)) {
                onComplete.run();
                return;
            }

            try {
                // Parse JSON data to extract content
                // The format is: {"choices":[{"delta":{"content":"text"}}]}
                processJsonData(data);
            } catch (Exception e) {
                logger.debug("Failed to parse SSE data: {}", data, e);
            }
        }
    }

    private void processJsonData(String jsonData) {
        try {
            // Simple JSON parsing without full ObjectMapper to keep it lightweight
            // Look for "content":" pattern in delta
            int choicesIdx = jsonData.indexOf("\"choices\"");
            if (choicesIdx == -1) return;

            int deltaIdx = jsonData.indexOf("\"delta\"", choicesIdx);
            if (deltaIdx == -1) return;

            // Check for tool_calls first
            int toolCallsIdx = jsonData.indexOf("\"tool_calls\"", deltaIdx);
            if (toolCallsIdx != -1) {
                onToolCall.accept(jsonData);
                return;
            }

            // Extract content
            int contentIdx = jsonData.indexOf("\"content\"", deltaIdx);
            if (contentIdx == -1) return;

            int colonIdx = jsonData.indexOf(":", contentIdx + 9);
            if (colonIdx == -1) return;

            // Find the start of the string value
            int startQuote = jsonData.indexOf("\"", colonIdx + 1);
            if (startQuote == -1) {
                // Check for null
                if (jsonData.indexOf("null", colonIdx) != -1) {
                    return;
                }
                return;
            }

            // Find the end of the string value, handling escape characters
            StringBuilder content = new StringBuilder();
            int i = startQuote + 1;
            while (i < jsonData.length()) {
                char c = jsonData.charAt(i);
                if (c == '\\' && i + 1 < jsonData.length()) {
                    char next = jsonData.charAt(i + 1);
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

        } catch (Exception e) {
            logger.debug("Error parsing SSE JSON", e);
        }
    }
}

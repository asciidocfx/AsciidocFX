package com.kodedu.copilot.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Consumer;

/**
 * Handles parsing of Server-Sent Events (SSE) stream from the Copilot API.
 * Processes incoming text lines and extracts content deltas.
 * <p>
 * Tool call arguments are streamed incrementally across multiple SSE chunks.
 * This handler accumulates the function name, tool call ID, and argument fragments,
 * then delivers a single complete tool call JSON to the callback once all chunks
 * have been received.
 */
public class CopilotStreamHandler {

    private static final Logger logger = LoggerFactory.getLogger(CopilotStreamHandler.class);

    private final Consumer<String> onContent;
    private final Consumer<String> onToolCall;
    private final Runnable onComplete;
    private final Consumer<String> onError;

    // Tool call accumulation state for streaming chunks
    private String toolCallName;
    private String toolCallId;
    private final StringBuilder toolCallArguments = new StringBuilder();

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
                flushToolCall();
                onComplete.run();
                return;
            }

            try {
                processJsonData(data);
            } catch (Exception e) {
                logger.debug("Failed to parse SSE data: {}", data, e);
            }
        }
    }

    private void processJsonData(String jsonData) {
        try {
            int choicesIdx = jsonData.indexOf("\"choices\"");
            if (choicesIdx == -1) return;

            int deltaIdx = jsonData.indexOf("\"delta\"", choicesIdx);

            // Check for tool_calls in delta — accumulate rather than dispatching immediately
            if (deltaIdx != -1) {
                int toolCallsIdx = jsonData.indexOf("\"tool_calls\"", deltaIdx);
                if (toolCallsIdx != -1) {
                    accumulateToolCall(jsonData, toolCallsIdx);
                    return;
                }
            }

            // This chunk has no tool_calls — flush any accumulated tool call data
            flushToolCall();

            // Extract content
            if (deltaIdx == -1) return;

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

    /**
     * Accumulates tool call data from a streaming chunk.
     * The function name and ID typically appear in the first chunk only,
     * while arguments are streamed across multiple chunks.
     */
    private void accumulateToolCall(String jsonData, int toolCallsIdx) {
        int functionIdx = jsonData.indexOf("\"function\"", toolCallsIdx);
        if (functionIdx != -1) {
            // Extract function name if present (typically first chunk only)
            int nameIdx = jsonData.indexOf("\"name\"", functionIdx);
            if (nameIdx != -1) {
                String name = extractJsonStringValue(jsonData, nameIdx + 6);
                if (name != null && !name.isEmpty()) {
                    toolCallName = name;
                }
            }

            // Extract and append arguments fragment
            int argsIdx = jsonData.indexOf("\"arguments\"", functionIdx);
            if (argsIdx != -1) {
                String args = extractJsonStringValue(jsonData, argsIdx + 11);
                if (args != null) {
                    toolCallArguments.append(args);
                }
            }
        }

        // Extract tool call ID if present (typically first chunk only)
        int idIdx = jsonData.indexOf("\"id\"", toolCallsIdx);
        if (idIdx != -1) {
            String id = extractJsonStringValue(jsonData, idIdx + 4);
            if (id != null && !id.isEmpty()) {
                toolCallId = id;
            }
        }
    }

    /**
     * Flushes accumulated tool call data by constructing a complete JSON
     * and passing it to the onToolCall callback. Resets accumulation state.
     */
    private void flushToolCall() {
        if (toolCallName == null || toolCallName.isEmpty()) {
            return;
        }

        String argsStr = toolCallArguments.toString();

        // Build a complete tool call JSON that AgentModeService can parse:
        // {"choices":[{"delta":{"tool_calls":[{"function":{"name":"...","arguments":"..."},"id":"..."}]}}]}
        StringBuilder json = new StringBuilder();
        json.append("{\"choices\":[{\"delta\":{\"tool_calls\":[{\"function\":{");
        json.append("\"name\":\"").append(escapeJson(toolCallName)).append("\",");
        json.append("\"arguments\":\"").append(escapeJson(argsStr)).append("\"");
        json.append("}");
        if (toolCallId != null) {
            json.append(",\"id\":\"").append(escapeJson(toolCallId)).append("\"");
        }
        json.append("}]}}]}");

        try {
            onToolCall.accept(json.toString());
        } catch (Exception e) {
            logger.error("Error executing tool call callback", e);
        }

        // Reset accumulation state
        toolCallName = null;
        toolCallId = null;
        toolCallArguments.setLength(0);
    }

    /**
     * Extracts a JSON string value starting from a position after the key name.
     * Handles escape sequences. Returns null if the value is not a string.
     *
     * @param jsonData   the raw JSON string
     * @param searchFrom position immediately after the key name (e.g., after {@code "name"})
     * @return the unescaped string value, or null if not found or not a string
     */
    private String extractJsonStringValue(String jsonData, int searchFrom) {
        int colonIdx = jsonData.indexOf(":", searchFrom);
        if (colonIdx == -1) return null;

        // Skip whitespace after colon to find the value start
        int valueStart = colonIdx + 1;
        while (valueStart < jsonData.length() && Character.isWhitespace(jsonData.charAt(valueStart))) {
            valueStart++;
        }

        // Value must be a string (starts with ")
        if (valueStart >= jsonData.length() || jsonData.charAt(valueStart) != '"') {
            return null;
        }

        // Parse the string value, handling escape sequences
        StringBuilder value = new StringBuilder();
        int i = valueStart + 1;
        while (i < jsonData.length()) {
            char c = jsonData.charAt(i);
            if (c == '\\' && i + 1 < jsonData.length()) {
                char next = jsonData.charAt(i + 1);
                switch (next) {
                    case '"': value.append('"'); i += 2; break;
                    case '\\': value.append('\\'); i += 2; break;
                    case 'n': value.append('\n'); i += 2; break;
                    case 't': value.append('\t'); i += 2; break;
                    case 'r': value.append('\r'); i += 2; break;
                    case '/': value.append('/'); i += 2; break;
                    case 'b': value.append('\b'); i += 2; break;
                    case 'f': value.append('\f'); i += 2; break;
                    default: value.append('\\'); value.append(next); i += 2; break;
                }
            } else if (c == '"') {
                break;
            } else {
                value.append(c);
                i++;
            }
        }
        return value.toString();
    }

    /**
     * Escapes a string for safe embedding as a JSON string value.
     */
    private String escapeJson(String s) {
        if (s == null) return "";
        StringBuilder sb = new StringBuilder(s.length());
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            switch (c) {
                case '"': sb.append("\\\""); break;
                case '\\': sb.append("\\\\"); break;
                case '\n': sb.append("\\n"); break;
                case '\r': sb.append("\\r"); break;
                case '\t': sb.append("\\t"); break;
                case '\b': sb.append("\\b"); break;
                case '\f': sb.append("\\f"); break;
                default:
                    if (c < 0x20) {
                        sb.append(String.format("\\u%04x", (int) c));
                    } else {
                        sb.append(c);
                    }
                    break;
            }
        }
        return sb.toString();
    }
}

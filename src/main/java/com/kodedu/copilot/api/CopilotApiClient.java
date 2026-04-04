package com.kodedu.copilot.api;

import com.kodedu.copilot.config.CopilotConfigBean;
import com.kodedu.copilot.model.CopilotMessage;
import com.kodedu.service.ThreadService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import jakarta.json.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;
import java.util.function.Consumer;

/**
 * Client for the GitHub Copilot Chat API.
 * Handles request building, sending, and streaming responses.
 */
@Component
public class CopilotApiClient {

    private static final Logger logger = LoggerFactory.getLogger(CopilotApiClient.class);

    private static final String CHAT_COMPLETIONS_URL = "https://api.githubcopilot.com/chat/completions";

    private final HttpClient httpClient;
    private final CopilotAuthService authService;
    private final CopilotConfigBean configBean;
    private final ThreadService threadService;

    @Value("${application.version}")
    private String appVersion;

    @Autowired
    public CopilotApiClient(CopilotAuthService authService, CopilotConfigBean configBean,
                            ThreadService threadService) {
        this.authService = authService;
        this.configBean = configBean;
        this.threadService = threadService;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(30))
                .build();
    }

    /**
     * Sends a streaming chat completion request to the Copilot API.
     *
     * @param messages   The conversation messages
     * @param tools      Optional tool definitions for Agent mode (JSON array string, or null)
     * @param onContent  Called for each content chunk
     * @param onToolCall Called when a tool call is received
     * @param onComplete Called when the stream is complete
     * @param onError    Called on error
     */
    public void sendStreamingRequest(List<CopilotMessage> messages, String tools,
                                     Consumer<String> onContent, Consumer<String> onToolCall,
                                     Runnable onComplete, Consumer<String> onError) {
        threadService.start(() -> {
            try {
                String token = authService.getValidToken();
                if (token == null) {
                    onError.accept("Not authenticated. Please sign in to GitHub Copilot.");
                    return;
                }

                String requestBody = buildRequestBody(messages, tools, true);

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(CHAT_COMPLETIONS_URL))
                        .header("Authorization", "Bearer " + token)
                        .header("Content-Type", "application/json")
                        .header("Accept", "text/event-stream")
                        .header("Editor-Version", "AsciidocFX/" + appVersion)
                        .header("Editor-Plugin-Version", "copilot-asciidocfx/1.0.0")
                        .header("Copilot-Integration-Id", "asciidocfx")
                        .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                        .build();

                HttpResponse<java.io.InputStream> response = httpClient.send(request,
                        HttpResponse.BodyHandlers.ofInputStream());

                if (response.statusCode() != 200) {
                    String errorBody = new String(response.body().readAllBytes(), StandardCharsets.UTF_8);
                    logger.error("Copilot API error: HTTP {} - {}", response.statusCode(), errorBody);
                    onError.accept("API Error: HTTP " + response.statusCode());
                    return;
                }

                CopilotStreamHandler streamHandler = new CopilotStreamHandler(
                        onContent, onToolCall, onComplete, onError);

                try (BufferedReader reader = new BufferedReader(
                        new InputStreamReader(response.body(), StandardCharsets.UTF_8))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        streamHandler.processLine(line);
                    }
                }

            } catch (Exception e) {
                logger.error("Failed to send streaming request", e);
                onError.accept("Request failed: " + e.getMessage());
            }
        });
    }

    /**
     * Sends a non-streaming request (used for inline completions and short requests).
     */
    public String sendRequest(List<CopilotMessage> messages) {
        try {
            String token = authService.getValidToken();
            if (token == null) {
                return null;
            }

            String requestBody = buildRequestBody(messages, null, false);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(CHAT_COMPLETIONS_URL))
                    .header("Authorization", "Bearer " + token)
                    .header("Content-Type", "application/json")
                    .header("Accept", "application/json")
                    .header("Editor-Version", "AsciidocFX/" + appVersion)
                    .header("Editor-Plugin-Version", "copilot-asciidocfx/1.0.0")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                return extractContentFromResponse(response.body());
            } else {
                logger.error("Copilot API error: HTTP {}", response.statusCode());
                return null;
            }
        } catch (Exception e) {
            logger.error("Failed to send request", e);
            return null;
        }
    }

    private String buildRequestBody(List<CopilotMessage> messages, String tools, boolean stream) {
        JsonObjectBuilder builder = Json.createObjectBuilder();
        builder.add("model", configBean.getModel());
        builder.add("stream", stream);
        builder.add("temperature", configBean.getTemperature());
        builder.add("max_tokens", configBean.getMaxTokens());

        JsonArrayBuilder messagesArray = Json.createArrayBuilder();
        for (CopilotMessage msg : messages) {
            JsonObjectBuilder msgBuilder = Json.createObjectBuilder();
            msgBuilder.add("role", msg.getRole().getValue());
            msgBuilder.add("content", msg.getContent() != null ? msg.getContent() : "");
            if (msg.getToolCallId() != null) {
                msgBuilder.add("tool_call_id", msg.getToolCallId());
            }
            if (msg.getName() != null) {
                msgBuilder.add("name", msg.getName());
            }
            messagesArray.add(msgBuilder);
        }
        builder.add("messages", messagesArray);

        if (tools != null && !tools.isEmpty()) {
            try (JsonReader reader = Json.createReader(new java.io.StringReader(tools))) {
                JsonArray toolsArray = reader.readArray();
                builder.add("tools", toolsArray);
            }
        }

        return builder.build().toString();
    }

    private String extractContentFromResponse(String responseBody) {
        try (JsonReader reader = Json.createReader(new java.io.StringReader(responseBody))) {
            JsonObject json = reader.readObject();
            JsonArray choices = json.getJsonArray("choices");
            if (choices != null && !choices.isEmpty()) {
                JsonObject choice = choices.getJsonObject(0);
                JsonObject message = choice.getJsonObject("message");
                if (message != null) {
                    return message.getString("content", "");
                }
            }
        } catch (Exception e) {
            logger.error("Failed to parse response", e);
        }
        return null;
    }
}

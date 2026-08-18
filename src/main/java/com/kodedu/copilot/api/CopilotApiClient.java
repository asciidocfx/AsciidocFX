package com.kodedu.copilot.api;

import com.kodedu.copilot.config.CopilotConfigBean;
import com.kodedu.copilot.model.CopilotMessage;
import com.kodedu.copilot.provider.ChatProviderKind;
import com.kodedu.copilot.provider.ChatProviderSpec;
import com.kodedu.copilot.provider.CopilotProviderCatalog;
import com.kodedu.service.ThreadService;
import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;
import jakarta.json.JsonReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

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
 * Routes chat requests to GitHub Copilot, OpenAI-compatible APIs, Anthropic,
 * Ollama, or a local CLI chosen in settings.
 */
@Component
public class CopilotApiClient {

    private static final Logger logger = LoggerFactory.getLogger(CopilotApiClient.class);

    private static final String GITHUB_CHAT_URL = "https://api.githubcopilot.com/chat/completions";

    private final HttpClient httpClient;
    private final CopilotAuthService authService;
    private final CopilotConfigBean configBean;
    private final CopilotProviderCatalog catalog;
    private final ThreadService threadService;
    private final CliChatClient cliChatClient = new CliChatClient();

    @Value("${application.version}")
    private String appVersion;

    @Autowired
    public CopilotApiClient(CopilotAuthService authService, CopilotConfigBean configBean,
                            CopilotProviderCatalog catalog, ThreadService threadService) {
        this.authService = authService;
        this.configBean = configBean;
        this.catalog = catalog;
        this.threadService = threadService;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(30))
                .build();
    }

    public void sendStreamingRequest(List<CopilotMessage> messages, String tools,
                                     Consumer<String> onContent, Consumer<String> onToolCall,
                                     Runnable onComplete, Consumer<String> onError) {
        threadService.start(() -> {
            try {
                ChatProviderSpec provider = resolveProvider();
                switch (provider.kind()) {
                    case GITHUB_COPILOT -> sendGithub(messages, tools, onContent, onToolCall, onComplete, onError);
                    case OPENAI_COMPATIBLE, OLLAMA -> sendOpenAiCompatible(provider, messages, tools,
                            onContent, onToolCall, onComplete, onError);
                    case ANTHROPIC -> sendAnthropic(provider, messages, onContent, onToolCall, onComplete, onError);
                    case CLI -> cliChatClient.send(provider, messages, onContent, onComplete, onError);
                }
            } catch (Exception e) {
                logger.error("Failed to send streaming request", e);
                onError.accept("Request failed: " + e.getMessage());
            }
        });
    }

    public String sendRequest(List<CopilotMessage> messages) {
        try {
            ChatProviderSpec provider = resolveProvider();
            if (provider.kind() == ChatProviderKind.CLI) {
                return null;
            }
            String token = resolveToken(provider);
            if (token == null && provider.needsApiKey()) {
                return null;
            }
            if (provider.kind() == ChatProviderKind.GITHUB_COPILOT && token == null) {
                return null;
            }

            boolean anthropic = provider.kind() == ChatProviderKind.ANTHROPIC;
            String body = anthropic ? buildAnthropicBody(messages, false) : buildOpenAiBody(messages, null, false);
            HttpRequest.Builder builder = HttpRequest.newBuilder()
                    .uri(URI.create(anthropic ? provider.chatEndpoint() : endpoint(provider)))
                    .header("Content-Type", "application/json")
                    .header("Accept", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(body));
            applyAuthHeaders(builder, provider, token, false);

            HttpResponse<String> response = httpClient.send(builder.build(), HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                return extractContentFromResponse(response.body(), anthropic);
            }
            logger.error("Chat API error: HTTP {}", response.statusCode());
            return null;
        } catch (Exception e) {
            logger.error("Failed to send request", e);
            return null;
        }
    }

    private void sendGithub(List<CopilotMessage> messages, String tools,
                            Consumer<String> onContent, Consumer<String> onToolCall,
                            Runnable onComplete, Consumer<String> onError) throws Exception {
        String token = authService.getValidToken();
        if (token == null) {
            onError.accept("Not authenticated. Please sign in to GitHub Copilot, or pick another provider.");
            return;
        }
        streamSse(URI.create(GITHUB_CHAT_URL), buildOpenAiBody(messages, tools, true),
                request -> request
                        .header("Authorization", "Bearer " + token)
                        .header("Content-Type", "application/json")
                        .header("Accept", "text/event-stream")
                        .header("Editor-Version", "AsciidocFX/" + appVersion)
                        .header("Editor-Plugin-Version", "copilot-asciidocfx/1.0.0")
                        .header("Copilot-Integration-Id", "vscode-chat"),
                false, onContent, onToolCall, onComplete, onError);
    }

    private void sendOpenAiCompatible(ChatProviderSpec provider, List<CopilotMessage> messages, String tools,
                                      Consumer<String> onContent, Consumer<String> onToolCall,
                                      Runnable onComplete, Consumer<String> onError) throws Exception {
        String token = resolveToken(provider);
        if (provider.needsApiKey() && (token == null || token.isBlank())) {
            onError.accept(apiKeyHint(provider));
            return;
        }
        streamSse(URI.create(endpoint(provider)), buildOpenAiBody(messages, tools, true),
                request -> {
                    request.header("Content-Type", "application/json");
                    applyAuthHeaders(request, provider, token, true);
                    return request;
                },
                false, onContent, onToolCall, onComplete, onError);
    }

    private void sendAnthropic(ChatProviderSpec provider, List<CopilotMessage> messages,
                               Consumer<String> onContent, Consumer<String> onToolCall,
                               Runnable onComplete, Consumer<String> onError) throws Exception {
        String token = resolveToken(provider);
        if (token == null || token.isBlank()) {
            onError.accept(apiKeyHint(provider));
            return;
        }
        streamSse(URI.create(provider.chatEndpoint()), buildAnthropicBody(messages, true),
                request -> request
                        .header("Content-Type", "application/json")
                        .header("Accept", "text/event-stream")
                        .header("x-api-key", token)
                        .header("anthropic-version", "2023-06-01"),
                true, onContent, onToolCall, onComplete, onError);
    }

    @FunctionalInterface
    private interface HeaderCustomizer {
        HttpRequest.Builder apply(HttpRequest.Builder builder);
    }

    private void streamSse(URI uri, String body, HeaderCustomizer headers, boolean anthropic,
                           Consumer<String> onContent, Consumer<String> onToolCall,
                           Runnable onComplete, Consumer<String> onError) throws Exception {
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(uri)
                .POST(HttpRequest.BodyPublishers.ofString(body));
        HttpRequest request = headers.apply(builder).build();

        HttpResponse<java.io.InputStream> response = httpClient.send(request,
                HttpResponse.BodyHandlers.ofInputStream());

        if (response.statusCode() != 200) {
            String errorBody = new String(response.body().readAllBytes(), StandardCharsets.UTF_8);
            logger.error("Chat API error: HTTP {} - {}", response.statusCode(), errorBody);
            onError.accept(httpErrorMessage(response.statusCode()));
            return;
        }

        CopilotStreamHandler streamHandler = new CopilotStreamHandler(
                onContent, onToolCall, onComplete, onError);

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(response.body(), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (anthropic) {
                    streamHandler.processAnthropicLine(line);
                } else {
                    streamHandler.processLine(line);
                }
            }
        }
        streamHandler.completeOnce();
    }

    private void applyAuthHeaders(HttpRequest.Builder request, ChatProviderSpec provider, String token, boolean stream) {
        if (token != null && !token.isBlank() && provider.kind() != ChatProviderKind.ANTHROPIC) {
            request.header("Authorization", "Bearer " + token);
        }
        if (provider.kind() == ChatProviderKind.ANTHROPIC && token != null) {
            request.header("x-api-key", token);
            request.header("anthropic-version", "2023-06-01");
        }
        if ("openrouter".equalsIgnoreCase(provider.id())) {
            request.header("HTTP-Referer", "https://github.com/asciidocfx/AsciidocFX");
            request.header("X-Title", "AsciidocFX");
        }
        if (stream) {
            request.header("Accept", "text/event-stream");
        }
    }

    private ChatProviderSpec resolveProvider() {
        String id = configBean.getProvider();
        return catalog.findProvider(id).orElseGet(() ->
                catalog.findProvider("github-copilot").orElse(new ChatProviderSpec(
                        "github-copilot", "GitHub Copilot", ChatProviderKind.GITHUB_COPILOT,
                        "https://api.githubcopilot.com", "", "", List.of(), List.of())));
    }

    private String endpoint(ChatProviderSpec provider) {
        if (provider.kind() == ChatProviderKind.GITHUB_COPILOT) {
            return GITHUB_CHAT_URL;
        }
        return provider.chatEndpoint();
    }

    private String resolveToken(ChatProviderSpec provider) {
        if (provider.usesGithubOAuth()) {
            return authService.getValidToken();
        }
        if (provider.kind() == ChatProviderKind.OLLAMA || provider.kind() == ChatProviderKind.CLI) {
            return "";
        }
        String stored = configBean.findApiKey(provider.id());
        if (stored != null && !stored.isBlank()) {
            return stored.trim();
        }
        if (provider.apiKeyEnv() != null && !provider.apiKeyEnv().isBlank()) {
            String env = System.getenv(provider.apiKeyEnv());
            if (env != null && !env.isBlank()) {
                return env.trim();
            }
        }
        if ("google".equalsIgnoreCase(provider.id())) {
            String gemini = System.getenv("GEMINI_API_KEY");
            if (gemini != null && !gemini.isBlank()) {
                return gemini.trim();
            }
        }
        return null;
    }

    private static String apiKeyHint(ChatProviderSpec provider) {
        String env = provider.apiKeyEnv() == null || provider.apiKeyEnv().isBlank()
                ? "the provider's API key"
                : provider.apiKeyEnv();
        return "No API key for " + provider.name() + ". Add it in Copilot Settings, or set " + env + ".";
    }

    private static String httpErrorMessage(int status) {
        if (status == 401 || status == 403) {
            return "API Error: HTTP " + status + " (check the provider API key)";
        }
        if (status == 404) {
            return "API Error: HTTP 404 (is Ollama running, or is the model name wrong?)";
        }
        return "API Error: HTTP " + status;
    }

    private String buildOpenAiBody(List<CopilotMessage> messages, String tools, boolean stream) {
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

    private String buildAnthropicBody(List<CopilotMessage> messages, boolean stream) {
        JsonObjectBuilder builder = Json.createObjectBuilder();
        builder.add("model", configBean.getModel());
        builder.add("stream", stream);
        builder.add("max_tokens", configBean.getMaxTokens());

        StringBuilder system = new StringBuilder();
        JsonArrayBuilder messagesArray = Json.createArrayBuilder();
        for (CopilotMessage msg : messages) {
            if (msg.getRole() == CopilotMessage.Role.SYSTEM) {
                if (!system.isEmpty()) {
                    system.append("\n\n");
                }
                system.append(msg.getContent() == null ? "" : msg.getContent());
                continue;
            }
            String role = msg.getRole() == CopilotMessage.Role.ASSISTANT ? "assistant" : "user";
            messagesArray.add(Json.createObjectBuilder()
                    .add("role", role)
                    .add("content", msg.getContent() != null ? msg.getContent() : ""));
        }
        if (!system.isEmpty()) {
            builder.add("system", system.toString());
        }
        builder.add("messages", messagesArray);
        return builder.build().toString();
    }

    private String extractContentFromResponse(String responseBody, boolean anthropic) {
        try (JsonReader reader = Json.createReader(new java.io.StringReader(responseBody))) {
            JsonObject json = reader.readObject();
            if (anthropic) {
                JsonArray content = json.getJsonArray("content");
                if (content != null && !content.isEmpty()) {
                    JsonObject block = content.getJsonObject(0);
                    return block.getString("text", "");
                }
                return null;
            }
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

package com.kodedu.copilot.provider;

import java.util.List;
import java.util.Locale;

public record ChatProviderSpec(
        String id,
        String name,
        ChatProviderKind kind,
        String baseUrl,
        String apiKeyEnv,
        String command,
        List<String> args,
        List<ChatModelSpec> models
) {
    public boolean usesGithubOAuth() {
        return kind == ChatProviderKind.GITHUB_COPILOT;
    }

    public boolean needsApiKey() {
        return kind == ChatProviderKind.OPENAI_COMPATIBLE || kind == ChatProviderKind.ANTHROPIC;
    }

    public String chatEndpoint() {
        return switch (kind) {
            case ANTHROPIC -> joinUrl(baseUrl, "messages");
            case GITHUB_COPILOT, OPENAI_COMPATIBLE, OLLAMA -> joinUrl(baseUrl, "chat/completions");
            case CLI -> "";
        };
    }

    private static String joinUrl(String base, String path) {
        if (base == null || base.isBlank()) {
            return path;
        }
        String trimmed = base.endsWith("/") ? base.substring(0, base.length() - 1) : base;
        String suffix = path.startsWith("/") ? path.substring(1) : path;
        if (trimmed.toLowerCase(Locale.ROOT).endsWith("/" + suffix.toLowerCase(Locale.ROOT))) {
            return trimmed;
        }
        return trimmed + "/" + suffix;
    }
}

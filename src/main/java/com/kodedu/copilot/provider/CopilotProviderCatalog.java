package com.kodedu.copilot.provider;

import com.kodedu.controller.ApplicationController;
import com.kodedu.helper.IOHelper;
import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Loads chat provider catalogs from {@code conf/copilot/providers/*.json}.
 */
@Component
public class CopilotProviderCatalog {

    private static final Logger logger = LoggerFactory.getLogger(CopilotProviderCatalog.class);

    private final ApplicationController controller;
    private final List<ChatProviderSpec> providers = new ArrayList<>();
    private HardwareProbe hardware = HardwareProbe.detect();

    @Autowired
    public CopilotProviderCatalog(ApplicationController controller) {
        this.controller = controller;
    }

    @PostConstruct
    public void load() {
        hardware = HardwareProbe.detect();
        providers.clear();
        Path dir = resolveProvidersDir();
        if (dir == null || !Files.isDirectory(dir)) {
            logger.warn("Copilot provider catalog missing at {}", dir);
        } else {
            try (Stream<Path> files = Files.list(dir)) {
                files.filter(p -> p.getFileName().toString().toLowerCase(Locale.ROOT).endsWith(".json"))
                        .sorted()
                        .forEach(this::loadFile);
            } catch (Exception e) {
                logger.error("Failed to list Copilot providers", e);
            }
        }
        if (providers.isEmpty()) {
            addGithubFallback();
        }
        logger.info("Loaded {} Copilot providers. {}", providers.size(), hardware.summary());
    }

    public List<ChatProviderSpec> providers() {
        return List.copyOf(providers);
    }

    public HardwareProbe hardware() {
        return hardware;
    }

    public Optional<ChatProviderSpec> findProvider(String id) {
        if (id == null) {
            return Optional.empty();
        }
        return providers.stream().filter(p -> p.id().equalsIgnoreCase(id)).findFirst();
    }

    public Optional<ChatModelSpec> findModel(String providerId, String modelId) {
        return findProvider(providerId).flatMap(p -> p.models().stream()
                .filter(m -> m.id().equals(modelId))
                .findFirst());
    }

    public Optional<ChatModelSpec> recommendedLocalModel() {
        return providers.stream()
                .filter(p -> p.kind() == ChatProviderKind.OLLAMA)
                .flatMap(p -> p.models().stream())
                .filter(hardware::fits)
                .max(Comparator.comparingDouble(ChatModelSpec::minRamGb)
                        .thenComparing(m -> m.stripped() ? 0 : 1));
    }

    public boolean isRecommended(String providerId, String modelId) {
        return recommendedLocalModel()
                .filter(m -> "ollama".equalsIgnoreCase(providerId) && m.id().equals(modelId))
                .isPresent();
    }

    private Path resolveProvidersDir() {
        try {
            Path installed = controller.getConfigPath().resolve("copilot/providers");
            if (Files.isDirectory(installed)) {
                return installed;
            }
        } catch (Exception ignored) {
            // fall through
        }
        Path fallback = IOHelper.getInstallationPath().resolve("conf/copilot/providers");
        return Files.isDirectory(fallback) ? fallback : null;
    }

    private void loadFile(Path path) {
        try (Reader reader = IOHelper.fileReader(path); JsonReader jsonReader = Json.createReader(reader)) {
            JsonObject json = jsonReader.readObject();
            ChatProviderKind kind = parseKind(json.getString("kind", "openai-compatible"));
            List<String> args = new ArrayList<>();
            if (json.containsKey("args") && json.get("args").getValueType() == jakarta.json.JsonValue.ValueType.ARRAY) {
                json.getJsonArray("args").forEach(v -> {
                    if (v instanceof jakarta.json.JsonString js) {
                        args.add(js.getString());
                    }
                });
            }
            List<ChatModelSpec> models = new ArrayList<>();
            JsonArray modelsJson = json.getJsonArray("models");
            if (modelsJson != null) {
                for (int i = 0; i < modelsJson.size(); i++) {
                    JsonObject m = modelsJson.getJsonObject(i);
                    models.add(new ChatModelSpec(
                            m.getString("id", ""),
                            m.getString("label", m.getString("id", "")),
                            m.containsKey("minRamGb") ? m.getJsonNumber("minRamGb").doubleValue() : 0,
                            m.containsKey("minVramGb") ? m.getJsonNumber("minVramGb").doubleValue() : 0,
                            m.getString("notes", ""),
                            m.getBoolean("stripped", false)
                    ));
                }
            }
            providers.add(new ChatProviderSpec(
                    json.getString("id", path.getFileName().toString()),
                    json.getString("name", json.getString("id", "Unknown")),
                    kind,
                    json.getString("baseUrl", ""),
                    json.getString("apiKeyEnv", ""),
                    json.getString("command", ""),
                    args,
                    models
            ));
        } catch (Exception e) {
            logger.error("Failed to load Copilot provider {}", path, e);
        }
    }

    private static ChatProviderKind parseKind(String kind) {
        return switch (kind.toLowerCase(Locale.ROOT)) {
            case "github-copilot", "github_copilot" -> ChatProviderKind.GITHUB_COPILOT;
            case "anthropic" -> ChatProviderKind.ANTHROPIC;
            case "ollama" -> ChatProviderKind.OLLAMA;
            case "cli" -> ChatProviderKind.CLI;
            default -> ChatProviderKind.OPENAI_COMPATIBLE;
        };
    }

    private void addGithubFallback() {
        providers.add(new ChatProviderSpec(
                "github-copilot",
                "GitHub Copilot",
                ChatProviderKind.GITHUB_COPILOT,
                "https://api.githubcopilot.com",
                "",
                "",
                List.of(),
                List.of(
                        new ChatModelSpec("gpt-4o", "GPT-4o", 0, 0, "", false),
                        new ChatModelSpec("gpt-4o-mini", "GPT-4o mini", 0, 0, "", false),
                        new ChatModelSpec("claude-sonnet-4", "Claude Sonnet 4", 0, 0, "", false)
                )
        ));
    }
}

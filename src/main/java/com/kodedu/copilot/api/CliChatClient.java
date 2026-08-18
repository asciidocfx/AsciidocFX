package com.kodedu.copilot.api;

import com.kodedu.copilot.model.CopilotMessage;
import com.kodedu.copilot.provider.ChatProviderSpec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * Runs a local CLI (Cursor, Claude Code, Gemini CLI) and streams stdout.
 * Missing binaries are reported as errors; they do not fail startup.
 */
final class CliChatClient {

    private static final Logger logger = LoggerFactory.getLogger(CliChatClient.class);

    void send(ChatProviderSpec provider, List<CopilotMessage> messages,
              Consumer<String> onContent, Runnable onComplete, Consumer<String> onError) {
        String command = resolveCommand(provider);
        if (command == null) {
            onError.accept(provider.name() + " CLI was not found on PATH. Install it, or pick an HTTP API provider.");
            return;
        }

        String prompt = lastUserPrompt(messages);
        if (prompt == null || prompt.isBlank()) {
            onError.accept("Nothing to send to " + provider.name());
            return;
        }

        List<String> cmd = new ArrayList<>();
        cmd.add(command);
        if (provider.args() != null) {
            cmd.addAll(provider.args());
        }
        cmd.add(prompt);

        logger.info("Running chat CLI: {}", command);
        try {
            ProcessBuilder builder = new ProcessBuilder(cmd);
            builder.redirectErrorStream(true);
            Process process = builder.start();
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8))) {
                String line;
                boolean first = true;
                while ((line = reader.readLine()) != null) {
                    if (Thread.currentThread().isInterrupted()) {
                        process.destroyForcibly();
                        onError.accept("Stopped");
                        return;
                    }
                    onContent.accept(first ? line : "\n" + line);
                    first = false;
                }
            }
            int code = process.waitFor();
            if (code != 0) {
                onError.accept(provider.name() + " CLI exited with code " + code);
                return;
            }
            onComplete.run();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            onError.accept("Stopped");
        } catch (Exception e) {
            logger.error("CLI chat failed", e);
            onError.accept("CLI failed: " + e.getMessage());
        }
    }

    private static String lastUserPrompt(List<CopilotMessage> messages) {
        for (int i = messages.size() - 1; i >= 0; i--) {
            CopilotMessage msg = messages.get(i);
            if (msg.getRole() == CopilotMessage.Role.USER) {
                return msg.getContent();
            }
        }
        return messages.isEmpty() ? "" : messages.getLast().getContent();
    }

    private static String resolveCommand(ChatProviderSpec provider) {
        List<String> candidates = new ArrayList<>();
        if (provider.command() != null && !provider.command().isBlank()) {
            candidates.add(provider.command());
        }
        if ("cursor-cli".equalsIgnoreCase(provider.id())) {
            candidates.add("cursor-agent");
            candidates.add("agent");
        }
        for (String name : candidates) {
            Optional<String> found = findOnPath(name);
            if (found.isPresent()) {
                return found.get();
            }
        }
        return null;
    }

    private static Optional<String> findOnPath(String command) {
        String os = System.getProperty("os.name", "").toLowerCase(Locale.ROOT);
        boolean windows = os.contains("win");
        List<String> lookup = windows ? List.of("where.exe", command) : List.of("which", command);
        try {
            Process process = new ProcessBuilder(lookup).redirectErrorStream(true).start();
            String first;
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8))) {
                first = reader.readLine();
            }
            int code = process.waitFor();
            if (code == 0 && first != null && !first.isBlank() && Files.isExecutable(Path.of(first.trim()))) {
                return Optional.of(first.trim());
            }
            if (code == 0 && first != null && !first.isBlank() && windows) {
                return Optional.of(first.trim());
            }
        } catch (Exception e) {
            logger.debug("PATH lookup failed for {}", command, e);
        }
        return Optional.empty();
    }
}

package com.kodedu.copilot.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kodedu.copilot.config.CopilotConfigBean;
import com.kodedu.service.ThreadService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

/**
 * Handles GitHub OAuth Device Flow authentication for Copilot access.
 */
@Component
public class CopilotAuthService {

    private static final Logger logger = LoggerFactory.getLogger(CopilotAuthService.class);

    private static final String DEVICE_CODE_URL = "https://github.com/login/device/code";
    private static final String ACCESS_TOKEN_URL = "https://github.com/login/oauth/access_token";
    private static final String COPILOT_TOKEN_URL = "https://api.github.com/copilot_internal/v2/token";
    // GitHub Copilot Chat client ID (public, used by VS Code Copilot extension)
    private static final String CLIENT_ID = "Iv1.b507a08c87ecfe98";

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final CopilotConfigBean configBean;
    private final ThreadService threadService;

    @Value("${application.version}")
    private String appVersion;

    @Autowired
    public CopilotAuthService(CopilotConfigBean configBean, ThreadService threadService) {
        this.configBean = configBean;
        this.threadService = threadService;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
        this.objectMapper = new ObjectMapper();
    }

    /**
     * Initiates the OAuth Device Flow. Calls onDeviceCode with the user code and verification URI
     * so the caller can display them in the UI. Returns a CompletableFuture that resolves when auth is complete.
     *
     * @param onDeviceCode Called with (userCode, verificationUri) when the device code is ready
     */
    public CompletableFuture<Boolean> authenticate(BiConsumer<String, String> onDeviceCode) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                // Step 1: Request device code
                String body = "client_id=" + URLEncoder.encode(CLIENT_ID, StandardCharsets.UTF_8) +
                        "&scope=" + URLEncoder.encode("copilot", StandardCharsets.UTF_8);

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(DEVICE_CODE_URL))
                        .header("Accept", "application/json")
                        .header("Content-Type", "application/x-www-form-urlencoded")
                        .POST(HttpRequest.BodyPublishers.ofString(body))
                        .build();

                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                JsonNode deviceResponse = objectMapper.readTree(response.body());

                String deviceCode = deviceResponse.get("device_code").asText();
                String userCode = deviceResponse.get("user_code").asText();
                String verificationUri = deviceResponse.get("verification_uri").asText();
                int interval = deviceResponse.has("interval") ? deviceResponse.get("interval").asInt() : 5;
                int expiresIn = deviceResponse.has("expires_in") ? deviceResponse.get("expires_in").asInt() : 900;

                // Step 2: Notify caller with device code info so it can display in UI
                if (onDeviceCode != null) {
                    onDeviceCode.accept(userCode, verificationUri);
                }

                // Step 3: Poll for access token
                long deadline = System.currentTimeMillis() + (expiresIn * 1000L);
                while (System.currentTimeMillis() < deadline) {
                    Thread.sleep(interval * 1000L);

                    String pollBody = "client_id=" + URLEncoder.encode(CLIENT_ID, StandardCharsets.UTF_8) +
                            "&device_code=" + URLEncoder.encode(deviceCode, StandardCharsets.UTF_8) +
                            "&grant_type=" + URLEncoder.encode("urn:ietf:params:oauth:grant-type:device_code", StandardCharsets.UTF_8);

                    HttpRequest pollRequest = HttpRequest.newBuilder()
                            .uri(URI.create(ACCESS_TOKEN_URL))
                            .header("Accept", "application/json")
                            .header("Content-Type", "application/x-www-form-urlencoded")
                            .POST(HttpRequest.BodyPublishers.ofString(pollBody))
                            .build();

                    HttpResponse<String> pollResponse = httpClient.send(pollRequest, HttpResponse.BodyHandlers.ofString());
                    JsonNode tokenResponse = objectMapper.readTree(pollResponse.body());

                    if (tokenResponse.has("access_token")) {
                        String accessToken = tokenResponse.get("access_token").asText();
                        threadService.runActionLater(() -> {
                            configBean.setAccessToken(accessToken);
                            configBean.save();
                        });
                        // Now exchange for Copilot token
                        return refreshCopilotToken(accessToken);
                    }

                    if (tokenResponse.has("error")) {
                        String error = tokenResponse.get("error").asText();
                        if ("authorization_pending".equals(error)) {
                            continue;
                        } else if ("slow_down".equals(error)) {
                            interval += 5;
                            continue;
                        } else {
                            logger.error("OAuth error: {}", error);
                            return false;
                        }
                    }
                }
                logger.error("Device code expired");
                return false;
            } catch (Exception e) {
                logger.error("Authentication failed", e);
                return false;
            }
        }, threadService.executor());
    }

    /**
     * Exchanges the GitHub access token for a Copilot session token.
     */
    public boolean refreshCopilotToken(String githubAccessToken) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(COPILOT_TOKEN_URL))
                    .header("Authorization", "token " + githubAccessToken)
                    .header("Accept", "application/json")
                    .header("Editor-Version", "AsciidocFX/" + appVersion)
                    .header("Editor-Plugin-Version", "copilot/1.0.0")
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                JsonNode tokenData = objectMapper.readTree(response.body());
                String copilotToken = tokenData.get("token").asText();
                long expiresAt = tokenData.get("expires_at").asLong();

                threadService.runActionLater(() -> {
                    configBean.setRefreshToken(copilotToken);
                    configBean.setTokenExpiresAt(expiresAt);
                    configBean.save();
                });
                return true;
            } else {
                logger.error("Failed to get Copilot token: HTTP {}", response.statusCode());
                return false;
            }
        } catch (Exception e) {
            logger.error("Failed to refresh Copilot token", e);
            return false;
        }
    }

    public boolean isAuthenticated() {
        return configBean.hasValidToken();
    }

    /**
     * Logs out from GitHub Copilot by clearing stored tokens.
     */
    public void logout() {
        threadService.runActionLater(() -> {
            configBean.clearTokens();
            configBean.save();
        });
        logger.info("Logged out from GitHub Copilot");
    }

    /**
     * Ensures we have a valid Copilot token, refreshing if necessary.
     */
    public String getValidToken() {
        if (configBean.hasValidToken()) {
            return configBean.getRefreshToken();
        }
        // Try to refresh
        String accessToken = configBean.getAccessToken();
        if (!accessToken.isEmpty()) {
            if (refreshCopilotToken(accessToken)) {
                return configBean.getRefreshToken();
            }
        }
        return null;
    }
}

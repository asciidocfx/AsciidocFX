package com.kodedu.copilot;

import com.kodedu.copilot.model.CopilotConversation;
import com.kodedu.copilot.model.CopilotPlan;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Main service interface for Copilot integration.
 */
public interface CopilotService {

    String label = "core::service::CopilotService";

    /**
     * Sends a message in the current mode and streams the response.
     */
    void sendMessage(String userMessage, Consumer<String> onChunk, Runnable onComplete, Consumer<String> onError);

    /**
     * Gets the current conversation.
     */
    CopilotConversation getConversation();

    /**
     * Starts a new conversation.
     */
    void newConversation();

    /**
     * Gets the current mode.
     */
    CopilotMode getCurrentMode();

    /**
     * Sets the current mode.
     */
    void setCurrentMode(CopilotMode mode);

    /**
     * Gets the current plan (Plan mode only).
     */
    CopilotPlan getCurrentPlan();

    /**
     * Applies a plan step.
     */
    void applyPlanStep(int stepIndex);

    /**
     * Checks if Copilot is authenticated.
     */
    boolean isAuthenticated();

    /**
     * Initiates authentication.
     * @param onDeviceCode Called with (userCode, verificationUri) when device code is ready
     * @param callback Called with true/false when auth completes
     */
    void authenticate(BiConsumer<String, String> onDeviceCode, Consumer<Boolean> callback);

    /**
     * Logs out from GitHub Copilot, clearing stored tokens.
     */
    void logout();

    /**
     * Stops any ongoing request.
     */
    void stopCurrentRequest();
}

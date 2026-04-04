package com.kodedu.copilot.service;

import com.kodedu.copilot.api.CopilotApiClient;
import com.kodedu.copilot.context.CopilotContextProvider;
import com.kodedu.copilot.model.CopilotConversation;
import com.kodedu.copilot.model.CopilotMessage;
import com.kodedu.copilot.model.CopilotPlan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Plan mode service: generates step-by-step plans for document changes.
 */
@Component
public class PlanModeService {

    private static final Logger logger = LoggerFactory.getLogger(PlanModeService.class);

    private static final String SYSTEM_PROMPT = """
            You are a planning assistant for AsciiDoc documents in the AsciidocFX editor.
            When the user describes a goal, create a clear step-by-step plan.
            
            Format each step as:
            **Step N:** Description of what to do
            
            For steps that involve code changes, include the specific changes in a code block.
            Use ```asciidoc for AsciiDoc code blocks.
            
            After listing all steps, provide a brief summary of the plan.
            
            The user's current document context is provided below.
            """;

    private final CopilotApiClient apiClient;
    private final CopilotContextProvider contextProvider;

    private CopilotPlan currentPlan;

    @Autowired
    public PlanModeService(CopilotApiClient apiClient, CopilotContextProvider contextProvider) {
        this.apiClient = apiClient;
        this.contextProvider = contextProvider;
    }

    /**
     * Creates a plan based on the user's goal.
     */
    public void createPlan(String userGoal, CopilotConversation conversation,
                           Consumer<String> onChunk, Runnable onComplete, Consumer<String> onError) {

        String context = contextProvider.buildContext();
        currentPlan = new CopilotPlan(userGoal, "");

        List<CopilotMessage> messages = new ArrayList<>();
        messages.add(CopilotMessage.system(SYSTEM_PROMPT + "\n\n" + context));

        // Add history
        List<CopilotMessage> history = conversation.getMessages();
        int startIdx = Math.max(0, history.size() - 6);
        for (int i = startIdx; i < history.size(); i++) {
            CopilotMessage msg = history.get(i);
            if (msg.getRole() != CopilotMessage.Role.SYSTEM) {
                messages.add(msg);
            }
        }

        CopilotMessage userMsg = CopilotMessage.user(userGoal);
        messages.add(userMsg);
        conversation.addMessage(userMsg);

        StringBuilder responseBuilder = new StringBuilder();
        apiClient.sendStreamingRequest(messages, null,
                chunk -> {
                    responseBuilder.append(chunk);
                    onChunk.accept(chunk);
                },
                toolCall -> {},
                () -> {
                    String response = responseBuilder.toString();
                    conversation.addMessage(CopilotMessage.assistant(response));
                    parsePlanFromResponse(response);
                    onComplete.run();
                },
                onError);
    }

    private void parsePlanFromResponse(String response) {
        // Parse "Step N:" patterns from the response
        String[] lines = response.split("\n");
        for (String line : lines) {
            String trimmed = line.trim();
            if (trimmed.matches("\\*\\*Step \\d+:?\\*\\*.*")) {
                String desc = trimmed.replaceFirst("\\*\\*Step \\d+:?\\*\\*\\s*", "");
                currentPlan.addStep(new CopilotPlan.PlanStep(desc));
            }
        }
    }

    public CopilotPlan getCurrentPlan() {
        return currentPlan;
    }
}

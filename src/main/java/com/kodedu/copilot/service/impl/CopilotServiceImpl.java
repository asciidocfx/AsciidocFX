package com.kodedu.copilot.service.impl;

import com.kodedu.copilot.CopilotMode;
import com.kodedu.copilot.CopilotService;
import com.kodedu.copilot.api.CopilotAuthService;
import com.kodedu.copilot.config.CopilotConfigBean;
import com.kodedu.copilot.model.CopilotConversation;
import com.kodedu.copilot.model.CopilotPlan;
import com.kodedu.copilot.service.AgentModeService;
import com.kodedu.copilot.service.AskModeService;
import com.kodedu.copilot.service.PlanModeService;
import com.kodedu.service.ThreadService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

/**
 * Main implementation of the CopilotService.
 * Delegates to mode-specific services based on the current mode.
 */
@Component(CopilotService.label)
public class CopilotServiceImpl implements CopilotService {

    private static final Logger logger = LoggerFactory.getLogger(CopilotServiceImpl.class);

    private final AskModeService askModeService;
    private final PlanModeService planModeService;
    private final AgentModeService agentModeService;
    private final CopilotAuthService authService;
    private final CopilotConfigBean configBean;
    private final ThreadService threadService;

    private CopilotMode currentMode = CopilotMode.ASK;
    private CopilotConversation conversation;
    private volatile boolean requestInProgress = false;

    @Autowired
    public CopilotServiceImpl(AskModeService askModeService, PlanModeService planModeService,
                              AgentModeService agentModeService, CopilotAuthService authService,
                              CopilotConfigBean configBean, ThreadService threadService) {
        this.askModeService = askModeService;
        this.planModeService = planModeService;
        this.agentModeService = agentModeService;
        this.authService = authService;
        this.configBean = configBean;
        this.threadService = threadService;
        this.conversation = new CopilotConversation("Default");

        // Set initial mode from config
        try {
            this.currentMode = CopilotMode.valueOf(configBean.getDefaultMode());
        } catch (Exception e) {
            this.currentMode = CopilotMode.ASK;
        }
    }

    @Override
    public void sendMessage(String userMessage, Consumer<String> onChunk,
                            Runnable onComplete, Consumer<String> onError) {
        if (!configBean.isEnabled()) {
            onError.accept("Copilot is disabled. Enable it in Settings.");
            return;
        }

        if (requestInProgress) {
            onError.accept("A request is already in progress.");
            return;
        }

        requestInProgress = true;

        Runnable wrappedComplete = () -> {
            requestInProgress = false;
            onComplete.run();
        };

        Consumer<String> wrappedError = (error) -> {
            requestInProgress = false;
            onError.accept(error);
        };

        switch (currentMode) {
            case ASK -> askModeService.ask(userMessage, conversation, onChunk, wrappedComplete, wrappedError);
            case PLAN -> planModeService.createPlan(userMessage, conversation, onChunk, wrappedComplete, wrappedError);
            case AGENT -> agentModeService.execute(userMessage, conversation, onChunk, wrappedComplete, wrappedError);
        }
    }

    @Override
    public CopilotConversation getConversation() {
        return conversation;
    }

    @Override
    public void newConversation() {
        this.conversation = new CopilotConversation("Conversation");
    }

    @Override
    public CopilotMode getCurrentMode() {
        return currentMode;
    }

    @Override
    public void setCurrentMode(CopilotMode mode) {
        this.currentMode = mode;
    }

    @Override
    public CopilotPlan getCurrentPlan() {
        return planModeService.getCurrentPlan();
    }

    @Override
    public void applyPlanStep(int stepIndex) {
        CopilotPlan plan = getCurrentPlan();
        if (plan != null && stepIndex >= 0 && stepIndex < plan.getSteps().size()) {
            CopilotPlan.PlanStep step = plan.getSteps().get(stepIndex);
            step.setStatus(CopilotPlan.StepStatus.COMPLETED);
            logger.info("Applied plan step {}: {}", stepIndex, step.getDescription());
        }
    }

    @Override
    public boolean isAuthenticated() {
        return authService.isAuthenticated();
    }

    @Override
    public void authenticate(Consumer<Boolean> callback) {
        authService.authenticate().thenAccept(success -> {
            threadService.runActionLater(() -> callback.accept(success));
        });
    }

    @Override
    public void stopCurrentRequest() {
        requestInProgress = false;
    }
}

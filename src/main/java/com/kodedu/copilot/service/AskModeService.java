package com.kodedu.copilot.service;

import com.kodedu.copilot.api.CopilotApiClient;
import com.kodedu.copilot.context.CopilotContextProvider;
import com.kodedu.copilot.model.CopilotConversation;
import com.kodedu.copilot.model.CopilotMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Ask mode service: handles question-answer interactions about documents.
 */
@Component
public class AskModeService {

    private static final Logger logger = LoggerFactory.getLogger(AskModeService.class);

    private static final String SYSTEM_PROMPT = """
            You are an expert AsciiDoc assistant integrated into AsciidocFX editor.
            You help users with:
            - Writing and editing AsciiDoc documents
            - AsciiDoc syntax and best practices
            - Document structure and organization
            - Converting between formats (Markdown, HTML, DocBook)
            - Diagrams (PlantUML, Mermaid), Math (MathJax), and other extensions
            
            When providing code examples, use AsciiDoc syntax unless the user specifically asks for another format.
            Be concise and helpful. Format your responses using Markdown.
            
            The user's current document context is provided below.
            """;

    private final CopilotApiClient apiClient;
    private final CopilotContextProvider contextProvider;

    @Autowired
    public AskModeService(CopilotApiClient apiClient, CopilotContextProvider contextProvider) {
        this.apiClient = apiClient;
        this.contextProvider = contextProvider;
    }

    /**
     * Sends a question with context and streams the response.
     */
    public void ask(String userMessage, CopilotConversation conversation,
                    Consumer<String> onChunk, Runnable onComplete, Consumer<String> onError) {

        String context = contextProvider.buildContext();

        // Build messages list
        List<CopilotMessage> messages = new ArrayList<>();
        messages.add(CopilotMessage.system(SYSTEM_PROMPT + "\n\n" + context));

        // Add conversation history (last 10 messages)
        List<CopilotMessage> history = conversation.getMessages();
        int startIdx = Math.max(0, history.size() - 10);
        for (int i = startIdx; i < history.size(); i++) {
            CopilotMessage msg = history.get(i);
            if (msg.getRole() != CopilotMessage.Role.SYSTEM) {
                messages.add(msg);
            }
        }

        // Add user message
        CopilotMessage userMsg = CopilotMessage.user(userMessage);
        messages.add(userMsg);
        conversation.addMessage(userMsg);

        // Stream response
        StringBuilder responseBuilder = new StringBuilder();
        apiClient.sendStreamingRequest(messages, null,
                chunk -> {
                    responseBuilder.append(chunk);
                    onChunk.accept(chunk);
                },
                toolCall -> {
                    // No tool calls in Ask mode
                },
                () -> {
                    conversation.addMessage(CopilotMessage.assistant(responseBuilder.toString()));
                    onComplete.run();
                },
                onError);
    }
}

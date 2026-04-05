package com.kodedu.copilot.component;

import com.kodedu.copilot.CopilotMode;
import com.kodedu.copilot.CopilotService;
import com.kodedu.service.ThreadService;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import org.kordamp.ikonli.fontawesome.FontAwesome;
import org.kordamp.ikonli.javafx.FontIcon;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;

/**
 * Main Copilot UI panel shown in the right sidebar.
 * Contains mode selector, chat view (WebView), and message input area.
 */
@Component
public class CopilotPanel extends VBox {

    private static final Logger logger = LoggerFactory.getLogger(CopilotPanel.class);

    private final CopilotService copilotService;
    private final ThreadService threadService;

    private WebView chatWebView;
    private WebEngine chatEngine;
    private TextArea inputArea;
    private Button sendButton;
    private Button stopButton;
    private Button newChatButton;
    private Button authButton;
    private ToggleGroup modeToggleGroup;
    private Label statusLabel;
    private boolean chatViewReady = false;

    @Value("${application.version}")
    private String appVersion;

    @Autowired
    public CopilotPanel(CopilotService copilotService, ThreadService threadService) {
        this.copilotService = copilotService;
        this.threadService = threadService;
    }

    @PostConstruct
    public void initialize() {
        threadService.runActionLater(() -> {
            buildUI();
        });
    }

    private void buildUI() {
        setSpacing(5);
        setPadding(new Insets(5));

        // --- Header with title and auth ---
        HBox header = new HBox(5);
        header.setAlignment(Pos.CENTER_LEFT);
        Label titleLabel = new Label("Copilot");
        titleLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        FontIcon copilotIcon = new FontIcon(FontAwesome.COMMENTING);
        titleLabel.setGraphic(copilotIcon);

        authButton = new Button();
        authButton.setGraphic(new FontIcon(FontAwesome.SIGN_IN));
        authButton.setTooltip(new Tooltip("Sign in to GitHub Copilot"));
        authButton.setOnAction(e -> handleAuth());
        authButton.getStyleClass().add("copilot-auth-button");

        newChatButton = new Button();
        newChatButton.setGraphic(new FontIcon(FontAwesome.PLUS));
        newChatButton.setTooltip(new Tooltip("New Conversation"));
        newChatButton.setOnAction(e -> handleNewChat());
        newChatButton.getStyleClass().add("copilot-new-chat-button");

        Pane spacer = new Pane();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        header.getChildren().addAll(titleLabel, spacer, newChatButton, authButton);

        // --- Mode selector ---
        modeToggleGroup = new ToggleGroup();
        ToggleButton askBtn = new ToggleButton("Ask");
        askBtn.setToggleGroup(modeToggleGroup);
        askBtn.setUserData(CopilotMode.ASK);
        askBtn.setSelected(true);
        askBtn.getStyleClass().add("copilot-mode-button");

        ToggleButton planBtn = new ToggleButton("Plan");
        planBtn.setToggleGroup(modeToggleGroup);
        planBtn.setUserData(CopilotMode.PLAN);
        planBtn.getStyleClass().add("copilot-mode-button");

        ToggleButton agentBtn = new ToggleButton("Agent");
        agentBtn.setToggleGroup(modeToggleGroup);
        agentBtn.setUserData(CopilotMode.AGENT);
        agentBtn.getStyleClass().add("copilot-mode-button");

        HBox modeBox = new HBox(askBtn, planBtn, agentBtn);
        modeBox.setAlignment(Pos.CENTER);
        modeBox.getStyleClass().add("copilot-mode-selector");
        modeBox.setSpacing(0);

        modeToggleGroup.selectedToggleProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                CopilotMode mode = (CopilotMode) newVal.getUserData();
                copilotService.setCurrentMode(mode);
                updateModeUI(mode);
            }
        });

        // --- Chat WebView ---
        chatWebView = new WebView();
        chatWebView.setContextMenuEnabled(false);
        chatEngine = chatWebView.getEngine();
        VBox.setVgrow(chatWebView, Priority.ALWAYS);

        // Load chat HTML
        String chatHtml = buildChatHtml();
        chatEngine.loadContent(chatHtml);

        chatEngine.getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {
            if (newState == javafx.concurrent.Worker.State.SUCCEEDED) {
                chatViewReady = true;
            }
        });

        // --- Status label ---
        statusLabel = new Label("");
        statusLabel.getStyleClass().add("copilot-status");

        // --- Input area ---
        inputArea = new TextArea();
        inputArea.setPromptText("Ask Copilot anything...");
        inputArea.setPrefRowCount(3);
        inputArea.setMaxHeight(100);
        inputArea.setWrapText(true);
        inputArea.getStyleClass().add("copilot-input");

        inputArea.setOnKeyPressed(event -> {
            if (event.getCode() == javafx.scene.input.KeyCode.ENTER && !event.isShiftDown()) {
                event.consume();
                handleSend();
            }
        });

        // --- Send / Stop buttons ---
        sendButton = new Button("Send");
        sendButton.setGraphic(new FontIcon(FontAwesome.PAPER_PLANE));
        sendButton.setOnAction(e -> handleSend());
        sendButton.setMaxWidth(Double.MAX_VALUE);
        sendButton.getStyleClass().add("copilot-send-button");

        stopButton = new Button("Stop");
        stopButton.setGraphic(new FontIcon(FontAwesome.STOP));
        stopButton.setOnAction(e -> handleStop());
        stopButton.setVisible(false);
        stopButton.setManaged(false);
        stopButton.setMaxWidth(Double.MAX_VALUE);
        stopButton.getStyleClass().add("copilot-stop-button");

        HBox buttonBox = new HBox(5, sendButton, stopButton);
        buttonBox.setAlignment(Pos.CENTER);
        HBox.setHgrow(sendButton, Priority.ALWAYS);
        HBox.setHgrow(stopButton, Priority.ALWAYS);

        getChildren().addAll(header, modeBox, chatWebView, statusLabel, inputArea, buttonBox);
    }

    private void handleSend() {
        String message = inputArea.getText().trim();
        if (message.isEmpty()) return;

        inputArea.clear();
        inputArea.setDisable(true);
        sendButton.setDisable(true);
        stopButton.setVisible(true);
        stopButton.setManaged(true);
        statusLabel.setText("Thinking...");

        // Add user message to chat
        appendUserMessage(message);

        // Start assistant message container
        startAssistantMessage();

        copilotService.sendMessage(message,
                chunk -> threadService.runActionLater(() -> appendToAssistantMessage(chunk)),
                () -> threadService.runActionLater(() -> {
                    finishAssistantMessage();
                    inputArea.setDisable(false);
                    sendButton.setDisable(false);
                    stopButton.setVisible(false);
                    stopButton.setManaged(false);
                    statusLabel.setText("");
                    inputArea.requestFocus();
                }),
                error -> threadService.runActionLater(() -> {
                    appendErrorMessage(error);
                    inputArea.setDisable(false);
                    sendButton.setDisable(false);
                    stopButton.setVisible(false);
                    stopButton.setManaged(false);
                    statusLabel.setText("");
                })
        );
    }

    private void handleStop() {
        copilotService.stopCurrentRequest();
        inputArea.setDisable(false);
        sendButton.setDisable(false);
        stopButton.setVisible(false);
        stopButton.setManaged(false);
        statusLabel.setText("Stopped");
    }

    private void handleNewChat() {
        copilotService.newConversation();
        if (chatViewReady) {
            chatEngine.executeScript("clearChat()");
        }
    }

    private void handleAuth() {
        if (copilotService.isAuthenticated()) {
            statusLabel.setText("Already authenticated ✓");
            return;
        }
        statusLabel.setText("Authenticating...");
        copilotService.authenticate(success -> {
            if (success) {
                statusLabel.setText("Authenticated ✓");
                authButton.setGraphic(new FontIcon(FontAwesome.CHECK));
            } else {
                statusLabel.setText("Authentication failed");
            }
        });
    }

    private void updateModeUI(CopilotMode mode) {
        switch (mode) {
            case ASK -> inputArea.setPromptText("Ask Copilot anything about your document...");
            case PLAN -> inputArea.setPromptText("Describe what you want to achieve...");
            case AGENT -> inputArea.setPromptText("Give the agent a task to perform...");
        }
    }

    // --- WebView chat manipulation ---

    private void appendUserMessage(String message) {
        if (chatViewReady) {
            String escaped = escapeForJs(message);
            chatEngine.executeScript("addUserMessage('" + escaped + "')");
        }
    }

    private void startAssistantMessage() {
        if (chatViewReady) {
            chatEngine.executeScript("startAssistantMessage()");
        }
    }

    private void appendToAssistantMessage(String chunk) {
        if (chatViewReady) {
            String escaped = escapeForJs(chunk);
            chatEngine.executeScript("appendToAssistantMessage('" + escaped + "')");
        }
    }

    private void finishAssistantMessage() {
        if (chatViewReady) {
            chatEngine.executeScript("finishAssistantMessage()");
        }
    }

    private void appendErrorMessage(String error) {
        if (chatViewReady) {
            String escaped = escapeForJs(error);
            chatEngine.executeScript("addErrorMessage('" + escaped + "')");
        }
    }

    private String escapeForJs(String text) {
        if (text == null) return "";
        return text.replace("\\", "\\\\")
                .replace("'", "\\'")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("<", "\\x3C")
                .replace(">", "\\x3E");
    }

    /**
     * Builds the HTML content for the chat WebView.
     */
    private String buildChatHtml() {
        return """
                <!DOCTYPE html>
                <html lang="en">
                <head>
                <meta charset="UTF-8">
                <style>
                * { margin: 0; padding: 0; box-sizing: border-box; }
                body {
                    font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
                    font-size: 13px;
                    background: #1e1e1e;
                    color: #d4d4d4;
                    padding: 10px;
                    overflow-y: auto;
                }
                #chat-container { display: flex; flex-direction: column; gap: 12px; }
                .message {
                    padding: 10px 14px;
                    border-radius: 8px;
                    max-width: 100%;
                    word-wrap: break-word;
                    line-height: 1.5;
                }
                .user-message {
                    background: #264f78;
                    color: #ffffff;
                    align-self: flex-end;
                    border-bottom-right-radius: 2px;
                }
                .assistant-message {
                    background: #2d2d2d;
                    color: #d4d4d4;
                    align-self: flex-start;
                    border-bottom-left-radius: 2px;
                }
                .error-message {
                    background: #5a1d1d;
                    color: #f48771;
                    border-left: 3px solid #f48771;
                }
                .welcome-message {
                    text-align: center;
                    color: #888;
                    padding: 30px 10px;
                }
                .welcome-message h3 { color: #d4d4d4; margin-bottom: 8px; }
                pre {
                    background: #1a1a1a;
                    border: 1px solid #333;
                    border-radius: 4px;
                    padding: 10px;
                    margin: 8px 0;
                    overflow-x: auto;
                    font-family: 'Cascadia Code', 'Fira Code', monospace;
                    font-size: 12px;
                    position: relative;
                }
                code {
                    font-family: 'Cascadia Code', 'Fira Code', monospace;
                    font-size: 12px;
                }
                .inline-code {
                    background: #333;
                    padding: 2px 5px;
                    border-radius: 3px;
                }
                .copy-btn {
                    position: absolute;
                    top: 4px;
                    right: 4px;
                    background: #444;
                    color: #ccc;
                    border: none;
                    border-radius: 3px;
                    padding: 2px 8px;
                    cursor: pointer;
                    font-size: 11px;
                }
                .copy-btn:hover { background: #555; }
                strong { color: #e0e0e0; }
                em { color: #c5c5c5; }
                ul, ol { padding-left: 20px; margin: 5px 0; }
                a { color: #4fc1ff; }
                .typing-indicator {
                    display: inline-block;
                    width: 8px;
                    height: 8px;
                    background: #569cd6;
                    border-radius: 50%;
                    animation: pulse 1s infinite;
                }
                @keyframes pulse {
                    0%, 100% { opacity: 0.3; }
                    50% { opacity: 1; }
                }
                </style>
                </head>
                <body>
                <div id="chat-container">
                    <div class="welcome-message">
                        <h3>🤖 GitHub Copilot</h3>
                        <p>Ask questions, create plans, or let the agent help you with your AsciiDoc documents.</p>
                    </div>
                </div>
                <script>
                var chatContainer = document.getElementById('chat-container');
                var currentAssistantDiv = null;
                var currentAssistantContent = '';

                function clearChat() {
                    chatContainer.innerHTML = '<div class="welcome-message"><h3>🤖 GitHub Copilot</h3><p>New conversation started.</p></div>';
                    currentAssistantDiv = null;
                    currentAssistantContent = '';
                }

                function addUserMessage(text) {
                    removeWelcome();
                    var div = document.createElement('div');
                    div.className = 'message user-message';
                    div.textContent = text;
                    chatContainer.appendChild(div);
                    scrollToBottom();
                }

                function startAssistantMessage() {
                    removeWelcome();
                    currentAssistantContent = '';
                    currentAssistantDiv = document.createElement('div');
                    currentAssistantDiv.className = 'message assistant-message';
                    currentAssistantDiv.innerHTML = '<span class="typing-indicator"></span>';
                    chatContainer.appendChild(currentAssistantDiv);
                    scrollToBottom();
                }

                function appendToAssistantMessage(chunk) {
                    if (!currentAssistantDiv) return;
                    currentAssistantContent += chunk;
                    currentAssistantDiv.innerHTML = renderMarkdown(currentAssistantContent);
                    scrollToBottom();
                }

                function finishAssistantMessage() {
                    if (currentAssistantDiv) {
                        currentAssistantDiv.innerHTML = renderMarkdown(currentAssistantContent);
                        addCopyButtons();
                    }
                    currentAssistantDiv = null;
                    currentAssistantContent = '';
                    scrollToBottom();
                }

                function addErrorMessage(text) {
                    var div = document.createElement('div');
                    div.className = 'message error-message';
                    div.textContent = '⚠️ ' + text;
                    chatContainer.appendChild(div);
                    scrollToBottom();
                }

                function removeWelcome() {
                    var welcome = chatContainer.querySelector('.welcome-message');
                    if (welcome) welcome.remove();
                }

                function scrollToBottom() {
                    window.scrollTo(0, document.body.scrollHeight);
                }

                function renderMarkdown(text) {
                    // Simple markdown rendering
                    var html = text;
                    // Code blocks
                    html = html.replace(/```(\\w*)\\n([\\s\\S]*?)```/g, function(match, lang, code) {
                        return '<pre><code class="' + lang + '">' + escapeHtml(code.trim()) + '</code></pre>';
                    });
                    // Inline code
                    html = html.replace(/`([^`]+)`/g, '<code class="inline-code">$1</code>');
                    // Bold
                    html = html.replace(/\\*\\*([^*]+)\\*\\*/g, '<strong>$1</strong>');
                    // Italic
                    html = html.replace(/\\*([^*]+)\\*/g, '<em>$1</em>');
                    // Line breaks
                    html = html.replace(/\\n/g, '<br>');
                    return html;
                }

                function escapeHtml(text) {
                    var div = document.createElement('div');
                    div.appendChild(document.createTextNode(text));
                    return div.innerHTML;
                }

                function addCopyButtons() {
                    var pres = chatContainer.querySelectorAll('pre');
                    pres.forEach(function(pre) {
                        if (!pre.querySelector('.copy-btn')) {
                            var btn = document.createElement('button');
                            btn.className = 'copy-btn';
                            btn.textContent = 'Copy';
                            btn.onclick = function() {
                                var code = pre.querySelector('code');
                                if (code) {
                                    var text = code.textContent;
                                    // Use clipboard API if available, otherwise fallback
                                    if (navigator.clipboard) {
                                        navigator.clipboard.writeText(text);
                                    }
                                    btn.textContent = 'Copied!';
                                    setTimeout(function() { btn.textContent = 'Copy'; }, 2000);
                                }
                            };
                            pre.appendChild(btn);
                        }
                    });
                }
                </script>
                </body>
                </html>
                """;
    }
}

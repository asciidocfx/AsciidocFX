package com.kodedu.copilot.component;

import com.kodedu.copilot.CopilotMode;
import com.kodedu.copilot.CopilotService;
import com.kodedu.copilot.config.CopilotConfigBean;
import com.kodedu.copilot.provider.ChatModelSpec;
import com.kodedu.copilot.provider.ChatProviderSpec;
import com.kodedu.copilot.provider.CopilotProviderCatalog;
import com.kodedu.config.EditorConfigBean;
import com.kodedu.controller.ApplicationController;
import com.kodedu.service.ThreadService;
import javafx.collections.FXCollections;
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
 * Contains mode selector, model selector, chat view (WebView), and message input area.
 */
@Component
public class CopilotPanel extends VBox {

    private static final Logger logger = LoggerFactory.getLogger(CopilotPanel.class);

    private final CopilotService copilotService;
    private final ThreadService threadService;
    private final EditorConfigBean editorConfigBean;
    private final CopilotConfigBean copilotConfigBean;
    private final CopilotProviderCatalog providerCatalog;
    private final ApplicationController controller;

    private WebView chatWebView;
    private WebEngine chatEngine;
    private TextArea inputArea;
    private Button sendButton;
    private Button stopButton;
    private Button newChatButton;
    private Button authButton;
    private Button logoutButton;
    private ToggleGroup modeToggleGroup;
    private ComboBox<ChatProviderSpec> providerComboBox;
    private ComboBox<ChatModelSpec> modelComboBox;
    private Label modelHintLabel;
    private PasswordField apiKeyField;
    private HBox apiKeyBox;
    private Label statusLabel;
    private boolean chatViewReady = false;

    @Value("${application.version}")
    private String appVersion;

    @Autowired
    public CopilotPanel(CopilotService copilotService, ThreadService threadService,
                        EditorConfigBean editorConfigBean, CopilotConfigBean copilotConfigBean,
                        CopilotProviderCatalog providerCatalog, ApplicationController controller) {
        this.copilotService = copilotService;
        this.threadService = threadService;
        this.editorConfigBean = editorConfigBean;
        this.copilotConfigBean = copilotConfigBean;
        this.providerCatalog = providerCatalog;
        this.controller = controller;
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

        // --- Header with title, auth, and logout ---
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

        logoutButton = new Button();
        logoutButton.setGraphic(new FontIcon(FontAwesome.SIGN_OUT));
        logoutButton.setTooltip(new Tooltip("Sign out from GitHub Copilot"));
        logoutButton.setOnAction(e -> handleLogout());
        logoutButton.getStyleClass().add("copilot-auth-button");
        logoutButton.setVisible(false);
        logoutButton.setManaged(false);

        newChatButton = new Button();
        newChatButton.setGraphic(new FontIcon(FontAwesome.PLUS));
        newChatButton.setTooltip(new Tooltip("New Conversation"));
        newChatButton.setOnAction(e -> handleNewChat());
        newChatButton.getStyleClass().add("copilot-new-chat-button");

        Pane spacer = new Pane();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        header.getChildren().addAll(titleLabel, spacer, newChatButton, authButton, logoutButton);

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

        // --- Provider + model ---
        VBox providerModelBox = buildProviderModelBox();

        // --- Chat WebView ---
        chatWebView = new WebView();
        chatWebView.setContextMenuEnabled(false);
        chatEngine = chatWebView.getEngine();
        VBox.setVgrow(chatWebView, Priority.ALWAYS);

        // Load chat HTML with theme awareness
        String chatHtml = buildChatHtml();
        chatEngine.loadContent(chatHtml);

        chatEngine.getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {
            if (newState == javafx.concurrent.Worker.State.SUCCEEDED) {
                chatViewReady = true;
            }
        });

        // Intercept link clicks in WebView to open in system browser
        chatEngine.locationProperty().addListener((obs, oldLocation, newLocation) -> {
            if (newLocation != null && !newLocation.isEmpty()
                    && (newLocation.startsWith("http://") || newLocation.startsWith("https://"))) {
                // Prevent WebView navigation
                chatEngine.loadContent(buildChatHtml());
                // Open in system browser
                controller.browseInDesktop(newLocation);
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

        getChildren().addAll(header, modeBox, providerModelBox, chatWebView, statusLabel, inputArea, buttonBox);

        // Update auth button state
        updateAuthButtons();
        if (providerComboBox.getValue() != null) {
            updateProviderChrome(providerComboBox.getValue());
        }
    }

    private VBox buildProviderModelBox() {
        providerComboBox = new ComboBox<>(FXCollections.observableArrayList(providerCatalog.providers()));
        providerComboBox.setMaxWidth(Double.MAX_VALUE);
        providerComboBox.getStyleClass().add("copilot-model-selector");
        providerComboBox.setTooltip(new Tooltip("Chat provider"));
        providerComboBox.setConverter(new javafx.util.StringConverter<>() {
            @Override
            public String toString(ChatProviderSpec spec) {
                return spec == null ? "" : spec.name();
            }

            @Override
            public ChatProviderSpec fromString(String string) {
                return providerCatalog.providers().stream()
                        .filter(p -> p.name().equals(string) || p.id().equals(string))
                        .findFirst()
                        .orElse(null);
            }
        });

        modelComboBox = new ComboBox<>();
        modelComboBox.setMaxWidth(Double.MAX_VALUE);
        modelComboBox.getStyleClass().add("copilot-model-selector");
        modelComboBox.setCellFactory(list -> new ListCell<>() {
            @Override
            protected void updateItem(ChatModelSpec item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : formatModel(item));
            }
        });
        modelComboBox.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(ChatModelSpec item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : formatModel(item));
            }
        });

        modelHintLabel = new Label();
        modelHintLabel.setWrapText(true);
        modelHintLabel.getStyleClass().add("copilot-status");

        apiKeyField = new PasswordField();
        apiKeyField.setPromptText("API key for this provider");
        HBox.setHgrow(apiKeyField, Priority.ALWAYS);
        apiKeyField.focusedProperty().addListener((obs, was, focused) -> {
            if (!focused) {
                persistApiKeyFromField();
            }
        });
        Label apiKeyLabel = new Label("API key:");
        apiKeyLabel.getStyleClass().add("copilot-status");
        apiKeyBox = new HBox(5, apiKeyLabel, apiKeyField);
        apiKeyBox.setAlignment(Pos.CENTER_LEFT);

        ChatProviderSpec initial = providerCatalog.findProvider(copilotConfigBean.getProvider())
                .or(() -> providerCatalog.providers().stream().findFirst())
                .orElse(null);
        if (initial != null) {
            providerComboBox.setValue(initial);
        }

        providerComboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                copilotConfigBean.setProvider(newVal.id());
                apiKeyField.setText(copilotConfigBean.findApiKey(newVal.id()));
                refillModels(newVal);
                updateProviderChrome(newVal);
            }
        });
        modelComboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && !newVal.id().isBlank()) {
                copilotConfigBean.setModel(newVal.id());
                updateModelHint(newVal);
            }
        });

        if (initial != null) {
            refillModels(initial);
            apiKeyField.setText(copilotConfigBean.findApiKey(initial.id()));
        }

        HBox providerRow = new HBox(5);
        providerRow.setAlignment(Pos.CENTER_LEFT);
        Label providerLabel = new Label("Provider:");
        providerLabel.getStyleClass().add("copilot-status");
        HBox.setHgrow(providerComboBox, Priority.ALWAYS);
        providerRow.getChildren().addAll(providerLabel, providerComboBox);

        HBox modelBox = new HBox(5);
        modelBox.setAlignment(Pos.CENTER_LEFT);
        Label modelLabel = new Label("Model:");
        modelLabel.getStyleClass().add("copilot-status");
        HBox.setHgrow(modelComboBox, Priority.ALWAYS);
        modelBox.getChildren().addAll(modelLabel, modelComboBox);

        VBox box = new VBox(5, providerRow, modelBox, modelHintLabel, apiKeyBox);
        return box;
    }

    private void refillModels(ChatProviderSpec provider) {
        var models = FXCollections.observableArrayList(provider.models());
        modelComboBox.setItems(models);
        ChatModelSpec selected = providerCatalog.findModel(provider.id(), copilotConfigBean.getModel())
                .orElse(null);
        if (selected == null && provider.kind() == com.kodedu.copilot.provider.ChatProviderKind.OLLAMA) {
            selected = providerCatalog.recommendedLocalModel().orElse(null);
        }
        if (selected == null && !models.isEmpty()) {
            selected = models.get(0);
        }
        modelComboBox.setValue(selected);
        if (selected != null) {
            copilotConfigBean.setModel(selected.id());
            updateModelHint(selected);
        }
    }

    private String formatModel(ChatModelSpec model) {
        String text = model.displayName();
        if (providerCatalog.isRecommended("ollama", model.id())
                && providerComboBox.getValue() != null
                && "ollama".equalsIgnoreCase(providerComboBox.getValue().id())) {
            text += " ★ recommended";
        }
        return text;
    }

    private void updateModelHint(ChatModelSpec model) {
        String req = model.requirementText();
        String hardware = providerCatalog.hardware().summary();
        if (providerComboBox.getValue() != null
                && providerComboBox.getValue().kind() == com.kodedu.copilot.provider.ChatProviderKind.OLLAMA) {
            String rec = providerCatalog.recommendedLocalModel()
                    .map(m -> " Suggested: " + m.displayName() + ".")
                    .orElse(" No catalogued model fits with the current headroom.");
            modelHintLabel.setText(hardware + "." + rec
                    + (req.isBlank() ? "" : " This model needs " + req + "."));
        } else if (!req.isBlank()) {
            modelHintLabel.setText(req);
        } else {
            modelHintLabel.setText("");
        }
    }

    private void updateProviderChrome(ChatProviderSpec provider) {
        boolean github = provider.usesGithubOAuth();
        boolean needsKey = provider.needsApiKey();
        apiKeyBox.setVisible(needsKey);
        apiKeyBox.setManaged(needsKey);
        authButton.setVisible(github && !copilotService.isAuthenticated());
        authButton.setManaged(github && !copilotService.isAuthenticated());
        logoutButton.setVisible(github && copilotService.isAuthenticated());
        logoutButton.setManaged(github && copilotService.isAuthenticated());
        if (provider.kind() == com.kodedu.copilot.provider.ChatProviderKind.OLLAMA) {
            statusLabel.setText("On-device Ollama (no API key). Start Ollama locally.");
        } else if (provider.kind() == com.kodedu.copilot.provider.ChatProviderKind.CLI) {
            statusLabel.setText("Uses " + provider.command() + " on PATH. HTTP APIs are preferred when you have a key.");
        }
    }

    private void persistApiKeyFromField() {
        String key = apiKeyField.getText();
        if (key == null) {
            return;
        }
        copilotConfigBean.setProviderApiKey(key);
        copilotConfigBean.save();
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
        copilotService.authenticate(
                // onDeviceCode: show verification link and user code in the chat panel
                (userCode, verificationUri) -> threadService.runActionLater(() -> {
                    showDeviceCodeInChat(userCode, verificationUri);
                }),
                success -> {
                    if (success) {
                        statusLabel.setText("Authenticated ✓");
                        updateAuthButtons();
                        if (chatViewReady) {
                            chatEngine.executeScript(
                                    "addSuccessMessage('✅ Successfully signed in to GitHub Copilot!')");
                        }
                    } else {
                        statusLabel.setText("Authentication failed");
                        appendErrorMessage("Authentication failed. Please try again.");
                    }
                }
        );
    }

    private void handleLogout() {
        copilotService.logout();
        updateAuthButtons();
        statusLabel.setText("Signed out");
        if (chatViewReady) {
            chatEngine.executeScript("clearChat()");
            chatEngine.executeScript(
                    "addSuccessMessage('Signed out from GitHub Copilot. Click Sign In to connect with a different account.')");
        }
    }

    private void updateAuthButtons() {
        ChatProviderSpec provider = providerComboBox == null ? null : providerComboBox.getValue();
        boolean github = provider == null || provider.usesGithubOAuth();
        boolean authenticated = copilotService.isAuthenticated();
        authButton.setVisible(github && !authenticated);
        authButton.setManaged(github && !authenticated);
        logoutButton.setVisible(github && authenticated);
        logoutButton.setManaged(github && authenticated);
        if (github && authenticated) {
            statusLabel.setText("Authenticated ✓");
        }
    }

    private void showDeviceCodeInChat(String userCode, String verificationUri) {
        if (chatViewReady) {
            String escapedCode = escapeForJs(userCode);
            String escapedUri = escapeForJs(verificationUri);
            chatEngine.executeScript(
                    "addDeviceCodeMessage('" + escapedCode + "', '" + escapedUri + "')");
        }
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

    private boolean isDarkTheme() {
        try {
            var themes = editorConfigBean.getEditorTheme();
            if (themes != null && !themes.isEmpty()) {
                String themeName = themes.get(0).getThemeName();
                return "Dark".equalsIgnoreCase(themeName);
            }
        } catch (Exception e) {
            logger.debug("Could not detect theme, defaulting to dark", e);
        }
        return true;
    }

    /**
     * Builds the HTML content for the chat WebView, adapting to the current theme.
     */
    private String buildChatHtml() {
        boolean dark = isDarkTheme();

        String bgColor = dark ? "#1e1e1e" : "#ffffff";
        String textColor = dark ? "#d4d4d4" : "#1e1e1e";
        String userMsgBg = dark ? "#264f78" : "#0078d4";
        String userMsgColor = "#ffffff";
        String assistantMsgBg = dark ? "#2d2d2d" : "#f3f3f3";
        String assistantMsgColor = dark ? "#d4d4d4" : "#1e1e1e";
        String errorMsgBg = dark ? "#5a1d1d" : "#fde7e9";
        String errorMsgColor = dark ? "#f48771" : "#d13438";
        String successMsgBg = dark ? "#1d3a1d" : "#dff6dd";
        String successMsgColor = dark ? "#4ec94e" : "#107c10";
        String codeBg = dark ? "#1a1a1a" : "#f5f5f5";
        String codeBorder = dark ? "#333" : "#ddd";
        String inlineCodeBg = dark ? "#333" : "#e8e8e8";
        String copyBtnBg = dark ? "#444" : "#ddd";
        String copyBtnColor = dark ? "#ccc" : "#333";
        String copyBtnHover = dark ? "#555" : "#ccc";
        String linkColor = dark ? "#4fc1ff" : "#0078d4";
        String welcomeSubColor = dark ? "#888" : "#666";
        String deviceBoxBg = dark ? "#1a3a5c" : "#e8f0fe";
        String deviceBoxBorder = dark ? "#264f78" : "#0078d4";
        String deviceCodeBg = dark ? "#1e1e1e" : "#ffffff";
        String deviceCodeColor = dark ? "#4fc1ff" : "#0078d4";
        String typingColor = dark ? "#569cd6" : "#0078d4";
        String strongColor = dark ? "#e0e0e0" : "#1e1e1e";
        String emColor = dark ? "#c5c5c5" : "#444444";

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
                    background: %s;
                    color: %s;
                    padding: 10px;
                    overflow-y: auto;
                }
                #chat-container { display: flex; flex-direction: column; gap: 12px; }
                .message {
                    padding: 10px 14px;
                    border-radius: 8px;
                    max-width: 100%%;
                    word-wrap: break-word;
                    line-height: 1.5;
                }
                .user-message {
                    background: %s;
                    color: %s;
                    align-self: flex-end;
                    border-bottom-right-radius: 2px;
                }
                .assistant-message {
                    background: %s;
                    color: %s;
                    align-self: flex-start;
                    border-bottom-left-radius: 2px;
                }
                .error-message {
                    background: %s;
                    color: %s;
                    border-left: 3px solid %s;
                }
                .success-message {
                    background: %s;
                    color: %s;
                    border-left: 3px solid %s;
                    padding: 10px 14px;
                    border-radius: 8px;
                    line-height: 1.5;
                }
                .device-code-box {
                    background: %s;
                    border: 1px solid %s;
                    border-radius: 8px;
                    padding: 16px;
                    text-align: center;
                    line-height: 1.8;
                }
                .device-code-box .code {
                    display: inline-block;
                    background: %s;
                    color: %s;
                    font-size: 22px;
                    font-weight: bold;
                    padding: 6px 16px;
                    border-radius: 6px;
                    letter-spacing: 3px;
                    margin: 8px 0;
                    font-family: 'Cascadia Code', 'Fira Code', monospace;
                    user-select: all;
                }
                .device-code-box a {
                    color: %s;
                    text-decoration: underline;
                    font-weight: bold;
                    font-size: 14px;
                }
                .device-code-box a:hover {
                    opacity: 0.8;
                }
                .device-code-box .step {
                    margin: 4px 0;
                }
                .welcome-message {
                    text-align: center;
                    color: %s;
                    padding: 30px 10px;
                }
                .welcome-message h3 { color: %s; margin-bottom: 8px; }
                pre {
                    background: %s;
                    border: 1px solid %s;
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
                    background: %s;
                    padding: 2px 5px;
                    border-radius: 3px;
                }
                .copy-btn {
                    position: absolute;
                    top: 4px;
                    right: 4px;
                    background: %s;
                    color: %s;
                    border: none;
                    border-radius: 3px;
                    padding: 2px 8px;
                    cursor: pointer;
                    font-size: 11px;
                }
                .copy-btn:hover { background: %s; }
                strong { color: %s; }
                em { color: %s; }
                ul, ol { padding-left: 20px; margin: 5px 0; }
                a { color: %s; }
                .typing-indicator {
                    display: inline-block;
                    width: 8px;
                    height: 8px;
                    background: %s;
                    border-radius: 50%%;
                    animation: pulse 1s infinite;
                }
                @keyframes pulse {
                    0%%, 100%% { opacity: 0.3; }
                    50%% { opacity: 1; }
                }
                </style>
                </head>
                <body>
                <div id="chat-container">
                    <div class="welcome-message">
                        <h3>\uD83E\uDD16 Copilot</h3>
                        <p>Ask questions, create plans, or let the agent help you with your AsciiDoc documents. Pick a provider — GitHub Copilot, OpenRouter, on-device Ollama, and others.</p>
                    </div>
                </div>
                <script>
                var chatContainer = document.getElementById('chat-container');
                var currentAssistantDiv = null;
                var currentAssistantContent = '';

                function clearChat() {
                    chatContainer.innerHTML = '<div class="welcome-message"><h3>\uD83E\uDD16 Copilot</h3><p>New conversation started.</p></div>';
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
                    div.textContent = '\\u26A0\\uFE0F ' + text;
                    chatContainer.appendChild(div);
                    scrollToBottom();
                }

                function addSuccessMessage(text) {
                    var div = document.createElement('div');
                    div.className = 'success-message';
                    div.textContent = text;
                    chatContainer.appendChild(div);
                    scrollToBottom();
                }

                function addDeviceCodeMessage(userCode, verificationUri) {
                    removeWelcome();
                    var div = document.createElement('div');
                    div.className = 'device-code-box';
                    div.innerHTML =
                        '<div class="step"><strong>Step 1:</strong> Click the link below to open GitHub:</div>' +
                        '<div class="step"><a href="' + verificationUri + '" target="_blank">' + verificationUri + '</a></div>' +
                        '<div class="step" style="margin-top:8px"><strong>Step 2:</strong> Enter this code:</div>' +
                        '<div><span class="code">' + userCode + '</span></div>' +
                        '<div class="step" style="margin-top:8px"><strong>Step 3:</strong> After authorizing, this will update automatically.</div>';
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
                    var html = text;
                    html = html.replace(/```(\\w*)\\n([\\s\\S]*?)```/g, function(match, lang, code) {
                        return '<pre><code class="' + lang + '">' + escapeHtml(code.trim()) + '</code></pre>';
                    });
                    html = html.replace(/`([^`]+)`/g, '<code class="inline-code">$1</code>');
                    html = html.replace(/\\*\\*([^*]+)\\*\\*/g, '<strong>$1</strong>');
                    html = html.replace(/\\*([^*]+)\\*/g, '<em>$1</em>');
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
                """.formatted(
                bgColor, textColor,
                userMsgBg, userMsgColor,
                assistantMsgBg, assistantMsgColor,
                errorMsgBg, errorMsgColor, errorMsgColor,
                successMsgBg, successMsgColor, successMsgColor,
                deviceBoxBg, deviceBoxBorder,
                deviceCodeBg, deviceCodeColor,
                linkColor,
                welcomeSubColor, textColor,
                codeBg, codeBorder,
                inlineCodeBg,
                copyBtnBg, copyBtnColor, copyBtnHover,
                strongColor, emColor,
                linkColor,
                typingColor
        );
    }
}

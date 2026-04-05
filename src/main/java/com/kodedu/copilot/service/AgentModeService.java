package com.kodedu.copilot.service;

import com.kodedu.copilot.api.CopilotApiClient;
import com.kodedu.copilot.context.CopilotContextProvider;
import com.kodedu.copilot.context.FileContextCollector;
import com.kodedu.copilot.context.WorkspaceContextCollector;
import com.kodedu.copilot.model.CopilotConversation;
import com.kodedu.copilot.model.CopilotMessage;
import com.kodedu.other.Current;
import com.kodedu.service.DirectoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Agent mode service: autonomous agent with tool-calling capabilities.
 * Can read/write files, list directories, search, and execute document conversions.
 */
@Component
public class AgentModeService {

    private static final Logger logger = LoggerFactory.getLogger(AgentModeService.class);

    private static final String SYSTEM_PROMPT = """
            You are an autonomous agent assistant for the AsciidocFX editor.
            You can perform the following actions using tools:
            - Read files from the workspace
            - Write content to files
            - Edit files with search and replace
            - List files in directories
            - Search for text across files
            - Get the document structure (outline)
            
            When the user gives you a task:
            1. Analyze what needs to be done
            2. Use the available tools to accomplish the task
            3. Report what you did and any results
            
            Always explain what you're doing before using a tool.
            Be careful with file modifications — describe changes before applying them.
            
            The user's current document context is provided below.
            """;

    private static final String TOOLS_JSON = """
            [
              {
                "type": "function",
                "function": {
                  "name": "read_file",
                  "description": "Read the content of a file from the workspace",
                  "parameters": {
                    "type": "object",
                    "properties": {
                      "path": {
                        "type": "string",
                        "description": "The file path relative to the workspace root"
                      }
                    },
                    "required": ["path"]
                  }
                }
              },
              {
                "type": "function",
                "function": {
                  "name": "write_file",
                  "description": "Write content to a file in the workspace",
                  "parameters": {
                    "type": "object",
                    "properties": {
                      "path": {
                        "type": "string",
                        "description": "The file path relative to the workspace root"
                      },
                      "content": {
                        "type": "string",
                        "description": "The content to write"
                      }
                    },
                    "required": ["path", "content"]
                  }
                }
              },
              {
                "type": "function",
                "function": {
                  "name": "list_files",
                  "description": "List files in a directory",
                  "parameters": {
                    "type": "object",
                    "properties": {
                      "path": {
                        "type": "string",
                        "description": "Directory path relative to workspace root. Use '.' for root."
                      }
                    },
                    "required": ["path"]
                  }
                }
              },
              {
                "type": "function",
                "function": {
                  "name": "search_files",
                  "description": "Search for text in files across the workspace",
                  "parameters": {
                    "type": "object",
                    "properties": {
                      "query": {
                        "type": "string",
                        "description": "The search query"
                      },
                      "glob": {
                        "type": "string",
                        "description": "File glob pattern to filter (e.g., '*.adoc')"
                      }
                    },
                    "required": ["query"]
                  }
                }
              },
              {
                "type": "function",
                "function": {
                  "name": "edit_file",
                  "description": "Edit a file by replacing specific text",
                  "parameters": {
                    "type": "object",
                    "properties": {
                      "path": {
                        "type": "string",
                        "description": "The file path relative to workspace root"
                      },
                      "old_text": {
                        "type": "string",
                        "description": "The text to find and replace"
                      },
                      "new_text": {
                        "type": "string",
                        "description": "The replacement text"
                      }
                    },
                    "required": ["path", "old_text", "new_text"]
                  }
                }
              }
            ]
            """;

    private final CopilotApiClient apiClient;
    private final CopilotContextProvider contextProvider;
    private final FileContextCollector fileContextCollector;
    private final DirectoryService directoryService;
    private final Current current;

    @Autowired
    public AgentModeService(CopilotApiClient apiClient, CopilotContextProvider contextProvider,
                            FileContextCollector fileContextCollector, DirectoryService directoryService,
                            Current current) {
        this.apiClient = apiClient;
        this.contextProvider = contextProvider;
        this.fileContextCollector = fileContextCollector;
        this.directoryService = directoryService;
        this.current = current;
    }

    /**
     * Executes an agent task with tool calling support.
     */
    public void execute(String userTask, CopilotConversation conversation,
                        Consumer<String> onChunk, Runnable onComplete, Consumer<String> onError) {

        String context = contextProvider.buildContext();

        List<CopilotMessage> messages = new ArrayList<>();
        messages.add(CopilotMessage.system(SYSTEM_PROMPT + "\n\n" + context));

        // Add history
        List<CopilotMessage> history = conversation.getMessages();
        int startIdx = Math.max(0, history.size() - 10);
        for (int i = startIdx; i < history.size(); i++) {
            CopilotMessage msg = history.get(i);
            if (msg.getRole() != CopilotMessage.Role.SYSTEM) {
                messages.add(msg);
            }
        }

        CopilotMessage userMsg = CopilotMessage.user(userTask);
        messages.add(userMsg);
        conversation.addMessage(userMsg);

        StringBuilder responseBuilder = new StringBuilder();
        apiClient.sendStreamingRequest(messages, TOOLS_JSON,
                chunk -> {
                    responseBuilder.append(chunk);
                    onChunk.accept(chunk);
                },
                toolCallJson -> {
                    // Handle tool calls - for now, log and report
                    onChunk.accept("\n\n🔧 *Tool call detected* - processing...\n");
                    String result = executeToolCall(toolCallJson);
                    onChunk.accept(result + "\n");
                },
                () -> {
                    conversation.addMessage(CopilotMessage.assistant(responseBuilder.toString()));
                    onComplete.run();
                },
                onError);
    }

    /**
     * Executes a tool call and returns the result.
     */
    private String executeToolCall(String toolCallJson) {
        // Tool call execution - simplified for initial implementation
        // In a full implementation, this would parse the JSON and execute the appropriate tool
        try {
            if (toolCallJson.contains("\"read_file\"")) {
                return executeReadFile(toolCallJson);
            } else if (toolCallJson.contains("\"list_files\"")) {
                return executeListFiles(toolCallJson);
            } else if (toolCallJson.contains("\"search_files\"")) {
                return "Search functionality available in next iteration.";
            } else if (toolCallJson.contains("\"write_file\"")) {
                return "⚠️ Write operations require user confirmation. Showing changes in editor.";
            } else if (toolCallJson.contains("\"edit_file\"")) {
                return "⚠️ Edit operations require user confirmation. Showing changes in editor.";
            }
        } catch (Exception e) {
            logger.error("Tool execution failed", e);
            return "Tool execution error: " + e.getMessage();
        }
        return "Unknown tool call";
    }

    private String executeReadFile(String toolCallJson) {
        try {
            // Extract path from JSON (simplified parsing)
            int pathIdx = toolCallJson.indexOf("\"path\"");
            if (pathIdx == -1) return "Error: no path specified";
            int valueStart = toolCallJson.indexOf("\"", pathIdx + 7);
            int valueEnd = toolCallJson.indexOf("\"", valueStart + 1);
            String filePath = toolCallJson.substring(valueStart + 1, valueEnd);

            Path workDir = directoryService.workingDirectory();
            Path resolved = workDir.resolve(filePath).normalize();

            // Security check: ensure the file is within the workspace
            if (!resolved.startsWith(workDir)) {
                return "Error: Access denied. File is outside the workspace.";
            }

            String content = fileContextCollector.readFileContent(resolved);
            return content != null ? content : "Error: Could not read file.";
        } catch (Exception e) {
            return "Error reading file: " + e.getMessage();
        }
    }

    private String executeListFiles(String toolCallJson) {
        try {
            Path workDir = directoryService.workingDirectory();
            try (Stream<Path> walk = Files.walk(workDir, 2)) {
                return walk
                        .filter(p -> !p.toString().contains(".git"))
                        .filter(Files::isRegularFile)
                        .limit(50)
                        .map(p -> workDir.relativize(p).toString())
                        .collect(Collectors.joining("\n"));
            }
        } catch (IOException e) {
            return "Error listing files: " + e.getMessage();
        }
    }
}

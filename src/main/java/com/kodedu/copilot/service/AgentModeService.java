package com.kodedu.copilot.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kodedu.copilot.api.CopilotApiClient;
import com.kodedu.copilot.context.CopilotContextProvider;
import com.kodedu.copilot.context.FileContextCollector;
import com.kodedu.copilot.model.CopilotConversation;
import com.kodedu.copilot.model.CopilotMessage;
import com.kodedu.other.Current;
import com.kodedu.service.DirectoryService;
import com.kodedu.service.EventService;
import com.kodedu.service.ThreadService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
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

    private static final int MAX_SEARCH_RESULTS = 50;
    private static final int MAX_LIST_FILES = 100;
    private static final int MAX_LIST_DEPTH = 3;
    private static final long MAX_WRITE_SIZE = 1_000_000; // 1MB write limit

    private static final String SYSTEM_PROMPT = """
            You are an autonomous agent assistant for the AsciidocFX editor.
            
            IMPORTANT: All file operations (read, write, edit, list, search) are relative to the user's \
            current working directory. When creating new files, always place them in the workspace root \
            or a logical subdirectory using relative paths (e.g., "README.adoc", "docs/guide.adoc"). \
            Never use absolute paths.
            
            You can perform the following actions using tools:
            - read_file: Read file content from the workspace
            - write_file: Create or overwrite a file in the workspace
            - edit_file: Edit a file by replacing specific text (search and replace)
            - list_files: List files in a directory
            - search_files: Search for text across workspace files
            - get_outline: Get the heading structure of an AsciiDoc document
            
            When the user gives you a task:
            1. Analyze what needs to be done
            2. Use the available tools to accomplish the task
            3. Report what you did and any results
            
            Always explain what you're doing before using a tool.
            Be careful with file modifications — describe changes before applying them.
            For new file creation, prefer .adoc extension for AsciiDoc content.
            
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
                        "description": "File path relative to the workspace root"
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
                  "description": "Create a new file or overwrite an existing file in the workspace. Parent directories are created automatically.",
                  "parameters": {
                    "type": "object",
                    "properties": {
                      "path": {
                        "type": "string",
                        "description": "File path relative to the workspace root (e.g., 'README.adoc', 'docs/guide.adoc')"
                      },
                      "content": {
                        "type": "string",
                        "description": "The full content to write to the file"
                      }
                    },
                    "required": ["path", "content"]
                  }
                }
              },
              {
                "type": "function",
                "function": {
                  "name": "edit_file",
                  "description": "Edit a file by finding and replacing the first occurrence of specific text. The old_text must match exactly.",
                  "parameters": {
                    "type": "object",
                    "properties": {
                      "path": {
                        "type": "string",
                        "description": "File path relative to the workspace root"
                      },
                      "old_text": {
                        "type": "string",
                        "description": "The exact text to find in the file"
                      },
                      "new_text": {
                        "type": "string",
                        "description": "The replacement text"
                      }
                    },
                    "required": ["path", "old_text", "new_text"]
                  }
                }
              },
              {
                "type": "function",
                "function": {
                  "name": "list_files",
                  "description": "List files and directories in a workspace path",
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
                  "description": "Search for text in files across the workspace. Returns matching file paths with line numbers and content.",
                  "parameters": {
                    "type": "object",
                    "properties": {
                      "query": {
                        "type": "string",
                        "description": "The text to search for (case-insensitive)"
                      },
                      "glob": {
                        "type": "string",
                        "description": "File glob pattern to filter files (e.g., '*.adoc', '*.md'). Omit to search all files."
                      }
                    },
                    "required": ["query"]
                  }
                }
              },
              {
                "type": "function",
                "function": {
                  "name": "get_outline",
                  "description": "Get the heading structure (outline) of an AsciiDoc document",
                  "parameters": {
                    "type": "object",
                    "properties": {
                      "path": {
                        "type": "string",
                        "description": "File path relative to the workspace root. If omitted, uses the current document."
                      }
                    },
                    "required": []
                  }
                }
              }
            ]
            """;

    private final CopilotApiClient apiClient;
    private final CopilotContextProvider contextProvider;
    private final FileContextCollector fileContextCollector;
    private final DirectoryService directoryService;
    private final EventService eventService;
    private final ThreadService threadService;
    private final Current current;
    private final ObjectMapper objectMapper;

    @Autowired
    public AgentModeService(CopilotApiClient apiClient, CopilotContextProvider contextProvider,
                            FileContextCollector fileContextCollector, DirectoryService directoryService,
                            EventService eventService, ThreadService threadService,
                            Current current, ObjectMapper objectMapper) {
        this.apiClient = apiClient;
        this.contextProvider = contextProvider;
        this.fileContextCollector = fileContextCollector;
        this.directoryService = directoryService;
        this.eventService = eventService;
        this.threadService = threadService;
        this.current = current;
        this.objectMapper = objectMapper;
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
                    onChunk.accept("\n\n🔧 *Tool call detected* — processing...\n");
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
        try {
            String toolName = extractToolName(toolCallJson);
            JsonNode arguments = extractArguments(toolCallJson);

            return switch (toolName) {
                case "read_file" -> executeReadFile(arguments);
                case "write_file" -> executeWriteFile(arguments);
                case "edit_file" -> executeEditFile(arguments);
                case "list_files" -> executeListFiles(arguments);
                case "search_files" -> executeSearchFiles(arguments);
                case "get_outline" -> executeGetOutline(arguments);
                default -> "Unknown tool: " + toolName;
            };
        } catch (Exception e) {
            logger.error("Tool execution failed", e);
            return "Tool execution error: " + e.getMessage();
        }
    }

    // ---- JSON Parsing Helpers ----

    /**
     * Extracts the tool/function name from a tool_calls SSE JSON chunk.
     */
    private String extractToolName(String toolCallJson) {
        try {
            JsonNode root = objectMapper.readTree(toolCallJson);
            // Navigate: choices[0].delta.tool_calls[0].function.name
            JsonNode choices = root.path("choices");
            if (choices.isArray() && !choices.isEmpty()) {
                JsonNode toolCalls = choices.get(0).path("delta").path("tool_calls");
                if (toolCalls.isArray() && !toolCalls.isEmpty()) {
                    JsonNode name = toolCalls.get(0).path("function").path("name");
                    if (!name.isMissingNode() && name.isTextual()) {
                        return name.asText();
                    }
                }
            }
        } catch (Exception e) {
            logger.debug("Could not parse tool name from JSON, falling back to string match", e);
        }
        // Fallback: simple string matching for robustness
        if (toolCallJson.contains("\"read_file\"")) return "read_file";
        if (toolCallJson.contains("\"write_file\"")) return "write_file";
        if (toolCallJson.contains("\"edit_file\"")) return "edit_file";
        if (toolCallJson.contains("\"list_files\"")) return "list_files";
        if (toolCallJson.contains("\"search_files\"")) return "search_files";
        if (toolCallJson.contains("\"get_outline\"")) return "get_outline";
        return "unknown";
    }

    /**
     * Extracts the arguments JSON from a tool_calls SSE JSON chunk.
     */
    private JsonNode extractArguments(String toolCallJson) {
        try {
            JsonNode root = objectMapper.readTree(toolCallJson);
            JsonNode choices = root.path("choices");
            if (choices.isArray() && !choices.isEmpty()) {
                JsonNode toolCalls = choices.get(0).path("delta").path("tool_calls");
                if (toolCalls.isArray() && !toolCalls.isEmpty()) {
                    JsonNode argsNode = toolCalls.get(0).path("function").path("arguments");
                    if (argsNode.isTextual()) {
                        return objectMapper.readTree(argsNode.asText());
                    } else if (argsNode.isObject()) {
                        return argsNode;
                    }
                }
            }
        } catch (Exception e) {
            logger.debug("Could not parse arguments from structured JSON, falling back to string extraction", e);
        }
        // Fallback: try to extract arguments as a top-level JSON object from the string
        return extractArgumentsFallback(toolCallJson);
    }

    /**
     * Fallback argument extraction using simple string parsing when structured parsing fails.
     */
    private JsonNode extractArgumentsFallback(String json) {
        try {
            // Try parsing the entire string as a JSON object with tool arguments
            JsonNode root = objectMapper.readTree(json);
            // Check if it already has argument fields directly
            if (root.has("path") || root.has("content") || root.has("query")) {
                return root;
            }
            // Check for "arguments" field
            JsonNode args = root.path("arguments");
            if (args.isTextual()) {
                return objectMapper.readTree(args.asText());
            }
            if (args.isObject()) {
                return args;
            }
        } catch (Exception e) {
            // JSON parsing failed — fallback returns empty object node
        }
        return objectMapper.createObjectNode();
    }

    // ---- Security Helpers ----

    /**
     * Resolves a relative path within the workspace and validates it is within bounds.
     * Returns null if the path is outside the workspace.
     */
    private Path resolveAndValidate(String relativePath) {
        Path workDir = directoryService.workingDirectory();
        Path resolved = workDir.resolve(relativePath).normalize();
        if (!resolved.startsWith(workDir)) {
            return null;
        }
        return resolved;
    }

    /**
     * Sends a working directory update event to refresh the file tree in the UI.
     */
    private void refreshFileTree() {
        threadService.runActionLater(() -> {
            try {
                directoryService.refreshWorkingDir();
            } catch (Exception e) {
                logger.debug("Could not refresh file tree", e);
            }
        });
    }

    // ---- Tool Implementations ----

    private String executeReadFile(JsonNode arguments) {
        String filePath = arguments.path("path").asText("");
        if (filePath.isEmpty()) {
            return "Error: no path specified.";
        }

        Path resolved = resolveAndValidate(filePath);
        if (resolved == null) {
            return "Error: Access denied. File is outside the workspace.";
        }

        String content = fileContextCollector.readFileContent(resolved);
        return content != null ? content : "Error: Could not read file '" + filePath + "'. File may not exist.";
    }

    private String executeWriteFile(JsonNode arguments) {
        String filePath = arguments.path("path").asText("");
        String content = arguments.path("content").asText("");

        if (filePath.isEmpty()) {
            return "Error: no path specified.";
        }
        if (content.length() > MAX_WRITE_SIZE) {
            return "Error: Content exceeds maximum write size of " + (MAX_WRITE_SIZE / 1024) + "KB.";
        }

        Path resolved = resolveAndValidate(filePath);
        if (resolved == null) {
            return "Error: Access denied. Target path is outside the workspace.";
        }

        try {
            // Create parent directories if they don't exist
            Path parentDir = resolved.getParent();
            if (parentDir != null && !Files.exists(parentDir)) {
                Files.createDirectories(parentDir);
            }

            boolean existed = Files.exists(resolved);
            Files.writeString(resolved, content);

            // Refresh file tree so the new/updated file is visible
            refreshFileTree();

            if (existed) {
                return "✅ File updated: " + filePath;
            } else {
                return "✅ File created: " + filePath;
            }
        } catch (IOException e) {
            logger.error("Failed to write file: {}", filePath, e);
            return "Error: Could not write file '" + filePath + "': " + e.getMessage();
        }
    }

    private String executeEditFile(JsonNode arguments) {
        String filePath = arguments.path("path").asText("");
        String oldText = arguments.path("old_text").asText("");
        String newText = arguments.path("new_text").asText("");

        if (filePath.isEmpty()) {
            return "Error: no path specified.";
        }
        if (oldText.isEmpty()) {
            return "Error: old_text must not be empty.";
        }

        Path resolved = resolveAndValidate(filePath);
        if (resolved == null) {
            return "Error: Access denied. File is outside the workspace.";
        }
        if (!Files.exists(resolved) || !Files.isRegularFile(resolved)) {
            return "Error: File '" + filePath + "' does not exist.";
        }

        try {
            String content = Files.readString(resolved);
            if (!content.contains(oldText)) {
                return "Error: The specified old_text was not found in '" + filePath + "'.";
            }

            // Replace only the first occurrence
            String updatedContent = content.replaceFirst(java.util.regex.Pattern.quote(oldText),
                    java.util.regex.Matcher.quoteReplacement(newText));
            Files.writeString(resolved, updatedContent);

            // Refresh file tree
            refreshFileTree();

            return "✅ File edited: " + filePath;
        } catch (IOException e) {
            logger.error("Failed to edit file: {}", filePath, e);
            return "Error: Could not edit file '" + filePath + "': " + e.getMessage();
        }
    }

    private String executeListFiles(JsonNode arguments) {
        String dirPath = arguments.path("path").asText(".");

        Path workDir = directoryService.workingDirectory();
        Path resolved = workDir.resolve(dirPath).normalize();

        if (!resolved.startsWith(workDir)) {
            return "Error: Access denied. Directory is outside the workspace.";
        }
        if (!Files.exists(resolved) || !Files.isDirectory(resolved)) {
            return "Error: Directory '" + dirPath + "' does not exist.";
        }

        try (Stream<Path> walk = Files.walk(resolved, MAX_LIST_DEPTH)) {
            String fileList = walk
                    .filter(p -> !p.equals(resolved))
                    .filter(p -> {
                        for (Path component : p) {
                            String name = component.toString();
                            if (".git".equals(name) || "node_modules".equals(name)) {
                                return false;
                            }
                        }
                        return true;
                    })
                    .limit(MAX_LIST_FILES)
                    .map(p -> {
                        String relative = workDir.relativize(p).toString();
                        return Files.isDirectory(p) ? relative + "/" : relative;
                    })
                    .sorted()
                    .collect(Collectors.joining("\n"));
            return fileList.isEmpty() ? "(empty directory)" : fileList;
        } catch (IOException e) {
            logger.error("Failed to list files: {}", dirPath, e);
            return "Error: Could not list files in '" + dirPath + "': " + e.getMessage();
        }
    }

    private String executeSearchFiles(JsonNode arguments) {
        String query = arguments.path("query").asText("");
        String globPattern = arguments.path("glob").asText("");

        if (query.isEmpty()) {
            return "Error: search query must not be empty.";
        }

        Path workDir = directoryService.workingDirectory();
        String queryLower = query.toLowerCase();
        List<String> results = new ArrayList<>();

        PathMatcher matcher = null;
        if (!globPattern.isEmpty()) {
            try {
                matcher = FileSystems.getDefault().getPathMatcher("glob:" + globPattern);
            } catch (Exception e) {
                return "Error: Invalid glob pattern '" + globPattern + "'.";
            }
        }
        final PathMatcher globMatcher = matcher;

        try {
            Files.walkFileTree(workDir, java.util.EnumSet.noneOf(FileVisitOption.class), MAX_LIST_DEPTH,
                    new SimpleFileVisitor<>() {
                        @Override
                        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                            if (results.size() >= MAX_SEARCH_RESULTS) {
                                return FileVisitResult.TERMINATE;
                            }
                            if (!attrs.isRegularFile() || attrs.size() > FileContextCollector.MAX_FILE_SIZE) {
                                return FileVisitResult.CONTINUE;
                            }
                            if (globMatcher != null && !globMatcher.matches(file.getFileName())) {
                                return FileVisitResult.CONTINUE;
                            }
                            try {
                                List<String> lines = Files.readAllLines(file);
                                String relativePath = workDir.relativize(file).toString();
                                for (int i = 0; i < lines.size() && results.size() < MAX_SEARCH_RESULTS; i++) {
                                    if (lines.get(i).toLowerCase().contains(queryLower)) {
                                        results.add(relativePath + ":" + (i + 1) + ": " + lines.get(i).trim());
                                    }
                                }
                            } catch (IOException e) {
                                logger.debug("Skipping unreadable file: {}", file, e);
                            }
                            return FileVisitResult.CONTINUE;
                        }

                        @Override
                        public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
                            if (dir.getFileName() != null &&
                                    (dir.getFileName().toString().equals(".git") ||
                                            dir.getFileName().toString().equals("node_modules"))) {
                                return FileVisitResult.SKIP_SUBTREE;
                            }
                            return FileVisitResult.CONTINUE;
                        }
                    });
        } catch (IOException e) {
            logger.error("Search failed", e);
            return "Error: Search failed: " + e.getMessage();
        }

        if (results.isEmpty()) {
            return "No matches found for '" + query + "'" +
                    (globPattern.isEmpty() ? "" : " with pattern '" + globPattern + "'") + ".";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("Found ").append(results.size()).append(" match(es)");
        if (results.size() >= MAX_SEARCH_RESULTS) {
            sb.append(" (results truncated)");
        }
        sb.append(":\n");
        for (String r : results) {
            sb.append("  ").append(r).append("\n");
        }
        return sb.toString();
    }

    private String executeGetOutline(JsonNode arguments) {
        String filePath = arguments.path("path").asText("");
        String content;

        if (filePath.isEmpty()) {
            // Use current document
            try {
                content = current.currentEditorValue();
                if (content == null || content.isEmpty()) {
                    return "No document is currently open.";
                }
            } catch (Exception e) {
                return "Error: Could not get current document content.";
            }
        } else {
            Path resolved = resolveAndValidate(filePath);
            if (resolved == null) {
                return "Error: Access denied. File is outside the workspace.";
            }
            content = fileContextCollector.readFileContent(resolved);
            if (content == null) {
                return "Error: Could not read file '" + filePath + "'.";
            }
        }

        // Parse AsciiDoc headings (= Title, == Section, === Subsection, etc.)
        // Check from longest prefix to shortest to avoid false matches
        String[] lines = content.split("\n");
        StringBuilder outline = new StringBuilder();
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i].trim();
            if (line.startsWith("===== ")) {
                outline.append("L").append(i + 1).append(":         ").append(line).append("\n");
            } else if (line.startsWith("==== ")) {
                outline.append("L").append(i + 1).append(":       ").append(line).append("\n");
            } else if (line.startsWith("=== ")) {
                outline.append("L").append(i + 1).append(":     ").append(line).append("\n");
            } else if (line.startsWith("== ")) {
                outline.append("L").append(i + 1).append(":   ").append(line).append("\n");
            } else if (line.startsWith("= ")) {
                outline.append("L").append(i + 1).append(": ").append(line).append("\n");
            }
        }

        return outline.isEmpty() ? "No headings found in the document." : outline.toString();
    }
}

package com.kodedu.copilot.context;

import com.kodedu.other.Current;
import com.kodedu.service.DirectoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.util.Optional;

/**
 * Provides context from the current editor state for Copilot requests.
 */
@Component
public class CopilotContextProvider {

    private static final Logger logger = LoggerFactory.getLogger(CopilotContextProvider.class);

    private final Current current;
    private final FileContextCollector fileContextCollector;
    private final WorkspaceContextCollector workspaceContextCollector;

    @Autowired
    public CopilotContextProvider(Current current,
                                  FileContextCollector fileContextCollector,
                                  WorkspaceContextCollector workspaceContextCollector) {
        this.current = current;
        this.fileContextCollector = fileContextCollector;
        this.workspaceContextCollector = workspaceContextCollector;
    }

    /**
     * Builds a context string from the current editor state.
     */
    public String buildContext() {
        StringBuilder context = new StringBuilder();

        try {
            // Current file path
            Optional<Path> currentPath = current.currentPath();
            currentPath.ifPresent(path -> {
                context.append("Current file: ").append(path.getFileName()).append("\n");
                context.append("File path: ").append(path).append("\n");
            });

            // Editor mode
            try {
                String mode = current.currentEditorMode();
                if (mode != null) {
                    context.append("File type: ").append(mode).append("\n");
                }
            } catch (Exception e) {
                // No active editor
            }

            // Selected text (if any)
            try {
                String selection = current.currentEditorSelection();
                if (selection != null && !selection.isEmpty()) {
                    context.append("\n--- Selected Text ---\n");
                    context.append(selection);
                    context.append("\n--- End Selected Text ---\n");
                }
            } catch (Exception e) {
                // No selection available
            }

            // Current document content
            try {
                String editorContent = current.currentEditorValue();
                if (editorContent != null && !editorContent.isEmpty()) {
                    context.append("\n--- Document Content ---\n");
                    context.append(editorContent);
                    context.append("\n--- End Document Content ---\n");
                }
            } catch (Exception e) {
                logger.debug("Could not get editor content", e);
            }

            // Workspace context
            String workspaceInfo = workspaceContextCollector.collectContext();
            if (workspaceInfo != null && !workspaceInfo.isEmpty()) {
                context.append("\n--- Workspace ---\n");
                context.append(workspaceInfo);
                context.append("\n--- End Workspace ---\n");
            }

        } catch (Exception e) {
            logger.debug("Error building context", e);
        }

        return context.toString();
    }

    /**
     * Builds a minimal context with just the selected text or current file.
     */
    public String buildMinimalContext() {
        StringBuilder context = new StringBuilder();
        try {
            String selection = current.currentEditorSelection();
            if (selection != null && !selection.isEmpty()) {
                context.append(selection);
            } else {
                String content = current.currentEditorValue();
                if (content != null) {
                    context.append(content);
                }
            }
        } catch (Exception e) {
            logger.debug("Error building minimal context", e);
        }
        return context.toString();
    }
}

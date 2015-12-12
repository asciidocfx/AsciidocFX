package com.kodcu.shell;

import com.kodcu.config.EditorConfigBean;
import com.kodcu.controller.ApplicationController;
import com.kodcu.service.DirectoryService;
import com.kodcu.service.ThreadService;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.Optional;

/**
 * Created by usta on 16.09.2015.
 */
@Component
@Scope("prototype")
@Lazy
public class ShellTab extends Tab {

    private VBox vertical = new VBox();
    private TextArea textArea = new TextArea();
    private TextField textField = new TextField();
    private TextField directoryField = new TextField();

    private LinkedList<String> commandHistory = new LinkedList<>();

    private Process process;
    private BufferedReader inputReader;
    private BufferedReader errorReader;
    private BufferedWriter outputWriter;

    private final ApplicationController controller;
    private final ThreadService threadService;
    private final DirectoryService directoryService;
    private final EditorConfigBean editorConfigBean;

    @Autowired
    public ShellTab(ApplicationController controller, ThreadService threadService, DirectoryService directoryService, EditorConfigBean editorConfigBean) {
        this.controller = controller;
        this.threadService = threadService;
        this.directoryService = directoryService;
        this.editorConfigBean = editorConfigBean;

        VBox.setVgrow(textArea, Priority.ALWAYS);
        vertical.getChildren().add(textArea);

        directoryField.setMinWidth(0);
        directoryField.setPrefColumnCount(0);
        directoryField.setEditable(false);

        directoryField.prefColumnCountProperty().bind(directoryField.lengthProperty().add(1));

        ContextMenu contextMenu = new ContextMenu();
        MenuItem newTab = new MenuItem("New");
        MenuItem closeTab = new MenuItem("Close");
        MenuItem closeOthers = new MenuItem("Close Others");
        MenuItem closeAll = new MenuItem("Close All");

        newTab.setOnAction(this.controller::newTerminal);
        closeTab.setOnAction(this.controller::closeTerminal);
        closeAll.setOnAction(this.controller::closeAllTerminal);
        closeOthers.setOnAction(this.controller::closeOtherTerminals);

        contextMenu.getItems().addAll(newTab, closeTab, closeOthers, closeAll);
        this.setContextMenu(contextMenu);

        textField.setStyle("-fx-background-color: seashell;");
        directoryField.setStyle("-fx-background-color: seashell;");

        HBox horizontal = new HBox(directoryField, textField);
        HBox.setHgrow(textField, Priority.ALWAYS);
        vertical.getChildren().add(horizontal);

        textArea.setWrapText(true);
        textArea.setEditable(false);

        textArea.textProperty().addListener((observable, oldValue, newValue) -> {
            threadService.runActionLater(() -> {
                textArea.setScrollTop(Double.MAX_VALUE);
            });
        });

        setContent(vertical);

        textField.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == KeyCode.UP) {
                String last = commandHistory.removeLast();
                commandHistory.addFirst(last);
                textField.setText(last);
            } else if (event.getCode() == KeyCode.DOWN) {
                String popped = commandHistory.removeFirst();
                commandHistory.addLast(popped);
                textField.setText(popped);
            }

            if (event.getCode() == KeyCode.UP || event.getCode() == KeyCode.DOWN) {
                threadService.runActionLater(() -> {
                    textField.positionCaret(textField.getText().length());
                }, true);
            }

        });

        textField.setOnAction(event -> {
            final String command = textField.getText();

            commandHistory.addLast(command);

            CommandChecker commandChecker = new CommandChecker(command.trim())
                    .checkCommand("clear", this::clearHistory)
                    .checkCommand("cls", this::clearHistory)
                    .checkCommand("exit", this::destroy)
                    .checkCommand("close", this::destroy);

            if (!commandChecker.isMatched()) {
                command(command);
            }

            textField.clear();
        });
    }

    public void initialize(Path terminalPath) {
        threadService.start(() -> {
            try {
                initializeProcess(terminalPath);
            } catch (Exception e) {
                // e.printStackTrace();
            }
        });
    }

    public void focusCommandInput() {
        threadService.runActionLater(() -> {
            textField.requestFocus();
        }, true);
    }

    public void print(String text) {

        threadService.runActionLater(() -> {
                    if (text.contains(">") || text.contains("$")) {
                        String[] split = text.split(">|\\$", 2);

                        if (split.length > 1) {
                            Optional<Path> optional = Optional.empty();
                            try {
                                optional = Optional.ofNullable(split[0])
                                        .map(Paths::get)
                                        .filter(Files::isDirectory)
                                        .filter(Files::exists);

                                if (optional.isPresent()) {
                                    directoryField.setText(split[0]);
                                }
                            } catch (Exception e) {
                            }

                            if (!optional.isPresent()) {
                                textArea.appendText(text + "\n");
                            }
                        }
                    } else {
                        textArea.appendText(text + "\n");
                    }
                }
        );
    }

    private void clearHistory() {
        textArea.clear();
    }

    public class CommandChecker {
        private final String text;
        private boolean matched;

        public CommandChecker(String text) {
            this.text = text;
        }

        public boolean isMatched() {
            return matched;
        }

        public CommandChecker checkCommand(String command, Runnable runnable) {
            if (!matched && text.equalsIgnoreCase(command)) {
                threadService.runActionLater(runnable);
                matched = true;
            }

            return this;
        }

    }

    public void command(String command) {
        threadService.start(() -> {
            try {
                outputWriter.write(command.trim());
                outputWriter.newLine();
                outputWriter.newLine();
                outputWriter.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private void initializeProcess(Path terminalPath) throws Exception {

        String os = System.getProperty("os.name").toLowerCase();

        String[] commands;
        if (os.contains("win")) {
            commands = editorConfigBean.getTerminalWinCommand().split("\\|");
        } else {
            commands = editorConfigBean.getTerminalNixCommand().split("\\|");
        }

        ProcessBuilder processBuilder = new ProcessBuilder(commands);
        processBuilder.redirectError(ProcessBuilder.Redirect.PIPE);
        processBuilder.redirectInput(ProcessBuilder.Redirect.PIPE);
        processBuilder.redirectOutput(ProcessBuilder.Redirect.PIPE);

        processBuilder.directory(directoryService.workingDirectory().toFile());

        Optional.ofNullable(terminalPath)
                .filter(Files::exists)
                .map(Path::toFile)
                .ifPresent(processBuilder::directory);

        this.process = processBuilder.start();

        Charset charset = Charset.forName(editorConfigBean.getTerminalCharset());
        this.inputReader = new BufferedReader(new InputStreamReader(process.getInputStream(), charset));
        this.errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream(), charset));
        this.outputWriter = new BufferedWriter(new OutputStreamWriter(process.getOutputStream(), charset));

        threadService.start(() -> {
            inputReader.lines().forEach(this::print);
        });

        threadService.start(() -> {
            errorReader.lines().forEach(this::print);
        });

        focusCommandInput();

        process.waitFor();
    }

    public void destroy() {

        ObservableList<Tab> tabs = this.getTabPane().getTabs();
        tabs.remove(this);

        Optional.ofNullable(process).ifPresent(Process::destroyForcibly);

    }

}

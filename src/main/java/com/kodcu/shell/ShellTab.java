package com.kodcu.shell;

import com.kodcu.component.WebkitCall;
import com.kodcu.config.EditorConfigBean;
import com.kodcu.controller.ApplicationController;
import com.kodcu.other.OSHelper;
import com.kodcu.service.DirectoryService;
import com.kodcu.service.ThreadService;
import com.pty4j.PtyProcess;
import com.pty4j.WinSize;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import netscape.javascript.JSObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.Collectors;

/**
 * Created by usta on 16.09.2015.
 */
@Component
@Scope("prototype")
@Lazy
public class ShellTab extends Tab {

    private WebView webView;

    private final ApplicationController controller;
    private final ThreadService threadService;
    private final DirectoryService directoryService;
    private final EditorConfigBean editorConfigBean;
    private PtyProcess process;
    private BufferedReader inputReader;
    private BufferedReader errorReader;
    private BufferedWriter outputWriter;
    private Path terminalPath;
    private boolean isReady = false;
    private String[] termCommand;
    private LinkedBlockingQueue<String> commandQueue = new LinkedBlockingQueue<>();

    public JSObject getWindow() {
        return (JSObject) webEngine().executeScript("window");
    }

    private WebEngine webEngine() {
        return webView.getEngine();
    }

    @Autowired
    public ShellTab(ApplicationController controller, ThreadService threadService, DirectoryService directoryService, EditorConfigBean editorConfigBean) {
        this.controller = controller;
        this.threadService = threadService;
        this.directoryService = directoryService;
        this.editorConfigBean = editorConfigBean;
    }


    public void initialize() {

        webView = new WebView();

        webView.getEngine().getLoadWorker().stateProperty().addListener((observable, oldValue, newValue) -> {
            getWindow().setMember("app", this);
        });

        threadService.runActionLater(() -> {
            webEngine().load(String.format("http://localhost:%d/afx/resource/hterm.html", controller.getPort()));
        });

        VBox box = new VBox(webView);
        box.setPadding(new Insets(5));
        setContent(box);

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
    }

    @WebkitCall
    public void resizeTerminal(int columns, int rows) {
        if (Objects.nonNull(process)) {
            threadService.runActionLater(() -> {
                process.setWinSize(new WinSize(columns, rows));
                process.setWinSize(new WinSize(columns, rows, (int) webView.getWidth(), (int) webView.getHeight()));
            }, true);
        }
    }

    @WebkitCall
    public void onTerminalReady() {

        threadService.start(() -> {
            isReady = true;
            try {
                initializeProcess();
            } catch (Exception e) {
                // e.printStackTrace();
            }
        });
    }

    @WebkitCall
    public void command(String command) throws InterruptedException {
        commandQueue.put(command);
        threadService.start(() -> {
            try {
                outputWriter.write(commandQueue.poll());
                outputWriter.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public void print(String... texts) {
        threadService.runActionLater(() -> {
            for (String text : texts) {
                getTerminalIO().call("print", text);
            }
        });

    }

    public void focusCursor() {
        threadService.runActionLater(() -> {
            webView.requestFocus();
            getTerminal().call("focus");
        }, true);
    }

    private JSObject getTerminal() {
        return (JSObject) webEngine().executeScript("t");
    }
    private JSObject getTerminalIO() {
        return (JSObject) webEngine().executeScript("t.io");
    }


    private void initializeProcess() throws Exception {

        if (OSHelper.isWindows()) {
            this.termCommand = editorConfigBean.getTerminalWinCommand().split("\\s+");
        } else {
            this.termCommand = editorConfigBean.getTerminalNixCommand().split("\\s+");
        }

        Map<String, String> envs = new HashMap<>(System.getenv());
        envs.put("TERM", "xterm");

        System.setProperty("PTY_LIB_FOLDER", controller.getConfigPath().resolve("libpty").toString());

        String charset = detectTerminalCharacter();

        if (Objects.nonNull(terminalPath) && Files.exists(terminalPath)) {
            this.process = PtyProcess.exec(termCommand, envs, terminalPath.toString());
        } else {
            this.process = PtyProcess.exec(termCommand, envs);
        }

        process.setWinSize(new WinSize(100, 20));
        this.inputReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        this.errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
        this.outputWriter = new BufferedWriter(new OutputStreamWriter(process.getOutputStream(), charset));

        threadService.start(() -> {
            printReader(inputReader);
        });

        threadService.start(() -> {
            printReader(errorReader);
        });

        focusCursor();

        process.waitFor();
    }

    private String detectTerminalCharacter() {

        String charset = "UTF-8";

        if (OSHelper.isWindows()) {
            return windowsCmdCharset().orElse(charset);
        } else {
            return unixTerminalCharset().orElse(charset);
        }

    }

    private void printReader(BufferedReader bufferedReader) {
        try {
            int nRead;
            char[] data = new char[10];

            while ((nRead = bufferedReader.read(data, 0, data.length)) != -1) {
                String[] strings = CharBuffer.wrap(data)
                        .chars()
                        .limit(nRead)
                        .mapToObj(e -> String.valueOf((char) e))
                        .toArray(size -> new String[size]);
                print(strings);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        /* try {
            int data = bufferedReader.read();
            while (data != -1) {
                print(data);
                data = bufferedReader.read();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }*/
    }

    public void destroy() {
        threadService.start(() -> {
            process.destroy();
        });
    }

    public void closeTab() {
        threadService.runActionLater(() -> {
            ObservableList<Tab> tabs = this.getTabPane().getTabs();
            tabs.remove(this);

            destroy();
        });
    }

    public Optional<String> unixTerminalCharset() {

        final String[] charset = {null};

        try {
            Process process = Runtime.getRuntime().exec(new String[]{termCommand[0], "-c", "locale charmap"});

            String result = new BufferedReader(new InputStreamReader(process.getInputStream()))
                    .lines().collect(Collectors.joining("\n"))
                    .trim();

            if (!result.isEmpty()) {
                charset[0] = result;
            }
        } catch (Exception e) {
            return Optional.empty();
        }

        return Optional.ofNullable(charset[0]);
    }

    public Optional<String> windowsCmdCharset() {

        final String[] charset = {null};

        try {
            Process process = Runtime.getRuntime().exec(new String[]{termCommand[0], "/C", "chcp"});

            String result = new BufferedReader(new InputStreamReader(process.getInputStream()))
                    .lines().collect(Collectors.joining("\n"))
                    .split(":")[1]
                    .trim();

            if (!result.isEmpty()) {
                Integer chcp = Integer.valueOf(result);
                charset[0] = "CP" + chcp;
            }
        } catch (Exception e) {
            return Optional.empty();
        }

        return Optional.ofNullable(charset[0]);
    }


    public void setTerminalPath(Path terminalPath) {
        this.terminalPath = terminalPath;
    }

    public Path getTerminalPath() {
        return terminalPath;
    }

    public boolean isReady() {
        return isReady;
    }
}

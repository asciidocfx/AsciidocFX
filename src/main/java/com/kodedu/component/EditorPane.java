package com.kodedu.component;

import com.kodedu.config.EditorConfigBean;
import com.kodedu.config.FoldStyle;
import com.kodedu.config.SpellcheckConfigBean;
import com.kodedu.controller.ApplicationController;
import com.kodedu.keyboard.KeyHelper;
import com.kodedu.other.IOHelper;
import com.kodedu.service.DirectoryService;
import com.kodedu.service.ParserService;
import com.kodedu.service.ThreadService;
import com.kodedu.service.convert.markdown.MarkdownService;
import com.kodedu.service.extension.AsciiTreeGenerator;
import com.kodedu.service.shortcut.ShortcutProvider;
import com.kodedu.service.ui.TabService;
import com.kodedu.spell.dictionary.Token;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.scene.control.*;
import javafx.scene.input.Dragboard;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.util.Callback;
import netscape.javascript.JSObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by usta on 09.04.2015.
 */
@Component
@Scope("prototype")
public class EditorPane extends AnchorPane {

    private final WebView webView;
    private final Logger logger = LoggerFactory.getLogger(EditorPane.class);
    private final ApplicationController controller;
    private final EditorConfigBean editorConfigBean;
    private final ThreadService threadService;
    private final ShortcutProvider shortcutProvider;
    private final ApplicationContext applicationContext;
    private final TabService tabService;
    private final AsciiTreeGenerator asciiTreeGenerator;
    private final ParserService parserService;
    private final SpellcheckConfigBean spellcheckConfigBean;
    private final ObservableList<Runnable> handleReadyTasks;
    private String mode = "ace/mode/asciidoc";
    private String initialEditorValue = "";
    private Path path;
    private FileTime lastModifiedTime;
    private static String lastInterPath;
    private final String escapeBackSlash = "(?<!\\\\)"; // ignores if word started with \
    private final String ignoreSuffix = "(?<!\\\\)"; // ignores if word started with \
    private final BooleanProperty ready = new SimpleBooleanProperty(false);
    private final ObjectProperty<Path> spellLanguage = new SimpleObjectProperty<>();
    private final AtomicBoolean contextOpen = new AtomicBoolean(false);

    private final BooleanProperty changedProperty = new SimpleBooleanProperty(false);

    @Value("${application.live.url}")
    private String liveUrl;

    @Value("${application.preview.url}")
    private String previewUrl;

    private final DirectoryService directoryService;
    private ContextMenu contextMenu;
    private Number pageX;
    private Number pageY;

    @Autowired
    public EditorPane(ApplicationController controller, EditorConfigBean editorConfigBean, ThreadService threadService, ShortcutProvider shortcutProvider, ApplicationContext applicationContext, TabService tabService, AsciiTreeGenerator asciiTreeGenerator, ParserService parserService, SpellcheckConfigBean spellcheckConfigBean, DirectoryService directoryService) {
        this.setVisible(false);
        this.controller = controller;
        this.editorConfigBean = editorConfigBean;
        this.threadService = threadService;
        this.shortcutProvider = shortcutProvider;
        this.applicationContext = applicationContext;
        this.tabService = tabService;
        this.asciiTreeGenerator = asciiTreeGenerator;
        this.spellcheckConfigBean = spellcheckConfigBean;
        this.directoryService = directoryService;
        this.handleReadyTasks = FXCollections.observableArrayList();
        this.parserService = parserService;
        this.webView = new WebView();
        this.ready.addListener(this::afterEditorReady);
        webEngine().setConfirmHandler(this::handleConfirm);
        initializeMargins();
        initializeEditorContextMenus();
    }

    private Boolean handleConfirm(String param) {
        if ("command:ready".equals(param)) {
            afterEditorLoaded();
        }
        return false;
    }

    private void afterEditorLoaded() {
        getWindow().setMember("afx", controller);
        getWindow().setMember("editorPane", this);
        updateOptions();

        if (Objects.nonNull(path)) {
            threadService.runTaskLater(() -> {
                final String content = IOHelper.readFile(path);
                setLastModifiedTime(IOHelper.getLastModifiedTime(path));
                threadService.runActionLater(() -> {
                    changeEditorMode();
                    setInitialized();
                    setEditorValue(content);
                    resetUndoManager();
                    ready.setValue(true);
                });
            });
        } else {
            setInitialized();
            setEditorValue(initialEditorValue);
            resetUndoManager();
            ready.setValue(true);
        }

        this.getChildren().add(webView);
        webView.requestFocus();
    }

    private void afterEditorReady(ObservableValue observable, boolean oldValue, boolean newValue) {
        if (newValue) {
            ObservableList<Runnable> runnables = FXCollections.observableArrayList(handleReadyTasks);
            handleReadyTasks.clear();
            for (Runnable runnable : runnables) {
                runnable.run();
            }

            updatePreviewUrl();
        }
    }

    public void updatePreviewUrl() {
        final String interPath = directoryService.interPath();

        final boolean isSameInterPath = Optional.ofNullable(interPath)
                .filter(i -> !i.isEmpty())
                .filter(i -> i.equals(lastInterPath))
                .isPresent();

        if (Objects.isNull(lastInterPath)) {
            lastInterPath = interPath;
            return;
        }

        if (isSameInterPath) {
            this.rerender();
            return;
        }

        threadService.runActionLater(() -> {
            if (is("asciidoc") || is("markdown")) {
                applicationContext.getBean(HtmlPane.class)
                        .load(String.format(previewUrl, controller.getPort(), lastInterPath = interPath));
            } else if (is("html")) {
                applicationContext.getBean(LiveReloadPane.class)
                        .load(String.format(liveUrl, controller.getPort(), lastInterPath = interPath));
            }
        }, true);
    }

    private void updateOptions() {
        webEngine().executeScript("updateOptions()");
    }

    private void setInitialized() {
        webEngine().executeScript("setInitialized()");
    }

    private void resetUndoManager() {
        webEngine().executeScript("resetUndoManager()");
    }

    private void initializeMargins() {
        AnchorPane.setBottomAnchor(this, 0D);
        AnchorPane.setTopAnchor(this, 0D);
        AnchorPane.setLeftAnchor(this, 0D);
        AnchorPane.setRightAnchor(this, 0D);
        VBox.setVgrow(this, Priority.ALWAYS);
        AnchorPane.setBottomAnchor(webView, 0D);
        AnchorPane.setTopAnchor(webView, 0D);
        AnchorPane.setLeftAnchor(webView, 0D);
        AnchorPane.setRightAnchor(webView, 0D);
        VBox.setVgrow(webView, Priority.ALWAYS);
    }

    public void load(String url) {
        if (Objects.nonNull(url))
            threadService.runActionLater(() -> {
                webEngine().load(url);
            }, true);
        else
            logger.error("Url is not loaded. Reason: null reference");
    }

    public String getLocation() {
        return webEngine().getLocation();
    }

    public Object call(String methodName, Object... args) {
        return getWindow().call(methodName, args);
    }

    public WebEngine webEngine() {
        return webView.getEngine();
    }

    public WebView getWebView() {
        return webView;
    }

    public void confirmHandler(Callback<String, Boolean> confirmHandler) {
        webEngine().setConfirmHandler(confirmHandler);
    }

    public JSObject getWindow() {
        return (JSObject) webEngine().executeScript("window");
    }

    public String getEditorValue() {
        return (String) webEngine().executeScript("editor.getValue()");
    }

    public void setEditorValue(String value) {
        threadService.runActionLater(() -> {
            getWindow().setMember("editorValue", value);
            webEngine().executeScript("setEditorValue(editorValue)");
            getWebView().requestFocus();
            updateFoldStyle();
        });


    }

    @WebkitCall(from = "editor")
    public void onThemeLoaded() {
        if (!isVisible()) {
            setVisible(true);
            getWebView().requestFocus();
        }
    }

    public void switchMode(Object... args) {
        threadService.runActionLater(() -> {
            this.call("switchMode", args);
        });

        updateFoldStyle();
    }

    public void rerender(Object... args) {
        try {
            webEngine().executeScript("rerender()");
        } catch (Exception e) {
            // no-op
        }
    }

    public void focus() {
        webView.requestFocus();
    }

    public void moveCursorTo(Integer lineno) {

        if (Objects.nonNull(lineno)) {

            final Optional<ViewPanel> viewPanelOptional = controller.getRightShowerHider().getShowing();

            viewPanelOptional.ifPresent(ViewPanel::disableScrollingAndJumping);


            try {
                webEngine().executeScript(String.format("editor.gotoLine(%d,3,false)", (lineno)));
                webEngine().executeScript(String.format("editor.scrollToLine(%d,false,false,function(){})", (lineno - 1)));
            } catch (Exception e) {
                logger.error("Error occured while moving cursor to line {}", lineno);
            }

            viewPanelOptional.ifPresent(ViewPanel::enableScrollingAndJumping);

        }
    }

    public void changeEditorMode() {
        if (Objects.nonNull(path)) {
            String mode = (String) webEngine().executeScript(String.format("changeEditorMode(\"%s\")", path.toUri().toString()));
            setMode(mode);
        }
        updateFoldStyle();
    }

    public String editorMode() {
        return (String) webEngine().executeScript("editorMode()");
    }

    public void fillModeList(ObservableList modeList) {
        threadService.runActionLater(() -> {
            this.call("fillModeList", modeList);
        });
    }

    public boolean is(String mode) {
        return ("ace/mode/" + mode).equalsIgnoreCase(this.mode);
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public String getMode() {
        return mode;
    }

    public void setTheme(String theme) {
        threadService.runActionLater(() -> {
            this.call("changeTheme", theme);
        });
    }

    public void setFontSize(int fontSize) {
        threadService.runActionLater(() -> {
            this.call("changeFontSize", fontSize);
        });
    }

    public void setShowGutter(Boolean showGutter) {
        threadService.runActionLater(() -> {
            this.call("setShowGutter", showGutter);
        });
    }

    public void setUseWrapMode(Boolean useWrapMode) {
        threadService.runActionLater(() -> {
            this.call("setUseWrapMode", useWrapMode);
        });
    }

    public void setWrapLimitRange(Integer wrapLimitRange) {
        threadService.runActionLater(() -> {
            this.call("setWrapLimitRange", wrapLimitRange);
        });
    }


    public void insert(String text) {
        threadService.runActionLater(() -> {
            JSObject editor = (JSObject) webEngine().executeScript("editor");
            editor.call("insert", text);
        });
    }

    public void execCommand(String command) {
        threadService.runActionLater(() -> {
            JSObject editor = (JSObject) webEngine().executeScript("editor");
            editor.call("execCommand", command);
        });
    }

    public String editorSelection() {
        return (String) webEngine().executeScript("editor.session.getTextRange(editor.getSelectionRange())");
    }

    public void initializeEditorContextMenus() {

        webView.setContextMenuEnabled(false);
        contextMenu = new ContextMenu();

        MenuItem cut = MenuItemBuilt.item("Cut").click(e -> {
            controller.cutCopy(editorSelection());
            execCommand("cut");
        });
        MenuItem copy = MenuItemBuilt.item("Copy").click(e -> {
            controller.cutCopy(editorSelection());
        });
        MenuItem pasteConverted = MenuItemBuilt.item("Paste converted").click(e -> {
            controller.paste();
        });
        MenuItem paste = MenuItemBuilt.item("Paste").click(e -> {
            controller.pasteRaw();
        });
        MenuItem indexSelection = MenuItemBuilt.item("Index selection").click(e -> {
            shortcutProvider.getProvider().addIndexSelection();
        });
        MenuItem includeAsSubDocument = MenuItemBuilt.item("Include selection").click(e -> {
            shortcutProvider.getProvider().includeAsSubdocument();
        });
        MenuItem replacements = MenuItemBuilt.item("Apply Replacements").click(this::replaceSubs);
        MenuItem markdownToAsciidoc = MenuItemBuilt.item("Markdown to Asciidoc").click(e -> {
            MarkdownService markdownService = applicationContext.getBean(MarkdownService.class);
            markdownService.convertToAsciidoc(getEditorValue(),
                    content -> threadService.runActionLater(() -> {
                        tabService.newDoc(content);
                    }));
        });


        final Menu editorLanguage = new Menu("Editor language");
        final Menu defaultLanguage = new Menu("Default language");

        ToggleGroup editorLanguageGroup = new ToggleGroup();
        ToggleGroup defaultLanguageGroup = new ToggleGroup();

        final RadioMenuItem disableSpeller = CheckItemBuilt.check("Disable spell check", false)
                .bindBi(spellcheckConfigBean.disableSpellCheckProperty())
                .click(e -> {
                    checkSpelling();
                })
                .build();

        Menu languageMenu = new Menu("Spell Checker");
        languageMenu.getItems().addAll(editorLanguage, defaultLanguage, disableSpeller);

        EventHandler<Event> contextMenuRequested = event -> {

            final ObservableList<MenuItem> contextMenuItems = contextMenu.getItems();

            final List<MenuItem> menuItems = Arrays.asList(cut, copy, paste, pasteConverted,
                    markdownToAsciidoc,
                    replacements,
                    indexSelection,
                    includeAsSubDocument,
                    languageMenu);

            for (MenuItem menuItem : menuItems) {
                if (!contextMenuItems.contains(menuItem)) {
                    contextMenuItems.add(menuItem);
                }
            }

            if (editorLanguage.getItems().isEmpty()) {

                editorLanguage.getItems().add(CheckItemBuilt.check("Use default language", true)
                        .click(e -> {
                            setSpellLanguage(null);
                            checkSpelling();
                        })
                        .group(editorLanguageGroup)
                        .build());

                final ObservableList<Path> languages = spellcheckConfigBean.getLanguages();

                for (Path language : languages) {
                    final String pathCleanName = IOHelper.getPathCleanName(language);
                    editorLanguage.getItems()
                            .add(CheckItemBuilt.check(pathCleanName, false)
                                    .click(e -> {
                                        setSpellLanguage(language);
                                        checkSpelling();
                                    })
                                    .group(editorLanguageGroup)
                                    .build());


                    defaultLanguage.getItems()
                            .add(CheckItemBuilt.check(pathCleanName, spellcheckConfigBean.defaultLanguageProperty().isEqualTo(language).get())
                                    .click(e -> {
                                        spellcheckConfigBean.setDefaultLanguage(language);
                                        checkSpelling();
                                    })
                                    .group(defaultLanguageGroup)
                                    .build());
                }

            }

            if (contextMenu.isShowing()) {
                contextMenu.hide();
            }

            markdownToAsciidoc.setVisible(isMarkdown());
            indexSelection.setVisible(isAsciidoc());

            if (event instanceof MouseEvent) {
                MouseEvent mouseEvent = (MouseEvent) event;
                contextMenu.show(getWebView(), mouseEvent.getScreenX(), mouseEvent.getScreenY() + 20);
                contextOpen.set(true);
            } else {
                updateCursorCoordinates();
                Bounds bounds = getWebView().localToScreen(getWebView().getLayoutBounds());

                contextMenu.show(getWebView(), pageX.doubleValue() + bounds.getMinX(), pageY.doubleValue() + bounds.getMinY() + 35);
                contextOpen.set(true);
            }

            checkWordSuggestions();

        };

        getWebView().addEventHandler(KeyEvent.KEY_PRESSED, event -> {
            if (KeyHelper.isContextMenu(event)) {
                event.consume();
                contextMenuRequested.handle(event);
                return;
            }

            if (contextMenu.isShowing()) {
                contextMenu.hide();
            }
        });

        getWebView().addEventFilter(KeyEvent.ANY, event -> {
            if (contextOpen.get()) {
                if (KeyHelper.isEnter(event)) {
                    event.consume();
                }
            }
        });

        contextMenu.setOnHidden(event -> {
            threadService.runActionLater(() -> {
                contextOpen.set(false);
            }, true);
        });

        getWebView().setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.SECONDARY) {
                event.consume();
                contextMenuRequested.handle(event);
            } else {
                if (contextMenu.isShowing()) {
                    contextMenu.hide();
                }
            }
        });

        getWebView().setOnDragDropped(event -> {
            Dragboard dragboard = event.getDragboard();
            boolean success = false;

            if (dragboard.hasFiles() && !dragboard.hasString()) {

                List<File> dragboardFiles = dragboard.getFiles();

                if (dragboardFiles.size() == 1) {
                    Path path = dragboardFiles.get(0).toPath();
                    if (Files.isDirectory(path)) {

                        threadService.runTaskLater(() -> {
                            StringBuffer buffer = new StringBuffer();
                            buffer.append("[tree,file=\"\"]");
                            buffer.append("\n--\n");
                            buffer.append(asciiTreeGenerator.generate(path));
                            buffer.append("\n--");
                            threadService.runActionLater(() -> {
                                insert(buffer.toString());
                            });
                        });

                        success = true;
                    }
                }

                Optional<String> block = parserService.toImageBlock(dragboardFiles);
                if (block.isPresent()) {
                    insert(block.get());
                    success = true;
                } else {
                    block = parserService.toIncludeBlock(dragboardFiles);
                    if (block.isPresent()) {
                        insert(block.get());
                        success = true;
                    }
                }

            }

            if (dragboard.hasHtml() && !success) {
                Optional<String> block = parserService.toWebImageBlock(dragboard.getHtml());
                if (block.isPresent()) {
                    insert(block.get());
                    success = true;
                }
            }

            if (dragboard.hasString() && !success) {
                insert(dragboard.getString());
                success = true;
            }

            event.setDropCompleted(success);
            event.consume();
        });
    }

    private void checkWordSuggestions() {
        webEngine().executeScript("checkWordSuggestions()");
    }

    private void checkSpelling() {
        webEngine().executeScript("checkSpelling()");
    }

    private String getSelectionOrAll() {
        return (String) webEngine().executeScript("getSelectionOrAll()");
    }

    private void replaceSubs(Event event) {

        String selection = getSelectionOrAll();

        threadService.runTaskLater(() -> {
            String result = controller.applyReplacements(selection);

            if (Objects.equals(selection, result))
                return;

            threadService.runActionLater(() -> {
                setEditorValue(getEditorValue().replace(selection, result));
            });
        });

    }

    @WebkitCall(from = "editor")
    public void appendWildcard() {
        threadService.runActionLater(() -> {
            setChangedProperty(true);
        });
    }

    public ObservableList<Runnable> getHandleReadyTasks() {
        return handleReadyTasks;
    }

    public void setInitialEditorValue(String initialEditorValue) {
        this.initialEditorValue = initialEditorValue;
    }

    public boolean isMarkdown() {
        return is("markdown");
    }

    public boolean isAsciidoc() {
        return is("asciidoc");
    }

    public Path getPath() {
        return path;
    }

    public void setPath(Path path) {
        this.path = path;
    }

    public FileTime getLastModifiedTime() {
        return lastModifiedTime;
    }

    public void setLastModifiedTime(FileTime lastModifiedTime) {
        this.lastModifiedTime = lastModifiedTime;
    }

    public boolean isHTML() {
        return is("html");
    }

    public boolean getReady() {
        return ready.get();
    }

    public BooleanProperty readyProperty() {
        return ready;
    }

    public Path getSpellLanguage() {
        return spellLanguage.get();
    }

    public ObjectProperty<Path> spellLanguageProperty() {
        return spellLanguage;
    }

    public void setSpellLanguage(Path spellLanguage) {
        this.spellLanguage.set(spellLanguage);
    }

    public void removeToLineStart() {
        webEngine().executeScript("editor.removeToLineStart()");
    }

    public void addTypo(Token token) {
        String tokenClass = token.isEmptySuggestion() ? "misspelled" : "misspelled-strong";

        webEngine().executeScript(String.format("addTypo(%d,%d,%d,\"%s\")",
                token.getRow(),
                token.getStart(),
                token.getEnd(),
                tokenClass));
    }

    public void showSuggestions(List<String> suggestions) {
        final ObservableList<MenuItem> contextMenuItems = contextMenu.getItems();

        contextMenuItems.removeIf(m -> m.getStyleClass().contains("spell-suggestion"));

        if (suggestions.isEmpty()) {
            return;
        }

        final List<MenuItem> spells = new ArrayList<>();

        for (String suggestion : suggestions) {
            final MenuItem menuItem = MenuItemBuilt.item(suggestion)
                    .clazz("spell-suggestion").click(event -> {
                        this.replaceMisspelled(suggestion);
                    });

            spells.add(menuItem);
        }

        final SeparatorMenuItem menuItem = new SeparatorMenuItem();
        menuItem.getStyleClass().add("spell-suggestion");
        spells.add(menuItem);

        contextMenuItems.addAll(0, spells);
    }

    private void replaceMisspelled(String suggestion) {
        webEngine().executeScript(String.format("replaceMisspelled(\"%s\")", suggestion));
    }

    public String tokenList() {
        return (String) webEngine().executeScript("JSON.stringify(getTokenList())");
    }

    public void updateFoldStyle() {
        FoldStyle foldStyle = editorConfigBean.getFoldStyle();
        setFoldStyle(foldStyle);
    }

    public void setFoldStyle(FoldStyle style) {

        if (Objects.isNull(style)) {
            return;
        }

        threadService.runActionLater(() -> {
            this.call("setFoldStyle", style.name().toLowerCase(Locale.ENGLISH));
        });
    }

    public void updateCursorCoordinates() {
        if (ready.get()) {
            JSObject coordinates = (JSObject) this.call("getCursorCoordinates");
            this.pageX = (Number) coordinates.getMember("pageX");
            this.pageY = (Number) coordinates.getMember("pageY");
        }
    }

    public boolean getChangedProperty() {
        return changedProperty.get();
    }

    public BooleanProperty changedPropertyProperty() {
        return changedProperty;
    }

    public void setChangedProperty(boolean changedProperty) {
        this.changedProperty.set(changedProperty);
    }
}

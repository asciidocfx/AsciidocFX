package com.kodedu.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.install4j.api.launcher.ApplicationLauncher;
import com.kodedu.animation.GifExporterFX;
import com.kodedu.boot.AppStarter;
import com.kodedu.component.*;
import com.kodedu.config.*;
import com.kodedu.engine.AsciidocConverterProvider;
import com.kodedu.engine.AsciidocNashornConverter;
import com.kodedu.engine.AsciidocWebkitConverter;
import com.kodedu.keyboard.KeyHelper;
import com.kodedu.logging.MyLog;
import com.kodedu.logging.TableViewLogAppender;
import com.kodedu.other.*;
import com.kodedu.outline.Section;
import com.kodedu.service.*;
import com.kodedu.service.convert.DocumentConverter;
import com.kodedu.service.convert.GitbookToAsciibookService;
import com.kodedu.service.convert.docbook.DocBookConverter;
import com.kodedu.service.convert.ebook.EpubConverter;
import com.kodedu.service.convert.ebook.MobiConverter;
import com.kodedu.service.convert.html.HtmlBookConverter;
import com.kodedu.service.convert.markdown.MarkdownService;
import com.kodedu.service.convert.odf.ODFConverter;
import com.kodedu.service.convert.slide.SlideConverter;
import com.kodedu.service.extension.MathJaxService;
import com.kodedu.service.extension.PlantUmlService;
import com.kodedu.service.extension.TreeService;
import com.kodedu.service.extension.chart.ChartProvider;
import com.kodedu.service.shortcut.ShortcutProvider;
import com.kodedu.service.table.AsciidocTableController;
import com.kodedu.service.ui.FileBrowseService;
import com.kodedu.service.ui.IndikatorService;
import com.kodedu.service.ui.TabService;
import com.kodedu.service.ui.TooltipTimeFixService;
import com.kodedu.spell.dictionary.DictionaryService;
import com.kodedu.terminalfx.TerminalBuilder;
import com.kodedu.terminalfx.TerminalTab;
import com.kodedu.terminalfx.config.TerminalConfig;
import com.sun.javafx.stage.StageHelper;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.HostServices;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTreeCell;
import javafx.scene.image.Image;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import netscape.javascript.JSObject;
import org.apache.commons.io.IOUtils;
import org.kordamp.ikonli.fontawesome.FontAwesome;
import org.kordamp.ikonli.javafx.FontIcon;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.springframework.web.util.UriComponentsBuilder;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.CodeSource;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.nio.file.StandardOpenOption.*;
import static java.util.Objects.nonNull;

@Component
public class ApplicationController extends TextWebSocketHandler implements Initializable {

    public Label goUpLabel;
    public VBox terminalLeftBox;
    public TabPane terminalTabPane;
    public ToggleButton workdirToggle;
    public ShowerHider leftShowerHider;
    public ShowerHider rightShowerHider;
    public ShowerHider bottomShowerHider;
    public HBox terminalHBox;
    public SplitPane mainVerticalSplitPane;
    public ToggleButton logToggleButton;
    public ToggleButton terminalToggleButton;
    public ToggleGroup leftToggleGroup;
    public VBox rightTooglesBox;
    public VBox configBox;
    public ToggleGroup rightToggleGroup;
    public ToggleButton toggleConfigButton;
    public Label basicSearch;
    public Button newTerminalButton;
    public Button closeTerminalButton;
    public Button recordTerminalButton;
    private Logger logger = LoggerFactory.getLogger(ApplicationController.class);
    public Label odfPro = new Label();
    public VBox logVBox;
    public Label statusText;
    public SplitPane editorSplitPane;
    public Label statusMessage;
    public MenuItem newFolder;
    public MenuItem newSlide;
    public Menu newMenu;
    public ProgressBar progressBar;
    public Menu favoriteDirMenu;
    public MenuItem addToFavoriteDir;
    public MenuItem afxVersionItem;
    public MenuItem renameFile;
    public MenuItem newFile;
    public TabPane tabPane;
    public SplitPane splitPane;
    public SplitPane splitPaneVertical;
    public TreeView<Item> fileSystemView;
    public Label workingDirButton;
    public Label refreshLabel;
    public AnchorPane rootAnchor;
    public ProgressIndicator indikator;
    public ListView<Item> recentListView;
    public MenuItem openFileTreeItem;
    public MenuItem deletePathItem;
    public MenuItem openFolderTreeItem;
    public MenuItem openFileListItem;
    public MenuItem openFolderListItem;
    public MenuItem copyPathListItem;
    public MenuItem copyTreeItem;
    public MenuItem copyListItem;
    public MenuButton leftButton;
    public Label htmlPro;
    public Label pdfPro;
    public Label ebookPro;
    public Label docbookPro;
    public Label browserPro;
    public VBox previewBox;
    public SeparatorMenuItem renameSeparator;
    public SeparatorMenuItem addToFavSeparator;
    private AnchorPane markdownTableAnchor;
    private Stage markdownTableStage;
    public TreeView<Section> outlineTreeView;

    private Path userHome = IOHelper.getPath(System.getProperty("user.home"));

    private TreeSet<Section> outlineList = new TreeSet<>();
    private ObservableList<DocumentMode> modeList = FXCollections.observableArrayList();

    private final Pattern bookArticleHeaderRegex
            = Pattern.compile("^:doctype:.*(book|article)", Pattern.MULTILINE);

    private final Pattern forceIncludeRegex
            = Pattern.compile("^:forceinclude:", Pattern.MULTILINE);

    private BooleanProperty stopRendering = new SimpleBooleanProperty(false);

    private AtomicBoolean includeAsciidocResource = new AtomicBoolean(false);

    private static ObservableList<MyLog> logList = FXCollections.synchronizedObservableList(FXCollections.observableArrayList());

    @Autowired
    public HtmlPane htmlPane;

    @Autowired
    public AsciidocWebkitConverter asciidocWebkitConverter;

    @Autowired
    private EditorConfigBean editorConfigBean;

    @Autowired
    private TerminalConfigBean terminalConfigBean;

    @Autowired
    private LocationConfigBean locationConfigBean;

    @Autowired
    private PreviewConfigBean previewConfigBean;

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private ConfigurationService configurationService;

    @Autowired
    private AsciidocTableController asciidocTableController;

    @Autowired
    private TreeService treeService;

    @Autowired
    private TooltipTimeFixService tooltipTimeFixService;

    @Autowired
    private TabService tabService;

    @Autowired
    private ODFConverter odfConverter;

    @Autowired
    private PlantUmlService plantUmlService;

    @Autowired
    private MathJaxService mathJaxService;

    @Autowired
    private DocBookConverter docBookConverter;

    @Autowired
    private HtmlBookConverter htmlBookService;

    @Autowired
    @Qualifier("pdfBookConverter")
    private DocumentConverter pdfBookConverter;

    @Autowired
    private EpubConverter epubConverter;

    @Autowired
    private Current current;

    @Autowired
    private FileBrowseService fileBrowser;

    @Autowired
    private IndikatorService indikatorService;

    @Autowired
    private MobiConverter mobiConverter;

    @Autowired
    private SampleBookService sampleBookService;

    @Autowired
    private Environment environment;

    @Autowired
    private ParserService parserService;

    @Autowired
    private DirectoryService directoryService;

    @Autowired
    private ThreadService threadService;

    @Autowired
    private ShortcutProvider shortcutProvider;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private Base64.Encoder base64Encoder;

    @Autowired
    private ChartProvider chartProvider;

    @Autowired
    private DictionaryService dictionaryService;

    @Autowired
    private SpellcheckConfigBean spellcheckConfigBean;

    private Stage stage;
    private List<WebSocketSession> sessionList = new ArrayList<>();
    private Scene scene;
    private AnchorPane asciidocTableAnchor;
    private Stage asciidocTableStage;

    private int port = 8080;
    private Path configPath;

    @Value("${application.version}")
    private String version;

    @Value("${application.issue}")
    private String issuePage;
    @Value("${application.forum}")
    private String issueForum;
    @Value("${application.gitter}")
    private String gitterChat;
    @Value("${application.github}")
    private String githubPage;

    @Autowired
    private SlideConverter slideConverter;

    @Autowired
    private SlidePane slidePane;
    private Path installationPath;

    private String logPath;

    @Value("${application.config.folder}")
    private String configDirName;

    @Autowired
    private LiveReloadPane liveReloadPane;
    private List<String> supportedModes;

    @Autowired
    private FileWatchService fileWatchService;

    private Timeline progressBarTimeline = null;

    @Autowired
    private StoredConfigBean storedConfigBean;

    @Autowired
    private AsciidocConverterProvider converterProvider;

    @Value("${application.worker.url}")
    private String workerUrl;

    @Value("${application.preview.url}")
    private String previewUrl;

    @Value("${application.mathjax.url}")
    private String mathjaxUrl;

    @Autowired
    private AsciidocNashornConverter nashornEngineConverter;

    @Value("${application.live.url}")
    private String liveUrl;

    private ConverterResult lastConverterResult;
    private HostServices hostServices;

    @Value("${application.donation}")
    private String donationUrl;
    private ToggleGroup configToggleGroup;
    private Scene asciidocTableScene;
    private Scene markdownTableScene;

    public void createAsciidocTable() {
        asciidocTableStage.showAndWait();
    }

    public void createMarkdownTable() {
        markdownTableStage.showAndWait();
    }

    @FXML
    private void fullScreen(ActionEvent event) {
        getStage().setFullScreen(!getStage().isFullScreen());
    }

    @FXML
    private void directoryView(ActionEvent event) {
        splitPane.setDividerPositions(0.1610294117647059, 0.5823529411764706);
    }

    private void generatePdf() {
        this.generatePdf(false);
    }

    private void generatePdf(boolean askPath) {

        if (!current.currentPath().isPresent()) {
            saveDoc();
        }

        threadService.runTaskLater(() -> {
            pdfBookConverter.convert(askPath);
        });
    }

    @FXML
    private void generateSampleBook(ActionEvent event) {

        DirectoryChooser directoryChooser = directoryService.newDirectoryChooser("Select a New Directory for sample book");
        File file = directoryChooser.showDialog(null);
        threadService.runTaskLater(() -> {
            sampleBookService.produceSampleBook(configPath, file.toPath());
            directoryService.setWorkingDirectory(Optional.of(file.toPath()));
            fileBrowser.browse(file.toPath());
            threadService.runActionLater(() -> {
                directoryView(null);
                tabService.addTab(file.toPath().resolve("book.adoc"));
            });
        });
    }

    public void convertDocbook() {
        convertDocbook(false);
    }

    public void convertDocbook(boolean askPath) {

        if (!current.currentPath().isPresent()) {
            saveDoc();
        }

        threadService.runTaskLater(() -> {

            indikatorService.startProgressBar();

            Path docbookPath = directoryService.getSaveOutputPath(ExtensionFilters.DOCBOOK, askPath);

            Consumer<String> step = docbook -> {
                final String finalDocbook = docbook;
                threadService.runTaskLater(() -> {
                    IOHelper.writeToFile(docbookPath, finalDocbook, CREATE, TRUNCATE_EXISTING, WRITE);
                });
                threadService.runActionLater(() -> {
                    ObservableList<Item> recentFiles = storedConfigBean.getRecentFiles();
                    recentFiles.remove(new Item(docbookPath));
                    recentFiles.add(0, new Item(docbookPath));
                });
                indikatorService.stopProgressBar();
            };

            docBookConverter.convert(false, step);

        });

    }

    private void convertEpub() {
        convertEpub(false);
    }

    private void convertEpub(boolean askPath) {
        threadService.runTaskLater(() -> {
            epubConverter.produceEpub3(askPath);
        });
    }

    @WebkitCall(from = "asciidoctor-math")
    public void math(String formula, String type, String imagesDir, String imageTarget, String nodename) {

        mathJaxService.processFormula(formula, imagesDir, imageTarget);
    }

    @WebkitCall(from = "mathjax.html")
    public void snapshotFormula(String formula, String imagesDir, String imageTarget) {
        mathJaxService.snapshotFormula(formula, imagesDir, imageTarget);
    }

    private void convertMobi() {
        convertMobi(false);
    }

    private void convertMobi(boolean askPath) {

        if (nonNull(locationConfigBean.getKindlegen())) {
            if (!Files.exists(IOHelper.getPath(locationConfigBean.getKindlegen()))) {
                locationConfigBean.setKindlegen(null);
            }
        }

        if (Objects.isNull(locationConfigBean.getKindlegen())) {
            FileChooser fileChooser = directoryService.newFileChooser("Select 'kindlegen' executable");
            File kindlegenFile = fileChooser.showOpenDialog(null);
            if (Objects.isNull(kindlegenFile)) {
                return;
            }

            locationConfigBean.setKindlegen(kindlegenFile.toPath().toString());
        }

        threadService.runTaskLater(() -> {
            mobiConverter.convert(askPath);
        });

    }

    private void generateHtml() {
        this.generateHtml(false);
    }

    private void generateHtml(boolean askPath) {

        if (!current.currentPath().isPresent()) {
            this.saveDoc();
        }

        threadService.runTaskLater(() -> {
            htmlBookService.convert(askPath);
        });
    }

    public void tree(String content, String type, String imagesDir, String imageTarget, String nodename) {
        if (content.split("#").length > content.split("\\|-").length) {
            createFileTree(content, type, imagesDir, imageTarget, nodename);
        } else {
            createHighlightFileTree(content, type, imagesDir, imageTarget, nodename);
        }
    }

    public void createFileTree(String tree, String type, String imagesDir, String imageTarget, String nodename) {

        threadService.runTaskLater(() -> {
            treeService.createFileTree(tree, type, imagesDir, imageTarget, nodename);
        });
    }

    public void createHighlightFileTree(String tree, String type, String imagesDir, String imageTarget, String nodename) {

        threadService.runTaskLater(() -> {
            treeService.createHighlightFileTree(tree, type, imagesDir, imageTarget, nodename);
        });
    }

    @FXML
    public void refreshWorkingDir() {

        Optional<Path> currentPath = current.currentPath().map(Path::getParent);

        if (currentPath.isPresent()) {
            directoryService.changeWorkigDir(currentPath.get());
        } else {
            fileBrowser.refresh();
        }

    }

    @FXML
    public void goHome() {
        directoryService.changeWorkigDir(userHome);
    }

    @WebkitCall
    public void imageToBase64Url(final String url, final int index) {

        threadService.runTaskLater(() -> {
            try {
                byte[] imageBuffer = restTemplate.getForObject(url, byte[].class);
                String imageBase64 = base64Encoder.encodeToString(imageBuffer);
                htmlPane.updateBase64Url(index, imageBase64);
            } catch (Exception e) {
                logger.error("Problem occured while converting image to base64 for {}", url);
            }
        });
    }

    public void stageWidthChanged(ObservableValue observable, Number oldValue, Number newValue) {
        if (!terminalToggleButton.isSelected() && !logToggleButton.isSelected()) {
            mainVerticalSplitPane.setDividerPosition(0, 1);
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {

    }

    public void initializeApp() {

        port = Integer.parseInt(environment.getProperty("local.server.port"));

        checkDuplicatedJars();
        initializeNashornConverter();
        initializeTerminal();

        terminalTabPane.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            Optional.ofNullable(newValue)
                    .map(e -> ((TerminalTab) e))
                    .filter(TerminalTab::isTerminalReady)
                    .ifPresent(TerminalTab::focusCursor);
        });

        Arrays.asList(htmlPane, slidePane, liveReloadPane).forEach(viewPanel -> VBox.getVgrow(viewPanel));

        threadService.runTaskLater(() -> {
            while (true) {
                try {
                    renderLoop();
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                }
            }
        });

        progressBar.prefWidthProperty().bind(rightShowerHider.widthProperty());

        progressBarTimeline = new Timeline(
                new KeyFrame(
                        Duration.ZERO,
                        new KeyValue(progressBar.progressProperty(), 0)
                ),
                new KeyFrame(
                        Duration.seconds(15),
                        new KeyValue(progressBar.progressProperty(), 1)
                ));

        threadService.runActionLater(() -> {
            rightShowerHider.setMaster(htmlPane);
        }, true);

        initializeLogViewer();
        initializeDoctypes();

        tooltipTimeFixService.fix();

        basicSearch.visibleProperty().bind(fileSystemView.focusedProperty().and(basicSearch.textProperty().isNotEmpty()));
        basicSearch.visibleProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                basicSearch.setText("");
            }
        });

        AtomicBoolean dontKeyType = new AtomicBoolean(true);
        fileSystemView.addEventHandler(KeyEvent.KEY_PRESSED, event -> {
            dontKeyType.set(true);

            if (KeyHelper.isBackSpace(event)) {
                if (basicSearch.isVisible()) {
                    event.consume();
                    Optional.ofNullable(basicSearch.getText())
                            .filter(t -> t.length() > 0)
                            .map(t -> t.substring(0, t.length() - 1))
                            .ifPresent(s -> {
                                basicSearch.setText(s);
                                fileBrowser.searchAndSelect(basicSearch.getText());
                            });
                }
            } else if (KeyHelper.isUp(event)) {
                if (basicSearch.isVisible()) {
                    event.consume();
                    fileBrowser.searchDownAndSelect(basicSearch.getText());
                }
            } else if (KeyHelper.isDown(event)) {
                if (basicSearch.isVisible()) {
                    event.consume();
                    fileBrowser.searchUpAndSelect(basicSearch.getText());
                }
            } else if (KeyHelper.isDelete(event)) {
                event.consume();
                deleteSelectedItems(event);
            } else if (KeyHelper.isCopy(event)) {
                event.consume();
                this.copyFiles(tabService.getSelectedTabPaths());
            } else if (KeyHelper.isF2(event)) {
                event.consume();
                this.renameFile(event);
            } else if (KeyHelper.isEnter(event)) {
                event.consume();
                TreeItem<Item> selectedItem = fileSystemView.getSelectionModel().getSelectedItem();

                if (Objects.isNull(selectedItem)) {
                    return;
                }

                Path selectedPath = selectedItem.getValue().getPath();
                tabService.previewDocument(selectedPath);
            }

            if (event.isConsumed()) {
                dontKeyType.set(false);
            }

        });

        fileSystemView.addEventHandler(KeyEvent.KEY_TYPED, event -> {

            if (dontKeyType.get()) {
                String eventText = Optional.ofNullable(event.getText())
                        .filter(e -> !e.isEmpty())
                        .orElseGet(() -> {
                            String character = event.getCharacter();
                            char[] chars = character.toCharArray();
                            if (chars.length > 0) {
                                String text = new String(chars);
                                if (chars.length == 1) {
                                    if (chars[0] == ' ') {
                                        return text;
                                    }
                                    return text.trim();
                                } else {
                                    return text;
                                }
                            }
                            return character;
                        });

                if (!eventText.isEmpty()) {
                    event.consume();
                    basicSearch.setText(basicSearch.getText() + eventText);
                    fileBrowser.searchAndSelect(basicSearch.getText());
                }
            }

        });

        afxVersionItem.setText(String.join(" ", "Version", version));

        ContextMenu htmlProMenu = new ContextMenu();
        htmlProMenu.getStyleClass().add("build-menu");
        htmlPro.setContextMenu(htmlProMenu);
        htmlPro.setOnMouseClicked(event -> {
            htmlProMenu.show(htmlPro, event.getScreenX(), 50);
        });
        htmlProMenu.getItems().add(MenuItemBuilt.item("Save").click(event -> {
            this.generateHtml();
        }));
        htmlProMenu.getItems().add(MenuItemBuilt.item("Save as").click(event -> {
            this.generateHtml(true);
        }));
        htmlProMenu.getItems().add(MenuItemBuilt.item("Copy source").tip("Copy HTML source").click(event -> {
            this.cutCopy(lastConverterResult.getRendered());
        }));
        htmlProMenu.getItems().add(MenuItemBuilt.item("Clone source").tip("Copy HTML source (Embedded images)").click(event -> {
            htmlPane.call("imageToBase64Url", new Object[]{});
        }));

        ContextMenu pdfProMenu = new ContextMenu();
        pdfProMenu.getStyleClass().add("build-menu");
        pdfProMenu.getItems().add(MenuItemBuilt.item("Save").click(event -> {
            this.generatePdf();
        }));
        pdfProMenu.getItems().add(MenuItemBuilt.item("Save as").click(event -> {
            this.generatePdf(true);
        }));
        pdfPro.setContextMenu(pdfProMenu);

        pdfPro.setOnMouseClicked(event -> {
            pdfProMenu.show(pdfPro, event.getScreenX(), 50);
        });

        ContextMenu docbookProMenu = new ContextMenu();
        docbookProMenu.getStyleClass().add("build-menu");
        docbookProMenu.getItems().add(MenuItemBuilt.item("Save").click(event -> {
            this.convertDocbook();
        }));
        docbookProMenu.getItems().add(MenuItemBuilt.item("Save as").click(event -> {
            this.convertDocbook(true);
        }));

        docbookPro.setContextMenu(docbookProMenu);

        docbookPro.setOnMouseClicked(event -> {
            docbookProMenu.show(docbookPro, event.getScreenX(), 50);
        });

        ContextMenu ebookProMenu = new ContextMenu();
        ebookProMenu.getStyleClass().add("build-menu");
        ebookProMenu.getItems().add(MenuBuilt.name("Mobi")
                .add(MenuItemBuilt.item("Save").click(event -> {
                    this.convertMobi();
                }))
                .add(MenuItemBuilt.item("Save as").click(event -> {
                    this.convertMobi(true);
                })).build());

        ebookProMenu.getItems().add(MenuBuilt.name("Epub")
                .add(MenuItemBuilt.item("Save").click(event -> {
                    this.convertEpub();
                }))
                .add(MenuItemBuilt.item("Save as").click(event -> {
                    this.convertEpub(true);
                })).build());

        ebookPro.setOnMouseClicked(event -> {
            ebookProMenu.show(ebookPro, event.getScreenX(), 50);
        });

        ebookPro.setContextMenu(ebookProMenu);

        ContextMenu odfProMenu = new ContextMenu();
        odfProMenu.getStyleClass().add("build-menu");
        odfProMenu.setAutoHide(true);
        odfProMenu.getItems().add(MenuItemBuilt.item("Save").click(event -> {
            odfProMenu.hide();
            this.generateODFDocument();
        }));
        odfProMenu.getItems().add(MenuItemBuilt.item("Save as").click(event -> {
            odfProMenu.hide();
            this.generateODFDocument(true);
        }));

        odfPro.setContextMenu(odfProMenu);

        odfPro.setOnMouseClicked(event -> {
            odfProMenu.show(odfPro, event.getScreenX(), 50);
        });

        browserPro.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                this.externalBrowse();
            }
        });

        fileSystemView.setCellFactory(param -> {
            TreeCell<Item> cell = new TextFieldTreeCell<Item>();
            cell.setOnDragDetected(event -> {
                Dragboard db = cell.startDragAndDrop(TransferMode.ANY);
                ClipboardContent content = new ClipboardContent();
                content.putFiles(Arrays.asList(cell.getTreeItem().getValue().getPath().toFile()));
                db.setContent(content);
            });
            return cell;
        });

        liveReloadPane.webEngine().setOnAlert(event -> {
            if ("LIVE_LOADED".equals(event.getData())) {
                liveReloadPane.setMember("afx", this);
//                current.currentEditor().rerender();
            }
        });

        htmlPane.webEngine().setOnAlert(event -> {
            if ("PREVIEW_LOADED".equals(event.getData())) {
                htmlPane.setMember("afx", this);
                current.currentEditor().rerender();
            }
        });

        asciidocWebkitConverter.webEngine().setOnAlert(event -> {
            if ("WORKER_LOADED".equals(event.getData())) {
                asciidocWebkitConverter.setMember("afx", this);
                htmlPane.load(String.format(previewUrl, port, directoryService.interPath()));
            }
        });

        asciidocWebkitConverter.load(String.format(workerUrl, port));

        openFileTreeItem.setOnAction(event -> {

            ObservableList<TreeItem<Item>> selectedItems = fileSystemView.getSelectionModel().getSelectedItems();

            selectedItems.stream()
                    .map(e -> e.getValue())
                    .map(e -> e.getPath())
                    .filter(path -> {
                        if (selectedItems.size() == 1) {
                            return true;
                        }
                        return !Files.isDirectory(path);
                    })
                    .forEach(tabService::previewDocument);
        });

        deletePathItem.setOnAction(this::deleteSelectedItems);

        openFolderTreeItem.setOnAction(event -> {
            Path path = tabService.getSelectedTabPath();
            path = Files.isDirectory(path) ? path : path.getParent();
            if (nonNull(path)) {
                openInDesktop(path);
            }
        });

        openFolderListItem.setOnAction(event -> {
            Path path = recentListView.getSelectionModel().getSelectedItem().getPath();
            path = Files.isDirectory(path) ? path : path.getParent();
            if (nonNull(path)) {
                openInDesktop(path);
            }
        });

        openFileListItem.setOnAction(this::openRecentListFile);

        copyPathListItem.setOnAction(event -> {
            this.cutCopy(recentListView.getSelectionModel().getSelectedItem().getPath().toString());
        });

        copyTreeItem.setOnAction(event -> {
            this.copyFiles(tabService.getSelectedTabPaths());
        });

        copyListItem.setOnAction(event -> {
            this.copyFiles(recentListView.getSelectionModel()
                    .getSelectedItems().stream()
                    .map(e -> e.getPath()).collect(Collectors.toList()));
        });

        fileSystemView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        fileSystemView.setOnMouseClicked(event -> {
            TreeItem<Item> selectedItem = fileSystemView.getSelectionModel().getSelectedItem();

            if (Objects.isNull(selectedItem)) {
                return;
            }

            event.consume();

            Path selectedPath = selectedItem.getValue().getPath();
            if (event.getButton() == MouseButton.PRIMARY) {
                if (event.getClickCount() >= 2) {
                    tabService.previewDocument(selectedPath);
                }
            }
        });

        fileSystemView.getSelectionModel().getSelectedIndices().addListener((ListChangeListener<? super Integer>) p -> {

            ObservableList<TreeItem<Item>> selectedItems = fileSystemView.getSelectionModel().getSelectedItems();

            if (Objects.isNull(selectedItems)) {
                return;
            }

            if (selectedItems.size() > 1) {
                renameFile.setVisible(false);
                newMenu.setVisible(false);
                addToFavoriteDir.setVisible(false);
                renameSeparator.setVisible(false);
                if (favoriteDirMenu.getItems().size() > 0) {
                    addToFavSeparator.setVisible(true);
                } else {
                    addToFavSeparator.setVisible(false);
                }
            } else if (selectedItems.size() == 1) {
                TreeItem<Item> itemTreeItem = selectedItems.get(0);
                Item value = itemTreeItem.getValue();
                Path path = value.getPath();

                Optional<Path> optional = Optional.ofNullable(selectedItems)
                        .filter(e -> e.size() > 0)
                        .map(e -> e.get(0).getValue())
                        .map(Item::getPath);

                if (!optional.isPresent()) {
                    return;
                }

                boolean isDirectory = Files.isDirectory(path);
                newMenu.setVisible(isDirectory);
                renameFile.setVisible(!isDirectory);
                renameSeparator.setVisible(true);
                addToFavoriteDir.setVisible(isDirectory);
                if (favoriteDirMenu.getItems().size() == 0 && !isDirectory) {
                    addToFavSeparator.setVisible(false);
                } else {
                    addToFavSeparator.setVisible(true);
                }
                ObservableList<String> favoriteDirectories = storedConfigBean.getFavoriteDirectories();
                if (favoriteDirectories.size() > 0) {
                    boolean inFavorite = favoriteDirectories.contains(path.toString());
                    addToFavoriteDir.setDisable(inFavorite);
                }
            }
        });

        tabService.initializeTabChangeListener(tabPane);
        threadService.runActionLater(() -> {
            detachStage = new Stage();
            detachStage.setTitle("AsciidocFX Preview");
            detachStage.initModality(Modality.WINDOW_MODAL);
            InputStream logoStream = AppStarter.class.getResourceAsStream("/logo.png");
            detachStage.getIcons().add(new Image(logoStream));
            IOUtils.closeQuietly(logoStream);
            detachStage.setOnCloseRequest(e -> {
                if (stage.isShowing()) {
                    ViewPanel.setMarkDetached();
                    e.consume();
                }
            });

            editorConfigBean.detachedPreviewProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue) {
                    detachPreview();
                } else {
                    attachPreview();
                }
            });

            if (editorConfigBean.isDetachedPreview()) {
                detachPreview();
            }

        }, true);

        this.checkNewVersion();
    }

    private Stage detachStage;

    public void detachPreview() {
        if (detachStage != null) {
            if (!detachStage.isShowing()) {
                splitPane.getItems().remove(previewBox);
                AnchorPane anchorPane = new AnchorPane();
                fitToParent(anchorPane);
                anchorPane.getChildren().add(previewBox);
                Scene scene = new Scene(anchorPane);
                detachStage.setScene(scene);
                applyCurrentTheme(detachStage);
                applyDetachedStagePosition(detachStage);
                detachStage.show();
            } else {
                logger.debug("Already detached");
            }
        } else {
            logger.error("Detach stage is not created yet");
        }
    }

    private void attachPreview() {
        if (detachStage != null) {
            if (detachStage.isShowing()) {
                splitPane.getItems().add(previewBox);
                detachStage.close();
                detachStage.hide();
            } else {
                logger.debug("Already attached");
            }
        } else {
            logger.error("Detach stage is not created yet");
        }
    }

    private void applyDetachedStagePosition(Stage stage) {
        double screenX = editorConfigBean.getPreviewScreenX();
        double screenY = editorConfigBean.getPreviewScreenY();
        double screenHeight = editorConfigBean.getPreviewScreenHeight();
        double screenWidth = editorConfigBean.getPreviewScreenWidth();

        if (nonNull(screenX)) {
            stage.setX(screenX);
        }

        if (nonNull(screenY)) {
            stage.setY(screenY);
        }

        if (nonNull(screenWidth)) {
            if (screenWidth != 0) {
                stage.setWidth(screenWidth);
            }
        }

        if (nonNull(screenHeight)) {
            if (screenHeight != 0) {
                stage.setHeight(screenHeight);
            }
        }

    }


    private void checkDuplicatedJars() {

        threadService.runTaskLater(() -> {
            try {
                Path lib = getInstallationPath().resolve("lib");
//            Path lib = IOHelper.getPath("C:\\Program Files\\AsciidocFX\\lib");

                if (Files.notExists(lib)) {
                    return;
                }

                Map<String, List<Path>> dependencies = IOHelper.list(lib)
                        .collect(Collectors.groupingBy(path -> {
                            Path fileName = path.getFileName();
                            String name = fileName.toString();

                            LinkedList<String> nameParts = new LinkedList<String>(Arrays.asList(name.split("-")));
//                        String lastPart = nameParts.get(nameParts.size() - 1);
//                        String version = lastPart.replaceAll("\\p{Alpha}", "");
                            nameParts.removeLast();
                            return String.join("-", nameParts);
                        }));

                List<String> duplicatePaths = dependencies
                        .entrySet()
                        .stream()
                        .filter(e -> e.getValue().size() > 1)
                        .map(e -> e.getValue())
                        .map(e -> {
                            return String.join("\n", e.stream().map(Path::toString).collect(Collectors.toList())) + "\n";
                        })
                        .collect(Collectors.toList());

                if (duplicatePaths.size() > 0) {
                    threadService.runActionLater(() -> {
                        AlertHelper.showDuplicateWarning(duplicatePaths, lib);
                    });
                }
            } catch (Exception e) {

            }
        });

    }

    private void deleteSelectedItems(Event event) {
        List<TreeItem<Item>> selectedItems = fileSystemView
                .getSelectionModel()
                .getSelectedItems()
                .stream()
                .collect(Collectors.toList());

        List<Path> pathList = selectedItems.stream()
                .map(e -> e.getValue())
                .map(e -> e.getPath())
                .collect(Collectors.toList());

        AlertHelper.deleteAlert(pathList).ifPresent(btn -> {
            if (btn == ButtonType.YES) {

                fileSystemView.setDisable(true);

                threadService.runTaskLater(() -> {
                    try {
                        boolean hasDirectory = false;
                        for (TreeItem<Item> selectedItem : selectedItems) {
//                            int selectedIndex = fileBrowser.findIndex(selectedItem);
                            Path path = selectedItem.getValue().getPath();

                            if (Files.isDirectory(path)) {

                                if (!hasDirectory) {
                                    hasDirectory = true;
                                    fileWatchService.unRegisterAllPath();
                                }

                                if (path.getRoot().equals(path)) {
                                    threadService.runActionLater(() -> {
                                        AlertHelper.okayAlert("You can't delete fileystem root");
                                    });
                                    continue;
                                }

                                IOHelper.deleteDirectory(path);
                            } else {
                                IOHelper.deleteIfExists(path);
                            }

//                            fileSystemView.getSelectionModel().select(selectedIndex);
                        }

                        if (hasDirectory) {
                            fileBrowser.refresh();
                        }
                    } catch (Exception ex) {
                        logger.error(ex.getMessage(), ex);
                    }

                    threadService.runActionLater(() -> {
                        fileSystemView.setDisable(false);
                        fileSystemView.requestFocus();
                    });
                });

            }

        });
    }

    public void openInDesktop(Path path) {
        try {
            hostServices.showDocument(path.toUri().toString());
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    public void browseInDesktop(String url) {
        try {
            hostServices.showDocument(UriComponentsBuilder.fromUriString(url).build().toUri().toASCIIString());
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    private ScheduledFuture<?> scheduledFuture = null;

    private void initializeTerminal() {

        terminalTabPane.getTabs().addListener(new ListChangeListener<Tab>() {
            @Override
            public void onChanged(Change<? extends Tab> c) {

                while (c.next()) {
                    recordTerminalButton.setDisable(c.getList().isEmpty());
                }

            }
        });

        recordTerminalButton.setOnAction(e -> {
            if (Objects.isNull(scheduledFuture)) {
                Tooltip.install(recordTerminalButton, new Tooltip("Stop"));
                recordTerminalButton.setGraphic(new FontIcon(FontAwesome.STOP_CIRCLE_O));
                scheduledFuture = this.recordTerminal(e);
            } else {
                Tooltip.install(recordTerminalButton, new Tooltip("Record"));
                recordTerminalButton.setGraphic(new FontIcon(FontAwesome.PLAY_CIRCLE));
                scheduledFuture.cancel(false);
                scheduledFuture = null;
            }
        });

        newTerminalButton.setOnAction(this::newTerminal);
        closeTerminalButton.setOnAction(this::closeTerminal);

    }

    private ScheduledFuture<?> recordTerminal(ActionEvent actionEvent) {

        Tab tab = terminalTabPane.getSelectionModel().getSelectedItem();

        if (Objects.isNull(tab)) {
            return null;
        }

        GifExporterFX gifExporterFX = applicationContext.getBean(GifExporterFX.class);

        try {
            String gifName = LocalDateTime.now().format(DateTimeFormatter.ofPattern("'Image'-ddMMyy-hhmmss.SSS'.gif'"));
            ScheduledFuture<?> scheduledFuture = gifExporterFX
                    .captureNow(tab.getContent(), directoryService.workingDirectory().resolve(gifName), 120, true);

            return scheduledFuture;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    TerminalBuilder terminalBuilder = new TerminalBuilder();

    @FXML
    public void newTerminal(ActionEvent actionEvent, Path... path) {

        if (!terminalToggleButton.isSelected()) {
            terminalToggleButton.fire();
        }

        TerminalConfig terminalConfig = terminalConfigBean.createTerminalConfig();
        terminalBuilder.setTerminalConfig(terminalConfig);

        Path terminalPath = Optional.ofNullable(path).filter(e -> e.length > 0).map(e -> e[0]).orElse(directoryService.workingDirectory());
        terminalBuilder.setTerminalPath(terminalPath);

        threadService.runActionLater(() -> {
            TerminalTab terminalTab = terminalBuilder.newTerminal();
            terminalTabPane.getTabs().add(terminalTab);
            terminalTabPane.getSelectionModel().select(terminalTab);
        }, true);

    }

    @FXML
    public void closeTerminal(ActionEvent actionEvent) {
        TerminalTab shellTab = (TerminalTab) terminalTabPane.getSelectionModel().getSelectedItem();
        Optional.ofNullable(shellTab).ifPresent(TerminalTab::closeTerminal);
    }

    public void closeAllTerminal(ActionEvent actionEvent) {
        ObservableList<Tab> tabs = FXCollections.observableArrayList(terminalTabPane.getTabs());

        for (Tab tab : tabs) {
            ((TerminalTab) tab).closeTerminal();
        }
    }

    public void closeOtherTerminals(ActionEvent actionEvent) {
        ObservableList<Tab> tabs = FXCollections.observableArrayList(terminalTabPane.getTabs());
        tabs.remove(terminalTabPane.getSelectionModel().getSelectedItem());

        for (Tab tab : tabs) {
            ((TerminalTab) tab).closeTerminal();
        }
    }

    private void initializeNashornConverter() {
        nashornEngineConverter.initialize();
    }

    public boolean getStopRendering() {
        return stopRendering.get();
    }

    public BooleanProperty stopRenderingProperty() {
        return stopRendering;
    }

    public void saveAllTabs() {

        tabPane.getTabs()
                .stream()
                .filter(t -> t instanceof MyTab)
                .map(t -> (MyTab) t)
                .filter(t -> !t.isNew())
                .forEach(MyTab::saveDoc);
    }

    public void loadAllTabs() {
        tabPane.getTabs()
                .stream()
                .filter(t -> t instanceof MyTab)
                .map(t -> (MyTab) t)
                .filter(t -> !t.isNew())
                .forEach(MyTab::reload);
    }

    public void initializeSaveOnBlur() {

        stage.focusedProperty().addListener((observable, oldValue, newValue) -> {

            Node focusOwner = stage.getScene().getFocusOwner();

            if (StageHelper.getStages().size() == 1) {
                if (!newValue) {
                    saveAllTabs();
                } else {
                    loadAllTabs();
                }
            }

            if (newValue) {
                if (nonNull(focusOwner)) {
//                    logger.info("Focus owner changed {}", focusOwner);
                    focusOwner.requestFocus();
                }
            }
        });

    }

    public void applyInitialConfigurations() {

        Double screenX = editorConfigBean.getScreenX();
        Double screenY = editorConfigBean.getScreenY();
        Double screenWidth = editorConfigBean.getScreenWidth();
        Double screenHeight = editorConfigBean.getScreenHeight();

        if (nonNull(screenX)) {
            stage.setX(screenX);
        }

        if (nonNull(screenY)) {
            stage.setY(screenY);
        }

        if (nonNull(screenWidth)) {
            if (screenWidth != 0) {
                stage.setWidth(screenWidth);
            }
        }

        if (nonNull(screenHeight)) {
            if (screenHeight != 0) {
                stage.setHeight(screenHeight);
            }
        }

        ObservableList<SplitPane.Divider> dividers = splitPane.getDividers();
        dividers.get(0).setPosition(editorConfigBean.getFirstSplitter());
        if (dividers.size() > 1) {
            dividers.get(1).setPosition(editorConfigBean.getSecondSplitter());
        }

        String aceTheme = editorConfigBean.getAceTheme().get(0);

        editorConfigBean.getEditorTheme().stream().findFirst().ifPresent(theme -> {
            applyTheme(theme, getAllStages());
            editorConfigBean.updateAceTheme(aceTheme);
        });

        editorConfigBean.getAceTheme().stream().findFirst().ifPresent(ace -> {
            applyForAllEditorPanes(editorPane -> editorPane.setTheme(ace));
        });

        applyForAllEditorPanes(editorPane -> editorPane.setShowGutter(editorConfigBean.getShowGutter()));
        applyForAllEditorPanes(editorPane -> editorPane.setUseWrapMode(editorConfigBean.getUseWrapMode()));
        applyForAllEditorPanes(editorPane -> editorPane.setWrapLimitRange(editorConfigBean.getWrapLimit()));
        applyForAllEditorPanes(editorPane -> editorPane.setFontSize(editorConfigBean.getFontSize()));
        applyForAllEditorPanes(editorPane -> editorPane.setFoldStyle(editorConfigBean.getFoldStyle()));

        ObservableList<Item> recentFilesList = storedConfigBean.getRecentFiles();
        ObservableList<String> favoriteDirectories = storedConfigBean.getFavoriteDirectories();

        recentListView.setCellFactory(param -> {
            ListCell<Item> cell = new ListCell<Item>() {
                @Override
                protected void updateItem(Item item, boolean empty) {
                    super.updateItem(item, empty);
                    if (nonNull(item)) {
                        setTooltip(new Tooltip(item.getPath().toString()));
                        setText(item.toString());
                    }
                }
            };
            return cell;
        });

        recentListView.setItems(recentFilesList);

        recentFilesList.addListener((ListChangeListener<Item>) c -> {
            recentListView.visibleProperty().setValue(c.getList().size() > 0);
            recentListView.getSelectionModel().selectFirst();
        });
        recentListView.setOnMouseClicked(event -> {
            if (event.getClickCount() > 1) {
                openRecentListFile(event);
            }
        });

        if (favoriteDirectories.size() == 0) {
            favoriteDirMenu.setVisible(false);
        } else {
            int size = 0;
            for (String favoriteDirectory : favoriteDirectories) {
                this.addItemToFavoriteDir(size++, favoriteDirectory);
            }
            this.includeClearAllToFavoriteDir();
        }

        favoriteDirectories.addListener((ListChangeListener<String>) c -> {
            c.next();
            favoriteDirMenu.setVisible(true);
            int size = favoriteDirMenu.getItems().size();
            boolean empty = size == 0;
            List<? extends String> addedSubList = c.getAddedSubList();
            for (String path : addedSubList) {
                if (size > 0) {
                    this.addItemToFavoriteDir(size++ - 2, path);
                } else {
                    this.addItemToFavoriteDir(size++, path);
                }
            }
            if (empty) {
                this.includeClearAllToFavoriteDir();
            }
        });

        // TODO: Lazy initial ?
        mathJaxService.reload();

        String workingDirectory = storedConfigBean.getWorkingDirectory();

        if (nonNull(workingDirectory)) {
            directoryService.changeWorkigDir(IOHelper.getPath(workingDirectory));
        }

    }

    private Stage[] getAllStages() {
        return new Stage[]{stage, detachStage, asciidocTableStage, markdownTableStage};
    }

    public void bindConfigurations() {

        editorConfigBean.getEditorTheme().addListener((ListChangeListener<EditorConfigBean.Theme>) c -> {
            c.next();
            if (c.wasAdded()) {
                applyTheme(c.getList().get(0), getAllStages());
            }
        });

        editorConfigBean.getAceTheme().addListener((ListChangeListener<String>) c -> {
            c.next();
            if (c.wasAdded()) {
                applyForAllEditorPanes(editorPane -> editorPane.setTheme(c.getList().get(0)));
            }
        });

        terminalConfigBean.setOnConfigChanged(() -> {
            applyForEachTerminal(terminalTab -> terminalTab.updatePrefs(terminalConfigBean.createTerminalConfig()));
        });

        locationConfigBean.mathjaxProperty().addListener((observable, oldValue, newValue) -> {
            if (nonNull(newValue)) {
                if (!newValue.equals(oldValue)) {
                    mathJaxService.reload();
                }
            }
        });

        ObservableList<SplitPane.Divider> dividers = splitPane.getDividers();

        dividers.get(0).positionProperty().bindBidirectional(editorConfigBean.firstSplitterProperty());
        if (dividers.size() > 1) {
            dividers.get(1).positionProperty().bindBidirectional(editorConfigBean.secondSplitterProperty());
        }

        SplitPane.Divider verticalDivider = mainVerticalSplitPane.getDividers().get(0);
        mainVerticalSplitPane.addEventFilter(MouseEvent.MOUSE_RELEASED, event -> {
            double position = verticalDivider.getPosition();
            if (position > 0.1 && position < 0.9) {
                editorConfigBean.setVerticalSplitter(position);
            }
        });

        editorConfigBean.showGutterProperty().addListener((observable, oldValue, newValue) -> {
            if (nonNull(newValue)) {
                applyForAllEditorPanes(editorPane -> editorPane.setShowGutter(newValue));
            }
        });

        editorConfigBean.useWrapModeProperty().addListener((observable, oldValue, newValue) -> {
            if (nonNull(newValue)) {
                applyForAllEditorPanes(editorPane -> editorPane.setUseWrapMode(newValue));
            }
        });

        editorConfigBean.wrapLimitProperty().addListener((observable, oldValue, newValue) -> {
            if (nonNull(newValue)) {
                applyForAllEditorPanes(editorPane -> editorPane.setWrapLimitRange(newValue));
            }
        });

        editorConfigBean.fontSizeProperty().addListener((observable, oldValue, newValue) -> {
            applyForAllEditorPanes(editorPane -> editorPane.setFontSize(newValue.intValue()));
        });

        editorConfigBean.foldStyleProperty().addListener((observable, oldValue, newValue) -> {
            applyForAllEditorPanes(editorPane -> editorPane.setFoldStyle(newValue));
        });

        storedConfigBean.workingDirectoryProperty().addListener((observable, oldValue, newValue) -> {
            if (nonNull(newValue) && Objects.isNull(oldValue)) {
                directoryService.changeWorkigDir(IOHelper.getPath(newValue));
            }
        });

        stage.xProperty().addListener((observable, oldValue, newValue) -> {
            editorConfigBean.setScreenX(newValue.doubleValue());
        });

        stage.yProperty().addListener((observable, oldValue, newValue) -> {
            editorConfigBean.setScreenY(newValue.doubleValue());
        });

        stage.widthProperty().addListener((observable, oldValue, newValue) -> {
            editorConfigBean.setScreenWidth(newValue.doubleValue());
        });

        stage.heightProperty().addListener((observable, oldValue, newValue) -> {
            editorConfigBean.setScreenHeight(newValue.doubleValue());
        });


        if (nonNull(detachStage)) {
            detachStage.xProperty().addListener((observable, oldValue, newValue) -> {
                editorConfigBean.setPreviewScreenX(newValue.doubleValue());
            });

            detachStage.yProperty().addListener((observable, oldValue, newValue) -> {
                editorConfigBean.setPreviewScreenY(newValue.doubleValue());
            });

            detachStage.widthProperty().addListener((observable, oldValue, newValue) -> {
                editorConfigBean.setPreviewScreenWidth(newValue.doubleValue());
            });

            detachStage.heightProperty().addListener((observable, oldValue, newValue) -> {
                editorConfigBean.setPreviewScreenHeight(newValue.doubleValue());
            });
        }

    }

    private void getImageSizeInfo(String path, Object info) {

        if (path.startsWith("/")) {
            path = path.substring(1);
        }

        Path parent = null;

        try {
            if (current.currentPath().isPresent()) {
                parent = current.currentPath().get().getParent();
            } else {
                parent = directoryService.workingDirectory();
            }
        } catch (Exception e) {
            logger.debug("Problem occured while getting parent path", e);
        }

        if (Objects.isNull(parent)) {
            return;
        }

        Path imagePath = parent.resolve(path);

        if (Files.notExists(imagePath)) {
            return;
        }

        try (ImageInputStream in = ImageIO.createImageInputStream(imagePath.toFile())) {
            final Iterator<ImageReader> readers = ImageIO.getImageReaders(in);
            if (readers.hasNext()) {
                ImageReader reader = readers.next();
                try {
                    reader.setInput(in);
                    int width = reader.getWidth(0);
                    int height = reader.getHeight(0);

                    if ((info instanceof JSObject)) {
                        JSObject object = (JSObject) info;
                        object.setMember("width", width);
                        object.setMember("height", height);
                    } else if (info instanceof jdk.nashorn.api.scripting.JSObject) {
                        jdk.nashorn.api.scripting.JSObject object = (jdk.nashorn.api.scripting.JSObject) info;
                        object.setMember("width", width);
                        object.setMember("height", height);
                        ;
                    }

                    reader.dispose();

                    return;
                } finally {
                    reader.dispose();
                }
            }
        } catch (Exception e) {
            logger.error("Problem occured while getting image size info", e);
        }
    }

    @WebkitCall(from = "asciidoctor-image-size-info")
    public void getImageInfo(final String path, Object info) {

        if ((info instanceof JSObject)) {
            threadService.runActionLater(() -> {
                getImageSizeInfo(path, info);
            });
        } else if (info instanceof jdk.nashorn.api.scripting.JSObject) {
            getImageSizeInfo(path, info);
        }
    }

    private void applyForAllEditorPanes(Consumer<EditorPane> editorPaneConsumer) {
        ObservableList<Tab> tabs = tabPane.getTabs();
        for (Tab tab : tabs) {
            if (tab instanceof MyTab) {
                MyTab myTab = (MyTab) tab;
                editorPaneConsumer.accept(myTab.getEditorPane());
            }
        }
    }

    private void applyForEachTerminal(Consumer<TerminalTab> terminalTabConsumer) {
        ObservableList<Tab> tabs = terminalTabPane.getTabs();
        for (Tab tab : tabs) {
            terminalTabConsumer.accept(((TerminalTab) tab));
        }
    }

    private void includeClearAllToFavoriteDir() {
        favoriteDirMenu.getItems().addAll(new SeparatorMenuItem(), MenuItemBuilt
                .item("Clear List")
                .click(event -> {
                    ObservableList<TreeItem<Item>> selectedItems = fileSystemView.getSelectionModel().getSelectedItems();
                    if (selectedItems.size() == 1) {
                        Path path = selectedItems.get(0).getValue().getPath();
                        boolean isDirectory = Files.isDirectory(path);
                        addToFavSeparator.setVisible(isDirectory);
                    } else {
                        addToFavSeparator.setVisible(false);
                    }
                    storedConfigBean.getFavoriteDirectories().clear();
                    favoriteDirMenu.getItems().clear();
                    favoriteDirMenu.setVisible(false);
                    addToFavoriteDir.setDisable(false);
                }));
    }

    private void addItemToFavoriteDir(int index, String path) {
        favoriteDirMenu.getItems().add(index, MenuItemBuilt
                .item(path)
                .tip("Go to favorite dir")
                .click(event -> {
                    directoryService.changeWorkigDir(IOHelper.getPath(path));
                }));
    }

    private void checkNewVersion() {
        threadService.schedule(() -> {
            try {
                if (!editorConfigBean.getAutoUpdate()) {
                    return;
                }

                ApplicationLauncher.launchApplication("504", null, false, new ApplicationLauncher.Callback() {
                            public void exited(int exitValue) {
                                //TODO add your code here (not invoked on event dispatch thread)
                            }

                            public void prepareShutdown() {
                                //TODO add your code here (not invoked on event dispatch thread)
                            }
                        }
                );
            } catch (Exception e) {
                // logger.error("Problem occured while checking new version", e);
            }
        }, 10, TimeUnit.SECONDS);
    }

    private void generateODFDocument(boolean askPath) {

        if (!current.currentPath().isPresent()) {
            this.saveDoc();
        }

        threadService.runTaskLater(() -> {
            odfConverter.generateODFDocument(askPath);
        });

    }

    private void generateODFDocument() {
        this.generateODFDocument(false);
    }

    private void initializeDoctypes() {

        try {
            ObjectMapper mapper = new ObjectMapper();
            Object readValue = mapper.readValue(getConfigPath().resolve("ace_doctypes.json").toFile(), new TypeReference<List<DocumentMode>>() {
            });
            modeList.addAll((Collection) readValue);

            supportedModes = modeList.stream()
                    .map(d -> d.getExtensions())
                    .filter(Objects::nonNull)
                    .flatMap(d -> Arrays.asList(d.split("\\|")).stream())
                    .collect(Collectors.toList());

        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Problem occured while loading document types", e);
        }
    }

    public Path getInstallationPath() {

        if (Objects.isNull(installationPath)) {
            try {
                String homeProp = System.getProperty("asciidocfx.home");
                if (homeProp != null) {
                    installationPath = new File(homeProp).toPath();
                } else {
                    //guess installation path
                    CodeSource codeSource = ApplicationController.class.getProtectionDomain().getCodeSource();
                    File jarFile = new File(codeSource.getLocation().toURI().getPath());
                    installationPath = jarFile.toPath().getParent().getParent();
                }
            } catch (Exception e) {
                logger.error("Problem occured while resolving conf and log paths", e);
            }
        }

        return installationPath;
    }

    public String getLogPath() {

        if (Objects.isNull(logPath)) {
            Optional<String> linuxHome = Optional.ofNullable(System.getenv("HOME"));
            Optional<String> windowsHome = Optional.ofNullable(System.getenv("USERPROFILE"));

            Stream.<Optional<String>>of(linuxHome, windowsHome)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .map(Paths::get)
                    .findFirst()
                    .ifPresent(path -> logPath = path.resolve(configDirName).resolve("log").toString());
        }

        return logPath;
    }

    @WebkitCall
    public void updateStatusBox(long row, long column, long linecount, long wordcount) {
        threadService.runActionLater(() -> {
            statusText.setText(String.format("(Characters: %d) (Lines: %d) (%d:%d)", wordcount, linecount, row, column));
        });
    }

    private void initializeLogViewer() {
        TableView<MyLog> logViewer = new TableView<>();
        logViewer.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        ContextMenu logViewerContextMenu = new ContextMenu();
        logViewerContextMenu.getItems().add(MenuItemBuilt.item("Copy").click(e -> {
            ObservableList<MyLog> rowList = (ObservableList) logViewer.getSelectionModel().getSelectedItems();

            StringBuilder clipboardString = new StringBuilder();

            for (MyLog rowLog : rowList) {
                clipboardString.append(String.format("%s => %s", rowLog.getLevel(), rowLog.getMessage()));
                clipboardString.append("\n\n");
            }

            ClipboardContent clipboardContent = new ClipboardContent();
            clipboardContent.putString(clipboardString.toString());

            Clipboard.getSystemClipboard().setContent(clipboardContent);
        }));

        logViewer.setContextMenu(logViewerContextMenu);
        logViewer.getStyleClass().add("log-viewer");

        FilteredList<MyLog> logFilteredList = new FilteredList<MyLog>(logList, log -> true);
        logViewer.setItems(logFilteredList);

//        logViewer.setColumnResizePolicy((param) -> true);
        logViewer.getItems().addListener((ListChangeListener<MyLog>) c -> {
            c.next();
            final int size = logViewer.getItems().size();
            if (size > 0) {
                logViewer.scrollTo(size - 1);
            }
        });

        TableColumn<MyLog, String> levelColumn = new TableColumn<>("Level");
        levelColumn.getStyleClass().add("level-column");
        TableColumn<MyLog, String> messageColumn = new TableColumn<>("Message");
        levelColumn.setCellValueFactory(new PropertyValueFactory<MyLog, String>("level"));
        messageColumn.setCellValueFactory(new PropertyValueFactory<MyLog, String>("message"));
        messageColumn.prefWidthProperty().bind(logViewer.widthProperty().subtract(levelColumn.widthProperty()).subtract(5));

        logViewer.setRowFactory(param -> new TableRow<MyLog>() {
            @Override
            protected void updateItem(MyLog item, boolean empty) {
                super.updateItem(item, empty);
                if (!empty) {
                    getStyleClass().removeAll("DEBUG", "INFO", "WARN", "ERROR");
                    getStyleClass().add(item.getLevel());
                }
            }
        });

        messageColumn.setCellFactory(param -> new TableCell<MyLog, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                if (item == getItem()) {
                    return;
                }

                super.updateItem(item, empty);

                if (item == null) {
                    super.setText(null);
                    super.setGraphic(null);
                } else {
                    Text text = new Text(item);
                    super.setGraphic(text);
                    text.wrappingWidthProperty().bind(messageColumn.widthProperty().subtract(0));
                }
            }
        });

        logViewer.getColumns().addAll(levelColumn, messageColumn);
        logViewer.setEditable(true);

        TableViewLogAppender.setThreadService(threadService);
        TableViewLogAppender.setLogList(logList);
        TableViewLogAppender.setStatusMessage(statusMessage);
        TableViewLogAppender.setShowHideLogs(logToggleButton);
        TableViewLogAppender.setLogViewer(logViewer);

        final EventHandler<ActionEvent> filterByLogLevel = event -> {
            ToggleButton logLevelItem = (ToggleButton) event.getTarget();
            if (nonNull(logLevelItem)) {
                logFilteredList.setPredicate(myLog -> {
                    String text = logLevelItem.getText();
                    return text.equals("All") || text.equalsIgnoreCase(myLog.getLevel());
                });
            }
        };

        ToggleGroup toggleGroup = new ToggleGroup();
        ToggleButton allToggle = ToggleButtonBuilt.item("All").tip("Show all").click(filterByLogLevel);
        ToggleButton errorToggle = ToggleButtonBuilt.item("Error").tip("Filter by Error").click(filterByLogLevel);
        ToggleButton warnToggle = ToggleButtonBuilt.item("Warn").tip("Filter by Warn").click(filterByLogLevel);
        ToggleButton infoToggle = ToggleButtonBuilt.item("Info").tip("Filter by Info").click(filterByLogLevel);
        ToggleButton debugToggle = ToggleButtonBuilt.item("Debug").tip("Filter by Debug").click(filterByLogLevel);

        toggleGroup.getToggles().addAll(
                allToggle,
                errorToggle,
                warnToggle,
                infoToggle,
                debugToggle
        );
        toggleGroup.selectToggle(allToggle);

        Button clearLogsButton = new Button("Clear");
        clearLogsButton.setOnAction(e -> {
            statusMessage.setText("");
            logList.clear();
        });

        Button browseLogsButton = new Button("Browse");
        browseLogsButton.setOnAction(e -> {
            openInDesktop(IOHelper.getPath(logPath));
        });

        TextField searchLogField = new TextField();
        searchLogField.setPromptText("Search in logs..");
        searchLogField.textProperty().addListener((observable, oldValue, newValue) -> {

            if (Objects.isNull(newValue)) {
                return;
            }

            if (newValue.isEmpty()) {
                logFilteredList.setPredicate(myLog -> true);
            }

            logFilteredList.setPredicate(myLog -> {

                final AtomicBoolean result = new AtomicBoolean(false);

                String message = myLog.getMessage();
                if (nonNull(message)) {
                    if (!result.get()) {
                        result.set(message.toLowerCase().contains(newValue.toLowerCase()));
                    }
                }

                String level = myLog.getLevel();
                String toggleText = ((ToggleButton) toggleGroup.getSelectedToggle()).getText();
                boolean inputContains = level.toLowerCase().contains(newValue.toLowerCase());

                if (nonNull(level)) {
                    if (!result.get()) {
                        result.set(inputContains);
                    }
                }

                boolean levelContains = toggleText.toLowerCase().equalsIgnoreCase(level);

                if (!toggleText.equals("All") && !levelContains) {
                    result.set(false);
                }

                return result.get();
            });
        });

        List<Control> controls = Arrays.asList(allToggle,
                errorToggle, warnToggle, infoToggle, debugToggle,
                searchLogField, clearLogsButton, browseLogsButton);

        FlowPane logFlowPane = new FlowPane(5, 5);

        for (Control control : controls) {
            logFlowPane.getChildren().add(control);
            control.prefHeightProperty().bind(searchLogField.heightProperty());
        }

        logViewer.setMinHeight(0);
        logVBox.setMinHeight(0);

        logVBox.getChildren().addAll(logFlowPane, logViewer);

        VBox.setVgrow(logViewer, Priority.ALWAYS);

    }

    private void openRecentListFile(Event event) {
        tabService.previewDocument(recentListView.getSelectionModel().getSelectedItem().getPath());
    }

    public void externalBrowse() {
        rightShowerHider.getShowing().ifPresent(ViewPanel::browse);
    }

    ChangeListener<Boolean> outlineTabChangeListener;

    @WebkitCall(from = "index")
    public void fillOutlines(Object doc) {

        if (outlineTreeView.isVisible()) {
            converterProvider.get(previewConfigBean).fillOutlines(doc);
        }

        if (nonNull(outlineTabChangeListener)) {
            outlineTreeView.visibleProperty().removeListener(outlineTabChangeListener);
        }

        outlineTabChangeListener = (observable, oldValue, newValue) -> {
            if (newValue) {
                converterProvider.get(previewConfigBean).fillOutlines(doc);
            }
        };
        outlineTreeView.visibleProperty().addListener(outlineTabChangeListener);

    }

    @WebkitCall(from = "index")
    public void clearOutline() {
        outlineList = new TreeSet<>();
    }

    @WebkitCall(from = "index")
    public void finishOutline() {

        threadService.runActionLater(() -> {

            if (outlineTreeView.getRoot() == null) {
                TreeItem<Section> rootItem = new TreeItem<>();
                rootItem.setExpanded(true);
                Section rootSection = new Section();
                rootSection.setLevel(-1);
                String outlineTitle = "Outline";
                rootSection.setTitle(outlineTitle);

                rootItem.setValue(rootSection);

                outlineTreeView.setRoot(rootItem);

                outlineTreeView.setOnMouseClicked(event -> {
                    try {
                        TreeItem<Section> item = outlineTreeView.getSelectionModel().getSelectedItem();
                        EditorPane editorPane = current.currentEditor();
                        editorPane.moveCursorTo(item.getValue().getLineno());
                    } catch (Exception e) {
                        logger.error("Problem occured while jumping from outline");
                    }
                });
            }

            if (outlineList.size() > 0) {
                outlineTreeView.getRoot().getChildren().clear();
            }

            for (Section section : outlineList) {
                TreeItem<Section> sectionItem = new TreeItem<>(section);
                sectionItem.setExpanded(true);
                outlineTreeView.getRoot().getChildren().add(sectionItem);

                TreeSet<Section> subsections = section.getSubsections();
                for (Section subsection : subsections) {
                    TreeItem<Section> subItem = new TreeItem<>(subsection);
                    subItem.setExpanded(true);
                    sectionItem.getChildren().add(subItem);
                    this.addSubSections(subItem, subsection.getSubsections());
                }
            }

        });

    }

    private void addSubSections(TreeItem<Section> subItem, TreeSet<Section> outlineList) {
        for (Section section : outlineList) {
            TreeItem<Section> sectionItem = new TreeItem<>(section);
            subItem.getChildren().add(sectionItem);

            TreeSet<Section> subsections = section.getSubsections();
            for (Section subsection : subsections) {
                TreeItem<Section> item = new TreeItem<>(subsection);
                sectionItem.getChildren().add(item);
                this.addSubSections(item, subsection.getSubsections());
            }
        }
    }

    @WebkitCall(from = "index")
    public void fillOutline(String parentLineNo, String level, String title, String lineno, String id) {

        Section section = new Section();
        section.setLevel(Integer.valueOf(level));
        section.setTitle(title);
        section.setLineno(Integer.valueOf(lineno));
        section.setId(id);

        if (Objects.isNull(parentLineNo)) {
            outlineList.add(section);
        } else {
            Integer parentLine = Integer.valueOf(parentLineNo);
            Optional<Section> parentSection = outlineList.stream()
                    .filter(e -> e.getLineno().equals(parentLine))
                    .findFirst();

            if (parentSection.isPresent()) {
                parentSection.get().getSubsections().add(section);
            } else {
                this.traverseEachSubSection(outlineList, parentLine, section);
            }
        }
    }

    private void traverseEachSubSection(TreeSet<Section> sections, Integer parentLine, Section section) {
        sections.stream().forEach(s -> {
            Optional<Section> subs = s.getSubsections().stream()
                    .filter(e -> e.getLineno().equals(parentLine))
                    .findFirst();

            if (subs.isPresent()) {
                subs.get().getSubsections().add(section);
            } else {
                this.traverseEachSubSection(s.getSubsections(), parentLine, section);
            }
        });
    }

    @FXML
    public void changeWorkingDir(Event actionEvent) {
        directoryService.askWorkingDir();
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        sessionList.add(session);

        Optional
                .ofNullable(lastConverterResult)
                .map(ConverterResult::getRendered)
                .ifPresent(this::sendOverWebSocket);
    }

    @FXML
    public void closeApp(ActionEvent event) {
        try {
            Map<String, ConfigurationBase> configurationBeansAsMap = applicationContext.getBeansOfType(ConfigurationBase.class);
            for (ConfigurationBase configurationBean : configurationBeansAsMap.values()) {
                configurationBean.save(event);
            }
        } catch (Exception e) {
            logger.error("Error while closing app", e);
        }
    }

    @FXML
    public void openDoc(Event... event) {
        tabService.openDoc();
    }

    @FXML
    public void newDoc(Event... event) {
        threadService.runActionLater(() -> {
            tabService.newDoc();
        }, true);
    }

    @WebkitCall(from = "editor")
    public boolean isLiveReloadPane() {
        return liveReloadPane.isVisible();
    }

    @WebkitCall(from = "editor")
    public void onscroll(Object pos, Object max) {
        rightShowerHider.getShowing()
                .ifPresent(s -> s.onscroll(pos, max));
    }

    @WebkitCall(from = "editor")
    public void scrollByLine(String text) {
        threadService.runActionLater(() -> {
            try {
                rightShowerHider.getShowing().ifPresent(w -> w.scrollByLine(text));
            } catch (Exception e) {
                logger.debug(e.getMessage(), e);
            }
        });
    }

    @WebkitCall(from = "click-binder")
    public void moveCursorTo(int line) {
        current.currentEditor().moveCursorTo(line);
    }

    @WebkitCall(from = "editor")
    public void scrollByPosition(String text) {
        threadService.runActionLater(() -> {
            try {
                String selection = asciidocWebkitConverter.findRenderedSelection(text);
                rightShowerHider.getShowing().ifPresent(w -> w.scrollByPosition(selection));
            } catch (Exception e) {
                logger.debug(e.getMessage(), e);
            }
        });
    }

    @WebkitCall(from = "asciidoctor-uml")
    public void uml(String uml, String type, String imagesDir, String imageTarget, String nodename) throws IOException {
        plantuml(uml, type, imagesDir, imageTarget, nodename);
    }

    @WebkitCall(from = "asciidoctor-uml")
    public void plantuml(String uml, String type, String imagesDir, String imageTarget, String nodename) throws IOException {

        threadService.runTaskLater(() -> {
            plantUmlService.plantUml(uml, type, imagesDir, imageTarget, nodename);
        });
    }

    @WebkitCall(from = "asciidoctor-uml")
    public void graphviz(String graphviz, String type, String imagesDir, String imageTarget, String nodename) throws IOException {
        this.plantuml(graphviz, type, imagesDir, imageTarget, nodename);
    }

    @WebkitCall(from = "asciidoctor-uml")
    public void ditaa(String ditaa, String type, String imagesDir, String imageTarget, String nodename) throws IOException {
        this.plantuml(ditaa, type, imagesDir, imageTarget, nodename);
    }

    @WebkitCall(from = "asciidoctor-chart")
    public void chartBuildFromCsv(String csvFile, String imagesDir, String imageTarget, String chartType, String options) {

        threadService.runActionLater(() -> {
            if (Objects.isNull(imageTarget) || Objects.isNull(chartType)) {
                return;
            }

            current.currentPath().map(Path::getParent).ifPresent(root -> {
                threadService.runTaskLater(() -> {
                    String csvContent = IOHelper.readFile(root.resolve(csvFile));

                    threadService.runActionLater(() -> {
                        try {
                            Map<String, String> optMap = parseChartOptions(options);
                            optMap.put("csv-file", csvFile);
                            chartProvider.getProvider(chartType).chartBuild(csvContent, imagesDir, imageTarget, optMap);

                        } catch (Exception e) {
                            logger.info(e.getMessage(), e);
                        }
                    });
                });
            });
        });
    }

    @WebkitCall(from = "asciidoctor-chart")
    public void chartBuild(String chartContent, String imagesDir, String imageTarget, String chartType, String options) {

        if (Objects.isNull(imageTarget) || Objects.isNull(chartType)) {
            return;
        }

        threadService.runActionLater(() -> {
            try {
                Map<String, String> optMap = parseChartOptions(options);
                chartProvider.getProvider(chartType).chartBuild(chartContent, imagesDir, imageTarget, optMap);

            } catch (Exception e) {
                logger.info(e.getMessage(), e);
            }
        });
    }

    private Map<String, String> parseChartOptions(String options) {
        Map<String, String> optMap = new HashMap<>();
        if (nonNull(options)) {
            String[] optPart = options.split(",");

            for (String opt : optPart) {
                String[] keyVal = opt.split("=");
                if (keyVal.length != 2) {
                    continue;
                }
                optMap.put(keyVal[0], keyVal[1]);
            }
        }
        return optMap;
    }

    @WebkitCall(from = "converter.js")
    public void completeWebWorkerExceptionally(Object taskId) {
        threadService.runTaskLater(() -> {
            final Map<String, CompletableFuture<ConverterResult>> workerTasks = asciidocWebkitConverter.getWebWorkerTasks();
            Optional.ofNullable(workerTasks.get(taskId))
                    .filter(c -> !c.isDone())
                    .ifPresent(c -> {
                        final RuntimeException ex = new RuntimeException(String.format("Task: %s is not completed", taskId));
                        c.completeExceptionally(ex);
                    });
            workerTasks.remove(taskId);
        });
    }

    @WebkitCall(from = "converter.js")
    public void completeWebWorker(String taskId, String rendered, String backend, String doctype) {
        threadService.runTaskLater(() -> {
            final ConverterResult converterResult = new ConverterResult(taskId, rendered, backend, doctype);
            final Map<String, CompletableFuture<ConverterResult>> workerTasks = asciidocWebkitConverter.getWebWorkerTasks();
            final CompletableFuture<ConverterResult> completableFuture = workerTasks.get(converterResult.getTaskId());

            Optional.ofNullable(completableFuture)
                    .filter(c -> !c.isDone())
                    .ifPresent(c -> {
                        c.complete(converterResult);
                    });

            workerTasks.remove(converterResult.getTaskId());
        });
    }

    private AtomicReference<Tuple<String, String>> latestTupleReference = new AtomicReference<>();
    private Semaphore renderLoopSemaphore = new Semaphore(1);

    private void renderLoop() throws InterruptedException {

        renderLoopSemaphore.acquire();

        if (stopRendering.get()) {
            return;
        }

        Tuple<String, String> tuple = latestTupleReference.get();

        if (Objects.isNull(tuple)) {
            return;
        }

        String text = tuple.getKey();
        String mode = tuple.getValue();

        try {

            boolean bookArticleHeader = this.bookArticleHeaderRegex.matcher(text).find();
            boolean forceInclude = this.forceIncludeRegex.matcher(text).find();

            if ("asciidoc".equalsIgnoreCase(mode)) {

                if (bookArticleHeader && !forceInclude) {
                    setIncludeAsciidocResource(true);
                }

                this.lastConverterResult = converterProvider.get(previewConfigBean).convertAsciidoc(text);

                setIncludeAsciidocResource(false);

                if (lastConverterResult.isBackend("html5")) {
                    updateRendered(lastConverterResult.getRendered());
                    rightShowerHider.showNode(htmlPane);
                }

                if (lastConverterResult.isBackend("revealjs") || lastConverterResult.isBackend("deckjs")) {
                    slidePane.setBackend(lastConverterResult.getBackend());
                    slideConverter.convert(lastConverterResult.getRendered());
                    rightShowerHider.showNode(slidePane);
                }

            } else if ("html".equalsIgnoreCase(mode)) {
//                if (liveReloadPane.getReady()) {
//                    liveReloadPane.updateDomdom();
//                } else {
                threadService.buff("htmlEditor")
                        .schedule(() -> {
                            liveReloadPane.load(String.format(liveUrl, port, directoryService.interPath()));
                        }, 500, TimeUnit.MILLISECONDS);
//                }

                rightShowerHider.showNode(liveReloadPane);

            } else if ("markdown".equalsIgnoreCase(mode)) {
                MarkdownService markdownService = applicationContext.getBean(MarkdownService.class);
                markdownService.convertToAsciidoc(text, asciidoc -> {
                    ConverterResult result = converterProvider.get(previewConfigBean).convertAsciidoc(asciidoc);
                    result.afterRender(this::updateRendered);
                });
                rightShowerHider.showNode(htmlPane);
            }

        } catch (Exception e) {
            setIncludeAsciidocResource(false);
            logger.error("Problem occured while rendering content", e);
        }
    }

    private void updateRendered(String rendered) {

        Optional.ofNullable(rendered)
                .ifPresent(html -> {
                    htmlPane.refreshUI(html);
                    sendOverWebSocket(html);
                });

    }

    private void sendOverWebSocket(String html) {
        if (sessionList.size() > 0) {
            threadService.runTaskLater(() -> {
                sessionList.stream().filter(WebSocketSession::isOpen).forEach(e -> {
                    try {
                        e.sendMessage(new TextMessage(html));
                    } catch (Exception ex) {
                        logger.error("Problem occured while sending content over WebSocket", ex);
                    }
                });
            });
        }
    }

    @WebkitCall(from = "editor")
    public void textListener(String text, String mode) {
        latestTupleReference.set(new Tuple<>(text, mode));
        if (renderLoopSemaphore.hasQueuedThreads()) {
            renderLoopSemaphore.release();
        }
    }

    @WebkitCall(from = "editor")
    public void checkWordSuggestions(String word) {
        final List<String> stringList = dictionaryService.getSuggestionMap()
                .getOrDefault(word, Arrays.asList());
        current.currentEditor().showSuggestions(stringList);
    }

    @WebkitCall(from = "editor")
    public void processTokens() {

        if (spellcheckConfigBean.getDisableSpellCheck()) {
            return;
        }

        final EditorPane editorPane = current.currentEditor();
        final String tokenList = editorPane.tokenList();
        final String mode = editorPane.editorMode();

        threadService.runTaskLater(() -> {
            dictionaryService.processTokens(editorPane, tokenList, mode);
        });
    }

    @WebkitCall(from = "asciidoctor-odf.js")
    public synchronized void convertToOdf(String name, Object obj) throws Exception {
        JSObject jObj = (JSObject) obj;
        odfConverter.buildDocument(name, jObj);
    }

    @WebkitCall
    public String getTemplate(String templateDir) {
        return asciidocWebkitConverter.getTemplate(templateDir);
    }

    public void cutCopy(String data) {
        ClipboardContent clipboardContent = new ClipboardContent();
        clipboardContent.putString(data.replaceAll("\\R", "\n"));
        Clipboard.getSystemClipboard().setContent(clipboardContent);
    }

    public void copyFiles(List<Path> paths) {

        Optional.ofNullable(paths)
                .filter((ps) -> !ps.isEmpty())
                .ifPresent(ps -> {
                    ClipboardContent clipboardContent = new ClipboardContent();
                    clipboardContent.putFiles(ps
                            .stream()
                            .map(Path::toFile)
                            .collect(Collectors.toList()));
                    Clipboard.getSystemClipboard().setContent(clipboardContent);
                });
    }

    @WebkitCall(from = "asciidoctor")
    public String readDefaultStylesheet() {

        Optional<Path> optional = Optional.ofNullable(locationConfigBean.getStylesheetDefault())
                .filter((s) -> !s.isEmpty())
                .map(Paths::get)
                .filter(Files::exists);

        Path path = optional.orElse(getConfigPath().resolve("public/css/asciidoctor-default.css"));

        return IOHelper.readFile(path);
    }

    @WebkitCall(from = "asciidoctor")
    public String readAsciidoctorResource(String uri, Integer parent) {

        if (uri.matches(".*?\\.(asc|adoc|ad|asciidoc|md|markdown)") && getIncludeAsciidocResource()) {
            return String.format("link:%s[]", uri);
        }

        PathFinderService fileReader = applicationContext.getBean("pathFinder", PathFinderService.class);
        Path path = fileReader.findPath(uri, parent);

        if (!Files.exists(path)) {
            return "404";
        } else {
            return IOHelper.readFile(path);
        }
    }

    @WebkitCall
    public String clipboardValue() {
        return Clipboard.getSystemClipboard().getString();
    }

    @WebkitCall
    public void pasteRaw() {

        EditorPane editorPane = current.currentEditor();
        Clipboard systemClipboard = Clipboard.getSystemClipboard();
        if (systemClipboard.hasFiles()) {
            Optional<String> block = parserService.toImageBlock(systemClipboard.getFiles());
            if (block.isPresent()) {
                editorPane.insert(block.get());
                return;
            }
        }

        if (systemClipboard.hasImage()) {
            Image image = systemClipboard.getImage();
            Optional<String> block = parserService.toImageBlock(image);
            if (block.isPresent()) {
                editorPane.insert(block.get());
                return;
            }
        }

        editorPane.execCommand("paste-raw-1");
    }

    @WebkitCall
    public void paste() {

        EditorPane editorPane = current.currentEditor();

        Clipboard systemClipboard = Clipboard.getSystemClipboard();
        if (systemClipboard.hasFiles()) {
            Optional<String> block = parserService.toImageBlock(systemClipboard.getFiles());
            if (block.isPresent()) {
                editorPane.insert(block.get());
                return;
            }
        }

        if (systemClipboard.hasImage()) {
            Image image = systemClipboard.getImage();
            Optional<String> block = parserService.toImageBlock(image);
            if (block.isPresent()) {
                editorPane.insert(block.get());
                return;
            }
        }

        try {

            if (systemClipboard.hasHtml() || asciidocWebkitConverter.isHtml(systemClipboard.getString())) {
                String content = Optional.ofNullable(systemClipboard.getHtml()).orElse(systemClipboard.getString());
                if (current.currentTab().isAsciidoc() || current.currentTab().isMarkdown()) {
                    content = (String) asciidocWebkitConverter.call(current.currentTab().htmlToMarkupFunction(), content);
                }
                editorPane.insert(content);
                return;
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

        editorPane.execCommand("paste-raw-1");

    }

    public void adjustSplitPane() {

        final Toggle selectedToggle1 = leftToggleGroup.getSelectedToggle();
        final Toggle selectedToggle2 = rightToggleGroup.getSelectedToggle();
        if (nonNull(selectedToggle1)) {
            ((ToggleButton) selectedToggle1).fire();
        }

        if (nonNull(selectedToggle2)) {
            ((ToggleButton) selectedToggle2).fire();
        }

        if (Objects.isNull(selectedToggle1) && Objects.isNull(selectedToggle2)) {
            ((ToggleButton) leftToggleGroup.getToggles().get(0)).fire();
            ((ToggleButton) rightToggleGroup.getToggles().get(0)).fire();
        }
    }

    public void saveDoc() {
        current.currentTab().saveDoc();
    }

    @FXML
    public void saveDoc(Event actionEvent) {
        current.currentTab().saveDoc();
    }

    public void fitToParent(Node node) {
        AnchorPane.setTopAnchor(node, 0.0);
        AnchorPane.setBottomAnchor(node, 0.0);
        AnchorPane.setLeftAnchor(node, 0.0);
        AnchorPane.setRightAnchor(node, 0.0);
    }

    public void saveAndCloseCurrentTab() {
//        this.saveDoc();
        threadService.runActionLater(current.currentTab()::close);
    }

    public ProgressIndicator getIndikator() {
        return indikator;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public Stage getStage() {
        return stage;
    }

    public Scene getScene() {
        return scene;
    }

    public void setScene(Scene scene) {
        this.scene = scene;
    }

    public void setAsciidocTableAnchor(AnchorPane asciidocTableAnchor) {
        this.asciidocTableAnchor = asciidocTableAnchor;
    }

    public AnchorPane getAsciidocTableAnchor() {
        return asciidocTableAnchor;
    }

    public void setAsciidocTableStage(Stage asciidocTableStage) {
        this.asciidocTableStage = asciidocTableStage;
    }

    public Stage getAsciidocTableStage() {
        return asciidocTableStage;
    }

    public SplitPane getSplitPane() {
        return splitPane;
    }

    public TreeView<Item> getFileSystemView() {
        return fileSystemView;
    }

    public AsciidocTableController getAsciidocTableController() {
        return asciidocTableController;
    }

    public TabPane getTabPane() {
        return tabPane;
    }

    public AnchorPane getRootAnchor() {
        return rootAnchor;
    }

    public int getPort() {
        return port;
    }

    public Path getConfigPath() {

        if (Objects.isNull(configPath)) {
            configPath = getInstallationPath().resolve("conf");
        }

        return configPath;
    }

    public Current getCurrent() {
        return current;
    }

    @FXML
    private void bugReport(ActionEvent actionEvent) {
        browseInDesktop(issuePage);
    }

    @FXML
    private void openCommunityForum(ActionEvent actionEvent) {
        browseInDesktop(issueForum);
    }

    @FXML
    private void openGitterChat(ActionEvent actionEvent) {
        browseInDesktop(gitterChat);
    }

    @FXML
    private void openGithubPage(ActionEvent actionEvent) {
        browseInDesktop(githubPage);
    }

    @FXML
    public void generateCheatSheet(ActionEvent actionEvent) {
        Path cheatsheetPath = getConfigPath().resolve("cheatsheet/cheatsheet.adoc");

        Path tempSheetPath = IOHelper.createTempDirectory(directoryService.workingDirectory(), "cheatsheet")
                .resolve("cheatsheet.adoc");

        IOHelper.copy(cheatsheetPath, tempSheetPath);

        tabService.addTab(tempSheetPath);
    }

    public void setMarkdownTableAnchor(AnchorPane markdownTableAnchor) {
        this.markdownTableAnchor = markdownTableAnchor;
    }

    public AnchorPane getMarkdownTableAnchor() {
        return markdownTableAnchor;
    }

    public void setMarkdownTableStage(Stage markdownTableStage) {
        this.markdownTableStage = markdownTableStage;
    }

    public Stage getMarkdownTableStage() {
        return markdownTableStage;
    }

    public ShortcutProvider getShortcutProvider() {
        return shortcutProvider;
    }

    @WebkitCall
    public void log(Object object) {
        debug(object);
    }

    @WebkitCall
    public void debug(Object object) {
        logger.debug(object + "");
    }

    @WebkitCall
    public void warn(Object object) {
        logger.warn(object + "");
    }

    @WebkitCall
    public void info(Object object) {
        logger.info(object + "");
    }

    @WebkitCall
    public void error(Object object) {
        logger.error(object + "");
    }

    @FXML
    public void createFolder(ActionEvent actionEvent) {

        DialogBuilder dialog = DialogBuilder.newFolderDialog();

        Consumer<String> consumer = result -> {
            if (dialog.isShowing()) {
                dialog.hide();
            }

            if (result.matches(DialogBuilder.FOLDER_NAME_REGEX)) {

                Path path = fileSystemView.getSelectionModel().getSelectedItem()
                        .getValue().getPath();

                Path folderPath = path.resolve(result);

                threadService.runTaskLater(() -> {
                    IOHelper.createDirectories(folderPath);
//                    threadService.runActionLater(() -> {
//                        directoryService.changeWorkigDir(folderPath);
//                    });
                });
            }
        };

        dialog.getEditor().setOnAction(event -> {
            consumer.accept(dialog.getEditor().getText().trim());
        });

        dialog.showAndWait().map(String::trim).ifPresent(consumer);
    }

    @FXML
    public void createFile(ActionEvent actionEvent) {

        DialogBuilder dialog = DialogBuilder.newFileDialog();

        Consumer<String> consumer = result -> {
            if (dialog.isShowing()) {
                dialog.hide();
            }

            if (result.matches(DialogBuilder.FILE_NAME_REGEX)) {

                Path path = fileSystemView.getSelectionModel().getSelectedItem()
                        .getValue().getPath();

                IOHelper.createDirectories(path);
                Optional<Exception> exception = IOHelper.writeToFile(path.resolve(result), "", CREATE_NEW, WRITE);

                if (!exception.isPresent()) {
                    tabService.addTab(path.resolve(result));
                }
            }
        };

        dialog.getEditor().setOnAction(event -> {
            consumer.accept(dialog.getEditor().getText().trim());
        });

        dialog.showAndWait().ifPresent(consumer);
    }

    @FXML
    public void renameFile(Event actionEvent) {

        RenameDialog dialog = RenameDialog.create();

        Path path = fileSystemView.getSelectionModel().getSelectedItem()
                .getValue().getPath();

        TextField editor = dialog.getEditor();
        editor.setText(path.getFileName().toString());

        Consumer<String> consumer = result -> {
            if (dialog.isShowing()) {
                dialog.hide();
            }

            threadService.runTaskLater(() -> {
                if (result.trim().matches("^[^\\\\/:?*\"<>|]+$")) {
                    IOHelper.move(path, path.getParent().resolve(result.trim()));
                }
            });
        };

        editor.setOnAction(event -> {
            consumer.accept(editor.getText());
        });

        if (!Files.isDirectory(path)) {
            int extensionIndex = editor.getText().lastIndexOf(".");
            if (extensionIndex > 0) {
                threadService.runActionLater(() -> {
                    editor.selectRange(0, extensionIndex);
                }, true);
            }
        }

        dialog.showAndWait().ifPresent(consumer);
    }

    @FXML
    public void gitbookToAsciibook(ActionEvent actionEvent) {

        File gitbookRoot = null;
        File asciibookRoot = null;

        BiPredicate<File, File> nullPathPredicate = (p1, p2)
                -> Objects.isNull(p1)
                || Objects.isNull(p2);

        DirectoryChooser gitbookChooser = new DirectoryChooser();
        gitbookChooser.setTitle("Select Gitbook Root Directory");
        gitbookRoot = gitbookChooser.showDialog(null);

        DirectoryChooser asciibookChooser = new DirectoryChooser();
        asciibookChooser.setTitle("Select Blank Asciibook Root Directory");
        asciibookRoot = asciibookChooser.showDialog(null);

        if (nullPathPredicate.test(gitbookRoot, asciibookRoot)) {
            AlertHelper.nullDirectoryAlert();
            return;
        }

        final File finalGitbookRoot = gitbookRoot;
        final File finalAsciibookRoot = asciibookRoot;

        threadService.runTaskLater(() -> {
            logger.debug("Gitbook to Asciibook conversion started");
            indikatorService.startProgressBar();
            GitbookToAsciibookService toAsciiBook = applicationContext.getBean(GitbookToAsciibookService.class);
            toAsciiBook.gitbookToAsciibook(finalGitbookRoot.toPath(), finalAsciibookRoot.toPath());
            indikatorService.stopProgressBar();
            logger.debug("Gitbook to Asciibook conversion ended");
        });

    }

    /*
    @WebkitCall
    public void fillModeList(String mode) {
        threadService.runActionLater(() -> {
            modeList.add(mode);
        });
    }*/
    public void clearImageCache(Path imagePath) {
        rightShowerHider.getShowing()
                .ifPresent(w -> w.clearImageCache(imagePath));
    }

    public void clearImageCache(String imagePath) {
        rightShowerHider.getShowing()
                .ifPresent(w -> w.clearImageCache(imagePath));
    }

    public ObservableList<DocumentMode> getModeList() {
        return modeList;
    }

    public List<String> getSupportedModes() {
        return supportedModes;
    }

    public boolean getIncludeAsciidocResource() {
        return includeAsciidocResource.get();
    }

    public void setIncludeAsciidocResource(boolean includeAsciidocResource) {
        this.includeAsciidocResource.set(includeAsciidocResource);
    }

    public ProgressBar getProgressBar() {
        return progressBar;
    }

    public Timeline getProgressBarTimeline() {
        return progressBarTimeline;
    }

    @FXML
    public void newSlide(ActionEvent actionEvent) {

        DialogBuilder dialog = DialogBuilder.newFolderDialog();

        dialog.showAndWait().map(String::trim).ifPresent(folderName -> {
            if (dialog.isShowing()) {
                dialog.hide();
            }

            if (folderName.matches(DialogBuilder.FOLDER_NAME_REGEX)) {

                Path path = fileSystemView.getSelectionModel().getSelectedItem()
                        .getValue().getPath();

                Path folderPath = path.resolve(folderName);

                threadService.runTaskLater(() -> {
                    IOHelper.createDirectories(folderPath);
                    indikatorService.startProgressBar();
                    IOHelper.copyDirectory(getConfigPath().resolve("slide/frameworks"), folderPath);
                    indikatorService.stopProgressBar();
                    threadService.runActionLater(() -> {
                        tabService.addTab(folderPath.resolve("slide.adoc"));
                    });
                    directoryService.changeWorkigDir(folderPath);
                });
            }
        });

    }

    @FXML
    public void addToFavoriteDir(ActionEvent actionEvent) {
        Path selectedTabPath = tabService.getSelectedTabPath();
        if (Files.isDirectory(selectedTabPath)) {
            ObservableList<String> favoriteDirectories = storedConfigBean.getFavoriteDirectories();
            boolean has = favoriteDirectories.contains(selectedTabPath.toString());
            if (!has) {
                favoriteDirectories.add(selectedTabPath.toString());
            }
        }
    }

    public EditorConfigBean getEditorConfigBean() {
        return editorConfigBean;
    }

    @FXML
    public void showSettings() {

    }

    @WebkitCall(from = "editor-shortcut")
    public void showWorkerPane() {
        threadService.runActionLater(() -> {

            ToggleButton toggleButton = new ToggleButton();
            toggleButton.setPrefSize(20, 80);
            toggleButton.setToggleGroup(rightToggleGroup);
            toggleButton.getStyleClass().addAll("corner-toggle-button", "corner-bottom-half");
            final Label label = new Label("Worker");
            label.setRotate(90);
            toggleButton.setGraphic(new Group(label));
            toggleButton.setPadding(Insets.EMPTY);
            toggleButton.setOnAction(this::toggleWorkerView);
            toggleButton.fire();

            final ObservableList<Node> children = rightTooglesBox.getChildren();

            children.add(toggleButton);

        });
    }

    private void toggleWorkerView(ActionEvent actionEvent) {
        rightShowerHider.showNode(asciidocWebkitConverter);
    }

    public void addRemoveRecentList(Path path) {
        if (Objects.isNull(path)) {
            return;
        }

        threadService.runActionLater(() -> {
            storedConfigBean.getRecentFiles().remove(new Item(path));
            storedConfigBean.getRecentFiles().add(0, new Item(path));
        });
    }

    public void goUp(ActionEvent actionEvent) {
        directoryService.goUp();
    }

    @FXML
    public void copyPath(ActionEvent actionEvent) {
        Path path = tabService.getSelectedTabPath();
        this.cutCopy(path.toString());
    }

    public void closeAllTabs(Event event) {
        ObservableList<Tab> tabs = FXCollections.observableArrayList(tabPane.getTabs());

        tabs.stream()
                .filter(t -> t instanceof MyTab)
                .map(t -> (MyTab) t).sorted((mo1, mo2) -> {
            if (mo1.isNew() && !mo2.isNew()) {
                return -1;
            } else if (mo2.isNew() && !mo1.isNew()) {
                return 1;
            }
            return 0;
        }).forEach(myTab -> {

            if (event.isConsumed()) {
                return;
            }

            ButtonType close = myTab.close();
            if (close == ButtonType.CANCEL) {
                event.consume();
            }
        });

        if (!event.isConsumed()) {
            if (nonNull(detachStage)) {
                detachStage.close();
            }
        }
    }

    public void openTerminalItem(ActionEvent actionEvent) {
        Path selectedTabPath = tabService.getSelectedTabPath();

        if (nonNull(selectedTabPath)) {
            if (!Files.isDirectory(selectedTabPath)) {
                selectedTabPath = selectedTabPath.getParent();
            }
        }

        newTerminal(actionEvent, selectedTabPath);
    }

    public void includeAsSubdocument() {
        String selection = current.currentEditor().editorSelection();

        DialogBuilder fileDialog = DialogBuilder.newFileDialog();
        Optional<String> filenameOptional = fileDialog.showAndWait().map(String::trim);

        if (filenameOptional.isPresent()) {
            String filename = filenameOptional.get();
            Path parent = current.currentTab().getParentOrWorkdir();
            Path path = parent.resolve(filename);

            IOHelper.createDirectories(path.getParent());
            Optional<Exception> exception = IOHelper.writeToFile(path, selection, CREATE_NEW, WRITE);

            if (!exception.isPresent()) {
                current.currentEditor().removeToLineStart();
                current.currentEditor().insert(String.format("\ninclude::%s[]\n", filename));
                tabService.addTab(path);
            }
        }
    }

    public VBox getConfigBox() {
        return configBox;
    }

    @WebkitCall(from = "asciidoctor-image-cache")
    public Integer readImageCache(String target) {
        return current.getCache().get(target);
    }

    public String applyReplacements(String text) {
        return converterProvider.get(previewConfigBean).applyReplacements(text);
    }

    @FXML
    public void toggleRecentView(ActionEvent actionEvent) {
        final ToggleButton source = (ToggleButton) actionEvent.getSource();
        splitPane.setDividerPosition(0, source.isSelected() ? 0.17 : 0);
        if (source.isSelected()) {
            leftShowerHider.showNode(recentListView);
        }
    }

    @FXML
    public void toggleWorkdirView(ActionEvent actionEvent) {
        final ToggleButton source = (ToggleButton) actionEvent.getSource();
        splitPane.setDividerPosition(0, source.isSelected() ? 0.17 : 0);
        if (source.isSelected()) {
            leftShowerHider.showDefaultNode();
        }
    }

    @FXML
    public void toggleOutlineView(ActionEvent actionEvent) {
        final ToggleButton source = (ToggleButton) actionEvent.getSource();
        splitPane.setDividerPosition(0, source.isSelected() ? 0.17 : 0);
        if (source.isSelected()) {
            leftShowerHider.showNode(outlineTreeView);
        }
    }

    @FXML
    public void togglePreviewView(ActionEvent actionEvent) {
        final ToggleButton source = (ToggleButton) actionEvent.getSource();
        splitPane.setDividerPosition(1, source.isSelected() ? 0.59 : 1);
        if (source.isSelected()) {
            rightShowerHider.showDefaultNode();
        }

    }

    @FXML
    public void toggleConfigurationView(ActionEvent actionEvent) {
        final ToggleButton source = (ToggleButton) actionEvent.getSource();
        splitPane.setDividerPosition(1, source.isSelected() ? 0.59 : 1);
        if (source.isSelected()) {
            rightShowerHider.showNode(configBox);
        }

    }

    @FXML
    public void toggleLogView(ActionEvent actionEvent) {
        final ToggleButton source = (ToggleButton) actionEvent.getSource();

        source.getStyleClass().remove("red-label");

        mainVerticalSplitPane.setDividerPosition(0, source.isSelected() ? editorConfigBean.getVerticalSplitter() : 1);

        if (source.isSelected()) {
            bottomShowerHider.showNode(logVBox);
        }

    }

    @FXML
    public void toggleTerminalView(ActionEvent actionEvent) {

        final ToggleButton source = (ToggleButton) actionEvent.getSource();

        mainVerticalSplitPane.setDividerPosition(0, source.isSelected() ? editorConfigBean.getVerticalSplitter() : 1);

        if (source.isSelected()) {
            bottomShowerHider.showNode(terminalHBox);
            if (terminalTabPane.getTabs().isEmpty()) {
                newTerminal(null);
            }
        }

    }

    public ShowerHider getRightShowerHider() {
        return rightShowerHider;
    }

    public void setHostServices(HostServices hostServices) {
        this.hostServices = hostServices;
    }

    public HostServices getHostServices() {
        return hostServices;
    }

    public void setConfigToggleGroup(ToggleGroup configToggleGroup) {
        this.configToggleGroup = configToggleGroup;
    }

    public ToggleGroup getConfigToggleGroup() {
        return configToggleGroup;
    }

    public String getClipboardImageFilePattern() {
        return editorConfigBean.getClipboardImageFilePattern();
    }

    public void initializeTabWatchListener() {
        getTabPane().getTabs().addListener((ListChangeListener<Tab>) c -> {
            c.next();
            List<? extends Tab> addedSubList = c.getAddedSubList();

            threadService.runTaskLater(() -> {
                tabService.applyForEachMyTab(myTab -> {
                    fileWatchService.registerPathWatcher(myTab.getPath());
                }, addedSubList);
            });
        });
    }

    public int getHangFileSizeLimit() {
        return editorConfigBean.getHangFileSizeLimit();
    }

    public void applyCurrentTheme(Stage... stages) {

        ObservableList<EditorConfigBean.Theme> editorTheme = editorConfigBean.getEditorTheme();

        if (editorTheme.isEmpty()) {
            return;
        }

        EditorConfigBean.Theme theme = editorTheme.get(0);
        String themeUri = theme.themeUri();

        if (Objects.isNull(themeUri)) {
            return;
        }

        threadService.runActionLater(() -> {
            try {

                String aceTheme = theme.getAceTheme();
//                editorConfigBean.updateAceTheme(aceTheme);

                for (Stage stage : stages) {
                    if (nonNull(stage) && nonNull(stage.getScene())) {
                        ObservableList<String> stylesheets = stage.getScene().getStylesheets();
                        stylesheets.clear();
                        stylesheets.add(themeUri);
                    }
                }

                terminalConfigBean.changeTheme(theme);

            } catch (Exception e) {
                logger.error("Error occured while setting new theme {}", theme);
            }
        });
    }

    public void applyTheme(EditorConfigBean.Theme theme, Stage... stages) {
        String themeUri = theme.themeUri();

        if (Objects.isNull(themeUri)) {
            return;
        }

        threadService.runActionLater(() -> {
            try {

                for (Stage stage : stages) {
                    if (nonNull(stage) && nonNull(stage.getScene())) {
                        ObservableList<String> stylesheets = stage.getScene().getStylesheets();
                        stylesheets.clear();
                        stylesheets.add(themeUri);
                    }
                }

                String aceTheme = theme.getAceTheme();
                editorConfigBean.updateAceTheme(aceTheme);

                terminalConfigBean.changeTheme(theme);

            } catch (Exception e) {
                logger.error("Error occured while setting new theme {}", theme);
            }
        });
    }

    public void setAsciidocTableScene(Scene asciidocTableScene) {
        this.asciidocTableScene = asciidocTableScene;
    }

    public Scene getAsciidocTableScene() {
        return asciidocTableScene;
    }

    public void setMarkdownTableScene(Scene markdownTableScene) {
        this.markdownTableScene = markdownTableScene;
    }

    public Scene getMarkdownTableScene() {
        return markdownTableScene;
    }

    public boolean isSkipHiddenFiles() {
        return editorConfigBean.isSkipHiddenFiles();
    }
}

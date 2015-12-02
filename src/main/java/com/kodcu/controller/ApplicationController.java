package com.kodcu.controller;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.install4j.api.launcher.ApplicationLauncher;
import com.kodcu.component.*;
import com.kodcu.config.*;
import com.kodcu.engine.AsciidocConverterProvider;
import com.kodcu.engine.AsciidocNashornConverter;
import com.kodcu.engine.AsciidocWebkitConverter;
import com.kodcu.logging.MyLog;
import com.kodcu.logging.TableViewLogAppender;
import com.kodcu.other.*;
import com.kodcu.outline.Section;
import com.kodcu.service.*;
import com.kodcu.service.convert.GitbookToAsciibookService;
import com.kodcu.service.convert.docbook.DocBookConverter;
import com.kodcu.service.convert.ebook.EpubConverter;
import com.kodcu.service.convert.ebook.MobiConverter;
import com.kodcu.service.convert.html.HtmlBookConverter;
import com.kodcu.service.convert.markdown.MarkdownService;
import com.kodcu.service.convert.odf.ODFConverter;
import com.kodcu.service.convert.pdf.AbstractPdfConverter;
import com.kodcu.service.convert.slide.SlideConverter;
import com.kodcu.service.extension.MathJaxService;
import com.kodcu.service.extension.PlantUmlService;
import com.kodcu.service.extension.TreeService;
import com.kodcu.service.extension.chart.ChartProvider;
import com.kodcu.service.shortcut.ShortcutProvider;
import com.kodcu.service.table.AsciidocTableController;
import com.kodcu.service.ui.FileBrowseService;
import com.kodcu.service.ui.IndikatorService;
import com.kodcu.service.ui.TabService;
import com.kodcu.service.ui.TooltipTimeFixService;
import com.kodcu.shell.ShellTab;
import com.sun.javafx.stage.StageHelper;
import de.jensd.fx.fontawesome.AwesomeDude;
import de.jensd.fx.fontawesome.AwesomeIcon;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.HostServices;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
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
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import netscape.javascript.JSObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.embedded.EmbeddedWebApplicationContext;
import org.springframework.context.ApplicationContext;
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
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.PosixFileAttributeView;
import java.nio.file.attribute.PosixFilePermission;
import java.security.CodeSource;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.nio.file.StandardOpenOption.*;


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
    public MenuButton donateButton;
    public ToggleButton toggleConfigButton;
    private Logger logger = LoggerFactory.getLogger(ApplicationController.class);
    public Label odfPro;
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
    public SeparatorMenuItem renameSeparator;
    public SeparatorMenuItem addToFavSeparator;
    private AnchorPane markdownTableAnchor;
    private Stage markdownTableStage;
    public TreeView<Section> outlineTreeView;

    private Path userHome = Paths.get(System.getProperty("user.home"));

    private TreeSet<Section> outlineList = new TreeSet<>();
    private ObservableList<DocumentMode> modeList = FXCollections.observableArrayList();

    private final Pattern bookArticleHeaderRegex =
            Pattern.compile("^:doctype:.*(book|article)", Pattern.MULTILINE);

    private final Pattern forceIncludeRegex =
            Pattern.compile("^:forceinclude:", Pattern.MULTILINE);

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
    private AbstractPdfConverter pdfBookConverter;

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
    private EmbeddedWebApplicationContext server;

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

    private Stage stage;
    private List<WebSocketSession> sessionList = new ArrayList<>();
    private ObjectProperty<Scene> scene = new SimpleObjectProperty<>();
    private AnchorPane asciidocTableAnchor;
    private Stage asciidocTableStage;
    private final Clipboard clipboard = Clipboard.getSystemClipboard();
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

        if (!current.currentPath().isPresent())
            saveDoc();

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
            fileWatchService.registerWatcher(file.toPath());
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

        if (!current.currentPath().isPresent())
            saveDoc();

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
    public void math(String formula, String type, String imagesDir, String imageTarget) {
        mathJaxService.appendFormula(formula, imagesDir, imageTarget);
    }

    @WebkitCall(from = "mathjax.html")
    public void svgToPng(String imagesDir, String imageTarget, String svg, String formula, float width, float height) {
        threadService.runTaskLater(() -> {
            mathJaxService.svgToPng(imagesDir, imageTarget, svg, formula, width, height);
        });
    }

    private void convertMobi() {
        convertMobi(false);
    }

    private void convertMobi(boolean askPath) {

        if (Objects.nonNull(locationConfigBean.getKindlegen())) {
            if (!Files.exists(Paths.get(locationConfigBean.getKindlegen()))) {
                locationConfigBean.setKindlegen(null);
            }
        }

        if (Objects.isNull(locationConfigBean.getKindlegen())) {
            FileChooser fileChooser = directoryService.newFileChooser("Select 'kindlegen' executable");
            File kindlegenFile = fileChooser.showOpenDialog(null);
            if (Objects.isNull(kindlegenFile))
                return;

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

        if (!current.currentPath().isPresent())
            this.saveDoc();

        threadService.runTaskLater(() -> {
            htmlBookService.convert(askPath);
        });
    }

    public void createFileTree(String tree, String type, String imagesDir, String imageTarget, String width, String height) {

        threadService.runTaskLater(() -> {
            treeService.createFileTree(tree, type, imagesDir, imageTarget, width, height);
        });
    }

    public void createHighlightFileTree(String tree, String type, String imagesDir, String imageTarget, String width, String height) {

        threadService.runTaskLater(() -> {
            treeService.createHighlightFileTree(tree, type, imagesDir, imageTarget, width, height);
        });
    }

    @FXML
    public void refreshWorkingDir() {
        current.currentPath().map(Path::getParent).ifPresent(directoryService::changeWorkigDir);
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

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        initializePaths();
//        initializePosixPermissions();
        initializeNashornConverter();
        initializeTerminal();
        initializeDonation();

        terminalTabPane.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            Optional.ofNullable(newValue)
                    .map(e -> ((ShellTab) e))
                    .ifPresent(ShellTab::focusCommandInput);
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

        threadService.runTaskLater(() -> {
            bindConfigurations();
            configurationService.loadConfigurations(this::checkNewVersion);
        });

        port = server.getEmbeddedServletContainer().getPort();

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

        // Convert menu label icons
        AwesomeDude.setIcon(htmlPro, AwesomeIcon.HTML5);
        AwesomeDude.setIcon(pdfPro, AwesomeIcon.FILE_PDF_ALT);
        AwesomeDude.setIcon(ebookPro, AwesomeIcon.BOOK);
        AwesomeDude.setIcon(docbookPro, AwesomeIcon.CODE);
        AwesomeDude.setIcon(odfPro, AwesomeIcon.FILE_WORD_ALT);
        AwesomeDude.setIcon(browserPro, AwesomeIcon.FLASH);


        // Left menu label icons

        AwesomeDude.setIcon(workingDirButton, AwesomeIcon.FOLDER_ALT, "14.0");
        AwesomeDude.setIcon(refreshLabel, AwesomeIcon.REFRESH, "14.0");
        AwesomeDude.setIcon(goUpLabel, AwesomeIcon.LEVEL_UP, "14.0");

        leftButton.setGraphic(AwesomeDude.createIconLabel(AwesomeIcon.ELLIPSIS_H, "14.0"));
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
            if (event.getButton() == MouseButton.PRIMARY)
                this.externalBrowse();
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
                current.currentEditor().rerender();
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
                        if (selectedItems.size() == 1)
                            return true;
                        return !Files.isDirectory(path);
                    })
                    .forEach(tabService::previewDocument);
        });

        deletePathItem.setOnAction(event -> {

            ObservableList<TreeItem<Item>> selectedItems = fileSystemView.getSelectionModel().getSelectedItems();

            List<Path> pathList = selectedItems.stream()
                    .map(e -> e.getValue())
                    .map(e -> e.getPath())
                    .collect(Collectors.toList());

            AlertHelper.deleteAlert(pathList).ifPresent(btn -> {
                if (btn == ButtonType.YES) {
                    pathList
                            .forEach(path -> threadService.runTaskLater(() -> {
                                if (Files.isDirectory(path)) {
                                    IOHelper.deleteDirectory(path);
                                } else {
                                    IOHelper.deleteIfExists(path);
                                }
                            }));
                }

            });

        });

        openFolderTreeItem.setOnAction(event -> {
            Path path = tabService.getSelectedTabPath();
            path = Files.isDirectory(path) ? path : path.getParent();
            if (Objects.nonNull(path)) {
                openInDesktop(path);
            }
        });

        openFolderListItem.setOnAction(event -> {
            Path path = recentListView.getSelectionModel().getSelectedItem().getPath();
            path = Files.isDirectory(path) ? path : path.getParent();
            if (Objects.nonNull(path))
                openInDesktop(path);
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
            if (Objects.isNull(selectedItem))
                return;

            event.consume();
            Path selectedPath = selectedItem.getValue().getPath();
            if (event.getButton() == MouseButton.PRIMARY)
                if (event.getClickCount() == 2)
                    tabService.previewDocument(selectedPath);
        });

        fileSystemView.getSelectionModel().getSelectedIndices().addListener((ListChangeListener<? super Integer>) p -> {

            ObservableList<TreeItem<Item>> selectedItems = fileSystemView.getSelectionModel().getSelectedItems();

            if (Objects.isNull(selectedItems))
                return;

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
                Path path = selectedItems.get(0).getValue().getPath();
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
                    boolean has = favoriteDirectories.contains(path.toString());
                    if (has) addToFavoriteDir.setDisable(true);
                    else addToFavoriteDir.setDisable(false);
                }
            }
        });

        tabService.initializeTabChangeListener(tabPane);
    }

    private void initializeDonation() {
        donateButton.setGraphic(AwesomeDude.createIconLabel(AwesomeIcon.PAYPAL, "14.0"));
        Tooltip.install(donateButton, new Tooltip("Donate to AsciidocFX project"));

        IntStream.of(10, 25, 50, 100)
                .forEach(unit -> {
                    final MenuItem menuItem = MenuItemBuilt.item("$ " + unit)
                            .click(event -> {
                                browseInDesktop(String.format(donationUrl, unit));
                            });
                    donateButton.getItems().add(menuItem);
                });

        donateButton.getItems().add(MenuItemBuilt.item("× Close")
                .click(event -> {
                    if (!toggleConfigButton.isSelected()) {
                        toggleConfigButton.fire();
                    }

                    if (Objects.nonNull(configToggleGroup)) {
                        final ObservableList<Toggle> toggles = configToggleGroup.getToggles();
                        if (!toggles.isEmpty()) {
                            ((ToggleButton) toggles.get(0)).fire();
                        }
                    }

                    threadService.schedule(() -> {
                        threadService.runActionLater(() -> {
                            editorConfigBean.showDonateProperty().setValue(false);
                        });
                    }, 2, TimeUnit.SECONDS);
                }));
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

    private void initializeTerminal() {
        Button newTerminalButton = AwesomeDude.createIconButton(AwesomeIcon.PLUS);
        Button closeTerminalButton = AwesomeDude.createIconButton(AwesomeIcon.CLOSE);

        terminalLeftBox.getChildren().add(newTerminalButton);
        terminalLeftBox.getChildren().add(closeTerminalButton);

        Tooltip.install(newTerminalButton, new Tooltip("New Terminal"));
        Tooltip.install(closeTerminalButton, new Tooltip("Close Terminal"));

        newTerminalButton.setOnAction(this::newTerminal);
        closeTerminalButton.setOnAction(this::closeTerminal);

        closeTerminalButton.setFocusTraversable(false);
        newTerminalButton.setFocusTraversable(false);

    }

    private AtomicInteger terminalNumber = new AtomicInteger();

    @FXML
    public void newTerminal(ActionEvent actionEvent, Path... path) {

        if (!terminalToggleButton.isSelected()) {
            terminalToggleButton.fire();
        }

        if (terminalTabPane.getTabs().isEmpty()) {
            terminalNumber.set(0);
        }

        ShellTab shellTab = applicationContext.getBean(ShellTab.class);
        shellTab.setText(String.format("Terminal#%d", terminalNumber.incrementAndGet()));
        terminalTabPane.getTabs().add(shellTab);
        terminalTabPane.getSelectionModel().select(shellTab);

        Path terminalPath = Optional.ofNullable(path).filter(e -> e.length > 0).map(e -> e[0]).orElse(null);
        shellTab.initialize(terminalPath);


    }

    @FXML
    public void closeTerminal(ActionEvent actionEvent) {
        ShellTab shellTab = (ShellTab) terminalTabPane.getSelectionModel().getSelectedItem();
        Optional.ofNullable(shellTab).ifPresent(ShellTab::destroy);
    }

    public void closeAllTerminal(ActionEvent actionEvent) {
        ObservableList<Tab> tabs = FXCollections.observableArrayList(terminalTabPane.getTabs());

        for (Tab tab : tabs) {
            ((ShellTab) tab).destroy();
        }
    }

    public void closeOtherTerminals(ActionEvent actionEvent) {
        ObservableList<Tab> tabs = FXCollections.observableArrayList(terminalTabPane.getTabs());
        tabs.remove(terminalTabPane.getSelectionModel().getSelectedItem());

        for (Tab tab : tabs) {
            ((ShellTab) tab).destroy();
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


    private void initializePosixPermissions() {

        try {
            PosixFileAttributeView fileAttributeView = Files.getFileAttributeView(configPath, PosixFileAttributeView.class);
            if (Objects.nonNull(fileAttributeView)) {
                HashSet<PosixFilePermission> perms = new HashSet<>();
                perms.add(PosixFilePermission.GROUP_WRITE);
                perms.add(PosixFilePermission.OTHERS_WRITE);
                perms.add(PosixFilePermission.OWNER_WRITE);
                perms.add(PosixFilePermission.GROUP_READ);
                perms.add(PosixFilePermission.OTHERS_READ);
                perms.add(PosixFilePermission.OWNER_READ);
                perms.add(PosixFilePermission.GROUP_EXECUTE);
                perms.add(PosixFilePermission.OTHERS_EXECUTE);
                perms.add(PosixFilePermission.OWNER_EXECUTE);

                Files.setPosixFilePermissions(configPath, perms);
            }
        } catch (Exception e) {
            logger.error("Problem occured while setting write permissions to {}", configPath, e);
        }
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
            if (StageHelper.getStages().size() == 1) {
                if (!newValue) {
                    saveAllTabs();
                } else {
                    loadAllTabs();
                }
            }
        });

    }

/*    public void initializeAutoSaver() {

        final AtomicReference<Instant> currentTime = new AtomicReference<>(Instant.now());

        stage.addEventFilter(EventType.ROOT, event -> {
            currentTime.set(Instant.now());
        });

        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (currentTime.get().plusSeconds(3).isBefore(Instant.now())) {
                    currentTime.set(Instant.now());

                    if (!stage.isFocused()) {
                        return;
                    }

                    saveAllTabs();
                }
            }
        }, 0, 500);
    }*/

    private void bindConfigurations() {

        /*ObjectProperty<Color> colorProperty = editorConfigBean.backgroundColorProperty();
        ObjectProperty<Color> innerColorProperty = editorConfigBean.innerBackgroundColorProperty();

        colorProperty.addListener((observable, oldValue, newValue) -> {
            if (Objects.nonNull(newValue)) {
                String backgroundColor = Integer.toHexString(newValue.hashCode());
                if (Objects.isNull(innerColorProperty.get())) {
                    rootAnchor.setStyle(String.format("-fx-base: #%s;", backgroundColor));
                } else {
                    String innerBackgroundColor = Integer.toHexString(innerColorProperty.get().hashCode());
                    rootAnchor.setStyle(String.format("-fx-base: #%s;-fx-control-inner-background: #%s", backgroundColor, innerBackgroundColor));
                }
            }
        });

        innerColorProperty.addListener((observable, oldValue, newValue) -> {
            if (Objects.nonNull(newValue)) {
                String innerBackgroundColor = Integer.toHexString(newValue.hashCode());
                if (Objects.isNull(colorProperty.get())) {
                    rootAnchor.setStyle(String.format("-fx-control-inner-background: #%s;", innerBackgroundColor));
                } else {
                    String backgroundColor = Integer.toHexString(colorProperty.get().hashCode());
                    rootAnchor.setStyle(String.format("-fx-base: #%s;-fx-control-inner-background: #%s", backgroundColor, innerBackgroundColor));
                }
            }
        });*/

        donateButton.managedProperty().bind(donateButton.visibleProperty());
        editorConfigBean.showDonateProperty().bindBidirectional(donateButton.visibleProperty());

        locationConfigBean.mathjaxProperty().addListener((observable, oldValue, newValue) -> {
            if (Objects.nonNull(newValue)) {
                if (!newValue.equals(oldValue)) {
                    mathJaxService.reload();
                }
            }
        });

        ObservableList<SplitPane.Divider> dividers = splitPane.getDividers();

        dividers.get(0).positionProperty().bindBidirectional(editorConfigBean.firstSplitterProperty());
        dividers.get(1).positionProperty().bindBidirectional(editorConfigBean.secondSplitterProperty());

        editorConfigBean.showGutterProperty().addListener((observable, oldValue, newValue) -> {
            if (Objects.nonNull(newValue)) {
                applyForAllEditorPanes(editorPane -> editorPane.setShowGutter(newValue));
            }
        });

        editorConfigBean.useWrapModeProperty().addListener((observable, oldValue, newValue) -> {
            if (Objects.nonNull(newValue)) {
                applyForAllEditorPanes(editorPane -> editorPane.setUseWrapMode(newValue));
            }
        });

        editorConfigBean.wrapLimitProperty().addListener((observable, oldValue, newValue) -> {
            if (Objects.nonNull(newValue)) {
                applyForAllEditorPanes(editorPane -> editorPane.setWrapLimitRange(newValue));
            }
        });

        ListChangeListener<String> themeChangeListener = c -> {
            c.next();
            if (c.wasAdded()) {
                String theme = c.getList().get(0);
                applyForAllEditorPanes(editorPane -> editorPane.setTheme(theme));
            }
        };

        editorConfigBean.editorThemeProperty().addListener((observable, oldValue, newValue) -> {
            if (Objects.nonNull(newValue)) {
                newValue.addListener(themeChangeListener);
            }
            if (Objects.nonNull(oldValue)) {
                oldValue.removeListener(themeChangeListener);
            }
        });

        editorConfigBean.fontSizeProperty().addListener((observable, oldValue, newValue) -> {
            applyForAllEditorPanes(editorPane -> editorPane.setFontSize(newValue.intValue()));
        });

        ObservableList<Item> recentFilesList = storedConfigBean.getRecentFiles();
        ObservableList<String> favoriteDirectories = storedConfigBean.getFavoriteDirectories();

        recentListView.setCellFactory(param -> {
            ListCell<Item> cell = new ListCell<Item>() {
                @Override
                protected void updateItem(Item item, boolean empty) {
                    super.updateItem(item, empty);
                    if (Objects.nonNull(item)) {
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
                if (size > 0) this.addItemToFavoriteDir(size++ - 2, path);
                else this.addItemToFavoriteDir(size++, path);
            }
            if (empty) this.includeClearAllToFavoriteDir();
        });

        storedConfigBean.workingDirectoryProperty().addListener((observable, oldValue, newValue) -> {
            if (Objects.nonNull(newValue) && Objects.isNull(oldValue)) {
                directoryService.changeWorkigDir(Paths.get(newValue));
            }
        });

    }

    private void getImageSizeInfo(String path, Object info) {

        if (path.startsWith("/"))
            path = path.substring(1);

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

        if (Objects.isNull(parent))
            return;

        Path imagePath = parent.resolve(path);

        if (Files.notExists(imagePath))
            return;

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
            MyTab myTab = (MyTab) tab;
            editorPaneConsumer.accept(myTab.getEditorPane());
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
                    } else addToFavSeparator.setVisible(false);
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
                    directoryService.changeWorkigDir(Paths.get(path));
                }));
    }

    private void checkNewVersion() {
        threadService.schedule(() -> {
            try {
                if (!editorConfigBean.getAutoUpdate())
                    return;

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

        if (!current.currentPath().isPresent())
            this.saveDoc();

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
            Object readValue = mapper.readValue(configPath.resolve("ace_doctypes.json").toFile(), new TypeReference<List<DocumentMode>>() {
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

    private void initializePaths() {

        try {
            CodeSource codeSource = ApplicationController.class.getProtectionDomain().getCodeSource();
            File jarFile = new File(codeSource.getLocation().toURI().getPath());
            installationPath = jarFile.toPath().getParent().getParent();
            configPath = installationPath.resolve("conf");

            Optional<String> linuxHome = Optional.ofNullable(System.getenv("HOME"));
            Optional<String> windowsHome = Optional.ofNullable(System.getenv("USERPROFILE"));

            Stream.<Optional<String>>of(linuxHome, windowsHome)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .map(Paths::get)
                    .findFirst()
                    .ifPresent(path -> logPath = path.resolve(configDirName).resolve("log").toString());

        } catch (URISyntaxException e) {
            logger.error("Problem occured while resolving conf and log paths", e);
        }

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

            clipboard.setContent(clipboardContent);
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
                if (item == getItem())
                    return;

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
            if (Objects.nonNull(logLevelItem)) {
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
            openInDesktop(Paths.get(logPath));
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
                if (Objects.nonNull(message)) {
                    if (!result.get())
                        result.set(message.toLowerCase().contains(newValue.toLowerCase()));
                }

                String level = myLog.getLevel();
                String toggleText = ((ToggleButton) toggleGroup.getSelectedToggle()).getText();
                boolean inputContains = level.toLowerCase().contains(newValue.toLowerCase());

                if (Objects.nonNull(level)) {
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
        rightShowerHider.getShowing().browse();
    }

    ChangeListener<Boolean> outlineTabChangeListener;

    @WebkitCall(from = "index")
    public void fillOutlines(Object doc) {

        if (outlineTreeView.isVisible()) {
            converterProvider.get(previewConfigBean).fillOutlines(doc);
        }

        if (Objects.nonNull(outlineTabChangeListener)) {
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

            outlineTreeView.getRoot().getChildren().clear();

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

        if (Objects.isNull(parentLineNo))
            outlineList.add(section);
        else {
            Integer parentLine = Integer.valueOf(parentLineNo);
            Optional<Section> parentSection = outlineList.stream()
                    .filter(e -> e.getLineno().equals(parentLine))
                    .findFirst();

            if (parentSection.isPresent())
                parentSection.get().getSubsections().add(section);
            else {
                this.traverseEachSubSection(outlineList, parentLine, section);
            }
        }
    }

    private void traverseEachSubSection(TreeSet<Section> sections, Integer parentLine, Section section) {
        sections.stream().forEach(s -> {
            Optional<Section> subs = s.getSubsections().stream()
                    .filter(e -> e.getLineno().equals(parentLine))
                    .findFirst();

            if (subs.isPresent())
                subs.get().getSubsections().add(section);
            else
                this.traverseEachSubSection(s.getSubsections(), parentLine, section);
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
    public void openDoc(Event event) {
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
        rightShowerHider.getShowing().onscroll(pos, max);
    }

    @WebkitCall(from = "editor")
    public void scrollByLine(String text) {
        threadService.runActionLater(() -> {
            try {
                rightShowerHider.getShowing().scrollByLine(text);
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
                rightShowerHider.getShowing().scrollByPosition(selection);

            } catch (Exception e) {
                logger.debug(e.getMessage(), e);
            }
        });
    }

    @WebkitCall(from = "asciidoctor-uml")
    public void uml(String uml, String type, String imagesDir, String imageTarget) throws IOException {
        plantuml(uml, type, imagesDir, imageTarget);
    }

    @WebkitCall(from = "asciidoctor-uml")
    public void plantuml(String uml, String type, String imagesDir, String imageTarget) throws IOException {

        threadService.runTaskLater(() -> {
            plantUmlService.plantUml(uml, type, imagesDir, imageTarget);
        });
    }

    @WebkitCall(from = "asciidoctor-ditaa")
    public void ditaa(String ditaa, String type, String imagesDir, String imageTarget) throws IOException {
        String plantUmlString = "@startditaa\n" + ditaa + "\n@endditaa\n";
        this.plantuml(plantUmlString, type, imagesDir, imageTarget);
    }

    @WebkitCall(from = "asciidoctor-chart")
    public void chartBuildFromCsv(String csvFile, String imagesDir, String imageTarget, String chartType, String options) {

        threadService.runActionLater(() -> {
            if (Objects.isNull(imageTarget) || Objects.isNull(chartType))
                return;

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

        if (Objects.isNull(imageTarget) || Objects.isNull(chartType))
            return;

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
        if (Objects.nonNull(options)) {
            String[] optPart = options.split(",");

            for (String opt : optPart) {
                String[] keyVal = opt.split("=");
                if (keyVal.length != 2)
                    continue;
                optMap.put(keyVal[0], keyVal[1]);
            }
        }
        return optMap;
    }

    @WebkitCall(from = "converter.js")
    public void completeWebWorkerExceptionally(Object message, Object taskId) {
        threadService.runTaskLater(() -> {
            final Map<String, CompletableFuture<ConverterResult>> workerTasks = asciidocWebkitConverter.getWebWorkerTasks();
            final CompletableFuture<ConverterResult> completableFuture = workerTasks.get(taskId);
            if (!completableFuture.isDone()) {
                completableFuture.completeExceptionally(new RuntimeException(String.format("Task: %s is not completed - %s", taskId, message)));
            }
        });
    }


    @WebkitCall(from = "converter.js")
    public void completeWebWorker(JSObject object) {
        threadService.runTaskLater(() -> {
            final ConverterResult converterResult = new ConverterResult(object);
            final Map<String, CompletableFuture<ConverterResult>> workerTasks = asciidocWebkitConverter.getWebWorkerTasks();
            final CompletableFuture<ConverterResult> completableFuture = workerTasks.get(converterResult.getTaskId());
            completableFuture.complete(converterResult);
        });
    }

    @WebkitCall(from = "editor")
    public void appendWildcard() {
        current.currentTab().setChangedProperty(true);
    }

    private AtomicReference<Tuple<String, String>> latestTupleReference = new AtomicReference<>();
    private Semaphore renderLoopSemaphore = new Semaphore(1);

    private void renderLoop() throws InterruptedException {

        renderLoopSemaphore.acquire();

        if (stopRendering.get()) {
            return;
        }

        Tuple<String, String> tuple = latestTupleReference.get();

        if (Objects.isNull(tuple))
            return;

        String text = tuple.getKey();
        String mode = tuple.getValue();

        try {

            boolean bookArticleHeader = this.bookArticleHeaderRegex.matcher(text).find();
            boolean forceInclude = this.forceIncludeRegex.matcher(text).find();

            if ("asciidoc".equalsIgnoreCase(mode)) {

                if (bookArticleHeader && !forceInclude)
                    setIncludeAsciidocResource(true);

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
                if (liveReloadPane.getReady()) {
                    liveReloadPane.updateDomdom();
                } else {
                    liveReloadPane.load(String.format(liveUrl, port, directoryService.interPath()));
                }

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
        renderLoopSemaphore.release();
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
        clipboardContent.putString(data);
        clipboard.setContent(clipboardContent);
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
                    clipboard.setContent(clipboardContent);
                });
    }

    @WebkitCall(from = "asciidoctor")
    public String readDefaultStylesheet() {

        Optional<Path> optional = Optional.ofNullable(locationConfigBean.getStylesheetDefault())
                .filter((s) -> !s.isEmpty())
                .map(Paths::get)
                .filter(Files::exists);

        Path path = optional.orElse(configPath.resolve("public/css/asciidoctor-default.css"));

        return IOHelper.readFile(path);
    }

    @WebkitCall(from = "asciidoctor")
    public String readAsciidoctorResource(String uri, Integer parent) {

        if (uri.matches(".*?\\.(asc|adoc|ad|asciidoc|md|markdown)") && getIncludeAsciidocResource())
            return String.format("link:%s[]", uri);

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
        return clipboard.getString();
    }

    @WebkitCall
    public void pasteRaw() {

        EditorPane editorPane = current.currentEditor();
        if (clipboard.hasFiles()) {
            Optional<String> block = parserService.toImageBlock(clipboard.getFiles());
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

        if (clipboard.hasFiles()) {
            Optional<String> block = parserService.toImageBlock(clipboard.getFiles());
            if (block.isPresent()) {
                editorPane.insert(block.get());
                return;
            }
        }

        try {

            if (clipboard.hasHtml() || asciidocWebkitConverter.isHtml(clipboard.getString())) {
                String content = Optional.ofNullable(clipboard.getHtml()).orElse(clipboard.getString());
                if (current.currentTab().isAsciidoc() || current.currentTab().isMarkdown())
                    content = (String) asciidocWebkitConverter.call(current.currentTab().htmlToMarkupFunction(), content);
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
        if (Objects.nonNull(selectedToggle1)) {
            ((ToggleButton) selectedToggle1).fire();
        }

        if (Objects.nonNull(selectedToggle2)) {
            ((ToggleButton) selectedToggle2).fire();
        }

        if (Objects.isNull(selectedToggle1) && Objects.isNull(selectedToggle2)) {
            ((ToggleButton) leftToggleGroup.getToggles().get(0)).fire();
            ((ToggleButton) rightToggleGroup.getToggles().get(0)).fire();
        }
    }

    @WebkitCall
    public void debug(String message) {
        logger.debug(message);
    }

    @WebkitCall
    public void error(String message) {
        logger.error(message);
    }

    @WebkitCall
    public void info(String message) {
        logger.info(message);
    }

    @WebkitCall
    public void warn(String message) {
        logger.warn(message);
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
        this.saveDoc();
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
        return scene.get();
    }

    public ObjectProperty<Scene> sceneProperty() {
        return scene;
    }

    public void setScene(Scene scene) {
        this.scene.set(scene);
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
        Path cheatsheetPath = configPath.resolve("cheatsheet/cheatsheet.adoc");

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

    public void log(Object... objects) {
        for (Object object : objects) {
            logger.error(object + "");
        }
    }

    @FXML
    public void createFolder(ActionEvent actionEvent) {

        DialogBuilder dialog = DialogBuilder.newFolderDialog();

        Consumer<String> consumer = result -> {
            if (dialog.isShowing())
                dialog.hide();

            if (result.matches(DialogBuilder.FOLDER_NAME_REGEX)) {

                Path path = fileSystemView.getSelectionModel().getSelectedItem()
                        .getValue().getPath();

                Path folderPath = path.resolve(result);


                threadService.runTaskLater(() -> {
                    IOHelper.createDirectories(folderPath);
                    threadService.runActionLater(() -> {
                        directoryService.changeWorkigDir(folderPath);
                    });
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
            if (dialog.isShowing())
                dialog.hide();

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
    public void renameFile(ActionEvent actionEvent) {

        RenameDialog dialog = RenameDialog.create();

        Path path = fileSystemView.getSelectionModel().getSelectedItem()
                .getValue().getPath();

        dialog.getEditor().setText(path.getFileName().toString());

        Consumer<String> consumer = result -> {
            if (dialog.isShowing())
                dialog.hide();

            if (result.trim().matches("^[^\\\\/:?*\"<>|]+$"))
                IOHelper.move(path, path.getParent().resolve(result.trim()));
        };

        dialog.getEditor().setOnAction(event -> {
            consumer.accept(dialog.getEditor().getText());
        });

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
        rightShowerHider.getShowing().clearImageCache(imagePath);
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
            if (dialog.isShowing())
                dialog.hide();

            if (folderName.matches(DialogBuilder.FOLDER_NAME_REGEX)) {

                Path path = fileSystemView.getSelectionModel().getSelectedItem()
                        .getValue().getPath();

                Path folderPath = path.resolve(folderName);

                threadService.runTaskLater(() -> {
                    IOHelper.createDirectories(folderPath);
                    indikatorService.startProgressBar();
                    IOHelper.copyDirectory(configPath.resolve("slide/frameworks"), folderPath);
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
        if (Objects.isNull(path))
            return;

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

        tabs.stream().map(t -> (MyTab) t).sorted((mo1, mo2) -> {
            if (mo1.isNew() && !mo2.isNew())
                return -1;
            else if (mo2.isNew() && !mo1.isNew()) {
                return 1;
            }
            return 0;
        }).forEach(myTab -> {

            if (event.isConsumed())
                return;

            ButtonType close = myTab.close();
            if (close == ButtonType.CANCEL)
                event.consume();
        });
    }

    public void openTerminalItem(ActionEvent actionEvent) {
        Path selectedTabPath = tabService.getSelectedTabPath();

        if (Objects.nonNull(selectedTabPath)) {
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

        mainVerticalSplitPane.setDividerPosition(0, source.isSelected() ? 0.40 : 1);

        if (source.isSelected()) {
            bottomShowerHider.showNode(logVBox);
        }


    }

    @FXML
    public void toggleTerminalView(ActionEvent actionEvent) {

        final ToggleButton source = (ToggleButton) actionEvent.getSource();

        mainVerticalSplitPane.setDividerPosition(0, source.isSelected() ? 0.40 : 1);

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
}
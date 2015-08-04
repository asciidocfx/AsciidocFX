package com.kodcu.controller;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.install4j.api.launcher.ApplicationLauncher;
import com.kodcu.bean.RecentFiles;
import com.kodcu.component.*;
import com.kodcu.config.*;
import com.kodcu.logging.MyLog;
import com.kodcu.logging.TableViewLogAppender;
import com.kodcu.other.*;
import com.kodcu.outline.Section;
import com.kodcu.service.*;
import com.kodcu.service.config.YamlService;
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
import com.kodcu.service.ui.*;
import com.sun.javafx.application.HostServicesDelegate;
import com.sun.webkit.dom.DocumentFragmentImpl;
import de.jensd.fx.fontawesome.AwesomeDude;
import de.jensd.fx.fontawesome.AwesomeIcon;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.*;
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
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTreeCell;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebHistory;
import javafx.scene.web.WebView;
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
import org.yaml.snakeyaml.Yaml;

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
import java.security.CodeSource;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static java.nio.file.StandardOpenOption.*;


@Component
public class ApplicationController extends TextWebSocketHandler implements Initializable {


    public TabPane previewTabPane;

    @Autowired
    public HtmlPane htmlPane;
    public Label odfPro;
    public VBox logVBox;
    public Label statusText;
    public SplitPane editorSplitPane;
    public Label statusMessage;
    public Label showHideLogs;
    public Tab outlineTab;
    public MenuItem newFolder;
    public MenuItem newSlide;
    public Menu newMenu;
    public ProgressBar progressBar;
    public Menu favoriteDirMenu;
    public MenuItem addToFavoriteDir;
    public MenuItem afxVersionItem;

    private Logger logger = LoggerFactory.getLogger(ApplicationController.class);

    private Path userHome = Paths.get(System.getProperty("user.home"));

    private TreeSet<Section> outlineList = new TreeSet<>();
    private ObservableList<DocumentMode> modeList = FXCollections.observableArrayList();

    private final Pattern bookArticleHeaderRegex =
            Pattern.compile("^:doctype:.*(book|article)", Pattern.MULTILINE);

    private final Pattern forceIncludeRegex =
            Pattern.compile("^:forceinclude:", Pattern.MULTILINE);

    public CheckMenuItem hidePreviewPanel;
    public MenuItem hideFileBrowser;
    public MenuButton panelShowHideMenuButton;
    public MenuItem renameFile;
    public MenuItem newFile;
    public TabPane tabPane;
    public SplitPane splitPane;
    public SplitPane splitPaneVertical;
    public TreeView<Item> treeView;
    public Label workingDirButton;
    public Label goHomeLabel;
    public Label refreshLabel;
    public AnchorPane rootAnchor;
    public ProgressIndicator indikator;
    public ListView<String> recentListView;
    public MenuItem openFileTreeItem;
    public MenuItem deletePathItem;
    public MenuItem openFolderTreeItem;
    public MenuItem openFileListItem;
    public MenuItem openFolderListItem;
    public MenuItem copyPathTreeItem;
    public MenuItem copyPathListItem;
    public MenuItem copyTreeItem;
    public MenuItem copyListItem;
    public MenuButton leftButton;
    private WebView mathjaxView;
    public Label htmlPro;
    public Label pdfPro;
    public Label ebookPro;
    public Label docbookPro;
    public Label browserPro;
    public SeparatorMenuItem renameSeparator;
    public SeparatorMenuItem addToFavSeparator;
    private AnchorPane markdownTableAnchor;
    private Stage markdownTableStage;

    private TreeView<Section> sectionTreeView;

    private BooleanProperty stopRendering = new SimpleBooleanProperty(false);

    private AtomicBoolean includeAsciidocResource = new AtomicBoolean(false);

    private static ObservableList<MyLog> logList = FXCollections.synchronizedObservableList(FXCollections.observableArrayList());


    @Autowired
    private EditorConfigBean editorConfigBean;
    @Autowired
    private PreviewConfigBean previewConfigBean;
    @Autowired
    private HtmlConfigBean htmlConfigBean;
    @Autowired
    private OdfConfigBean odfConfigBean;
    @Autowired
    private DocbookConfigBean docbookConfigBean;

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private ConfigurationService configurationService;

    @Autowired
    private AsciidocTableController asciidocTableController;

    @Autowired
    private GitbookToAsciibookService gitbookToAsciibook;

    @Autowired
    private PathOrderService pathOrder;

    @Autowired
    private TreeService treeService;

    @Autowired
    private TooltipTimeFixService tooltipTimeFixService;

    @Autowired
    private TabService tabService;

    @Autowired
    private ODFConverter odfConverter;

    @Autowired
    private PathResolverService pathResolver;

    @Autowired
    private PlantUmlService plantUmlService;

    @Autowired
    private EditorService editorService;

    @Autowired
    private MathJaxService mathJaxService;

    @Autowired
    private YamlService yamlService;

    @Autowired
    private WebviewService webviewService;

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
    private AwesomeService awesomeService;

    @Autowired
    private DirectoryService directoryService;

    @Autowired
    private ThreadService threadService;

    @Autowired
    private DocumentService documentService;

    @Autowired
    private EpubController epubController;

    @Autowired
    private ShortcutProvider shortcutProvider;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private Base64.Encoder base64Encoder;

    @Autowired
    private ChartProvider chartProvider;

    @Autowired
    private MarkdownService markdownService;

    private Stage stage;
    private StringProperty lastRendered = new SimpleStringProperty();
    private List<WebSocketSession> sessionList = new ArrayList<>();
    private ObjectProperty<Scene> scene = new SimpleObjectProperty<>();
    private AnchorPane asciidocTableAnchor;
    private Stage asciidocTableStage;
    private final Clipboard clipboard = Clipboard.getSystemClipboard();
    private final ObservableList<String> recentFilesList = FXCollections.synchronizedObservableList(FXCollections.observableArrayList());
    private int port = 8080;
    private HostServicesDelegate hostServices;
    private Path configPath;
    private BooleanProperty fileBrowserVisibility = new SimpleBooleanProperty(false);
    private BooleanProperty previewPanelVisibility = new SimpleBooleanProperty(false);

    private final List<String> bookNames = Arrays.asList("book.asc", "book.txt", "book.asciidoc", "book.adoc", "book.ad");

    private Map<String, String> shortCuts;
    private RecentFiles recentFiles;

    private final ChangeListener<String> lastRenderedChangeListener = (observableValue, old, nev) -> {

        if (Objects.isNull(nev))
            return;

        threadService.runActionLater(() -> {
            htmlPane.refreshUI(nev);
        });

        sessionList.stream().filter(e -> e.isOpen()).forEach(e -> {
            try {
                e.sendMessage(new TextMessage(nev));
            } catch (Exception ex) {
                logger.error("Problem occured while sending content over WebSocket", ex);
            }
        });
    };

    @Value("${application.version}")
    private String version;

    @Autowired
    private SlideConverter slideConverter;

    @Autowired
    private SlidePane slidePane;
    private Path installationPath;
    private Path logPath;

    @Autowired
    private LiveReloadPane liveReloadPane;
    private List<String> supportedModes;

    @Autowired
    private FileWatchService fileWatchService;
    private PreviewTab previewTab;

    private Timeline progressBarTimeline = null;
    private ObservableList<String> favoriteDirectories = FXCollections.observableArrayList();

    @Autowired
    private FileWatchService watchService;

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

        threadService.runTaskLater(() -> {
            if (!current.currentPath().isPresent())
                saveDoc();

            threadService.runActionLater(() -> {

                Path currentTabPath = current.currentPath().get();
                Path currentTabPathDir = currentTabPath.getParent();
                String tabText = current.getCurrentTabText().replace("*", "").trim();

                Path docbookPath;

                if (askPath) {
                    FileChooser fileChooser = directoryService.newFileChooser("Save Docbook file");
                    fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Docbook", "*.xml"));
                    docbookPath = fileChooser.showSaveDialog(null).toPath();
                } else
                    docbookPath = currentTabPathDir.resolve(tabText + ".xml");

                Consumer<String> step = docbook -> {
                    final String finalDocbook = docbook;
                    threadService.runTaskLater(() -> {
                        IOHelper.writeToFile(docbookPath, finalDocbook, CREATE, TRUNCATE_EXISTING, WRITE);
                    });
                    threadService.runActionLater(() -> {
                        getRecentFilesList().remove(docbookPath.toString());
                        getRecentFilesList().add(0, docbookPath.toString());
                    });
                };

                docBookConverter.convert(false, step);

            });

        });

    }

    private void convertEpub() {
        convertEpub(false);
    }

    private void convertEpub(boolean askPath) {
        epubConverter.produceEpub3(askPath);
    }

    public void appendFormula(String fileName, String formula) {
        mathJaxService.appendFormula(fileName, formula);
    }

    public void svgToPng(String fileName, String svg, String formula, float width, float height) {
        threadService.runTaskLater(() -> {
            mathJaxService.svgToPng(fileName, svg, formula, width, height);
        });
    }

    private void convertMobi() {
        convertMobi(false);
    }

    private void convertMobi(boolean askPath) {


        if (Objects.nonNull(editorConfigBean.getKindlegen())) {
            if (!Files.exists(editorConfigBean.getKindlegen())) {
                editorConfigBean.setKindlegen(null);
            }
        }

        if (Objects.isNull(editorConfigBean.getKindlegen())) {
            FileChooser fileChooser = directoryService.newFileChooser("Select 'kindlegen' executable");
            File kindlegenFile = fileChooser.showOpenDialog(null);
            if (Objects.isNull(kindlegenFile))
                return;

            editorConfigBean.setKindlegen(kindlegenFile.toPath().getParent());
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

    public void createFileTree(String tree, String type, String fileName, String width, String height) {

        threadService.runTaskLater(() -> {
            treeService.createFileTree(tree, type, fileName, width, height);
        });
    }

    public void createHighlightFileTree(String tree, String type, String fileName, String width, String height) {

        threadService.runTaskLater(() -> {
            treeService.createHighlightFileTree(tree, type, fileName, width, height);
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
                threadService.runActionLater(() -> {
                    htmlPane.updateBase64Url(index, imageBase64);

                });
            } catch (Exception e) {
                logger.error("Problem occured while converting image to base64 for {}", url);
            }
        });
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        threadService.runActionLater(() -> {
            configurationService.loadConfigurations();
            this.bindConfigurations();
        }, true);

        port = server.getEmbeddedServletContainer().getPort();

        progressBar.prefWidthProperty().bind(previewTabPane.widthProperty());

        progressBarTimeline = new Timeline(
                new KeyFrame(
                        Duration.ZERO,
                        new KeyValue(progressBar.progressProperty(), 0)
                ),
                new KeyFrame(
                        Duration.seconds(15),
                        new KeyValue(progressBar.progressProperty(), 1)
                ));

        this.previewTab = new PreviewTab("Preview", htmlPane);
        this.previewTab.setClosable(false);

        threadService.runActionLater(() -> {
            previewTabPane.getTabs().add(previewTab);
        }, true);

        // Hide tab if one in tabpane
        previewTabPane.getTabs().addListener((ListChangeListener) change -> {
            final StackPane header = (StackPane) previewTabPane.lookup(".tab-header-area");

            if (header != null) {
                if (previewTabPane.getTabs().size() == 1)
                    header.setPrefHeight(0);
                else
                    header.setPrefHeight(-1);
            }
        });
        previewTabPane.setRotateGraphic(true);

        outlineTab.getTabPane()
                .getSelectionModel()
                .selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> {
                    if (newValue == outlineTab) {
                        current.currentEditor().rerender();
                    }
                });

        initializePaths();
        initializeLogViewer();
        initializeDoctypes();

        previewTabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.ALL_TABS);
        previewTabPane.setSide(Side.RIGHT);
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
        AwesomeDude.setIcon(panelShowHideMenuButton, AwesomeIcon.COLUMNS, "14.0");
        AwesomeDude.setIcon(refreshLabel, AwesomeIcon.REFRESH, "14.0");
        AwesomeDude.setIcon(goHomeLabel, AwesomeIcon.HOME, "14.0");

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
            this.cutCopy(lastRendered.getValue());
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


        loadConfigurations();
        loadRecentFileList();
        loadShortCuts();


        recentListView.setItems(recentFilesList);
        recentFilesList.addListener((ListChangeListener<String>) c -> {
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

        treeView.setCellFactory(param -> {
            TreeCell<Item> cell = new TextFieldTreeCell<Item>();
            cell.setOnDragDetected(event -> {
                Dragboard db = cell.startDragAndDrop(TransferMode.ANY);
                ClipboardContent content = new ClipboardContent();
                content.putFiles(Arrays.asList(cell.getTreeItem().getValue().getPath().toFile()));
                db.setContent(content);
            });
            return cell;
        });

        lastRendered.addListener(lastRenderedChangeListener);

        // MathJax
        mathjaxView = new WebView();
        mathjaxView.setVisible(false);
        rootAnchor.getChildren().add(mathjaxView);
        WebEngine mathjaxEngine = mathjaxView.getEngine();
        mathjaxEngine.getLoadWorker().stateProperty().addListener((observableValue1, state, state2) -> {
            JSObject window = (JSObject) mathjaxEngine.executeScript("window");
            if (window.getMember("afx").equals("undefined"))
                window.setMember("afx", this);
        });

        threadService.runActionLater(() -> {
            mathjaxEngine.load(String.format("http://localhost:%d/mathjax.html", port));
        });

        htmlPane.load(String.format("http://localhost:%d/preview.html", port));

        /// Treeview
        if (Objects.nonNull(recentFiles.getWorkingDirectory())) {
            Path path = Paths.get(recentFiles.getWorkingDirectory());
            Optional<Path> optional = Files.notExists(path) ? Optional.empty() : Optional.of(path);
            directoryService.setWorkingDirectory(optional);
        }


        Path workDir = directoryService.getWorkingDirectory().orElse(userHome);
        fileBrowser.browse(workDir);
        watchService.registerWatcher(workDir);

        openFileTreeItem.setOnAction(event -> {

            ObservableList<TreeItem<Item>> selectedItems = treeView.getSelectionModel().getSelectedItems();

            selectedItems.stream()
                    .map(e -> e.getValue())
                    .map(e -> e.getPath())
                    .filter(path -> {
                        if (selectedItems.size() == 1)
                            return true;
                        return !Files.isDirectory(path);
                    })
                    .forEach(directoryService.getOpenFileConsumer()::accept);
        });

        deletePathItem.setOnAction(event -> {

            ObservableList<TreeItem<Item>> selectedItems = treeView.getSelectionModel().getSelectedItems();

            AlertHelper.deleteAlert().ifPresent(btn -> {
                if (btn == ButtonType.YES)
                    selectedItems.stream()
                            .map(e -> e.getValue())
                            .map(e -> e.getPath())
                            .forEach(path -> threadService.runTaskLater(() -> {
                                if (Files.isDirectory(path)) {
                                    IOHelper.deleteDirectory(path);
                                } else {
                                    IOHelper.deleteIfExists(path);
                                }
                            }));

            });

        });

        openFolderTreeItem.setOnAction(event -> {
            Path path = tabService.getSelectedTabPath();
            path = Files.isDirectory(path) ? path : path.getParent();
            if (Objects.nonNull(path))
                getHostServices().showDocument(path.toString());
        });

        openFolderListItem.setOnAction(event -> {
            Path path = Paths.get(recentListView.getSelectionModel().getSelectedItem());
            path = Files.isDirectory(path) ? path : path.getParent();
            if (Objects.nonNull(path))
                getHostServices().showDocument(path.toString());
        });

        openFileListItem.setOnAction(this::openRecentListFile);

        copyPathTreeItem.setOnAction(event -> {
            Path path = tabService.getSelectedTabPath();
            this.cutCopy(path.toString());
        });

        copyPathListItem.setOnAction(event -> {
            this.cutCopy(recentListView.getSelectionModel().getSelectedItem());
        });

        copyTreeItem.setOnAction(event -> {
            Path path = tabService.getSelectedTabPath();
            this.copyFile(path);
        });

        copyListItem.setOnAction(event -> {
            Path path = Paths.get(recentListView.getSelectionModel().getSelectedItem());
            this.copyFile(path);
        });

        treeView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        treeView.setOnMouseClicked(event -> {
            TreeItem<Item> selectedItem = treeView.getSelectionModel().getSelectedItem();
            if (Objects.isNull(selectedItem))
                return;
            Path selectedPath = selectedItem.getValue().getPath();
            if (event.getButton() == MouseButton.PRIMARY)
                if (event.getClickCount() == 2)
                    directoryService.getOpenFileConsumer().accept(selectedPath);
        });

        treeView.getSelectionModel().getSelectedIndices().addListener((ListChangeListener<? super Integer>) p -> {

            ObservableList<TreeItem<Item>> selectedItems = treeView.getSelectionModel().getSelectedItems();
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
                if (favoriteDirectories.size() > 0) {
                    boolean has = favoriteDirectories.contains(path.toString());
                    if (has) addToFavoriteDir.setDisable(true);
                    else addToFavoriteDir.setDisable(false);
                }
            }
        });

        htmlPane.webEngine().setOnAlert(event -> {
            if ("PREVIEW_LOADED".equals(event.getData())) {

                if (htmlPane.getMember("afx").equals("undefined")) {
                    htmlPane.setMember("afx", this);
                }

                if (Objects.nonNull(lastRendered.getValue()))
                    lastRenderedChangeListener.changed(null, null, lastRendered.getValue());
            }
        });

        CheckMenuItem renderingCheckbox = new CheckMenuItem();
        renderingCheckbox.setGraphic(new Label("Stop rendering"));
        stopRendering.bind(renderingCheckbox.selectedProperty());

        ContextMenu previewContextMenu = new ContextMenu(
                MenuItemBuilt.item("Go back").click(event -> {
                    WebHistory history = htmlPane.webEngine().getHistory();
                    if (history.getCurrentIndex() != 0)
                        history.go(-1);

                }),
                MenuItemBuilt.item("Go forward").click(event -> {
                    WebHistory history = htmlPane.webEngine().getHistory();
                    if (history.getCurrentIndex() + 1 != history.getEntries().size())
                        history.go(+1);
                }),
                new SeparatorMenuItem(),
                renderingCheckbox,
                new SeparatorMenuItem(),
                MenuItemBuilt.item("Copy Html").click(event -> {
                    DocumentFragmentImpl selectionDom = (DocumentFragmentImpl) htmlPane.webEngine().executeScript("window.getSelection().getRangeAt(0).cloneContents()");
                    ClipboardContent content = new ClipboardContent();
                    content.putHtml(XMLHelper.nodeToString(selectionDom, true));
                    clipboard.setContent(content);
                }),
                MenuItemBuilt.item("Copy Text").click(event -> {
                    String selection = (String) htmlPane.webEngine().executeScript("window.getSelection().toString()");
                    ClipboardContent content = new ClipboardContent();
                    content.putString(selection);
                    clipboard.setContent(content);
                }),
                MenuItemBuilt.item("Copy Source").click(event -> {
                    DocumentFragmentImpl selectionDom = (DocumentFragmentImpl) htmlPane.webEngine().executeScript("window.getSelection().getRangeAt(0).cloneContents()");
                    ClipboardContent content = new ClipboardContent();
                    content.putString(XMLHelper.nodeToString(selectionDom, true));
                    clipboard.setContent(content);
                }),
                new SeparatorMenuItem(),
                MenuItemBuilt.item("Refresh").click(event -> {
                    htmlPane.webEngine().executeScript("clearImageCache()");
                }),
                MenuItemBuilt.item("Reload").click(event -> {
                    htmlPane.webEngine().reload();
                })
        );
        previewContextMenu.setAutoHide(true);
        htmlPane.getWebView().setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.SECONDARY) {
                previewContextMenu.show(htmlPane.getWebView(), event.getScreenX(), event.getScreenY());
            } else {
                previewContextMenu.hide();
            }
        });

        tabService.initializeTabChangeListener(tabPane);

        newDoc(null);

        Platform.runLater(() -> {
//            editorSplitPane.setDividerPositions(1);
//            splitPane.setDividerPositions();
        });

        threadService.runTaskLater(this::checkNewVersion);
//        threadService.runTaskLater(this::initializeAutoSaver);

    }

   /* private void initializeAutoSaver() {
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
                    ObservableList<Tab> tabs = tabPane.getTabs();

                    for (Tab tab : tabs) {
                        MyTab myTab = (MyTab) tab;
                        if(!myTab.isNew()){
                        }
                    }
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

        editorConfigBean.directoryPanelProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                showFileBrowser();
            } else {
                hideFileBrowser(null);
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
    }

    @WebkitCall(from = "asciidoctor-image-size-info")
    public JSObject getImageInfo(String path, JSObject info) {

        Path parent = null;

        try {
            if (current.currentPath().isPresent()) {
                parent = current.currentPath().get().getParent();
            } else {
                parent = directoryService.workingDirectory();
            }
        } catch (Exception e) {
            logger.error("Problem occured while getting image size info", e);
        }

        Path imagePath = parent.resolve(path);

        if (Files.notExists(imagePath))
            return info;

        try (ImageInputStream in = ImageIO.createImageInputStream(imagePath.toFile())) {
            final Iterator<ImageReader> readers = ImageIO.getImageReaders(in);
            if (readers.hasNext()) {
                ImageReader reader = readers.next();
                try {
                    reader.setInput(in);
                    int width = reader.getWidth(0);
                    int height = reader.getHeight(0);

                    info.setMember("width", width);
                    info.setMember("height", height);

                    reader.dispose();

                    return info;
                } finally {
                    reader.dispose();
                }
            }
        } catch (Exception e) {
            logger.error("Problem occured while getting image size info", e);
        }
        return info;
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
                    ObservableList<TreeItem<Item>> selectedItems = treeView.getSelectionModel().getSelectedItems();
                    if (selectedItems.size() == 1) {
                        Path path = selectedItems.get(0).getValue().getPath();
                        boolean isDirectory = Files.isDirectory(path);
                        addToFavSeparator.setVisible(isDirectory);
                    } else addToFavSeparator.setVisible(false);
                    favoriteDirectories.clear();
                    favoriteDirMenu.getItems().clear();
                    recentFiles.getFavoriteDirectories().clear();
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
        try {
            ApplicationLauncher.launchApplication("504", null, false, new ApplicationLauncher.Callback() {
                        public void exited(int exitValue) {
                            //TODO add your code here (not invoked on event dispatch thread)
                        }

                        public void prepareShutdown() {
                            //TODO add your code here (not invoked on event dispatch thread)
                        }
                    }
            );
        } catch (IOException e) {
            // logger.error("Problem occured while checking new version", e);
        }
    }

    private void generateODFDocument(boolean askPath) {

        if (!current.currentPath().isPresent())
            this.saveDoc();

        odfConverter.generateODFDocument(askPath);

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
            logPath = installationPath.resolve("log");
        } catch (URISyntaxException e) {
            logger.error("Problem occured while resolving conf and log paths", e);
        }

    }

    @WebkitCall
    public void updateStatusBox(long row, long column, long linecount, long wordcount) {
        threadService.runTaskLater(() -> {
            threadService.runActionLater(() -> {
                statusText.setText(String.format("(Characters: %d) (Lines: %d) (%d:%d)", wordcount, linecount, row, column));
            });
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

        showHideLogs.setOnMouseClicked(event->{
            showHideLogs.getStyleClass().removeAll("red-label");
        });

        TableViewLogAppender.setLogList(logList);
        TableViewLogAppender.setStatusMessage(statusMessage);
        TableViewLogAppender.setShowHideLogs(showHideLogs);
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
            getHostServices().showDocument(logPath.toUri().toString());
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

        HBox logHBox = new HBox();

        for (Control control : controls) {
            logHBox.getChildren().add(control);
            HBox.setMargin(control, new Insets(3));
            control.prefHeightProperty().bind(searchLogField.heightProperty());
        }

        AwesomeDude.setIcon(showHideLogs, AwesomeIcon.CHEVRON_CIRCLE_UP, "14.0");

        HBox.setMargin(showHideLogs, new Insets(0, 0, 0, 3));
        HBox.setMargin(statusText, new Insets(0, 3, 0, 0));

        showHideLogs.setOnMouseClicked(event -> {
            showHideLogs.setRotate(showHideLogs.getRotate() + 180);
            if (showHideLogs.getRotate() % 360 == 0)
                editorSplitPane.setDividerPositions(1);
            else
                editorSplitPane.setDividerPositions(0.5);
        });

        scene.addListener((observableScene, oldScene, newScene) -> {
            if (Objects.nonNull(newScene)) {
                newScene.heightProperty().addListener((observable, oldValue, newValue) -> {
                    if (showHideLogs.getRotate() % 360 == 0) {
                        threadService.runActionLater(() -> {
                            editorSplitPane.setDividerPositions(1);
                        }, true);
                    }
                });
            }
        });

        logVBox.getChildren().addAll(logHBox, logViewer);

        VBox.setVgrow(logViewer, Priority.ALWAYS);

    }

    private void loadShortCuts() {
        try {
            String yamlC = IOHelper.readFile(configPath.resolve("shortcuts.yml"));

            Yaml yaml = new Yaml();
            this.shortCuts = yaml.loadAs(yamlC, Map.class);

        } catch (Exception e) {
            logger.error("Problem occured while loading shortcuts.yml file", e);
        }
    }

    private void openRecentListFile(Event event) {
        Path path = Paths.get(recentListView.getSelectionModel().getSelectedItem());

        directoryService.getOpenFileConsumer().accept(path);

    }

    private void loadConfigurations() {
//        try {
//            String yamlContent = IOHelper.readFile(configPath.resolve("config.yml"));
//            Yaml yaml = new Yaml();
//            config = yaml.loadAs(yamlContent, Config.class);
//
//        } catch (Exception e) {
//            logger.error("Problem occured while loading config.yml file", e);
//        }


    }

    private void loadRecentFileList() {

        try {
            String yamlContent = IOHelper.readFile(configPath.resolve("recentFiles.yml"));
            Yaml yaml = new Yaml();
            recentFiles = yaml.loadAs(yamlContent, RecentFiles.class);

            recentFilesList.addAll(recentFiles.getFiles());
            favoriteDirectories.addAll(recentFiles.getFavoriteDirectories());
        } catch (Exception e) {
            logger.error("Problem occured while loading recent file list", e);
        }
    }

    public void externalBrowse() {
        ObservableList<Tab> tabs = previewTabPane.getTabs();
        for (Tab tab : tabs) {
            if (tab.isSelected()) {
                Node content = tab.getContent();
                if (Objects.nonNull(content))
                    ((ViewPanel) content).browse();
            }
        }
    }

    @WebkitCall(from = "index")
    public void fillOutlines(JSObject doc) {
        if (outlineTab.isSelected())
            threadService.runActionLater(() -> {
                htmlPane.fillOutlines(doc);
            });
    }

    @WebkitCall(from = "index")
    public void clearOutline() {
        outlineList = new TreeSet<>();
    }

    @WebkitCall(from = "index")
    public void finishOutline() {

        threadService.runTaskLater(() -> {

            if (Objects.isNull(sectionTreeView)) {
                sectionTreeView = new TreeView<Section>();
                TreeItem<Section> rootItem = new TreeItem<>();
                rootItem.setExpanded(true);
                Section rootSection = new Section();
                rootSection.setLevel(-1);
                String outlineTitle = "Outline";
                rootSection.setTitle(outlineTitle);

                rootItem.setValue(rootSection);

                sectionTreeView.setRoot(rootItem);

                sectionTreeView.setOnMouseClicked(event -> {
                    try {
                        TreeItem<Section> item = sectionTreeView.getSelectionModel().getSelectedItem();
                        EditorPane editorPane = current.currentEditor();
                        editorPane.moveCursorTo(item.getValue().getLineno());
                    } catch (Exception e) {
                        logger.error("Problem occured while jumping from outline");
                    }
                });
            }

            sectionTreeView.getRoot().getChildren().clear();

            for (Section section : outlineList) {
                TreeItem<Section> sectionItem = new TreeItem<>(section);
                sectionItem.setExpanded(true);
                sectionTreeView.getRoot().getChildren().add(sectionItem);

                TreeSet<Section> subsections = section.getSubsections();
                for (Section subsection : subsections) {
                    TreeItem<Section> subItem = new TreeItem<>(subsection);
                    subItem.setExpanded(true);
                    sectionItem.getChildren().add(subItem);
                    this.addSubSections(subItem, subsection.getSubsections());
                }
            }

            threadService.runActionLater(() -> {
                if (outlineList.size() == 0) {
                    outlineTab.setContent(new Label(" There is no Asciidoc header to generate outline."));
                } else {
                    outlineTab.setContent(sectionTreeView);
                }
            });
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
        directoryService.changeWorkigDir();
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        sessionList.add(session);
        String value = lastRendered.getValue();
        if (Objects.nonNull(value))
            session.sendMessage(new TextMessage(value));
    }

    @FXML
    public void closeApp(ActionEvent event) {
        try {
            Map<String, ConfigurationBase> configurationBeansAsMap = applicationContext.getBeansOfType(ConfigurationBase.class);
            for (ConfigurationBase configurationBean : configurationBeansAsMap.values()) {
                configurationBean.save(event);
            }
            yamlService.persist();
        } catch (Exception e) {
            logger.error("Error while closing app", e);
        }
    }

    @FXML
    public void openDoc(Event event) {
        documentService.openDoc();
    }

    @FXML
    public void newDoc(Event... event) {
        threadService.runActionLater(() -> {
            documentService.newDoc();
        }, true);
    }

    @WebkitCall(from = "editor")
    public boolean isLiveReloadPane() {
        return previewTab.getContent() == liveReloadPane;
    }

    @WebkitCall(from = "editor")
    public void onscroll(Object pos, Object max) {
        Node content = previewTab.getContent();
        if (Objects.nonNull(content)) {
            ((ViewPanel) content).onscroll(pos, max);
        }
    }

    @WebkitCall(from = "editor")
    public void scrollToCurrentLine(String text) {

        if (previewTab.getContent() == slidePane) {
            slidePane.flipThePage(htmlPane.findRenderedSelection(text)); // slide
        }

        if (previewTab.getContent() == htmlPane) {
            threadService.runActionLater(() -> {
                try {
                    htmlPane.call("runScroller", text);
                } catch (Exception e) {
                    logger.debug(e.getMessage(), e);
                }
            });
        }

    }

    public void plantUml(String uml, String type, String fileName) throws IOException {

        threadService.runTaskLater(() -> {
            plantUmlService.plantUml(uml, type, fileName);
        });
    }


    public void chartBuildFromCsv(String csvFile, String fileName, String chartType, String options) {

        if (Objects.isNull(fileName) || Objects.isNull(chartType))
            return;

        getCurrent().currentPath().map(Path::getParent).ifPresent(root -> {
            threadService.runTaskLater(() -> {
                String csvContent = IOHelper.readFile(root.resolve(csvFile));

                threadService.runActionLater(() -> {
                    try {
                        Map<String, String> optMap = parseChartOptions(options);
                        optMap.put("csv-file", csvFile);
                        chartProvider.getProvider(chartType).chartBuild(csvContent, fileName, optMap);

                    } catch (Exception e) {
                        logger.info(e.getMessage(), e);
                    }
                });

            });

        });
    }

    public void chartBuild(String chartContent, String fileName, String chartType, String options) {

        if (Objects.isNull(fileName) || Objects.isNull(chartType))
            return;

        threadService.runActionLater(() -> {
            try {
                Map<String, String> optMap = parseChartOptions(options);
                chartProvider.getProvider(chartType).chartBuild(chartContent, fileName, optMap);

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

    @WebkitCall(from = "editor")
    public void appendWildcard() {
        String currentTabText = current.getCurrentTabText();
        if (!currentTabText.contains(" *"))
            current.setCurrentTabText(currentTabText + " *");
    }

    @WebkitCall(from = "editor")
    public void textListener(String text, String mode) {

        if (stopRendering.get()) {
            return;
        }

        threadService.runTaskLater(() -> {

            boolean bookArticleHeader = this.bookArticleHeaderRegex.matcher(text).find();
            boolean forceInclude = this.forceIncludeRegex.matcher(text).find();

            threadService.runActionLater(() -> {

                try {
                    if ("asciidoc".equalsIgnoreCase(mode)) {

                        if (bookArticleHeader && !forceInclude)
                            setIncludeAsciidocResource(true);

                        ConverterResult result = htmlPane.convertAsciidoc(text);

                        setIncludeAsciidocResource(false);

                        if (result.isBackend("html5")) {
                            lastRendered.setValue(result.getRendered());
                            previewTab.setContent(htmlPane);
                        }

                        if (result.isBackend("revealjs") || result.isBackend("deckjs")) {
                            slidePane.setBackend(result.getBackend());
                            slideConverter.convert(result.getRendered());
                        }

                    } else if ("html".equalsIgnoreCase(mode)) {

                        if (previewTab.getContent() != liveReloadPane) {
                            liveReloadPane.setOnSuccess(() -> {
                                liveReloadPane.setMember("afx", this);
                                liveReloadPane.initializeDiffReplacer();
                            });
                            liveReloadPane.load(String.format("http://localhost:%d/livereload/index.reload", port));
                        } else {
                            liveReloadPane.updateDomdom();
                        }

                        previewTab.setContent(liveReloadPane);

                    } else if ("markdown".equalsIgnoreCase(mode)) {
                        markdownService.convertToAsciidoc(text, asciidoc -> {
                            threadService.runActionLater(() -> {
                                ConverterResult result = htmlPane.convertAsciidoc(asciidoc);
                                result.afterRender(lastRendered::setValue);
                            });
                        });
                        previewTab.setContent(htmlPane);
                    }
                } catch (Exception e) {
                    setIncludeAsciidocResource(false);
                    logger.error("Problem occured while rendering content", e);
                }

            });

        });
    }

    @WebkitCall(from = "asciidoctor-odf.js")
    public void convertToOdf(String name, Object obj) throws Exception {
        JSObject jObj = (JSObject) obj;
        odfConverter.buildDocument(name, jObj);
    }

    @WebkitCall
    public String getTemplate(String templateName, String templateDir) throws IOException {
        return htmlPane.getTemplate(templateName, templateDir);
    }

    public void cutCopy(String data) {
        ClipboardContent clipboardContent = new ClipboardContent();
        clipboardContent.putString(data);
        clipboard.setContent(clipboardContent);
    }

    public void copyFile(Path path) {
        ClipboardContent clipboardContent = new ClipboardContent();
        clipboardContent.putFiles(Arrays.asList(path.toFile()));
        clipboard.setContent(clipboardContent);
    }

    @WebkitCall(from = "asciidoctor")
    public String readDefaultStylesheet() {
        final CompletableFuture<String> completableFuture = new CompletableFuture();

        completableFuture.runAsync(() -> {
            threadService.runTaskLater(() -> {
                Path defaultCssPath = configPath.resolve("data/stylesheets/asciidoctor-default.css");
                String defaultCss = IOHelper.readFile(defaultCssPath);
                completableFuture.complete(defaultCss);
            });
        });

        return completableFuture.join();
    }

    @WebkitCall(from = "asciidoctor")
    public String readAsciidoctorResource(String uri, Integer parent) {

        if (uri.matches(".*?\\.(asc|adoc|ad|asciidoc|md|markdown)") && getIncludeAsciidocResource())
            return String.format("link:%s[]", uri);

        final CompletableFuture<String> completableFuture = new CompletableFuture();

        completableFuture.runAsync(() -> {
            threadService.runTaskLater(() -> {
                PathFinderService fileReader = applicationContext.getBean("pathFinder", PathFinderService.class);
                Path path = fileReader.findPath(uri, parent);

                if (!Files.exists(path)) {
                    completableFuture.complete("404");
                } else {
                    completableFuture.complete(IOHelper.readFile(path));
                }
            });
        });

        return completableFuture.join();
    }

    @WebkitCall
    public String clipboardValue() {
        return clipboard.getString();
    }

    @WebkitCall
    public void pasteRaw() {

        JSObject editor = (JSObject) current.currentEngine().executeScript("editor");
        if (clipboard.hasFiles()) {
            Optional<String> block = parserService.toImageBlock(clipboard.getFiles());
            if (block.isPresent()) {
                editor.call("insert", block.get());
                return;
            }
        }

        editor.call("execCommand", "paste-raw-1");
    }

    @WebkitCall
    public void paste() {

        JSObject window = (JSObject) htmlPane.webEngine().executeScript("window");
        JSObject editor = (JSObject) current.currentEngine().executeScript("editor");

        if (clipboard.hasFiles()) {
            Optional<String> block = parserService.toImageBlock(clipboard.getFiles());
            if (block.isPresent()) {
                editor.call("insert", block.get());
                return;
            }
        }

        try {

            if (clipboard.hasHtml() || (Boolean) window.call("isHtml", clipboard.getString())) {
                String content = Optional.ofNullable(clipboard.getHtml()).orElse(clipboard.getString());
                if (current.currentTab().isAsciidoc() || current.currentTab().isMarkdown())
                    content = (String) window.call(current.currentTab().htmlToMarkupFunction(), content);
                editor.call("insert", content);
                return;
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

        editor.call("execCommand", "paste-raw-1");

    }


    public void adjustSplitPane() {
        if (splitPane.getDividerPositions()[0] > 0.1) {
            hideFileAndPreviewPanels(null);
        } else {
            showFileBrowser();
            showPreviewPanel();
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
        documentService.saveDoc();
    }

    @FXML
    public void saveDoc(Event actionEvent) {
        documentService.saveDoc();
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

    public TreeView<Item> getTreeView() {
        return treeView;
    }

    public void setHostServices(HostServicesDelegate hostServices) {
        this.hostServices = hostServices;
    }

    public HostServicesDelegate getHostServices() {
        return hostServices;
    }

    public AsciidocTableController getAsciidocTableController() {
        return asciidocTableController;
    }

    public StringProperty getLastRendered() {
        return lastRendered;
    }

    public ObservableList<String> getRecentFilesList() {
        return recentFilesList;
    }

    public TabPane getTabPane() {
        return tabPane;
    }

    public WebView getMathjaxView() {
        return mathjaxView;
    }

    public ChangeListener<String> getLastRenderedChangeListener() {
        return lastRenderedChangeListener;
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

    public Map<String, String> getShortCuts() {
        if (Objects.isNull(shortCuts))
            shortCuts = new HashMap<>();
        return shortCuts;
    }

    @FXML
    private void bugReport(ActionEvent actionEvent) {
        getHostServices().showDocument("https://github.com/asciidocfx/AsciidocFX/issues");
    }

    @FXML
    private void openCommunityForum(ActionEvent actionEvent) {
        getHostServices().showDocument("https://groups.google.com/d/forum/asciidocfx-discuss");
    }

    @FXML
    private void openGitterChat(ActionEvent actionEvent) {
        getHostServices().showDocument("https://gitter.im/asciidocfx/AsciidocFX");
    }

    @FXML
    private void openGithubPage(ActionEvent actionEvent) {
        getHostServices().showDocument("https://github.com/asciidocfx/AsciidocFX");
    }

    @FXML
    public void generateCheatSheet(ActionEvent actionEvent) {
        Path cheatsheetPath = configPath.resolve("cheatsheet/cheatsheet.adoc");
        tabService.addTab(cheatsheetPath);
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

    @FXML
    public void createFolder(ActionEvent actionEvent) {

        DialogBuilder dialog = DialogBuilder.newFolderDialog();

        Consumer<String> consumer = result -> {
            if (dialog.isShowing())
                dialog.hide();

            if (result.matches(DialogBuilder.FOLDER_NAME_REGEX)) {

                Path path = treeView.getSelectionModel().getSelectedItem()
                        .getValue().getPath();

                Path folderPath = path.resolve(result);


                threadService.runTaskLater(() -> {
                    IOHelper.createDirectory(folderPath);
                    directoryService.changeWorkigDir(folderPath);
                });
            }
        };

        dialog.getEditor().setOnAction(event -> {
            consumer.accept(dialog.getEditor().getText());
        });

        dialog.showAndWait().ifPresent(consumer);
    }

    @FXML
    public void createFile(ActionEvent actionEvent) {

        DialogBuilder dialog = DialogBuilder.newFileDialog();

        Consumer<String> consumer = result -> {
            if (dialog.isShowing())
                dialog.hide();

            if (result.matches(DialogBuilder.FILE_NAME_REGEX)) {

                Path path = treeView.getSelectionModel().getSelectedItem()
                        .getValue().getPath();

                IOHelper.writeToFile(path.resolve(result), "");
                tabService.addTab(path.resolve(result));
// dikkat
//                threadService.runActionLater(() -> {
//                    directoryService.changeWorkigDir(path);
//                });
            }
        };

        dialog.getEditor().setOnAction(event -> {
            consumer.accept(dialog.getEditor().getText());
        });

        dialog.showAndWait().ifPresent(consumer);
    }

    @FXML
    public void renameFile(ActionEvent actionEvent) {

        RenameDialog dialog = RenameDialog.create();

        Path path = treeView.getSelectionModel().getSelectedItem()
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
            gitbookToAsciibook.gitbookToAsciibook(finalGitbookRoot.toPath(), finalAsciibookRoot.toPath());
            indikatorService.stopProgressBar();
            logger.debug("Gitbook to Asciibook conversion ended");
        });

    }

    public boolean getFileBrowserVisibility() {
        return fileBrowserVisibility.get();
    }

    public BooleanProperty fileBrowserVisibilityProperty() {
        return fileBrowserVisibility;
    }

    public boolean getPreviewPanelVisibility() {
        return previewPanelVisibility.get();
    }

    public BooleanProperty previewPanelVisibilityProperty() {
        return previewPanelVisibility;
    }

    @FXML
    public void hideFileBrowser(ActionEvent actionEvent) {
        splitPane.setDividerPosition(0, 0);
        fileBrowserVisibility.setValue(true);
    }

    public void showFileBrowser() {
        splitPane.setDividerPositions(0.195, splitPane.getDividerPositions()[1]);
        fileBrowserVisibility.setValue(false);

    }

    public void hidePreviewPanel() {
        splitPane.setDividerPosition(1, 1);
        previewPanelVisibility.setValue(true);
    }

    @FXML
    public void togglePreviewPanel(ActionEvent actionEvent) {
        if (hidePreviewPanel.isSelected()) {
            hidePreviewPanel();
        } else {
            showPreviewPanel();
        }
    }

    public void showPreviewPanel() {
        splitPane.setDividerPosition(1, 0.6);
        previewPanelVisibility.setValue(false);
        hidePreviewPanel.setSelected(false);
    }

    @FXML
    public void hideFileAndPreviewPanels(ActionEvent actionEvent) {
        hidePreviewPanel.setSelected(true);
        togglePreviewPanel(actionEvent);
        hideFileBrowser(actionEvent);
    }
/*
    @WebkitCall
    public void fillModeList(String mode) {
        threadService.runActionLater(() -> {
            modeList.add(mode);
        });
    }*/

    public RecentFiles getRecentFiles() {
        return recentFiles;
    }

    public void clearImageCache() {
        htmlPane.webEngine().executeScript("clearImageCache()");
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

    public void removeChildElement(Node node) {
        getRootAnchor().getChildren().remove(node);
    }

    @FXML
    public void switchSlideView(ActionEvent actionEvent) {
        splitPane.setDividerPositions(0, 0.45);
        fileBrowserVisibility.setValue(true);
    }

    public TabPane getPreviewTabPane() {
        return previewTabPane;
    }

    public PreviewTab getPreviewTab() {
        return previewTab;
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

        dialog.showAndWait().ifPresent(folderName -> {
            if (dialog.isShowing())
                dialog.hide();

            if (folderName.matches(DialogBuilder.FOLDER_NAME_REGEX)) {

                Path path = treeView.getSelectionModel().getSelectedItem()
                        .getValue().getPath();

                Path folderPath = path.resolve(folderName);

                threadService.runTaskLater(() -> {
                    IOHelper.createDirectory(folderPath);
                    indikatorService.startProgressBar();
                    IOHelper.copyDirectory(configPath.resolve("slide/frameworks"), folderPath);
                    indikatorService.stopProgressBar();
                    directoryService.changeWorkigDir(folderPath);
                    threadService.runActionLater(() -> {
                        tabService.addTab(folderPath.resolve("slide.adoc"));
                        this.switchSlideView(actionEvent);
                    });
                });
            }
        });

    }

    @FXML
    public void addToFavoriteDir(ActionEvent actionEvent) {
        Path selectedTabPath = tabService.getSelectedTabPath();
        if (Files.isDirectory(selectedTabPath)) {
            boolean has = favoriteDirectories.contains(selectedTabPath.toString());
            if (!has) {
                favoriteDirectories.add(selectedTabPath.toString());
                recentFiles.getFavoriteDirectories().add(selectedTabPath.toString());
            }
        }
    }

    public EditorConfigBean getEditorConfigBean() {
        return editorConfigBean;
    }

    @FXML
    public void showSettings() {
        configurationService.showConfig();
    }
}
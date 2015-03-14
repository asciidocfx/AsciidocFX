package com.kodcu.controller;


import com.esotericsoftware.yamlbeans.YamlReader;
import com.kodcu.bean.Config;
import com.kodcu.bean.RecentFiles;
import com.kodcu.bean.ShortCuts;
import com.kodcu.component.DeleteAlert;
import com.kodcu.component.MenuBuilt;
import com.kodcu.component.MenuItemBuilt;
import com.kodcu.other.Current;
import com.kodcu.other.IOHelper;
import com.kodcu.other.Item;
import com.kodcu.service.*;
import com.kodcu.service.config.YamlService;
import com.kodcu.service.convert.*;
import com.kodcu.service.extension.MathJaxService;
import com.kodcu.service.extension.PlantUmlService;
import com.kodcu.service.extension.TreeService;
import com.kodcu.service.ui.*;
import com.sun.javafx.application.HostServicesDelegate;
import de.jensd.fx.fontawesome.AwesomeDude;
import de.jensd.fx.fontawesome.AwesomeIcon;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.concurrent.Worker;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTreeCell;
import javafx.scene.input.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Popup;
import javafx.stage.Stage;
import netscape.javascript.JSObject;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.EmbeddedWebApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.CodeSource;
import java.util.*;
import java.util.function.Consumer;

import static java.nio.file.StandardOpenOption.*;


@Controller
public class ApplicationController extends TextWebSocketHandler implements Initializable {

    private Logger logger = LoggerFactory.getLogger(ApplicationController.class);

    private Path userHome = Paths.get(System.getProperty("user.home"));

    public TabPane tabPane;
    public WebView previewView;
    public SplitPane splitPane;
    public SplitPane splitPaneVertical;
    public TreeView<Item> treeView;
    public Label splitHideButton;
    public Label workingDirButton;
    public Label goUpLabel;
    public Label goHomeLabel;
    public Label refreshLabel;
    public AnchorPane rootAnchor;
    public MenuBar recentFilesBar;
    public ProgressIndicator indikator;
    public ListView<String> recentListView;
    public MenuItem openFileTreeItem;
    public MenuItem removePathItem;
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

    @Autowired
    private TablePopupService tablePopupController;

    @Autowired
    private PathOrderService pathOrder;

    @Autowired
    private TreeService treeService;

    @Autowired
    private TooltipTimeFixService tooltipTimeFixService;

    @Autowired
    private TabService tabService;

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
    private RenderService renderService;

    @Autowired
    private DocBookService docBookService;

    @Autowired
    private Html5BookService htmlBookService;

    @Autowired
    private Html5ArticleService htmlArticleService;

    @Autowired
    private FopPdfService fopServiceRunner;

    @Autowired
    private Epub3Service epub3Service;

    @Autowired
    private Current current;

    @Autowired
    private FileBrowseService fileBrowser;

    @Autowired
    private IndikatorService indikatorService;

    @Autowired
    private KindleMobiService kindleMobiService;

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
    private ScrollService scrollService;

    @Autowired
    private DocumentService documentService;

    @Autowired
    private EpubController epubController;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private Base64.Encoder base64Encoder;

    private Stage stage;
    private WebEngine previewEngine;
    private StringProperty lastRendered = new SimpleStringProperty();
    private List<WebSocketSession> sessionList = new ArrayList<>();
    private Scene scene;
    private AnchorPane tableAnchor;
    private Stage tableStage;
    private final Clipboard clipboard = Clipboard.getSystemClipboard();
    private final ObservableList<String> recentFiles = FXCollections.synchronizedObservableList(FXCollections.observableArrayList());
    private AnchorPane configAnchor;
    private Stage configStage;
    private int port = 8080;
    private HostServicesDelegate hostServices;
    private Path configPath;
    private Config config;

    private final List<String> bookNames = Arrays.asList("book.asc", "book.txt", "book.asciidoc", "book.adoc", "book.ad");

    private Map<String, String> shortCuts;

    private final ChangeListener<String> lastRenderedChangeListener = (observableValue, old, nev) -> {

        if (Objects.isNull(nev))
            return;

        threadService.runActionLater(() -> {
            renderService.getWindow().setMember("lastRenderedValue", nev);
            previewEngine.executeScript("refreshUI(lastRenderedValue)");
        });

        sessionList.stream().filter(e -> e.isOpen()).forEach(e -> {
            try {
                e.sendMessage(new TextMessage(nev));
            } catch (Exception ex) {
                logger.info(ex.getMessage(), ex);
            }
        });
    };

    @FXML
    public void createTable(Event event) {
        threadService.runTaskLater(() -> {
            threadService.runActionLater(() -> {
                tableStage.showAndWait();
            });
        });
    }

    @FXML
    private void openConfig(ActionEvent event) {
        configStage.show();
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
            if (current.currentIsBook()) {
                fopServiceRunner.generateBook(askPath);
            } else {
                fopServiceRunner.generateArticle(askPath);
            }
        });
    }

    @FXML
    private void generateSampleBook(ActionEvent event) {

        DirectoryChooser directoryChooser = directoryService.newDirectoryChooser("Select a New Directory for sample book");
        File file = directoryChooser.showDialog(null);
        threadService.runTaskLater(() -> {
            sampleBookService.produceSampleBook(configPath, file.toPath());
            directoryService.setWorkingDirectory(Optional.of(file.toPath()));
            fileBrowser.browse(treeView, file.toPath());
            threadService.runActionLater(() -> {
                directoryView(null);
                tabService.addTab(file.toPath().resolve("book.asc"));
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
                    getRecentFiles().remove(docbookPath.toString());
                    getRecentFiles().add(0, docbookPath.toString());
                };

                if (current.currentIsBook()) {
                    docBookService.generateDocbook(step);
                } else {
                    docBookService.generateDocbookArticle(step);
                }

            });

        });

    }

    private void convertEpub() {
        convertEpub(false);
    }

    private void convertEpub(boolean askPath) {
        epub3Service.produceEpub3(askPath);
    }

    public String appendFormula(String fileName, String formula) {
        return mathJaxService.appendFormula(fileName, formula);
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

        if (Objects.nonNull(config.getKindlegenDir())) {
            if (!Files.exists(Paths.get(config.getKindlegenDir()))) {
                config.setKindlegenDir(null);
            }
        }

        if (Objects.isNull(config.getKindlegenDir())) {
            FileChooser fileChooser = directoryService.newFileChooser("Select 'kindlegen' executable");
            File kindlegenFile = fileChooser.showOpenDialog(null);
            if (Objects.isNull(kindlegenFile))
                return;

            config.setKindlegenDir(kindlegenFile.toPath().getParent().toString());
        }

        threadService.runTaskLater(() -> {
            kindleMobiService.produceMobi(askPath);
        });

    }

    private void generateHtml() {
        this.generateHtml(false);
    }

    private void generateHtml(boolean askPath) {

        if (!current.currentPath().isPresent())
            this.saveDoc();

        threadService.runTaskLater(() -> {
            if (current.currentIsBook())
                htmlBookService.convertHtmlBook(askPath);
            else
                htmlArticleService.convertHtmlArticle(askPath);
        });
    }

    public void createFileTree(String tree, String type, String fileName, String width, String height) {

        threadService.runTaskLater(() -> {
            treeService.createFileTree(tree, type, fileName, width, height);
        });
    }

    @FXML
    public void goUp() {
        directoryService.goUp();
    }

    @FXML
    public void refreshWorkingDir() {
        current.currentPath().map(Path::getParent).ifPresent(directoryService::changeWorkigDir);
    }

    @FXML
    public void goHome() {
        directoryService.changeWorkigDir(userHome);
    }

    public void imageToBase64Url(final String url, final int index) {

        threadService.runTaskLater(() -> {
            try {
                byte[] imageBuffer = restTemplate.getForObject(url, byte[].class);
                String imageBase64 = base64Encoder.encodeToString(imageBuffer);
                threadService.runActionLater(() -> {
                    previewEngine.executeScript(String.format("updateBase64Url(%d,'%s')", index, imageBase64));
                });
            } catch (Exception e) {
                logger.info(e.getMessage(), e);
            }
        });
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        tooltipTimeFixService.fix();

        // Convert menu label icons
        AwesomeDude.setIcon(htmlPro, AwesomeIcon.HTML5);
        AwesomeDude.setIcon(pdfPro, AwesomeIcon.FILE_PDF_ALT);
        AwesomeDude.setIcon(ebookPro, AwesomeIcon.BOOK);
        AwesomeDude.setIcon(docbookPro, AwesomeIcon.CODE);
        AwesomeDude.setIcon(browserPro, AwesomeIcon.FLASH);


        // Left menu label icons
        AwesomeDude.setIcon(workingDirButton, AwesomeIcon.FOLDER_ALT, "14.0");
        AwesomeDude.setIcon(splitHideButton, AwesomeIcon.CHEVRON_LEFT, "14.0");
        AwesomeDude.setIcon(refreshLabel, AwesomeIcon.REFRESH, "14.0");
        AwesomeDude.setIcon(goUpLabel, AwesomeIcon.LEVEL_UP, "14.0");
        AwesomeDude.setIcon(goHomeLabel, AwesomeIcon.HOME, "14.0");

        leftButton.setGraphic(AwesomeDude.createIconLabel(AwesomeIcon.ELLIPSIS_H, "14.0"));

        ContextMenu htmlProMenu = new ContextMenu();
        htmlProMenu.getStyleClass().add("build-menu");
        htmlPro.setContextMenu(htmlProMenu);
        htmlPro.setOnMouseClicked(event -> {
            htmlProMenu.show(htmlPro, event.getScreenX(), 50);
        });
        htmlProMenu.getItems().add(MenuItemBuilt.item("Save").onclick(event -> {
            this.generateHtml();
        }));
        htmlProMenu.getItems().add(MenuItemBuilt.item("Save as").onclick(event -> {
            this.generateHtml(true);
        }));
        htmlProMenu.getItems().add(MenuItemBuilt.item("Copy source").tip("Copy HTML source").onclick(event -> {
            this.cutCopy(lastRendered.getValue());
        }));
        htmlProMenu.getItems().add(MenuItemBuilt.item("Clone source").tip("Copy HTML source (Embedded images)").onclick(event -> {
            previewEngine.executeScript("imageToBase64Url()");
        }));

        ContextMenu pdfProMenu = new ContextMenu();
        pdfProMenu.getStyleClass().add("build-menu");
        pdfProMenu.getItems().add(MenuItemBuilt.item("Save").onclick(event -> {
            this.generatePdf();
        }));
        pdfProMenu.getItems().add(MenuItemBuilt.item("Save as").onclick(event -> {
            this.generatePdf(true);
        }));
        pdfPro.setContextMenu(pdfProMenu);

        pdfPro.setOnMouseClicked(event -> {
            pdfProMenu.show(pdfPro, event.getScreenX(), 50);
        });

        ContextMenu docbookProMenu = new ContextMenu();
        docbookProMenu.getStyleClass().add("build-menu");
        docbookProMenu.getItems().add(MenuItemBuilt.item("Save").onclick(event -> {
            this.convertDocbook();
        }));
        docbookProMenu.getItems().add(MenuItemBuilt.item("Save as").onclick(event -> {
            this.convertDocbook(true);
        }));

        docbookPro.setContextMenu(docbookProMenu);

        docbookPro.setOnMouseClicked(event -> {
            docbookProMenu.show(docbookPro, event.getScreenX(), 50);
        });

        ContextMenu ebookProMenu = new ContextMenu();
        ebookProMenu.getStyleClass().add("build-menu");
        ebookProMenu.getItems().add(MenuBuilt.name("Mobi")
                .add(MenuItemBuilt.item("Save").onclick(event -> {
                    this.convertMobi();
                }))
                .add(MenuItemBuilt.item("Save as").onclick(event -> {
                    this.convertMobi(true);
                })).build());

        ebookProMenu.getItems().add(MenuBuilt.name("Epub")
                .add(MenuItemBuilt.item("Save").onclick(event -> {
                    this.convertEpub();
                }))
                .add(MenuItemBuilt.item("Save as").onclick(event -> {
                    this.convertEpub(true);
                })).build());

        ebookPro.setOnMouseClicked(event -> {
            ebookProMenu.show(ebookPro, event.getScreenX(), 50);
        });

        ebookPro.setContextMenu(ebookProMenu);

        browserPro.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY)
                this.externalBrowse();
        });

        port = server.getEmbeddedServletContainer().getPort();

        loadConfigurations();
        loadRecentFileList();
        loadShortCuts();

        recentListView.setItems(recentFiles);
        recentFiles.addListener((ListChangeListener<String>) c -> {
            recentListView.visibleProperty().setValue(c.getList().size() > 0);
            recentListView.getSelectionModel().selectFirst();
        });
        recentListView.setOnMouseClicked(event -> {
            if (event.getClickCount() > 1) {
                openRecentListFile(event);
            }
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
            if (window.getMember("app").equals("undefined"))
                window.setMember("app", this);
        });
        mathjaxEngine.load(String.format("http://localhost:%d/mathjax.html", port));

        previewEngine = previewView.getEngine();
        previewEngine.load(String.format("http://localhost:%d/preview.html", port));
        previewEngine.getLoadWorker().stateProperty().addListener((observableValue1, state, state2) -> {
            if (state2 == Worker.State.SUCCEEDED) {
                JSObject window = (JSObject) previewEngine.executeScript("window");
                if (window.getMember("app").equals("undefined")) {
                    window.setMember("app", this);
                }
            }
        });
        previewEngine.getLoadWorker().exceptionProperty().addListener((ov, t, t1) -> {
            logger.info(t1.getMessage(), t1);
        });


        /// Treeview
        if (Objects.nonNull(config.getWorkingDirectory())) {
            Path path = Paths.get(config.getWorkingDirectory());
            Optional<Path> optional = Files.notExists(path) ? Optional.empty() : Optional.of(path);
            directoryService.setWorkingDirectory(optional);
        }


        Path workDir = directoryService.getWorkingDirectory().orElse(userHome);
        fileBrowser.browse(treeView, workDir);

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

        removePathItem.setOnAction(event -> {

            ObservableList<TreeItem<Item>> selectedItems = treeView.getSelectionModel().getSelectedItems();

            DeleteAlert.alert().ifPresent(button -> {
                if (button == ButtonType.YES)
                    selectedItems.stream()
                            .map(e -> e.getValue())
                            .map(e -> e.getPath())
                            .forEach(IOHelper::deleteIfExists);
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
            boolean selectedAnyFolder = ((ObservableList<Integer>) p.getList()).stream().anyMatch(index -> {
                TreeItem<Item> item = treeView.getTreeItem(index);
                Path itemPath = item.getValue().getPath();
                boolean isFile = itemPath.toFile().isFile();
                return !isFile;
            });

            if (selectedAnyFolder)
                removePathItem.setDisable(true);
            else
                removePathItem.setDisable(false);
        });

        previewView.setContextMenuEnabled(false);
        ContextMenu previewContextMenu = new ContextMenu(MenuItemBuilt.item("Refresh").onclick(event -> {
            previewEngine.executeScript("clearImageCache()");
        }));
        previewContextMenu.setAutoHide(true);
        previewView.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.SECONDARY) {
                previewContextMenu.show(previewView, event.getScreenX(), event.getScreenY());
            } else {
                previewContextMenu.hide();
            }
        });

        newDoc(null);
    }

    private void loadShortCuts() {
        try {
            YamlReader yamlReader =
                    new YamlReader(new FileReader(configPath.resolve("shortcuts.yml").toFile()));
            yamlReader.getConfig().setClassTag("ShortCuts", ShortCuts.class);
            shortCuts = yamlReader.read(ShortCuts.class).getKeys();

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    private void openRecentListFile(Event event) {
        Path path = Paths.get(recentListView.getSelectionModel().getSelectedItem());

        directoryService.getOpenFileConsumer().accept(path);

    }

    private void loadConfigurations() {
        try {
            CodeSource codeSource = ApplicationController.class.getProtectionDomain().getCodeSource();
            File jarFile = new File(codeSource.getLocation().toURI().getPath());
            configPath = jarFile.toPath().getParent().getParent().resolve("conf");

            YamlReader yamlReader =
                    new YamlReader(new FileReader(configPath.resolve("config.yml").toFile()));
            yamlReader.getConfig().setClassTag("Config", Config.class);
            config = yamlReader.read(Config.class);

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

        if (!config.getDirectoryPanel())
            threadService.runActionLater(() -> {
                splitPane.setDividerPositions(0, 0.51);
            });

    }

    private void loadRecentFileList() {

        try {
            YamlReader yamlReader =
                    new YamlReader(new FileReader(configPath.resolve("recentFiles.yml").toFile()));
            yamlReader.getConfig().setClassTag("RecentFiles", RecentFiles.class);
            RecentFiles readed = yamlReader.read(RecentFiles.class);

            recentFiles.addAll(readed.getFiles());
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    public void externalBrowse() {
        hostServices.showDocument(String.format("http://localhost:%d/index.html", port));
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
    public void closeApp(ActionEvent event) throws IOException {
        yamlService.persist();
    }

    @FXML
    public void openDoc(Event event) {
        documentService.openDoc();
    }

    @FXML
    public void newDoc(Event event) {
        threadService.runActionLater(() -> {
            documentService.newDoc();
        });
    }

    @FXML
    public void hideLeftSplit(Event event) {
        splitPane.setDividerPositions(0, 0.51);
    }

    public void onscroll(Object pos, Object max) {
        scrollService.onscroll(pos, max);
    }

    public void scrollToCurrentLine(String text) {
        scrollService.scrollToCurrentLine(text);
    }

    public void plantUml(String uml, String type, String fileName) throws IOException {

        threadService.runTaskLater(() -> {
            plantUmlService.plantUml(uml, type, fileName);
        });
    }

    public void appendWildcard() {
        String currentTabText = current.getCurrentTabText();
        if (!currentTabText.contains(" *"))
            current.setCurrentTabText(currentTabText + " *");
    }

    public void textListener(String text) {

        threadService.runTaskLater(() -> {
            renderService.convertBasicHtml(text, rendered -> {
                if (Objects.nonNull(rendered))
                    lastRendered.setValue(rendered);
            });
        });
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

    public String clipboardValue() {
        return clipboard.getString();
    }

    public void paste(boolean showContextMenu) {

        JSObject window = renderService.getWindow();
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

                if (showContextMenu) {
                    ContextMenu contextMenu = new ContextMenu();
                    contextMenu.getItems().addAll(MenuItemBuilt.item("Paste").onclick(event -> {
                        String html = Optional.ofNullable(clipboard.getHtml()).orElse(clipboard.getString());
                        String content = (String) window.call(current.currentTab().htmlToMarkupFunction(), html);
                        editor.call("insert", content);
                    }));
                    contextMenu.getItems().addAll(MenuItemBuilt.item("Paste raw").onclick(event -> {
                        editor.call("insert", clipboard.getString());
                    }));
                    contextMenu.show(stage);
                } else {
                    String html = Optional.ofNullable(clipboard.getHtml()).orElse(clipboard.getString());
                    String content = (String) window.call(current.currentTab().htmlToMarkupFunction(), html);
                    editor.call("insert", content);
                }
                return;
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

        editor.call("insert",clipboard.getString());

    }

    public String pasteRaw() {

        if (clipboard.hasFiles()) {
            Optional<String> block = parserService.toImageBlock(clipboard.getFiles());
            if (block.isPresent())
                return block.get();
        }

        return clipboard.getString();
    }

    public void adjustSplitPane() {
        if (splitPane.getDividerPositions()[0] > 0.1)
            splitPane.setDividerPositions(0, 1);
        else
            splitPane.setDividerPositions(0.18, 0.60);
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

    public void setScene(Scene scene) {
        this.scene = scene;
    }

    public Scene getScene() {
        return scene;
    }

    public void setTableAnchor(AnchorPane tableAnchor) {
        this.tableAnchor = tableAnchor;
    }

    public AnchorPane getTableAnchor() {
        return tableAnchor;
    }

    public void setTableStage(Stage tableStage) {
        this.tableStage = tableStage;
    }

    public Stage getTableStage() {
        return tableStage;
    }

    public void setConfigAnchor(AnchorPane configAnchor) {
        this.configAnchor = configAnchor;
    }

    public AnchorPane getConfigAnchor() {
        return configAnchor;
    }

    public void setConfigStage(Stage configStage) {
        this.configStage = configStage;
    }

    public Stage getConfigStage() {
        return configStage;
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

    public Config getConfig() {
        return config;
    }

    public TablePopupService getTablePopupController() {
        return tablePopupController;
    }

    public StringProperty getLastRendered() {
        return lastRendered;
    }

    public ObservableList<String> getRecentFiles() {
        return recentFiles;
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

    public WebView getPreviewView() {
        return previewView;
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
    private void openAbout(ActionEvent actionEvent) throws IOException {
        Popup popup = new Popup();
        AnchorPane anchorPane = new AnchorPane();
        anchorPane.getStyleClass().add("about-popup");
        popup.setAutoHide(true);

        try (InputStream stream = ApplicationController.class.getResourceAsStream("/banner.txt");) {
            String banner = IOUtils.toString(stream);
            Label bannerLabel = new Label(banner);
            bannerLabel.getStyleClass().add("banner-label");
            anchorPane.getChildren().add(bannerLabel);
        }

        popup.getContent().add(anchorPane);
        popup.show(getStage());
    }

    @FXML
    public void generateCheatSheet(ActionEvent actionEvent) {
        Path cheatsheetPath = configPath.resolve("cheatsheet/cheatsheet.asc");
        tabService.addTab(cheatsheetPath);
    }
}
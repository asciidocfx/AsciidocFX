package com.kodcu.controller;


import com.esotericsoftware.yamlbeans.YamlException;
import com.esotericsoftware.yamlbeans.YamlReader;
import com.kodcu.bean.Config;
import com.kodcu.bean.RecentFiles;
import com.kodcu.bean.ShortCuts;
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
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
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
import javafx.stage.Stage;
import netscape.javascript.JSObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.EmbeddedWebApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.CodeSource;
import java.util.*;
import java.util.stream.StreamSupport;

import static java.nio.file.StandardOpenOption.*;


@Controller
public class ApplicationController extends TextWebSocketHandler implements Initializable {

    private Logger logger = LoggerFactory.getLogger(ApplicationController.class);

    public TabPane tabPane;
    public WebView previewView;
    public SplitPane splitPane;
    public SplitPane splitPaneVertical;
    public TreeView<Item> treeView;
    public Label splitHideButton;
    public Label workingDirButton;
    public AnchorPane rootAnchor;
    public MenuBar recentFilesBar;
    public ProgressBar indikator;
    public ListView<String> recentListView;
    public MenuItem openFileTreeItem;
    public MenuItem openFileListItem;
    public MenuItem copyPathTreeItem;
    public MenuItem copyPathListItem;
    public MenuItem copyTreeItem;
    public MenuItem copyListItem;
    private WebView mathjaxView;

    @Autowired
    private TablePopupService tablePopupController;

    @Autowired
    private TreeService treeService;

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
    private DocBookService docBookController;

    @Autowired
    private Html5BookService htmlBookService;

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

    private Stage stage;
    private WebEngine previewEngine;
    private StringProperty lastRendered = new SimpleStringProperty();
    private List<WebSocketSession> sessionList = new ArrayList<>();
    private Scene scene;
    private AnchorPane tableAnchor;
    private Stage tableStage;
    private Clipboard clipboard = Clipboard.getSystemClipboard();
    private ObservableList<String> recentFiles = FXCollections.synchronizedObservableList(FXCollections.observableArrayList());
    private AnchorPane configAnchor;
    private Stage configStage;
    private int port = 8080;
    private HostServicesDelegate hostServices;
    private Path configPath;
    private Config config;

    private List<String> bookNames = Arrays.asList("book.asc", "book.txt", "book.asciidoc", "book.adoc", "book.ad");

    private Map<String, String> shortCuts;

    private ChangeListener<String> lastRenderedChangeListener = (observableValue, old, nev) -> {

        if (Objects.isNull(nev))
            return;

        threadService.runActionLater(run -> {
            previewEngine.executeScript(String.format("refreshUI('%s')", IOHelper.normalize(nev)));
        });

        sessionList.stream().filter(e -> e.isOpen()).forEach(e -> {
            try {
                e.sendMessage(new TextMessage(nev));
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });
    };

    @FXML
    public void createTable(Event event) {
        threadService.runActionLater(run -> {
            tableStage.show();
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

    @FXML
    private void generatePdf(ActionEvent event) {
        threadService.runTaskLater((task) -> {
            Path currentPath = directoryService.workingDirectory();
            docBookController.generateDocbook(previewEngine, currentPath, false);
            fopServiceRunner.generateBook(currentPath, configPath);
        });
    }

    @FXML
    private void generateSampleBook(ActionEvent event) {

        DirectoryChooser directoryChooser = directoryService.newDirectoryChooser("Select a New Directory for sample book");
        File file = directoryChooser.showDialog(null);
        threadService.runTaskLater((task) -> {
            sampleBookService.produceSampleBook(configPath, file.toPath());
            directoryService.setWorkingDirectory(Optional.of(file.toPath()));
            fileBrowser.browse(treeView, file.toPath());
            Platform.runLater(() -> {
                directoryView(null);
                tabService.addTab(file.toPath().resolve("book.asc"));
            });
        });
    }

    @FXML
    private void convertDocbook(ActionEvent event) {

        threadService.runTaskLater(task -> {
            Path currentPath = directoryService.workingDirectory();
            docBookController.generateDocbook(previewEngine, currentPath, true);
        });

    }

    @FXML
    private void convertEpub(ActionEvent event) throws Exception {

        threadService.runTaskLater((task) -> {
            Path currentPath = directoryService.workingDirectory();
            docBookController.generateDocbook(previewEngine, currentPath, false);
            epub3Service.produceEpub3(currentPath, configPath);
        });
    }

    public String appendFormula(String fileName, String formula) {
        return mathJaxService.appendFormula(fileName, formula);
    }

    public void svgToPng(String fileName, String svg, String formula, float width, float height) {
        mathJaxService.svgToPng(fileName, svg, formula, width, height);
    }

    @FXML
    private void convertMobi(ActionEvent event) throws Exception {

        Path currentPath = directoryService.workingDirectory();

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

        threadService.runTaskLater((task) -> {
            epub3Service.produceEpub3(currentPath, configPath);
            kindleMobiService.produceMobi(currentPath, config.getKindlegenDir());
        });

    }

    @FXML
    private void generateHtml(ActionEvent event) {
        threadService.runTaskLater(run -> {
            Path currentPath = directoryService.workingDirectory();
            htmlBookService.produceXhtml5(previewEngine, currentPath, configPath);
        });
    }

    public String createFileTree(String tree, String type, String fileName, String width, String height) throws IOException {
        return treeService.createFileTree(tree, type, fileName, width, height);
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {

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
            JSObject window = (JSObject) previewEngine.executeScript("window");
            if (window.getMember("app").equals("undefined"))
                window.setMember("app", this);
        });
        previewEngine.getLoadWorker().exceptionProperty().addListener((ov, t, t1) -> {
            t1.printStackTrace();
        });


        /// Treeview
        if (Objects.nonNull(config.getWorkingDirectory())) {
            Optional<Path> optional = Optional.ofNullable(Paths.get(config.getWorkingDirectory()));
            directoryService.setWorkingDirectory(optional);
        }

        Path home = Paths.get(System.getProperty("user.home"));
        Path workDir = directoryService.getWorkingDirectory().orElse(home);
        fileBrowser.browse(treeView, workDir);

        //
        AwesomeDude.setIcon(workingDirButton, AwesomeIcon.FOLDER_ALT, "14.0");
        AwesomeDude.setIcon(splitHideButton, AwesomeIcon.CHEVRON_LEFT, "14.0");

        tabPane.getTabs().addListener((ListChangeListener<Tab>) c -> {
            if (tabPane.getTabs().isEmpty())
                threadService.runActionLater(this::newDoc);
        });

        openFileTreeItem.setOnAction(event -> {
            Path path = tabService.getSelectedTabPath();
            directoryService.getOpenFileConsumer().accept(path);
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

        treeView.setOnMouseClicked(event -> {
            boolean fxApplicationThread = Platform.isFxApplicationThread();
            TreeItem<Item> selectedItem = treeView.getSelectionModel().getSelectedItem();
            if (Objects.isNull(selectedItem))
                return;
            Path selectedPath = selectedItem.getValue().getPath();
            if (event.getButton() == MouseButton.PRIMARY)
                if (Files.isDirectory(selectedPath)) {
                    try {
                        if (selectedItem.getChildren().size() == 0) {
                            StreamSupport
                                    .stream(Files.newDirectoryStream(selectedPath).spliterator(), false)
                                    .filter(path -> !pathResolver.isHidden(path))
                                    .filter(pathResolver::isViewable)
                                    .sorted()
                                    .forEach(path -> {
                                        selectedItem.getChildren().add(new TreeItem<>(new Item(path), awesomeService.getIcon(path)));
                                    });
                        }
                        selectedItem.setExpanded(!selectedItem.isExpanded());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else if (event.getClickCount() > 1) {
                    directoryService.getOpenFileConsumer().accept(selectedPath);
                }
        });

        threadService.runActionLater(this::newDoc);

    }

    private void loadShortCuts() {
        try {
            YamlReader yamlReader =
                    new YamlReader(new FileReader(configPath.resolve("shortcuts.yml").toFile()));
            yamlReader.getConfig().setClassTag("ShortCuts", ShortCuts.class);
            shortCuts = yamlReader.read(ShortCuts.class).getKeys();

        } catch (Exception e) {
            e.printStackTrace();
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
            e.printStackTrace();
        }

        if (!config.getDirectoryPanel())
            Platform.runLater(() -> {
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
        } catch (YamlException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
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
        documentService.newDoc();
    }

    @FXML
    public void hideLeftSplit(Event event) {
        splitPane.setDividerPositions(0, 0.51);
    }

    public void applySohrtCuts() {
        Set<String> keySet = shortCuts.keySet();
        for (String key : keySet) {
            current.currentEngine().executeScript(String.format("addNewCommand('%s','%s')", key, shortCuts.get(key)));
        }
    }

    public void onscroll(Object pos, Object max) {
        scrollService.onscroll(pos, max);
    }

    public void scrollToCurrentLine(String text) {
        scrollService.scrollToCurrentLine(text);
    }

    public String plantUml(String uml, String type, String fileName) throws IOException {
        return plantUmlService.plantUml(uml, type, fileName);
    }

    public void appendWildcard() {
        Label label = current.currentTabLabel();

        if (!label.getText().contains(" *"))
            label.setText(label.getText() + " *");
    }

    public void textListener(String text) {

        threadService.runTaskLater(task -> {
            String rendered = renderService.convertBasicHtml(previewEngine, text);
            if (Objects.nonNull(rendered))
                lastRendered.setValue(rendered);
        });

    }

    public void htmlOnePage() {

        if (bookNames.contains(current.getCurrentTabText())) {
            generateHtml(null);
            return;
        }

        if (!current.currentPath().isPresent())
            saveDoc();

        threadService.runTaskLater(task -> {
            indikatorService.startCycle();

            String html = renderService.convertHtmlArticle(previewEngine);
            String tabText = current.getCurrentTabText().replace("*", "").trim();

            Path currentPath = directoryService.currentPath();
            Path path = currentPath.getParent().resolve(tabText.concat(".html"));
            IOHelper.writeToFile(path, html, CREATE, TRUNCATE_EXISTING, WRITE);
            indikatorService.hideIndikator();
            threadService.runActionLater(run -> {
                recentFiles.remove(path.toString());
                recentFiles.add(0, path.toString());
            });

        });

    }

    public void cutCopy(String data) {
        ClipboardContent clipboardContent = new ClipboardContent();
        clipboardContent.putString(data);
        clipboard.setContent(clipboardContent);
    }

    public void pdfOnePage() {

        if (bookNames.contains(current.getCurrentTabText())) {
            generatePdf(null);
            return;
        }

        if (!current.currentPath().isPresent())
            saveDoc();

        threadService.runTaskLater(task -> {
            Path currentPath = directoryService.currentPath();
            String docbook = docBookController.generateDocbookArticle(previewEngine, currentPath);
            fopServiceRunner.generateArticle(currentPath.getParent(), configPath, docbook);
        });

    }

    public void copyFile(Path path) {
        ClipboardContent clipboardContent = new ClipboardContent();
        clipboardContent.putFiles(Arrays.asList(path.toFile()));
        clipboard.setContent(clipboardContent);
    }


    public String paste() {
        if (clipboard.hasFiles()) {
            Optional<String> block = parserService.toImageBlock(clipboard.getFiles());
            if (block.isPresent())
                return block.get();
        }

        if (clipboard.hasImage() && clipboard.hasHtml()) {
            Optional<String> block = parserService.toWebImageBlock(clipboard.getHtml());
            if (block.isPresent())
                return block.get();
        }

        return clipboard.getString();
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
        tabPane.getTabs().remove(current.currentTab());
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
}
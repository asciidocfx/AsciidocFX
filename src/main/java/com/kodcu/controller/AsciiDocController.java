package com.kodcu.controller;


import com.esotericsoftware.yamlbeans.YamlException;
import com.esotericsoftware.yamlbeans.YamlReader;
import com.esotericsoftware.yamlbeans.YamlWriter;
import com.kodcu.bean.Config;
import com.kodcu.bean.RecentFiles;
import com.kodcu.other.Current;
import com.kodcu.other.IOHelper;
import com.kodcu.other.Item;
import com.kodcu.service.*;
import com.sun.javafx.application.HostServicesDelegate;
import de.jensd.fx.fontawesome.AwesomeDude;
import de.jensd.fx.fontawesome.AwesomeIcon;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ListChangeListener;
import javafx.concurrent.Task;
import javafx.concurrent.Worker;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Screen;
import javafx.stage.Stage;
import netscape.javascript.JSObject;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.EmbeddedWebApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.xml.sax.SAXException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.CodeSource;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;


@Controller
public class AsciiDocController extends TextWebSocketHandler implements Initializable {

    Logger logger = LoggerFactory.getLogger(AsciiDocController.class);

    public TabPane tabPane;
    public WebView previewView;
    public MenuItem openItem;
    public MenuItem newItem;
    public MenuItem saveItem;
    public SplitPane splitPane;
    public Menu recentMenu;
    public TreeView<Item> treeView;
    public Button splitHideButton;
    public Button WorkingDirButton;

    public MenuBar menubar;
    public HBox windowHBox;
    public ProgressIndicator indikator;
    public Hyperlink lastConvertedFileLink;

    @Autowired
    private TablePopupController tablePopupController;

    @Autowired
    private AsciiDoctorRenderService docConverter;

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


    private Stage stage;
    private WebEngine previewEngine;
    private StringProperty lastRendered = new SimpleStringProperty();
    private List<WebSocketSession> sessionList = new ArrayList<>();
    private Scene scene;
    private AnchorPane tableAnchor;
    private Stage tableStage;

    private Clipboard clipboard = Clipboard.getSystemClipboard();
    private Optional<Path> initialDirectory = Optional.empty();
    private Set<Path> recentFiles = new HashSet<>();

    private String waitForGetValue;
    private String waitForSetValue;

    private AnchorPane configAnchor;
    private Stage configStage;

    @Autowired
    private EmbeddedWebApplicationContext server;

    private int tomcatPort = 8080;
    private HostServicesDelegate hostServices;
    private double sceneXOffset;
    private double sceneYOffset;
    private Path configPath;
    private Config config;
    private Optional<String> workingDirectory;
    private Optional<Path> lastConvertedFile = Optional.empty();
    private String scrollerJs;

    @FXML
    private void createTable(ActionEvent event) throws IOException {
        tableStage.show();
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
    private void generatePdf(ActionEvent event) throws IOException, SAXException {

//        Path currentPath = initialDirectory.map(path -> Files.isDirectory(path) ? path : path.getParent()).get();
        Path currentPath = Paths.get(workingDirectory.get());
        docBookController.generateDocbook(previewEngine, currentPath, false);

        invokeTask((task) -> {
            fopServiceRunner.generate(currentPath, configPath);
        });
    }

    @FXML
    private void generateSampleBook(ActionEvent event) {

        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Select a New Directory for sample book");
        File file = directoryChooser.showDialog(null);
        invokeTask((task) -> {
            sampleBookService.produceSampleBook(configPath, file.toPath());
            workingDirectory = Optional.of(file.toString());
            initialDirectory = Optional.of(file.toPath());
            fileBrowser.browse(treeView, this, file.toString());
            Platform.runLater(() -> {
                directoryView(null);
                addTab(file.toPath().resolve("book.asc"));
            });
        });
    }

    @FXML
    private void convertDocbook(ActionEvent event) {
        Path currentPath = Paths.get(workingDirectory.get());
//        Path currentPath = initialDirectory.map(path -> Files.isDirectory(path) ? path : path.getParent()).get();
        docBookController.generateDocbook(previewEngine, currentPath, true);

    }

    @FXML
    private void openLastConvertedFile(ActionEvent event) {
        lastConvertedFile.ifPresent(path -> {
            getHostServices().showDocument(path.toUri().toString());
        });

    }

    @FXML
    private void convertEpub(ActionEvent event) throws Exception {

//        Path currentPath = initialDirectory.map(path -> Files.isDirectory(path) ? path : path.getParent()).get();
        Path currentPath = Paths.get(workingDirectory.get());
        docBookController.generateDocbook(previewEngine, currentPath, false);

        invokeTask((task) -> {
            epub3Service.produceEpub3(currentPath, configPath);
        });
    }

    private <T> void invokeTask(Consumer<Task<T>> consumer) {

        Task<T> task = new Task<T>() {
            @Override
            protected T call() throws Exception {
                consumer.accept(this);
                return null;
            }
        };

        new Thread(task).start();

    }

    @FXML
    private void convertMobi(ActionEvent event) throws Exception {


        Path currentPath = Paths.get(workingDirectory.get());

        if (Objects.nonNull(config.getKindlegenDir())) {
            if (!Files.exists(Paths.get(config.getKindlegenDir()))) {
                config.setKindlegenDir(null);
            }
        }

        if (Objects.isNull(config.getKindlegenDir())) {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Select 'kindlegen' File");
            File kindlegenFile = fileChooser.showOpenDialog(null);
            if (Objects.isNull(kindlegenFile))
                return;

            config.setKindlegenDir(kindlegenFile.toPath().getParent().toString());

        }


        convertEpub(null);

        invokeTask((task) -> {
            kindleMobiService.produceMobi(currentPath, config.getKindlegenDir());
        });

    }

    //    @FXML
    private void generateHtml(ActionEvent event) {

        convertDocbook(null);
        Path currentPath = Paths.get(workingDirectory.get());
        htmlBookService.produceXhtml5(currentPath, configPath);
    }


    @FXML
    private void maximize(Event event) {

        // Change stage properties
        Rectangle2D bounds = Screen.getPrimary().getVisualBounds();

        if (bounds.getHeight() == stage.getHeight() && bounds.getWidth() == stage.getWidth()) {
            stage.setX(50);
            stage.setY(50);
            stage.setWidth(bounds.getWidth() * 0.8);
            stage.setHeight(bounds.getHeight() * 0.8);
        } else {
            stage.setX(bounds.getMinX());
            stage.setY(bounds.getMinY());
            stage.setWidth(bounds.getWidth());
            stage.setHeight(bounds.getHeight());
        }
    }

    @FXML
    private void minimize(ActionEvent event) {
        getStage().setIconified(true);
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        runActionLater(this::newDoc);

        try {
            CodeSource codeSource = AsciiDocController.class.getProtectionDomain().getCodeSource();
            File jarFile = new File(codeSource.getLocation().toURI().getPath());
            configPath = jarFile.toPath().getParent().getParent().resolve("conf");
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        loadConfigurations();
        loadRecentFileList();


        tomcatPort = server.getEmbeddedServletContainer().getPort();

        waitForGetValue = IOHelper.convert(AsciiDocController.class.getResourceAsStream("/waitForGetValue.js"));
        waitForSetValue = IOHelper.convert(AsciiDocController.class.getResourceAsStream("/waitForSetValue.js"));
        scrollerJs = IOHelper.convert(AsciiDocController.class.getResourceAsStream("/scroller.js"));

        lastRendered.addListener((observableValue, old, nev) -> {
            sessionList.stream().filter(e -> e.isOpen()).forEach(e -> {
                try {
                    e.sendMessage(new TextMessage(nev));
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            });
        });


        previewEngine = previewView.getEngine();
        previewEngine.load(String.format("http://localhost:%d/index.html", tomcatPort));

        previewEngine.getLoadWorker().stateProperty().addListener((observableValue1, state, state2) -> {
            if (state2 == Worker.State.SUCCEEDED) {
                JSObject window = (JSObject) previewEngine.executeScript("window");
                window.setMember("app", this);
            }
        });

        previewEngine.getLoadWorker().exceptionProperty().addListener((ov, t, t1) -> {
            t1.printStackTrace();
        });


        /// Treeview

        workingDirectory = Optional.ofNullable(config.getWorkingDirectory());

        String workDir = workingDirectory.orElse(System.getProperty("user.home"));
//
        fileBrowser.browse(treeView, this, workDir);

        //

        AwesomeDude.setIcon(WorkingDirButton, AwesomeIcon.FOLDER_ALT);
        AwesomeDude.setIcon(splitHideButton, AwesomeIcon.CHEVRON_LEFT);

        //

        menubar.setOnMouseClicked(event -> {
            if (event.getClickCount() > 1)
                maximize(event);
        });

        menubar.setOnMousePressed(event -> {
            sceneXOffset = event.getSceneX();
            sceneYOffset = event.getSceneY();
        });
        menubar.setOnMouseDragged(event -> {

            double maxWidth = Screen.getPrimary().getVisualBounds().getWidth();
            double maxHeight = Screen.getPrimary().getVisualBounds().getHeight();
            double currentWidth = getStage().getWidth();
            double currentXPosition = getStage().getX();


            double kose = (getStage().getX() + event.getSceneX()) - maxWidth;

            // Sag tarafa yaslan
            if (kose >= -2 && kose <= 2 && ((100 * event.getSceneX()) / currentWidth) > 50) {
                getStage().setHeight(maxHeight);
                getStage().setY(0);
                getStage().setX(maxWidth - currentWidth);
            }
            // Sol tarafa yaslan
            else if ((getStage().getX() + event.getSceneX()) <= 2
                    && (getStage().getX() + event.getSceneX()) >= -2
                    && ((100 * event.getSceneX()) / currentWidth) < 50) {
                getStage().setHeight(maxHeight);
                getStage().setY(0);
                getStage().setX(0);
            }
            // Dolan
            else {
                getStage().setX(event.getScreenX() - sceneXOffset);
                getStage().setY(event.getScreenY() - sceneYOffset);
            }

        });

        //


        indikator.visibleProperty().addListener((observable, oldValue, newValue) -> {
            lastConvertedFile.ifPresent(path -> {
                lastConvertedFileLink.setVisible(!newValue);
            });
        });

        tabPane.getTabs().addListener((ListChangeListener<Tab>) c -> {
            if (tabPane.getTabs().isEmpty())
                runActionLater(this::newDoc);
        });

    }

    private void runActionLater(Consumer<ActionEvent> consumer) {
        Platform.runLater(() -> {
            consumer.accept(null);
        });
    }

    private void loadConfigurations() {
        try {
            YamlReader yamlReader =
                    new YamlReader(new FileReader(configPath.resolve("config.yml").toFile()));
            yamlReader.getConfig().setClassTag("Config", Config.class);
            config = yamlReader.read(Config.class);

        } catch (YamlException | FileNotFoundException e) {
            e.printStackTrace();
        }

        if (!config.getDirectoryPanel())
            Platform.runLater(() -> {
                splitPane.setDividerPositions(0, 0.5);
            });

    }

    private void loadRecentFileList() {

        try {
            YamlReader yamlReader =
                    new YamlReader(new FileReader(configPath.resolve("recentFiles.yml").toFile()));
            yamlReader.getConfig().setClassTag("RecentFiles", RecentFiles.class);
            RecentFiles readed = yamlReader.read(RecentFiles.class);

            readed.getFiles()
                    .stream()
                    .map(path -> Paths.get(path))
                    .forEach(recentFiles::add);
        } catch (YamlException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void externalBrowse() {

        hostServices.showDocument(String.format("http://localhost:%d/index.html", tomcatPort));
    }

    @FXML
    public void changeWorkingDir(ActionEvent actionEvent) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        initialDirectory.ifPresent(path -> {
            if (Files.isDirectory(path))
                directoryChooser.setInitialDirectory(path.toFile());
            else
                directoryChooser.setInitialDirectory(path.getParent().toFile());
        });
        directoryChooser.setTitle("Select Working Directory");
        File selectedDir = directoryChooser.showDialog(null);
        if (Objects.nonNull(selectedDir)) {
            config.setWorkingDirectory(selectedDir.toString());
            workingDirectory = Optional.of(selectedDir.toString());
            initialDirectory = Optional.of(selectedDir.toPath());
            fileBrowser.browse(treeView, this, selectedDir.toString());
        }
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        sessionList.add(session);
        String value = lastRendered.getValue();
        if (Objects.nonNull(value))
            session.sendMessage(new TextMessage(value));

    }

    @FXML
    private void closeApp(ActionEvent event) throws IOException {

        List<String> fileList = recentFiles
                .stream()
                .collect(Collectors.toList())
                .stream()
                .map(path -> path.toString())
                .collect(Collectors.toList());

        File recentFileYml = configPath.resolve("recentFiles.yml").toFile();
        YamlWriter yamlWriter = new YamlWriter(new FileWriter(recentFileYml));
        yamlWriter.getConfig().setClassTag("RecentFiles", RecentFiles.class);
        yamlWriter.write(new RecentFiles(fileList));
        yamlWriter.close();

        //

        File configYml = configPath.resolve("config.yml").toFile();
        yamlWriter = new YamlWriter(new FileWriter(configYml));
        yamlWriter.getConfig().setClassTag("Config", Config.class);
        yamlWriter.write(config);
        yamlWriter.close();

        Platform.exit();
        System.exit(0);

    }

    @FXML
    private void openDoc(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Asciidoc", "*.asciidoc", "*.adoc", "*.asc", "*.ad", "*.txt"));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("All", "*.*"));
        initialDirectory.ifPresent(e -> {
            if (Files.isDirectory(e))
                fileChooser.setInitialDirectory(e.toFile());
            else
                fileChooser.setInitialDirectory(e.getParent().toFile());
        });
        List<File> chosenFiles = fileChooser.showOpenMultipleDialog(stage);
        if (chosenFiles != null) {
            initialDirectory = Optional.of(chosenFiles.get(0).toPath());
            chosenFiles.stream().map(e -> e.toPath()).forEach(this::addTab);
            recentFiles.addAll(chosenFiles.stream().map(e -> e.toPath()).collect(Collectors.toList()));
        }

    }

    @FXML
    private void recentFileList(Event event) {
        List<MenuItem> menuItems = recentFiles.stream().filter(path -> !Files.isDirectory(path)).map(path -> {
            MenuItem menuItem = new MenuItem();
            menuItem.setText(path.toAbsolutePath().toString());
            menuItem.setOnAction(actionEvent -> {
                addTab(path);
            });
            return menuItem;
        }).limit(config.getRecentFileListSize()).collect(Collectors.toList());

        recentMenu.getItems().clear();
        recentMenu.getItems().addAll(menuItems);

    }

    @FXML
    public void newDoc(ActionEvent event) {

        WebView webView = createWebView();
        AnchorPane anchorPane = new AnchorPane();
        anchorPane.getChildren().add(webView);
        fitToParent(webView);
        Tab tab = createTab();
        tab.setContent(anchorPane);
        tab.selectedProperty().addListener((observableValue, before, after) -> {
            if (after) {
                current.putTab(tab, current.getNewTabPaths().get(tab), webView);
                WebEngine webEngine = webView.getEngine();

                if (webEngine.getLoadWorker().getState() == Worker.State.SUCCEEDED)
                    webEngine.executeScript(waitForGetValue);
            }
        });
        ((Label) tab.getGraphic()).setText("new *");
        current.putTab(tab, null, webView);
        tabPane.getTabs().add(tab);

    }

    public void addTab(Path path) {

        AnchorPane anchorPane = new AnchorPane();
        WebView webView = createWebView();
        WebEngine webEngine = webView.getEngine();
        webEngine.getLoadWorker().stateProperty().addListener((observableValue1, state, state2) -> {
            if (state2 == Worker.State.SUCCEEDED) {
                webEngine.executeScript(String.format(waitForSetValue, IOHelper.normalize(IOHelper.readFile(path))));
            }
        });

        anchorPane.getChildren().add(webView);

        fitToParent(webView);

        Tab tab = createTab();
        ((Label) tab.getGraphic()).setText(path.getFileName().toString());
        tab.setContent(anchorPane);

        tab.selectedProperty().addListener((observableValue, before, after) -> {
            if (after) {
                current.putTab(tab, path, webView);
                webEngine.executeScript(waitForGetValue);

            }
        });

        current.putTab(tab, path, webView);
        tabPane.getTabs().add(tab);

        Tab lastTab = tabPane.getTabs().get(tabPane.getTabs().size() - 1);
        tabPane.getSelectionModel().select(lastTab);

        recentFiles.add(path);

    }

    @FXML
    public void hideLeftSplit(ActionEvent event) {
        splitPane.setDividerPositions(0, 0.5);
    }

    private Tab createTab() {
        Tab tab = new Tab();

        MenuItem menuItem0 = new MenuItem("Close All Tabs");
        menuItem0.setOnAction(actionEvent -> {
            tabPane.getTabs().clear();
        });
        MenuItem menuItem1 = new MenuItem("Close All Other Tabs");
        menuItem1.setOnAction(actionEvent -> {
            List<Tab> blackList = new ArrayList<>();
            blackList.addAll(tabPane.getTabs());
            blackList.remove(tab);
            tabPane.getTabs().removeAll(blackList);
        });

        ContextMenu contextMenu = new ContextMenu();
        contextMenu.getItems().addAll(menuItem0, menuItem1);

        tab.contextMenuProperty().setValue(contextMenu);
        Label label = new Label();

        label.setOnMouseClicked(mouseEvent -> {
            if (mouseEvent.getClickCount() > 1) {
                if (splitPane.getDividerPositions()[0] > 0.1)
                    splitPane.setDividerPositions(0, 1);
                else
                    splitPane.setDividerPositions(0.1610294117647059, 0.5823529411764706);
            }
        });

        tab.setGraphic(label);


        return tab;
    }


    private WebView createWebView() {

        WebView webView = new WebView();


        WebEngine webEngine = webView.getEngine();
        JSObject window = (JSObject) webEngine.executeScript("window");
        window.setMember("app", this);
        webEngine.load(String.format("http://localhost:%d/editor.html", tomcatPort));

        return webView;
    }

    public void onscroll(Object pos, Object max) {
        if (Objects.isNull(pos) || Objects.isNull(max))
            return;

        Number position = (Number) pos; // current scroll position for editor
        Number maximum = (Number) max; // max scroll position for editor

        double ratio = (position.doubleValue() * 100) / maximum.doubleValue();
        Integer browserMaxScroll = (Integer) previewEngine.executeScript("document.documentElement.scrollHeight - document.documentElement.clientHeight;");
        double browserScrollOffset = (Double.valueOf(browserMaxScroll) * ratio) / 100.0;
        previewEngine.executeScript(String.format("window.scrollTo(0, %f )", browserScrollOffset));

    }

    public void scrollToCurrentLine(String text) {

        if ("".equals(text))
            return;

        String format = String.format(scrollerJs, text);
        try {
            previewEngine.executeScript(format);
        } catch (Exception e) {

        }
    }

    @RequestMapping(value = {"**.asciidoc", "**.asc", "**.txt", "**.ad", "**.adoc"}, method = RequestMethod.GET)
    @ResponseBody
    public DeferredResult<String> asciidoc(HttpServletRequest request) {

        DeferredResult<String> deferredResult = new DeferredResult<String>();

        String uri = request.getRequestURI();

        if (uri.startsWith("/"))
            uri = uri.substring(1);

        if (Objects.nonNull(current.currentPath())) {
            Path ascFile = current.currentParentRoot().resolve(uri);

            Platform.runLater(() -> {
                this.addTab(ascFile);
            });

            deferredResult.setResult("OK");
        }

        return deferredResult;
    }

    @RequestMapping(value = {"/**/{extension:(?:\\w|\\W)+\\.(?:jpg|bmp|gif|jpeg|png|webp)$}"}, method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<byte[]> images(HttpServletRequest request, @PathVariable("extension") String extension) {


        Enumeration<String> headerNames = request.getHeaderNames();
        String uri = request.getRequestURI();
        byte[] temp = new byte[]{};
        if (uri.startsWith("/"))
            uri = uri.substring(1);

        if (Objects.nonNull(current.currentPath())) {
            try {
                Path imageFile = current.currentParentRoot().resolve(uri);
                FileInputStream fileInputStream = new FileInputStream(imageFile.toFile());
                temp = IOUtils.toByteArray(fileInputStream);
                IOUtils.closeQuietly(fileInputStream);
            } catch (Exception ex) {
                logger.debug(ex.getMessage(), ex);
            }
        }

        return new ResponseEntity<>(temp, HttpStatus.OK);
    }

    public void appendWildcard() {
        Label label = (Label) current.getCurrentTab().getGraphic();

        if (!label.getText().contains(" *"))
            label.setText(label.getText() + " *");
    }

    public void textListener(String text) {

        Platform.runLater(() -> {
            docConverter.asciidocToHtml(previewEngine, text);
        });

    }


    public void cutCopy(String data) {
        ClipboardContent clipboardContent = new ClipboardContent();
        clipboardContent.putString(data);
        clipboard.setContent(clipboardContent);
    }

    public String paste() {
        return clipboard.getString();
    }

    @FXML
    public void saveDoc() {
        Path currentPath = current.currentPath();
        if (currentPath == null) {
            FileChooser chooser = new FileChooser();
            chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Asciidoc", "*.asciidoc", "*.adoc", "*.asc", "*.ad", "*.txt"));
            File file = chooser.showSaveDialog(null);
            if (file == null)
                return;
            IOHelper.writeToFile(file, (String) current.currentEngine().executeScript("editor.getValue();"), TRUNCATE_EXISTING, CREATE);
            current.putTab(current.getCurrentTab(), file.toPath(), current.currentView());
            current.setCurrentTabText(file.toPath().getFileName().toString());
            recentFiles.add(file.toPath());
        } else {
            IOHelper.writeToFile(currentPath.toFile(), (String) current.currentEngine().executeScript("editor.getValue();"), TRUNCATE_EXISTING, CREATE);
        }

        Label label = (Label) current.getCurrentTab().getGraphic();
        label.setText(label.getText().replace(" *", ""));
    }

    private void fitToParent(Node node) {
        AnchorPane.setTopAnchor(node, 0.0);
        AnchorPane.setBottomAnchor(node, 0.0);
        AnchorPane.setLeftAnchor(node, 0.0);
        AnchorPane.setRightAnchor(node, 0.0);
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

    public Optional<Path> getInitialDirectory() {
        return initialDirectory;
    }

    public Config getConfig() {
        return config;
    }

    public Optional<Path> getLastConvertedFile() {
        return lastConvertedFile;
    }

    public void setLastConvertedFile(Optional<Path> lastConvertedFile) {
        this.lastConvertedFile = lastConvertedFile;
    }

    public TablePopupController getTablePopupController() {
        return tablePopupController;
    }

    public StringProperty getLastRendered() {
        return lastRendered;
    }

}

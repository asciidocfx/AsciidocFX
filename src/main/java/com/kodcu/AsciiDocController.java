package com.kodcu;


import de.jensd.fx.fontawesome.AwesomeDude;
import de.jensd.fx.fontawesome.AwesomeIcon;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import netscape.javascript.JSObject;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
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

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;


@Controller
public class AsciiDocController extends TextWebSocketHandler implements Initializable {

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


    @Autowired
    private TablePopupController tablePopupController;

    @Autowired
    private Current current;

    @Autowired
    private FileBrowser fileBrowser;

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

    private Map<Path, TreeItem<Item>> dirTreeMap = new HashMap<>();

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

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        waitForGetValue = IOHelper.convert(AsciiDocController.class.getResourceAsStream("/waitForGetValue.js"));
        waitForSetValue = IOHelper.convert(AsciiDocController.class.getResourceAsStream("/waitForSetValue.js"));

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
        previewEngine.load("http://localhost:8080/index.html");
        previewEngine.getLoadWorker().exceptionProperty().addListener((ov, t, t1) -> {
            System.out.println("Received exception: " + t1.getMessage());
        });


        /// Treeview

        initialDirectory = Optional.of(Paths.get(System.getProperty("user.home")));
        fileBrowser.browse(treeView, this, System.getProperty("user.home"));

        //

        AwesomeDude.setIcon(WorkingDirButton, AwesomeIcon.FOLDER_OPEN_ALT);
        AwesomeDude.setIcon(splitHideButton, AwesomeIcon.CHEVRON_LEFT);

    }

    @FXML
    public void changeWorkingDir(ActionEvent actionEvent) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Select Working Directory");
        File selectedDir = directoryChooser.showDialog(null);
        if (Objects.nonNull(selectedDir)) {
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
    private void closeApp(ActionEvent event) {
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
        }).limit(20).collect(Collectors.toList());

        recentMenu.getItems().clear();
        recentMenu.getItems().addAll(menuItems);

    }

    @FXML
    private void newDoc(ActionEvent event) {
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
        tab.textProperty().setValue("new");
        tabPane.getTabs().add(tab);

        current.putTab(tab, null, webView);
    }


    public void addTab(Path path) {
        AnchorPane anchorPane = new AnchorPane();
        WebView webView = createWebView();
        WebEngine webEngine = webView.getEngine();
        webEngine.getLoadWorker().stateProperty().addListener((observableValue1, state, state2) -> {
            if (state2 == Worker.State.SUCCEEDED) {
                webEngine.executeScript(String.format(waitForSetValue, IOHelper.readFile(path)));
            }
        });

        anchorPane.getChildren().add(webView);

        fitToParent(webView);

        Tab tab = createTab();
        tab.textProperty().setValue(path.getFileName().toString());
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

    }

    @FXML
    public void hideLeftSplit(ActionEvent event) {
        splitPane.setDividerPositions(0.001, 0.5);
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
        return tab;
    }


    private WebView createWebView() {

        WebView webView = new WebView();

        WebEngine webEngine = webView.getEngine();
        JSObject window = (JSObject) webEngine.executeScript("window");
        window.setMember("app", this);
        webEngine.load("http://localhost:8080/editor.html");
        return webView;
    }

    public void onscroll(Object param) {
        if (Objects.isNull(param)) return;
        Number position = (Number) param;

//        if (Objects.nonNull(param))
        previewEngine.executeScript(String.format("window.scrollTo(0, %f )", position.doubleValue()));

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
    public ResponseEntity<byte[]> images(HttpServletRequest request, @PathVariable("extension") String extension) throws IOException {


        Enumeration<String> headerNames = request.getHeaderNames();
        String uri = request.getRequestURI();
        byte[] temp = new byte[]{};
        if (uri.startsWith("/"))
            uri = uri.substring(1);

        if (Objects.nonNull(current.currentPath())) {
            Path imageFile = current.currentParentRoot().resolve(uri);
            FileInputStream fileInputStream = new FileInputStream(imageFile.toFile());
            temp = IOUtils.toByteArray(fileInputStream);
            IOUtils.closeQuietly(fileInputStream);

        }

        return new ResponseEntity<>(temp, HttpStatus.OK);
    }

    public void textListener(ObservableValue observableValue, String old, String nev) {
        try {
            Platform.runLater(() -> {
//                previewEngine.executeScript("var asciidocOpts = Opal.hash2(['attributes'], {'attributes': ['backend=docbook5', 'doctype=book']});");
//                String rendered = (String) previewEngine.executeScript("Opal.Asciidoctor.$render('" + IO.normalize(nev) + "',asciidocOpts);");
                String nonnormalize = nev;
                String normalize = nev;
                String rendered = (String) previewEngine.executeScript(String.format("Opal.Asciidoctor.$render('%s');", IOHelper.normalize(normalize)));
                lastRendered.setValue(rendered);
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }

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
            current.getCurrentTab().setText(file.toPath().getFileName().toString());

            recentFiles.add(file.toPath());
        } else {
            IOHelper.writeToFile(currentPath.toFile(), (String) current.currentEngine().executeScript("editor.getValue();"), TRUNCATE_EXISTING, CREATE);
        }
    }

    private void fitToParent(Node node) {
        AnchorPane.setTopAnchor(node, 0.0);
        AnchorPane.setBottomAnchor(node, 0.0);
        AnchorPane.setLeftAnchor(node, 0.0);
        AnchorPane.setRightAnchor(node, 0.0);
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

    public Map<Path, TreeItem<Item>> getDirTreeMap() {
        return dirTreeMap;
    }
}

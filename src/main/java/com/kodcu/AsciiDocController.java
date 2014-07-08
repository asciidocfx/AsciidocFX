package com.kodcu;


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
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import netscape.javascript.JSObject;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
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

    public TabPane tabu;
    public WebView previewView;
    public MenuItem openItem;
    public MenuItem newItem;
    public MenuItem saveItem;
    public SplitPane splitter;
    public Menu recentMenu;

    @Autowired
    private TablePopupController tablePopupController;

    @Autowired
    private Current current;

    private Stage stage;
    private WebEngine previewEngine;
    private StringProperty lastRendered = new SimpleStringProperty("<b>...</b>");
    private List<WebSocketSession> sessionList = new ArrayList<>();
    private Scene scene;
    private AnchorPane tableAnchor;
    private Stage tableStage;

    private Clipboard clipboard = Clipboard.getSystemClipboard();
    private Optional<Path> initialDirectory = Optional.empty();
    private Set<Path> recentFiles = new HashSet<>();

    private String waitForGetValue;
    private String waitForSetValue;
    private String loadConfig;

    private StringProperty fontSize = new SimpleStringProperty("14");
    private StringProperty scrollSpeed = new SimpleStringProperty("0.1");
    private StringProperty theme = new SimpleStringProperty("katzenmilch");
    private AnchorPane configAnchor;
    private Stage configStage;

    @Autowired
    private ConfigController configController;


    @FXML
    private void createTable(ActionEvent event) throws IOException {
        tableStage.show();
    }

    @FXML
    private void openConfig(ActionEvent event) {
        configStage.show();
    }

    @FXML
    private void togglePreview(ActionEvent event) {
        double position = splitter.getDividerPositions()[0];
        splitter.setDividerPositions((position == 0.5) ? 1 : 0.5);
    }

    @FXML
    private void fullScreen(ActionEvent event) {
        getStage().setFullScreen(!getStage().isFullScreen());
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        try {
            PropertiesConfiguration configuration = new PropertiesConfiguration(Paths.get(System.getProperty("user.home")).resolve("asciidocfx.properties").toFile());
            fontSize.setValue(configuration.getString("editor.fontsize", "14"));
            scrollSpeed.setValue(configuration.getString("editor.scroll.speed", "0.1"));
            theme.setValue(configuration.getString("editor.theme", "ace"));

            configController.getFontSizeSlider().setValue(Double.valueOf(fontSize.getValue()));
            configController.getMouseSpeedSlider().setValue(Double.valueOf(scrollSpeed.getValue()));
            configController.getThemeSelector().getSelectionModel().select(configuration.getString("editor.theme", "ace"));

        } catch (ConfigurationException e) {
        }

        waitForGetValue = IO.convert(AsciiDocController.class.getResourceAsStream("/waitForGetValue.js"));
        waitForSetValue = IO.convert(AsciiDocController.class.getResourceAsStream("/waitForSetValue.js"));
        loadConfig = IO.convert(AsciiDocController.class.getResourceAsStream("/loadConfig.js"));

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
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        sessionList.add(session);
        session.sendMessage(new TextMessage(lastRendered.getValue()));

    }

    @FXML
    private void closeApp(ActionEvent event) {
        Platform.exit();
        System.exit(0);
    }

    @FXML
    private void openDoc(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Asciidoc", "*.asciidoc", "*.adoc"));
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
        tabu.getTabs().add(tab);

        current.putTab(tab, null, webView);
    }

    public String getLoadConfig() {
        return loadConfig;
    }

    private void addTab(Path path) {

        AnchorPane anchorPane = new AnchorPane();
        WebView webView = createWebView();
        WebEngine webEngine = webView.getEngine();
        webEngine.getLoadWorker().stateProperty().addListener((observableValue1, state, state2) -> {
            if (state2 == Worker.State.SUCCEEDED) {
                webEngine.executeScript(String.format(waitForSetValue, IO.readFile(path)));
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
        tabu.getTabs().add(tab);

        Tab lastTab = tabu.getTabs().get(tabu.getTabs().size() - 1);
        tabu.getSelectionModel().select(lastTab);

    }

    private Tab createTab() {
        Tab tab = new Tab();
        MenuItem menuItem0 = new MenuItem("Close All Tabs");
        menuItem0.setOnAction(actionEvent -> {
            tabu.getTabs().clear();
        });
        MenuItem menuItem1 = new MenuItem("Close All Other Tabs");
        menuItem1.setOnAction(actionEvent -> {
            List<Tab> blackList = new ArrayList<>();
            blackList.addAll(tabu.getTabs());
            blackList.remove(tab);
            tabu.getTabs().removeAll(blackList);
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
        webEngine.getLoadWorker().stateProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == Worker.State.SUCCEEDED) {
                webEngine.executeScript(String.format(loadConfig, fontSize.get(), theme.get(), scrollSpeed.get()));
            }
        });

        return webView;
    }

    public void onscroll(Object param) {
        if (Objects.isNull(param)) return;
        Number position = (Number) param;

        if (Objects.nonNull(param))
            previewEngine.executeScript(String.format("window.scrollTo(0, %f )", position.doubleValue()));

    }


    @RequestMapping(value = "/notfound/**", method = RequestMethod.GET)
    @ResponseBody
    public byte[] hello(HttpServletRequest request) throws IOException {

        String uri = request.getRequestURI();
        uri = uri.replace("/notfound", "");
        byte[] temp = new byte[]{};


        if (current.currentPath() != null) {

            if (uri.startsWith("/"))
                uri = uri.substring(1);
            Path resolved = current.currentRootPath().resolve(uri);

            FileInputStream fileInputStream = new FileInputStream(resolved.toFile());
            temp = IOUtils.toByteArray(fileInputStream);
            IOUtils.closeQuietly(fileInputStream);
        }

        return temp;

    }

    public void textListener(ObservableValue observableValue, String old, String nev) {
        try {
            Platform.runLater(() -> {
//                previewEngine.executeScript("var asciidocOpts = Opal.hash2(['attributes'], {'attributes': ['backend=docbook5', 'doctype=book']});");
//                String rendered = (String) previewEngine.executeScript("Opal.Asciidoctor.$render('" + IO.normalize(nev) + "',asciidocOpts);");
                String nonnormalize = nev;
                String normalize = nev;
                String rendered = (String) previewEngine.executeScript(String.format("Opal.Asciidoctor.$render('%s');", IO.normalize(normalize)));
                lastRendered.setValue(rendered);
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    public StringProperty getFontSize() {
        return fontSize;
    }

    public StringProperty fontSizeProperty() {
        return fontSize;
    }

    public StringProperty getScrollSpeed() {
        return scrollSpeed;
    }

    public StringProperty scrollSpeedProperty() {
        return scrollSpeed;
    }

    public StringProperty getTheme() {
        return theme;
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
            chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Asciidoc", "*.asciidoc", "*.adoc"));
            File file = chooser.showSaveDialog(null);
            if (file == null)
                return;
            IO.writeToFile(file, (String) current.currentEngine().executeScript("editor.getValue();"), TRUNCATE_EXISTING, CREATE);
            current.putTab(current.getCurrentTab(), file.toPath(), current.currentView());
            current.getCurrentTab().setText(file.toPath().getFileName().toString());

            recentFiles.add(file.toPath());
        } else {
            IO.writeToFile(currentPath.toFile(), (String) current.currentEngine().executeScript("editor.getValue();"), TRUNCATE_EXISTING, CREATE);
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


}

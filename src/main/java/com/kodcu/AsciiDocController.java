package com.kodcu;


import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import netscape.javascript.JSObject;
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
import java.util.*;

import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;


@Controller
public class AsciiDocController extends TextWebSocketHandler implements Initializable {

    public TabPane tabu;
    public WebView browser;
    public MenuItem openItem;
    public MenuItem newItem;
    public MenuItem saveItem;
    public SplitPane splitter;

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

    Clipboard clipboard = Clipboard.getSystemClipboard();
    private Optional<Path> initialDirectory = Optional.empty();


    @FXML
    private void createTable(ActionEvent event) throws IOException {
        tableStage.show();
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

        lastRendered.addListener((observableValue, old, nev) -> {
            sessionList.stream().filter(e -> e.isOpen()).forEach(e -> {
                try {
                    e.sendMessage(new TextMessage(nev));
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            });
        });

        previewEngine = browser.getEngine();
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
        }

    }

    @FXML
    private void newDoc(ActionEvent event) {
        WebView textArea = createEditor();
        AnchorPane anchorPane = new AnchorPane();
        anchorPane.getChildren().add(textArea);
        fitToParent(textArea);
        Tab tab = new Tab();
        tab.setContent(anchorPane);
        tab.selectedProperty().addListener((observableValue, before, after) -> {
            if (after) {
                current.putTab(tab, current.getNewTabPaths().get(tab), textArea);
                if (textArea.getEngine().getLoadWorker().getState() == Worker.State.SUCCEEDED)
                    textListener(null, null, (String) textArea.getEngine().executeScript("editor.getValue();"));
            }
        });
        tab.textProperty().setValue("new");
        tabu.getTabs().add(tab);

        current.putTab(tab, null, textArea);
    }


    private void addTab(Path path) {

        AnchorPane anchorPane = new AnchorPane();
        WebView textArea = createEditor();
        textArea.getEngine().getLoadWorker().stateProperty().addListener((observableValue1, state, state2) -> {
            if (state2 == Worker.State.SUCCEEDED) {
                textArea.getEngine().executeScript(String.format("editor.setValue('%s');", IO.readFile(path)));
            }
        });

        anchorPane.getChildren().add(textArea);

        fitToParent(textArea);

        Tab tab = new Tab();
        tab.textProperty().setValue(path.getFileName().toString());
        tab.setContent(anchorPane);


        tab.selectedProperty().addListener((observableValue, before, after) -> {
            if (after) {
                current.putTab(tab, path, textArea);

                if (textArea.getEngine().getLoadWorker().getState() == Worker.State.SUCCEEDED)
                    textListener(null, null, (String) textArea.getEngine().executeScript("editor.getValue();"));

            }
        });

        current.putTab(tab, path, textArea);
        tabu.getTabs().add(tab);

        Tab lastTab = tabu.getTabs().get(tabu.getTabs().size() - 1);
        tabu.getSelectionModel().select(lastTab);

    }


    private WebView createEditor() {

        WebView view = new WebView();

        WebEngine webEngine = view.getEngine();
        JSObject window = (JSObject) webEngine.executeScript("window");
        window.setMember("app", this);
        webEngine.load("http://localhost:8080/editor.html");

        webEngine.getLoadWorker().stateProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == Worker.State.SUCCEEDED) {
                webEngine.executeScript("editor.session.on('changeScrollTop', function () { app.onscroll(editor.getSession().getScrollTop()); });");
                webEngine.executeScript("editor.getSession().on('change',function(){ app.textListener(null, null, editor.getValue()); });");

                webEngine.executeScript(IO.convert(AsciiDocController.class.getResourceAsStream("/keyboard_fix.js")));
            }
        });

        return view;
    }

    public void onscroll(Object param) {
        Number position = (Number) param;
        if (Objects.nonNull(position))
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
                String rendered = (String) previewEngine.executeScript("Opal.Asciidoctor.$render('" + IO.normalize(nev) + "');");
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
            chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Asciidoc", "*.asciidoc"));
            File file = chooser.showSaveDialog(null);
            if (file == null)
                return;
            IO.writeToFile(file, (String) current.currentTextArea().getEngine().executeScript("editor.getValue();"), TRUNCATE_EXISTING, CREATE);
            current.putTab(current.getCurrentTab(), file.toPath(), current.currentTextArea());
            current.getCurrentTab().setText(file.toPath().getFileName().toString());
        } else {
            IO.writeToFile(currentPath.toFile(), (String) current.currentTextArea().getEngine().executeScript("editor.getValue();"), TRUNCATE_EXISTING, CREATE);
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
}

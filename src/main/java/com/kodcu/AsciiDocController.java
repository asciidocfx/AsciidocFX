package com.kodcu;


import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
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
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

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
//    public TableView tablePopupTable;

    @Autowired
    private TablePopupController tablePopupController;

    @Autowired
    private Current current;

    private Stage stage;
    private WebEngine engine;
    private StringProperty lastRendered = new SimpleStringProperty("<b>...</b>");
    private List<WebSocketSession> sessionList = new ArrayList<>();
    private Scene scene;
    private AnchorPane tableAnchor;
    private Stage tableStage;


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

        engine = browser.getEngine();
        engine.load("http://localhost:8080/index.html");
        engine.getLoadWorker().exceptionProperty().addListener((ov, t, t1) -> {
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
        List<File> chosenFiles = fileChooser.showOpenMultipleDialog(stage);
        if (chosenFiles != null)
            chosenFiles.stream().map(e -> e.toPath()).forEach(this::addTab);
    }

    @FXML
    private void newDoc(ActionEvent event) {
        TextArea textArea = createTextArea();
        AnchorPane anchorPane = new AnchorPane();
        anchorPane.getChildren().add(textArea);
        fitToParent(textArea);
        Tab tab = new Tab();
        tab.setContent(anchorPane);
        tab.selectedProperty().addListener((observableValue, before, after) -> {
            if (after) {
                current.putTab(tab, current.getNewTabPaths().get(tab), textArea);
                textListener(null, null, textArea.getText());
            }
        });
        tab.textProperty().setValue("new");
        tabu.getTabs().add(tab);

        current.putTab(tab, null, textArea);
    }


    private void addTab(Path path) {

        AnchorPane anchorPane = new AnchorPane();
        TextArea textArea = createTextArea();
        textArea.textProperty().setValue(IO.readFile(path));

        anchorPane.getChildren().add(textArea);
        fitToParent(textArea);

        Tab tab = new Tab();
        tab.textProperty().setValue(path.getFileName().toString());
        tab.setContent(anchorPane);

        tab.selectedProperty().addListener((observableValue, before, after) -> {
            if (after) {
                current.putTab(tab, path, textArea);
                textListener(null, null, textArea.getText());
            }
        });

        current.putTab(tab, path, textArea);
        tabu.getTabs().add(tab);

        Tab lastTab = tabu.getTabs().get(tabu.getTabs().size() - 1);
        tabu.getSelectionModel().select(lastTab);

    }


    private TextArea createTextArea() {
        TextArea textArea = new TextArea();
        textArea.textProperty().addListener(this::textListener);
        textArea.wrapTextProperty().setValue(true);

        textArea.setOnMouseClicked(event -> {
            if (event.getClickCount() > 1) {
                String selectedText = textArea.getSelectedText();
                String[] splitted = selectedText.trim().split("[^a-zA-Z0-9]");
                textArea.selectRange(textArea.getAnchor(), textArea.getAnchor() + splitted[0].trim().length());
            }
        });


        textArea.scrollTopProperty().addListener((observableValue, old, nev) -> {

            ScrollBar textAreaScroll = (ScrollBar) tabu.getSelectionModel().selectedItemProperty().getValue().getContent().lookup(".scroll-bar");

            double textAreaScrollRatio = (textAreaScroll.getValue() * 100) / textAreaScroll.getMax();

            Integer browserMaxScroll = (Integer) engine.executeScript("document.documentElement.scrollHeight - document.documentElement.clientHeight;");
            double browserScrollOffset = (Double.valueOf(browserMaxScroll) * textAreaScrollRatio) / 100.0;

            engine.executeScript("window.scrollTo(0," + browserScrollOffset + ");");
        });

        return textArea;
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

    private void textListener(ObservableValue observableValue, String old, String nev) {

        try {
            String rendered = (String) engine.executeScript("Opal.Asciidoctor.$render('" + IO.normalize(nev) + "');");
            lastRendered.setValue(rendered);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

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
            IO.writeToFile(file, current.currentTextArea().textProperty().getValue(), TRUNCATE_EXISTING, CREATE);
            current.putTab(current.getCurrentTab(), file.toPath(), current.currentTextArea());
            current.getCurrentTab().setText(file.toPath().getFileName().toString());
        } else {
            IO.writeToFile(currentPath.toFile(), current.currentTextArea().textProperty().getValue(), TRUNCATE_EXISTING, CREATE);
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

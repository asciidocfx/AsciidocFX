package com.kodcu.service.ui;

import com.kodcu.controller.ApplicationController;
import com.kodcu.other.Current;
import de.jensd.fx.fontawesome.AwesomeDude;
import de.jensd.fx.fontawesome.AwesomeIcon;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by usta on 25.12.2014.
 */
@Component
public class EditorService {

    @Autowired
    private Current current;

    @Autowired
    private ApplicationController controller;

    public Node createEditorVBox(WebView webView) {
        MenuBar menuBar = new MenuBar();
        menuBar.getStyleClass().add("editorToolsBar");
        String iconSize = "14.0";

        Label saveLabel = AwesomeDude.createIconLabel(AwesomeIcon.SAVE, iconSize);
        Label newLabel = AwesomeDude.createIconLabel(AwesomeIcon.FILE_TEXT_ALT, iconSize);
        Label openLabel = AwesomeDude.createIconLabel(AwesomeIcon.FOLDER_ALTPEN_ALT, iconSize);
        Label boldLabel = AwesomeDude.createIconLabel(AwesomeIcon.BOLD, iconSize);
        Label italicLabel = AwesomeDude.createIconLabel(AwesomeIcon.ITALIC, iconSize);
        Label headerLabel = AwesomeDude.createIconLabel(AwesomeIcon.HEADER, iconSize);
        Label codeLabel = AwesomeDude.createIconLabel(AwesomeIcon.CODE, iconSize);
        Label ulListLabel = AwesomeDude.createIconLabel(AwesomeIcon.LIST_UL, iconSize);
        Label olListLabel = AwesomeDude.createIconLabel(AwesomeIcon.LIST_ALTL, iconSize);
        Label tableLabel = AwesomeDude.createIconLabel(AwesomeIcon.TABLE, iconSize);
        Label imageLabel = AwesomeDude.createIconLabel(AwesomeIcon.IMAGE, iconSize);
        Label subscriptLabel = AwesomeDude.createIconLabel(AwesomeIcon.SUBSCRIPT, iconSize);
        Label superScriptLabel = AwesomeDude.createIconLabel(AwesomeIcon.SUPERSCRIPT, iconSize);
        Label underlineLabel = AwesomeDude.createIconLabel(AwesomeIcon.UNDERLINE, iconSize);
        Label hyperlinkLabel = AwesomeDude.createIconLabel(AwesomeIcon.LINK, iconSize);
        Label strikethroughLabel = AwesomeDude.createIconLabel(AwesomeIcon.STRIKETHROUGH, iconSize);


        // Events
        newLabel.setOnMouseClicked(controller::newDoc);
        openLabel.setOnMouseClicked(controller::openDoc);
        saveLabel.setOnMouseClicked(controller::saveDoc);
        boldLabel.setOnMouseClicked(event -> {
            current.currentEngine().executeScript("boldText()");
        });
        italicLabel.setOnMouseClicked(event -> {
            current.currentEngine().executeScript("italicizeText()");
        });

        codeLabel.setOnMouseClicked(event -> {
            current.currentEngine().executeScript("addSourceCode()");
        });

        tableLabel.setOnMouseClicked(controller::createTable);

        subscriptLabel.setOnMouseClicked(event -> {
            current.currentEngine().executeScript("subScript()");
        });

        superScriptLabel.setOnMouseClicked(event -> {
            current.currentEngine().executeScript("superScript()");
        });

        imageLabel.setOnMouseClicked(event -> {
            current.currentEngine().executeScript("addImageSection()");
        });

        headerLabel.setOnMouseClicked(event -> {
            current.currentEngine().executeScript("addHeading()");
        });

        ulListLabel.setOnMouseClicked(event -> {
            current.currentEngine().executeScript("addUlList()");
        });

        olListLabel.setOnMouseClicked(event -> {
            current.currentEngine().executeScript("addOlList()");
        });

        underlineLabel.setOnMouseClicked(event -> {
            current.currentEngine().executeScript("underlinedText()");
        });

        hyperlinkLabel.setOnMouseClicked(event -> {
            current.currentEngine().executeScript("addHyperLink()");
        });

        strikethroughLabel.setOnMouseClicked(event -> {
            current.currentEngine().executeScript("addStrikeThroughText()");
        });

        menuBar.getMenus().addAll(
                new Menu("", newLabel),
                new Menu("", openLabel),
                new Menu("", saveLabel),
                new Menu("", boldLabel),
                new Menu("", italicLabel),
                new Menu("", underlineLabel),
                new Menu("", strikethroughLabel),
                new Menu("", headerLabel),
                new Menu("", hyperlinkLabel),
                new Menu("", codeLabel),
                new Menu("", ulListLabel),
                new Menu("", olListLabel),
                new Menu("", tableLabel),
                new Menu("", imageLabel),
                new Menu("", subscriptLabel),
                new Menu("", superScriptLabel)
        );

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setContent(webView);
        scrollPane.setFitToHeight(true);
        scrollPane.setFitToWidth(true);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        return new VBox(menuBar, scrollPane);
    }
}

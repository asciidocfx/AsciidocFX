package com.kodcu.service.ui;

import com.kodcu.component.LabelBuilt;
import com.kodcu.component.MyTab;
import com.kodcu.controller.ApplicationController;
import com.kodcu.other.Current;
import de.jensd.fx.fontawesome.AwesomeDude;
import de.jensd.fx.fontawesome.AwesomeIcon;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
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

    public Node createEditorVBox(WebView webView,MyTab myTab) {
        VBox vbox = new VBox();
        String iconSize = "14.0";
        double minSize = 14.01;

        Label saveLabel = LabelBuilt.icon(AwesomeIcon.SAVE, iconSize, minSize)
                .clazz("top-label")
                .tip("Save").click(controller::saveDoc).build();

        Label newLabel = LabelBuilt.icon(AwesomeIcon.FILE_TEXT_ALT, iconSize, minSize)
                .clazz("top-label").tip("New File").click(controller::newDoc).build();

        Label openLabel = LabelBuilt.icon(AwesomeIcon.FOLDER_ALTPEN_ALT, iconSize, minSize)
                .clazz("top-label").tip("Open File").click(controller::openDoc).build();

        Label boldLabel = LabelBuilt.icon(AwesomeIcon.BOLD, iconSize, minSize)
                .clazz("top-label").tip("Bold").click(event -> {
            current.currentEngine().executeScript("boldText()");
        }).build();

        Label italicLabel = LabelBuilt.icon(AwesomeIcon.ITALIC, iconSize, minSize)
                .clazz("top-label").tip("Italic").click(event -> {
            current.currentEngine().executeScript("italicizeText()");
        }).build();

        Label headerLabel = LabelBuilt.icon(AwesomeIcon.HEADER, iconSize, minSize)
                .clazz("top-label").tip("Headings").click(event -> {
            current.currentEngine().executeScript("addHeading()");
        }).build();

        Label codeLabel = LabelBuilt.icon(AwesomeIcon.CODE, iconSize, minSize)
                .clazz("top-label").tip("Code Snippet").click(event -> {
            current.currentEngine().executeScript("addSourceCode()");
        }).build();

        Label ulListLabel = LabelBuilt.icon(AwesomeIcon.LIST_UL, iconSize, minSize)
                .clazz("top-label").tip("Bulleted List").click(event -> {
            current.currentEngine().executeScript("addUlList()");
        }).build();

        Label olListLabel = LabelBuilt.icon(AwesomeIcon.LIST_ALTL, iconSize, minSize)
                .clazz("top-label").tip("Numbered List").click(event -> {
            current.currentEngine().executeScript("addOlList()");
        }).build();

        Label tableLabel = LabelBuilt.icon(AwesomeIcon.TABLE, iconSize, minSize)
                .clazz("top-label").tip("Table").click(controller::createTable).build();

        Label imageLabel = LabelBuilt.icon(AwesomeIcon.IMAGE, iconSize, minSize)
                .clazz("top-label").tip("Image").click(event -> {
            current.currentEngine().executeScript("addImageSection()");
        }).build();

        Label subscriptLabel = LabelBuilt.icon(AwesomeIcon.SUBSCRIPT, iconSize, minSize)
                .clazz("top-label").tip("Subscript").click(event -> {
            current.currentEngine().executeScript("subScript()");
        }).build();

        Label superScriptLabel = LabelBuilt.icon(AwesomeIcon.SUPERSCRIPT, iconSize, minSize)
                .clazz("top-label").tip("Superscript").click(event -> {
            current.currentEngine().executeScript("superScript()");
        }).build();

        Label underlineLabel = LabelBuilt.icon(AwesomeIcon.UNDERLINE, iconSize, minSize)
                .clazz("top-label").tip("Underline").click(event -> {
            current.currentEngine().executeScript("underlinedText()");
        }).build();

        Label hyperlinkLabel = LabelBuilt.icon(AwesomeIcon.LINK, iconSize, minSize)
                .clazz("top-label").tip("Hyperlink").click(event -> {
            current.currentEngine().executeScript("addHyperLink()");
        }).build();

        Label strikethroughLabel = LabelBuilt.icon(AwesomeIcon.STRIKETHROUGH, iconSize, minSize)
                .clazz("top-label").tip("Strikethrough").click(event -> {
            current.currentEngine().executeScript("addStrikeThroughText()");
        }).build();

        Label openMenuLabel = LabelBuilt.icon(AwesomeIcon.CHEVRON_CIRCLE_DOWN, iconSize, minSize)
                .clazz("top-label").tip("More...").build();

        openMenuLabel.setOnMouseClicked(event -> {
            int childSize = vbox.getChildren().size();
            if (childSize == 2) {
                openMenuLabel.setText(AwesomeIcon.CHEVRON_CIRCLE_DOWN.toString());
                Tooltip.install(openMenuLabel, new Tooltip("More..."));
                vbox.getChildren().remove(1);
            } else {
                openMenuLabel.setText(AwesomeIcon.CHEVRON_CIRCLE_UP.toString());
                openMenuLabel.getProperties().clear();
                vbox.getChildren().add(createSecondEditorVBox(iconSize));
            }
        });

        ChoiceBox choiceBox = new ChoiceBox();
        choiceBox.getItems().addAll("Asciidoc","Markdown");
        choiceBox.getSelectionModel().selectFirst();

        myTab.setMarkup(choiceBox);

        HBox topMenu = new HBox(newLabel,
                openLabel,
                saveLabel,
                boldLabel,
                italicLabel,
                underlineLabel,
                strikethroughLabel,
                headerLabel,
                hyperlinkLabel,
                codeLabel,
                ulListLabel,
                olListLabel,
                tableLabel,
                imageLabel,
                subscriptLabel,
                superScriptLabel,
                choiceBox,
                openMenuLabel);

        topMenu.setAlignment(Pos.CENTER_LEFT);

        topMenu.setOnMouseClicked(event -> {
            webView.requestFocus();
        });

        topMenu.setSpacing(10);
        topMenu.getStyleClass().add("top-menu");

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setContent(webView);
        scrollPane.setFitToHeight(true);
        scrollPane.setFitToWidth(true);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);
        vbox.getChildren().add(topMenu);
        return new VBox(vbox, scrollPane);
    }

    private Node createSecondEditorVBox(String iconSize) {
        Label quoteLabel = AwesomeDude.createIconLabel(AwesomeIcon.QUOTE_LEFT, iconSize);

        quoteLabel.setOnMouseClicked(e -> {
            current.currentEngine().executeScript("addQuote()");
        });

        Tooltip.install(quoteLabel, new Tooltip("Blockquote"));

        HBox topMenu = new HBox(quoteLabel);

        topMenu.setSpacing(10);
        topMenu.getStyleClass().add("top-menu");
        topMenu.setStyle("-fx-padding:0 10px 5px 10px;");

        return topMenu;
    }
}

package com.kodcu.service.ui;

import com.kodcu.component.LabelBuilt;
import com.kodcu.controller.ApplicationController;
import com.kodcu.other.Current;
import de.jensd.fx.fontawesome.AwesomeDude;
import de.jensd.fx.fontawesome.AwesomeIcon;
import javafx.scene.Node;
import javafx.scene.control.*;
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
        VBox vbox = new VBox();
        MenuBar menuBar = new MenuBar();
        menuBar.getStyleClass().add("editorToolsBar");
        String iconSize = "14.0";
        double minSize = 14.01;

        Label saveLabel = LabelBuilt.icon(AwesomeIcon.SAVE, iconSize, minSize).tip("Save").click(controller::saveDoc).build();

        Label newLabel = LabelBuilt.icon(AwesomeIcon.FILE_TEXT_ALT, iconSize, minSize).tip("New File").click(controller::newDoc).build();

        Label openLabel = LabelBuilt.icon(AwesomeIcon.FOLDER_ALTPEN_ALT, iconSize, minSize).tip("Open File").click(controller::openDoc).build();

        Label boldLabel = LabelBuilt.icon(AwesomeIcon.BOLD, iconSize, minSize).tip("Bold").click(event -> {
            current.currentEngine().executeScript("boldText()");
        }).build();

        Label italicLabel = LabelBuilt.icon(AwesomeIcon.ITALIC, iconSize, minSize).tip("Italic").click(event -> {
            current.currentEngine().executeScript("italicizeText()");
        }).build();

        Label headerLabel = LabelBuilt.icon(AwesomeIcon.ITALIC, iconSize, minSize).tip("Headings").click(event -> {
            current.currentEngine().executeScript("addHeading()");
        }).build();

        Label codeLabel = LabelBuilt.icon(AwesomeIcon.CODE, iconSize, minSize).tip("Code Snippet").click(event -> {
            current.currentEngine().executeScript("addSourceCode()");
        }).build();

        Label ulListLabel = LabelBuilt.icon(AwesomeIcon.LIST_UL, iconSize, minSize).tip("Bulleted List").click(event -> {
            current.currentEngine().executeScript("addUlList()");
        }).build();

        Label olListLabel = LabelBuilt.icon(AwesomeIcon.LIST_ALTL, iconSize, minSize).tip("Numbered List").click(event -> {
            current.currentEngine().executeScript("addOlList()");
        }).build();

        Label tableLabel = LabelBuilt.icon(AwesomeIcon.TABLE, iconSize, minSize).tip("Table").click(controller::createTable).build();

        Label imageLabel = LabelBuilt.icon(AwesomeIcon.IMAGE, iconSize, minSize).tip("Image").click(event -> {
            current.currentEngine().executeScript("addImageSection()");
        }).build();

        Label subscriptLabel = LabelBuilt.icon(AwesomeIcon.SUBSCRIPT, iconSize, minSize).tip("Subscript").click(event -> {
            current.currentEngine().executeScript("subScript()");
        }).build();

        Label superScriptLabel = LabelBuilt.icon(AwesomeIcon.SUPERSCRIPT, iconSize, minSize).tip("Superscript").click(event -> {
            current.currentEngine().executeScript("superScript()");
        }).build();

        Label underlineLabel = LabelBuilt.icon(AwesomeIcon.UNDERLINE, iconSize, minSize).tip("Underline").click(event -> {
            current.currentEngine().executeScript("underlinedText()");
        }).build();

        Label hyperlinkLabel = LabelBuilt.icon(AwesomeIcon.LINK, iconSize, minSize).tip("Hyperlink").click(event -> {
            current.currentEngine().executeScript("addHyperLink()");
        }).build();

        Label strikethroughLabel = LabelBuilt.icon(AwesomeIcon.STRIKETHROUGH, iconSize, minSize).tip("Strikethrough").click(event -> {
            current.currentEngine().executeScript("addStrikeThroughText()");
        }).build();

        Label openMenuLabel = LabelBuilt.icon(AwesomeIcon.CHEVRON_CIRCLE_DOWN, iconSize, minSize).tip("More...").build();

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
                new Menu("", superScriptLabel),
                new Menu("", openMenuLabel)
        );

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setContent(webView);
        scrollPane.setFitToHeight(true);
        scrollPane.setFitToWidth(true);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        vbox.getChildren().add(menuBar);
        return new VBox(vbox, scrollPane);
    }

    private Node createSecondEditorVBox(String iconSize) {
        MenuBar menuBar = new MenuBar();
        menuBar.getStyleClass().add("editorToolsBar");

        Label quoteLabel = AwesomeDude.createIconLabel(AwesomeIcon.QUOTE_LEFT, iconSize);

        quoteLabel.setOnMouseClicked(e -> {
            current.currentEngine().executeScript("addQuote()");
        });

        Tooltip.install(quoteLabel, new Tooltip("Blockquote"));

        menuBar.getMenus().addAll(
                new Menu("", quoteLabel));

        return menuBar;
    }
}

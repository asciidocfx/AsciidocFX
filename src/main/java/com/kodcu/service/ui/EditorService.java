package com.kodcu.service.ui;

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
        Label openMenuLabel = AwesomeDude.createIconLabel(AwesomeIcon.CHEVRON_CIRCLE_DOWN, iconSize);
//        Label highlightLabel = new Label(" A ");

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

        Tooltip.install(newLabel, new Tooltip("New File"));
        Tooltip.install(openLabel, new Tooltip("Open File"));
        Tooltip.install(saveLabel, new Tooltip("Save"));
        Tooltip.install(boldLabel, new Tooltip("Bold"));
        Tooltip.install(italicLabel, new Tooltip("Italic"));
        Tooltip.install(underlineLabel, new Tooltip("Underline"));
        Tooltip.install(strikethroughLabel, new Tooltip("Strikethrough"));
        Tooltip.install(headerLabel, new Tooltip("Headings"));
        Tooltip.install(hyperlinkLabel, new Tooltip("Hyperlink"));
        Tooltip.install(codeLabel, new Tooltip("Code Snippet"));
        Tooltip.install(ulListLabel, new Tooltip("Bulleted List"));
        Tooltip.install(olListLabel, new Tooltip("Numbered List"));
        Tooltip.install(tableLabel, new Tooltip("Table"));
        Tooltip.install(imageLabel, new Tooltip("Image"));
        Tooltip.install(subscriptLabel, new Tooltip("Subscript"));
        Tooltip.install(superScriptLabel, new Tooltip("Superscript"));
        Tooltip.install(openMenuLabel, new Tooltip("More..."));

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

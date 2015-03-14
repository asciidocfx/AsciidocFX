package com.kodcu.service.ui;

import com.kodcu.component.LabelBuilt;
import com.kodcu.component.MyTab;
import com.kodcu.controller.ApplicationController;
import com.kodcu.other.Current;
import de.jensd.fx.fontawesome.AwesomeIcon;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
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

    private final Current current;
    private final ApplicationController controller;

    @Autowired
    public EditorService(final Current current, final ApplicationController controller) {
        this.current = current;
        this.controller = controller;
    }

    public Node createEditorVBox(WebView webView, MyTab myTab) {
        final VBox vbox = new VBox();
        String iconSize = "14.0";
        double minSize = 14.01;

        final Label saveLabel = LabelBuilt.icon(AwesomeIcon.SAVE, iconSize, minSize)
                .clazz("top-label")
                .tip("Save").click(controller::saveDoc).build();

        final Label newLabel = LabelBuilt.icon(AwesomeIcon.FILE_TEXT_ALT, iconSize, minSize)
                .clazz("top-label").tip("New File").click(controller::newDoc).build();

        final Label openLabel = LabelBuilt.icon(AwesomeIcon.FOLDER_ALTPEN_ALT, iconSize, minSize)
                .clazz("top-label").tip("Open File").click(controller::openDoc).build();

        final Label boldLabel = LabelBuilt.icon(AwesomeIcon.BOLD, iconSize, minSize)
                .clazz("top-label").tip("Bold").click(event -> {
                    current.currentEngine().executeScript("boldText()");
                }).build();

        final Label italicLabel = LabelBuilt.icon(AwesomeIcon.ITALIC, iconSize, minSize)
                .clazz("top-label").tip("Italic").click(event -> {
                    current.currentEngine().executeScript("italicizeText()");
                }).build();

        final Label headerLabel = LabelBuilt.icon(AwesomeIcon.HEADER, iconSize, minSize)
                .clazz("top-label").tip("Headings").click(event -> {
                    current.currentEngine().executeScript("addHeading()");
                }).build();

        final Label codeLabel = LabelBuilt.icon(AwesomeIcon.CODE, iconSize, minSize)
                .clazz("top-label").tip("Code Snippet").click(event -> {
                    current.currentEngine().executeScript("addSourceCode()");
                }).build();

        final Label ulListLabel = LabelBuilt.icon(AwesomeIcon.LIST_UL, iconSize, minSize)
                .clazz("top-label").tip("Bulleted List").click(event -> {
                    current.currentEngine().executeScript("addUlList()");
                }).build();

        final Label olListLabel = LabelBuilt.icon(AwesomeIcon.LIST_ALTL, iconSize, minSize)
                .clazz("top-label").tip("Numbered List").click(event -> {
                    current.currentEngine().executeScript("addOlList()");
                }).build();

        final Label tableLabel = LabelBuilt.icon(AwesomeIcon.TABLE, iconSize, minSize)
                .clazz("top-label").tip("Table").click(controller::createTable).build();

        final Label imageLabel = LabelBuilt.icon(AwesomeIcon.IMAGE, iconSize, minSize)
                .clazz("top-label").tip("Image").click(event -> {
                    current.currentEngine().executeScript("addImageSection()");
                }).build();

        final Label subscriptLabel = LabelBuilt.icon(AwesomeIcon.SUBSCRIPT, iconSize, minSize)
                .clazz("top-label").tip("Subscript").click(event -> {
                    current.currentEngine().executeScript("subScript()");
                }).build();

        final Label superScriptLabel = LabelBuilt.icon(AwesomeIcon.SUPERSCRIPT, iconSize, minSize)
                .clazz("top-label").tip("Superscript").click(event -> {
                    current.currentEngine().executeScript("superScript()");
                }).build();

        final Label underlineLabel = LabelBuilt.icon(AwesomeIcon.UNDERLINE, iconSize, minSize)
                .clazz("top-label").tip("Underline").click(event -> {
                    current.currentEngine().executeScript("underlinedText()");
                }).build();

        final Label hyperlinkLabel = LabelBuilt.icon(AwesomeIcon.LINK, iconSize, minSize)
                .clazz("top-label").tip("Hyperlink").click(event -> {
                    current.currentEngine().executeScript("addHyperLink()");
                }).build();

        final Label strikethroughLabel = LabelBuilt.icon(AwesomeIcon.STRIKETHROUGH, iconSize, minSize)
                .clazz("top-label").tip("Strikethrough").click(event -> {
                    current.currentEngine().executeScript("addStrikeThroughText()");
                }).build();

        final Label openMenuLabel = LabelBuilt.icon(AwesomeIcon.CHEVRON_CIRCLE_DOWN, iconSize, minSize)
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
                vbox.getChildren().add(createSecondEditorVBox(iconSize, minSize));
            }
        });

        final ChoiceBox<String> choiceBox = new ChoiceBox<>();
        choiceBox.setManaged(true);
        choiceBox.setVisible(true);
        choiceBox.getItems().addAll("Asciidoc", "Markdown");
        choiceBox.getSelectionModel().selectFirst();

        myTab.setMarkup(choiceBox);

        final HBox topMenu = new HBox(newLabel,
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

        final ScrollPane scrollPane = new ScrollPane();
        scrollPane.setContent(webView);
        scrollPane.setFitToHeight(true);
        scrollPane.setFitToWidth(true);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);
        vbox.getChildren().add(topMenu);
        return new VBox(vbox, scrollPane);
    }

    private Node createSecondEditorVBox(final String iconSize, final double minSize) {

        final Label quoteLabel = LabelBuilt.icon(AwesomeIcon.QUOTE_LEFT, iconSize, minSize)
                .clazz("top-label").tip("Blockquote").click(event -> {
                    current.currentEngine().executeScript("addQuote()");
                }).build();

        final HBox topMenu = new HBox(quoteLabel);

        topMenu.setSpacing(10);
        topMenu.getStyleClass().add("top-menu");
        topMenu.setStyle("-fx-padding:0 10px 5px 10px;");

        return topMenu;
    }
}

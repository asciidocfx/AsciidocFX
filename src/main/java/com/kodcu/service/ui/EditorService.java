package com.kodcu.service.ui;

import com.kodcu.component.LabelBuilt;
import com.kodcu.component.MyTab;
import com.kodcu.controller.ApplicationController;
import com.kodcu.other.Current;
import com.kodcu.service.shortcut.ShortcutProvider;
import de.jensd.fx.fontawesome.AwesomeIcon;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * Created by usta on 25.12.2014.
 */
@Component
public class EditorService {

    @Autowired
    private Current current;

    @Autowired
    private ApplicationController controller;

    @Autowired
    private ShortcutProvider shortcutProvider;

    public Node createEditorVBox(WebView webView, MyTab myTab) {
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
                    shortcutProvider.getProvider().addBold();
                }).build();

        Label italicLabel = LabelBuilt.icon(AwesomeIcon.ITALIC, iconSize, minSize)
                .clazz("top-label").tip("Italic").click(event -> {
                    shortcutProvider.getProvider().addItalic();
                }).build();

        Label headerLabel = LabelBuilt.icon(AwesomeIcon.HEADER, iconSize, minSize)
                .clazz("top-label").tip("Headings").click(event -> {
                    shortcutProvider.getProvider().addHeading();
                }).build();

        Label codeLabel = LabelBuilt.icon(AwesomeIcon.CODE, iconSize, minSize)
                .clazz("top-label").tip("Code Snippet").click(event -> {
                    shortcutProvider.getProvider().addCode("");
                }).build();

        Label ulListLabel = LabelBuilt.icon(AwesomeIcon.LIST_UL, iconSize, minSize)
                .clazz("top-label").tip("Bulleted List").click(event -> {
                    shortcutProvider.getProvider().addUnorderedList();
                }).build();

        Label olListLabel = LabelBuilt.icon(AwesomeIcon.LIST_ALTL, iconSize, minSize)
                .clazz("top-label").tip("Numbered List").click(event -> {
                    shortcutProvider.getProvider().addOrderedList();
                }).build();

        Label tableLabel = LabelBuilt.icon(AwesomeIcon.TABLE, iconSize, minSize)
                .clazz("top-label").tip("Table").click(event->{
                    shortcutProvider.getProvider().addTable();;
                }).build();

        Label imageLabel = LabelBuilt.icon(AwesomeIcon.IMAGE, iconSize, minSize)
                .clazz("top-label").tip("Image").click(event -> {
                    shortcutProvider.getProvider().addImage();
                }).build();

        Label subscriptLabel = LabelBuilt.icon(AwesomeIcon.SUBSCRIPT, iconSize, minSize)
                .clazz("top-label").tip("Subscript").click(event -> {
                    shortcutProvider.getProvider().addSubscript();
                }).build();

        Label superScriptLabel = LabelBuilt.icon(AwesomeIcon.SUPERSCRIPT, iconSize, minSize)
                .clazz("top-label").tip("Superscript").click(event -> {
                    shortcutProvider.getProvider().addSuperscript();
                }).build();

        Label underlineLabel = LabelBuilt.icon(AwesomeIcon.UNDERLINE, iconSize, minSize)
                .clazz("top-label").tip("Underline").click(event -> {
                    shortcutProvider.getProvider().addUnderline();
                }).build();

        Label hyperlinkLabel = LabelBuilt.icon(AwesomeIcon.LINK, iconSize, minSize)
                .clazz("top-label").tip("Hyperlink").click(event -> {
                    shortcutProvider.getProvider().addHyperlink();
                }).build();

        Label strikethroughLabel = LabelBuilt.icon(AwesomeIcon.STRIKETHROUGH, iconSize, minSize)
                .clazz("top-label").tip("Strikethrough").click(event -> {
                    shortcutProvider.getProvider().addStrike();
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
                vbox.getChildren().add(createSecondEditorVBox(iconSize, minSize));
            }
        });

        ChoiceBox choiceBox = new ChoiceBox();
        choiceBox.setManaged(true);
        choiceBox.setVisible(true);
        choiceBox.getItems().addAll("Asciidoc", "Markdown");
        choiceBox.getSelectionModel().selectFirst();

        myTab.setMarkup(choiceBox);

        choiceBox.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue != newValue) {
                Arrays.asList(underlineLabel, strikethroughLabel, subscriptLabel, superScriptLabel)
                        .forEach(item -> {
                            item.setManaged(newValue.intValue() == 0);
                            item.setVisible(newValue.intValue() == 0);
                        });

            }
        });

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

    private Node createSecondEditorVBox(String iconSize, double minSize) {

        Label quoteLabel = LabelBuilt.icon(AwesomeIcon.QUOTE_LEFT, iconSize, minSize)
                .clazz("top-label").tip("Blockquote").click(event -> {
                    shortcutProvider.getProvider().addQuote();
                }).build();

        HBox topMenu = new HBox(quoteLabel);

        topMenu.setSpacing(10);
        topMenu.getStyleClass().add("top-menu");
        topMenu.setStyle("-fx-padding:0 10px 5px 10px;");

        return topMenu;
    }
}

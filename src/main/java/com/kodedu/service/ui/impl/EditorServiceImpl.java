package com.kodedu.service.ui.impl;

import com.kodedu.component.EditorPane;
import com.kodedu.component.LabelBuilt;
import com.kodedu.component.MenuItemBuilt;
import com.kodedu.component.MyTab;
import com.kodedu.controller.ApplicationController;
import com.kodedu.other.DocumentMode;
import com.kodedu.service.shortcut.ShortcutProvider;
import com.kodedu.service.ui.EditorService;

import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import org.kordamp.ikonli.fontawesome.FontAwesome;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by usta on 25.12.2014.
 */
@Component(EditorService.label)
public class EditorServiceImpl implements EditorService {

    @Autowired
    private ApplicationController controller;

    @Autowired
    private ShortcutProvider shortcutProvider;

    @Override
    public Node createEditorVBox(EditorPane editorPane, MyTab myTab) {
        FlowPane flowPane = new FlowPane();
        String iconSize = "14.0";
        double minSize = 14.01;


        final Label saveLabel = LabelBuilt.icon(FontAwesome.SAVE, minSize)
                .clazz("top-label")
                .tip("Save").click(controller::saveDoc).build();

        final Label newLabel = LabelBuilt.icon(FontAwesome.FILE_TEXT_O, minSize)
                .clazz("top-label").tip("New File").click(controller::newDoc).build();

        final Label openLabel = LabelBuilt.icon(FontAwesome.FOLDER_OPEN_O, minSize)
                .clazz("top-label").tip("Open File").click(controller::openDoc).build();

        final Label boldLabel = LabelBuilt.icon(FontAwesome.BOLD, minSize)
                .clazz("top-label").tip("Bold").click(event -> {
                    shortcutProvider.getProvider().addBold();
                }).build();

        final Label italicLabel = LabelBuilt.icon(FontAwesome.ITALIC, minSize)
                .clazz("top-label").tip("Italic").click(event -> {
                    shortcutProvider.getProvider().addItalic();
                }).build();

        final Label headerLabel = LabelBuilt.icon(FontAwesome.HEADER, minSize)
                .clazz("top-label").tip("Headings").click(event -> {
                    shortcutProvider.getProvider().addHeading();
                }).build();

        final Label codeLabel = LabelBuilt.icon(FontAwesome.CODE, minSize)
                .clazz("top-label").tip("Code Snippet").click(event -> {
                    shortcutProvider.getProvider().addCode("");
                }).build();

        final Label ulListLabel = LabelBuilt.icon(FontAwesome.LIST_UL, minSize)
                .clazz("top-label").tip("Bulleted List").click(event -> {
                    shortcutProvider.getProvider().addUnorderedList();
                }).build();

        final Label olListLabel = LabelBuilt.icon(FontAwesome.LIST_ALT, minSize)
                .clazz("top-label").tip("Numbered List").click(event -> {
                    shortcutProvider.getProvider().addOrderedList();
                }).build();

        final Label tableLabel = LabelBuilt.icon(FontAwesome.TABLE, minSize)
                .clazz("top-label").tip("Table").click(event -> {
                    shortcutProvider.getProvider().addTable();
                    ;
                }).build();

        final Label imageLabel = LabelBuilt.icon(FontAwesome.IMAGE, minSize)
                .clazz("top-label").tip("Image").click(event -> {
                    shortcutProvider.getProvider().addImage();
                }).build();

        final Label subscriptLabel = LabelBuilt.icon(FontAwesome.SUBSCRIPT, minSize)
                .clazz("top-label").tip("Subscript").click(event -> {
                    shortcutProvider.getProvider().addSubscript();
                }).build();

        final Label superScriptLabel = LabelBuilt.icon(FontAwesome.SUPERSCRIPT, minSize)
                .clazz("top-label").tip("Superscript").click(event -> {
                    shortcutProvider.getProvider().addSuperscript();
                }).build();

        final Label underlineLabel = LabelBuilt.icon(FontAwesome.UNDERLINE, minSize)
                .clazz("top-label").tip("Underline").click(event -> {
                    shortcutProvider.getProvider().addUnderline();
                }).build();

        final Label hyperlinkLabel = LabelBuilt.icon(FontAwesome.LINK, minSize)
                .clazz("top-label").tip("Hyperlink").click(event -> {
                    shortcutProvider.getProvider().addHyperlink();
                }).build();

        final Label strikethroughLabel = LabelBuilt.icon(FontAwesome.STRIKETHROUGH, minSize)
                .clazz("top-label").tip("Strikethrough").click(event -> {
                    shortcutProvider.getProvider().addStrike();
                }).build();

        final Label quoteLabel = LabelBuilt.icon(FontAwesome.QUOTE_LEFT, minSize)
                .clazz("top-label").tip("Blockquote").click(event -> {
                    shortcutProvider.getProvider().addQuote();
                }).build();

        final Label openMenuLabel = LabelBuilt.icon(FontAwesome.CHEVRON_CIRCLE_DOWN, minSize)
                .clazz("top-label").tip("More...").build();

        Pane placeholderPane = new Pane();
        placeholderPane.maxWidth(Integer.MAX_VALUE);
        placeholderPane.prefHeight(1);
        placeholderPane.prefWidth(1);
        HBox.setHgrow(placeholderPane, Priority.ALWAYS);

        List<Node> moreMenuButtons = new LinkedList<>();

        openMenuLabel.setOnMouseClicked(event -> {
            if (moreMenuButtons.isEmpty()) {
                moreMenuButtons.addAll(createMoreMenuButtons(iconSize, minSize));
            }

            openMenuLabel.setRotate(openMenuLabel.getRotate() + 180);
            ObservableList<Node> children = flowPane.getChildren();
            if (openMenuLabel.getRotate() % 360 == 0) {
                children.removeAll(moreMenuButtons);
            } else {
                children.addAll(children.indexOf(openMenuLabel) - 1, moreMenuButtons);
            }
        });

        ObservableList<DocumentMode> modeList = controller.getModeList();

        MenuButton menuButton = new MenuButton("Doctype");
        menuButton.setMinWidth(70);

        Map<String, List<String>> modulus = modeList.stream()
                .map(e -> e.getCaption())
                .collect(Collectors.groupingBy(e -> e.substring(0, 1)));

        for (Map.Entry<String, List<String>> listEntry : modulus.entrySet()) {
            Menu e = new Menu(listEntry.getKey());
            menuButton.getItems().add(e);

            for (String val : listEntry.getValue()) {
                MenuItem menuItem = new MenuItem(val);
                e.getItems().add(menuItem);
                menuItem.setOnAction(event -> {
                    menuButton.setText(menuItem.getText());
                    modeList.stream().filter(d -> d.getCaption().equals(menuItem.getText()))
                            .findFirst().ifPresent(documentMode -> {
                        editorPane.setMode(documentMode.getMode());
                        editorPane.switchMode(documentMode.getMode());
                    });
                    ;
                });
            }
        }

        flowPane.getChildren().addAll(
                newLabel,
                openLabel,
                saveLabel,
                boldLabel,
                italicLabel,
                underlineLabel,
                strikethroughLabel,
                headerLabel,
                hyperlinkLabel,
                quoteLabel,
                codeLabel,
                ulListLabel,
                olListLabel,
                tableLabel,
                imageLabel,
                subscriptLabel,
                superScriptLabel,
                menuButton,
                openMenuLabel,
                placeholderPane);

        flowPane.setHgap(7.5);
        flowPane.setVgap(5);
        flowPane.setAlignment(Pos.CENTER_LEFT);

        flowPane.setOnMouseClicked(event -> {
            editorPane.focus();
        });

        flowPane.setMinWidth(0); // fix
        flowPane.getStyleClass().add("top-menu");

        final ScrollPane scrollPane = new ScrollPane();
        scrollPane.setContent(editorPane);
        scrollPane.setFitToHeight(true);
        scrollPane.setFitToWidth(true);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);
        return new VBox(flowPane, scrollPane);
    }

    private List<Node> createMoreMenuButtons(final String iconSize, final double minSize) {

        MenuButton admonitionButton = new MenuButton("Admonitions");
        admonitionButton.setFocusTraversable(false);
        admonitionButton.getItems().add(new MenuItem("NOTE"));
        admonitionButton.getItems().add(new MenuItem("TIP"));
        admonitionButton.getItems().add(new MenuItem("IMPORTANT"));
        admonitionButton.getItems().add(new MenuItem("CAUTION"));
        admonitionButton.getItems().add(new MenuItem("WARNING"));

        admonitionButton.getItems().stream().forEach(item -> {
            item.setOnAction(e -> {
                shortcutProvider.getProvider().addAdmonition(item.getText());
            });
        });

        MenuButton blocks = new MenuButton("Blocks");
        blocks.setFocusTraversable(false);
        blocks.getItems().add(MenuItemBuilt.item("Sidebar").click(event -> {
            shortcutProvider.getProvider().addSidebarBlock();
        }));
        blocks.getItems().add(MenuItemBuilt.item("Example").click(event -> {
            shortcutProvider.getProvider().addExampleBlock();
        }));
        blocks.getItems().add(MenuItemBuilt.item("Passthrough").click(event -> {
            shortcutProvider.getProvider().addPassthroughBlock();
        }));
        blocks.getItems().add(MenuItemBuilt.item("Blockquote").click(event -> {
            shortcutProvider.getProvider().addQuote();
        }));

        final MenuButton documentHelpers = new MenuButton("Document helpers");
        documentHelpers.setFocusTraversable(false);
        documentHelpers.getItems().add(MenuItemBuilt.item("Book header").click(event -> {
            shortcutProvider.getProvider().addBookHeader();
        }));

        documentHelpers.getItems().add(MenuItemBuilt.item("Article header").click(event -> {
            shortcutProvider.getProvider().addArticleHeader();
        }));

        documentHelpers.getItems().add(MenuItemBuilt.item("Colophon").click(event -> {
            shortcutProvider.getProvider().addColophon();
        }));

        documentHelpers.getItems().add(MenuItemBuilt.item("Preface").click(event -> {
            shortcutProvider.getProvider().addPreface();
        }));

        documentHelpers.getItems().add(MenuItemBuilt.item("Dedication").click(event -> {
            shortcutProvider.getProvider().addDedication();
        }));

        documentHelpers.getItems().add(MenuItemBuilt.item("Appendix").click(event -> {
            shortcutProvider.getProvider().addAppendix();
        }));

        documentHelpers.getItems().add(MenuItemBuilt.item("Glossary").click(event -> {
            shortcutProvider.getProvider().addGlossary();
        }));

        documentHelpers.getItems().add(MenuItemBuilt.item("Bibliography").click(event -> {
            shortcutProvider.getProvider().addBibliography();
        }));

        documentHelpers.getItems().add(MenuItemBuilt.item("Colophon").click(event -> {
            shortcutProvider.getProvider().addColophon();
        }));

        documentHelpers.getItems().add(MenuItemBuilt.item("Index").click(event -> {
            shortcutProvider.getProvider().addIndex();
        }));

        final MenuButton extensions = new MenuButton("Extensions");
        extensions.setFocusTraversable(false);
        extensions.getItems().add(MenuItemBuilt.item("Mathjax (block)").click(event -> {
            shortcutProvider.getProvider().addMathBlock();
        }));
        extensions.getItems().add(MenuItemBuilt.item("Mathjax (inline)").click(event -> {
            shortcutProvider.getProvider().addMath2Block();
        }));
        extensions.getItems().add(MenuItemBuilt.item("PlantUML").click(event -> {
            shortcutProvider.getProvider().addUmlBlock();
        }));
        extensions.getItems().add(MenuItemBuilt.item("Ditaa").click(event -> {
            shortcutProvider.getProvider().addDitaaBlock();
        }));

        extensions.getItems().add(MenuItemBuilt.item("Filesystem Tree").click(event -> {
            shortcutProvider.getProvider().addTreeBlock();
        }));

        extensions.getItems().add(MenuItemBuilt.item("Mermaid").click(event -> {
            shortcutProvider.getProvider().addMermaidBlock();
        }));

        final MenuButton chartMenu = new MenuButton("Charts");
        chartMenu.setFocusTraversable(false);

        chartMenu.getItems().add(MenuItemBuilt.item("Pie").click(event -> {
            shortcutProvider.getProvider().addPieChart();
        }));

        chartMenu.getItems().add(MenuItemBuilt.item("Bar").click(event -> {
            shortcutProvider.getProvider().addBarChart();
        }));

        chartMenu.getItems().add(MenuItemBuilt.item("Line").click(event -> {
            shortcutProvider.getProvider().addLineChart();
        }));

        chartMenu.getItems().add(MenuItemBuilt.item("Area").click(event -> {
            shortcutProvider.getProvider().addAreaChart();
        }));

        chartMenu.getItems().add(MenuItemBuilt.item("Scatter").click(event -> {
            shortcutProvider.getProvider().addScatterChart();
        }));

        chartMenu.getItems().add(MenuItemBuilt.item("Bubble").click(event -> {
            shortcutProvider.getProvider().addBubbleChart();
        }));

        chartMenu.getItems().add(MenuItemBuilt.item("Stacked Area").click(event -> {
            shortcutProvider.getProvider().addStackedAreaChart();
        }));

        chartMenu.getItems().add(MenuItemBuilt.item("Stacked Bar").click(event -> {
            shortcutProvider.getProvider().addStackedBarChart();
        }));

        return Arrays.asList(blocks, admonitionButton, documentHelpers, extensions, chartMenu);
    }
}

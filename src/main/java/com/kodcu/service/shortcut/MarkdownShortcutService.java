package com.kodcu.service.shortcut;

import com.kodcu.component.AlertHelper;
import com.kodcu.controller.ApplicationController;
import com.kodcu.other.Current;
import com.kodcu.service.table.MarkdownTableController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by usta on 13.03.2015.
 */
@Component
public class MarkdownShortcutService implements ShortcutService {

    private final Current current;
    private final ApplicationController controller;
    private final MarkdownTableController tableController;

    @Autowired
    public MarkdownShortcutService(Current current, ApplicationController controller, MarkdownTableController tableController) {
        this.current = current;
        this.controller = controller;
        this.tableController = tableController;
    }

    @Override
    public void addBold() {
        current.currentEngine().executeScript("editorMenu.markdown.boldText()");
    }

    @Override
    public void addBasicTable(String row, String column) {
        tableController.createBasicTable(row, column);
    }

    @Override
    public void addItalic() {
        current.currentEngine().executeScript("editorMenu.markdown.italicizeText()");
    }

    @Override
    public void addHeading() {
        current.currentEngine().executeScript("editorMenu.markdown.addHeading()");
    }

    @Override
    public void addCode(String lang) {
        current.currentEngine().executeScript("editorMenu.markdown.addSourceCode('" + lang + "')");
    }

    @Override
    public void addUnorderedList() {
        current.currentEngine().executeScript("editorMenu.markdown.addUlList()");
    }

    @Override
    public void addOrderedList() {
        current.currentEngine().executeScript("editorMenu.markdown.addOlList()");
    }

    @Override
    public void addTable() {
        controller.createMarkdownTable();
    }

    @Override
    public void addImage() {
        current.currentEngine().executeScript("editorMenu.markdown.addImageSection()");
    }

    @Override
    public void addSubscript() {
        current.currentEngine().executeScript("editorMenu.markdown.subScript()");
    }

    @Override
    public void addSuperscript() {
        current.currentEngine().executeScript("editorMenu.markdown.superScript()");
    }

    @Override
    public void addUnderline() {
        current.currentEngine().executeScript("editorMenu.markdown.underlinedText()");
    }

    @Override
    public void addHyperlink() {
        current.currentEngine().executeScript("editorMenu.markdown.addHyperLink()");
    }

    @Override
    public void addStrike() {
        current.currentEngine().executeScript("editorMenu.markdown.addStrikeThroughText()");
    }

    @Override
    public void addQuote() {
        current.currentEngine().executeScript("editorMenu.markdown.addQuote()");
    }

    @Override
    public void addHighlight() {
        // no-op
        AlertHelper.notImplementedDialog();
    }

    @Override
    public void addAdmonition(String type) {
        // no-op
        AlertHelper.notImplementedDialog();
    }

    @Override
    public void addSidebarBlock() {
        // no-op
        AlertHelper.notImplementedDialog();
    }

    @Override
    public void addExampleBlock() {
        // no-op
        AlertHelper.notImplementedDialog();
    }

    @Override
    public void addPassthroughBlock() {
        // no-op
        AlertHelper.notImplementedDialog();
    }

    @Override
    public void addIndexSelection() {
        // no-op
        AlertHelper.notImplementedDialog();
    }

    @Override
    public void addColophon() {
        AlertHelper.notImplementedDialog();
    }

    @Override
    public void addPreface() {
        AlertHelper.notImplementedDialog();
    }

    @Override
    public void addDedication() {
        AlertHelper.notImplementedDialog();
    }

    @Override
    public void addAppendix() {
        AlertHelper.notImplementedDialog();
    }

    @Override
    public void addGlossary() {
        AlertHelper.notImplementedDialog();
    }

    @Override
    public void addBibliography() {
        AlertHelper.notImplementedDialog();
    }

    @Override
    public void addIndex() {
        AlertHelper.notImplementedDialog();
    }

    @Override
    public void addBookHeader() {
        current.currentEngine().executeScript("editorMenu.asciidoc.addBookHeader()");
    }

    @Override
    public void addArticleHeader() {
        current.currentEngine().executeScript("editorMenu.asciidoc.addArticleHeader()");
    }

    @Override
    public void addMathBlock() {
        AlertHelper.notImplementedDialog();
    }

    @Override
    public void addUmlBlock() {
        AlertHelper.notImplementedDialog();
    }

    @Override
    public void addDitaaBlock() {
        AlertHelper.notImplementedDialog();
    }
    
    @Override
    public void addTreeBlock() {
        AlertHelper.notImplementedDialog();
    }

    @Override
    public void addPieChart() {
        AlertHelper.notImplementedDialog();
    }

    @Override
    public void addBarChart() {
        AlertHelper.notImplementedDialog();
    }

    @Override
    public void addLineChart() {
        AlertHelper.notImplementedDialog();
    }

    @Override
    public void addAreaChart() {
        AlertHelper.notImplementedDialog();
    }

    @Override
    public void addScatterChart() {
        AlertHelper.notImplementedDialog();
    }

    @Override
    public void addBubbleChart() {
        AlertHelper.notImplementedDialog();
    }

    @Override
    public void addStackedAreaChart() {
        AlertHelper.notImplementedDialog();
    }

    @Override
    public void addStackedBarChart() {
        AlertHelper.notImplementedDialog();
    }
}

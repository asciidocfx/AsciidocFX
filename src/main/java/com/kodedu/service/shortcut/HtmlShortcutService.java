package com.kodedu.service.shortcut;

import com.kodedu.controller.ApplicationController;
import com.kodedu.other.Current;
import com.kodedu.service.table.AsciidocTableController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by usta on 02.10.2015.
 */
@Component
public class HtmlShortcutService implements ShortcutService {

    private final Current current;
    private final ApplicationController controller;
    private final AsciidocTableController tableController;

    @Autowired
    public HtmlShortcutService(Current current, ApplicationController controller, AsciidocTableController tableController) {
        this.current = current;
        this.controller = controller;
        this.tableController = tableController;
    }

    @Override
    public void addBold() {
        current.currentEngine().executeScript("editorMenu.html.boldText()");
    }

    @Override
    public void addItalic() {
        current.currentEngine().executeScript("editorMenu.html.italicizeText()");
    }

    @Override
    public void addHeading() {
        current.currentEngine().executeScript("editorMenu.html.addHeading()");
    }

    @Override
    public void addCode(String lang) {
        current.currentEngine().executeScript(String.format("editorMenu.html.addSourceCode(\"%s\")", lang));
    }

    @Override
    public void addUnorderedList() {
        current.currentEngine().executeScript("editorMenu.html.addUlList()");
    }

    @Override
    public void addOrderedList() {
        current.currentEngine().executeScript("editorMenu.html.addOlList()");
    }

    @Override
    public void addTable() {
    }

    @Override
    public void addBasicTable(String row, String column) {
    }

    @Override
    public void addImage() {
        current.currentEngine().executeScript("editorMenu.html.addImageSection()");
    }

    @Override
    public void addSubscript() {
        current.currentEngine().executeScript("editorMenu.html.subScript()");
    }

    @Override
    public void addSuperscript() {
        current.currentEngine().executeScript("editorMenu.html.superScript()");
    }

    @Override
    public void addUnderline() {
        current.currentEngine().executeScript("editorMenu.html.underlinedText()");
    }

    @Override
    public void addHyperlink() {
        current.currentEngine().executeScript("editorMenu.html.addHyperLink()");
    }

    @Override
    public void addStrike() {
        current.currentEngine().executeScript("editorMenu.html.addStrikeThroughText()");
    }

    @Override
    public void addQuote() {
        current.currentEngine().executeScript("editorMenu.html.addQuote()");
    }

    @Override
    public void addHighlight() {
    }

    @Override
    public void addAdmonition(String type) {
    }

    @Override
    public void addSidebarBlock() {
    }

    @Override
    public void addExampleBlock() {
    }

    @Override
    public void addPassthroughBlock() {
    }

    @Override
    public void addIndexSelection() {
    }

    @Override
    public void addColophon() {
    }

    @Override
    public void addPreface() {
    }

    @Override
    public void addDedication() {
    }

    @Override
    public void addAppendix() {
    }

    @Override
    public void addGlossary() {
    }

    @Override
    public void addBibliography() {
    }

    @Override
    public void addIndex() {
    }

    @Override
    public void addBookHeader() {
    }

    @Override
    public void addArticleHeader() {
    }

    @Override
    public void addMathBlock() {
    }

    @Override
    public void addMath2Block() {
    }

    @Override
    public void addUmlBlock() {
    }

    @Override
    public void addDitaaBlock() {
    }

    @Override
    public void addTreeBlock() {
    }

    @Override
    public void addPieChart() {
    }

    @Override
    public void addBarChart() {
    }

    @Override
    public void addLineChart() {
    }

    @Override
    public void addAreaChart() {
    }

    @Override
    public void addScatterChart() {
    }

    @Override
    public void addBubbleChart() {
    }

    @Override
    public void addStackedAreaChart() {
    }

    @Override
    public void addStackedBarChart() {
    }

    @Override
    public void includeAsSubdocument() {

    }


}

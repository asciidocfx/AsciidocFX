package com.kodcu.service.shortcut;

import com.kodcu.controller.ApplicationController;
import com.kodcu.other.Current;
import com.kodcu.service.table.AsciidocTableController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by usta on 13.03.2015.
 */
@Component
public class AsciidocShortcutService implements ShortcutService {

    private final Current current;
    private final ApplicationController controller;
    private final AsciidocTableController tableController;

    @Autowired
    public AsciidocShortcutService(Current current, ApplicationController controller, AsciidocTableController tableController) {
        this.current = current;
        this.controller = controller;
        this.tableController = tableController;
    }

    @Override
    public void addBold() {
        current.currentEngine().executeScript("editorMenu.asciidoc.boldText()");
    }

    @Override
    public void addItalic() {
        current.currentEngine().executeScript("editorMenu.asciidoc.italicizeText()");
    }

    @Override
    public void addHeading() {
        current.currentEngine().executeScript("editorMenu.asciidoc.addHeading()");
    }

    @Override
    public void addCode(String lang) {
        current.currentEngine().executeScript("editorMenu.asciidoc.addSourceCode('" + lang + "')");
    }

    @Override
    public void addUnorderedList() {
        current.currentEngine().executeScript("editorMenu.asciidoc.addUlList()");
    }

    @Override
    public void addOrderedList() {
        current.currentEngine().executeScript("editorMenu.asciidoc.addOlList()");
    }

    @Override
    public void addTable() {
        controller.createAsciidocTable();
    }

    @Override
    public void addBasicTable(String row, String column) {
        tableController.createBasicTable(row,column);
    }

    @Override
    public void addImage() {
        current.currentEngine().executeScript("editorMenu.asciidoc.addImageSection()");
    }

    @Override
    public void addSubscript() {
        current.currentEngine().executeScript("editorMenu.asciidoc.subScript()");
    }

    @Override
    public void addSuperscript() {
        current.currentEngine().executeScript("editorMenu.asciidoc.superScript()");
    }

    @Override
    public void addUnderline() {
        current.currentEngine().executeScript("editorMenu.asciidoc.underlinedText()");
    }

    @Override
    public void addHyperlink() {
        current.currentEngine().executeScript("editorMenu.asciidoc.addHyperLink()");
    }

    @Override
    public void addStrike() {
        current.currentEngine().executeScript("editorMenu.asciidoc.addStrikeThroughText()");
    }

    @Override
    public void addQuote() {
        current.currentEngine().executeScript("editorMenu.asciidoc.addQuote()");
    }

    @Override
    public void addHighlight() {
        current.currentEngine().executeScript("editorMenu.asciidoc.highlightedText()");
    }
}

package com.kodcu.service.shortcut;

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
        tableController.createBasicTable(row,column);
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
    }
}

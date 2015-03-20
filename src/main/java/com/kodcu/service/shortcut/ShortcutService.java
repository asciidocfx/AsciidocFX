package com.kodcu.service.shortcut;

/**
 * Created by usta on 13.03.2015.
 */
public interface ShortcutService {

    void addBold();
    void addItalic();
    void addHeading();
    void addCode(String lang);
    void addUnorderedList();
    void addOrderedList();
    void addTable();
    void addBasicTable(String row, String column);
    void addImage();
    void addSubscript();
    void addSuperscript();
    void addUnderline();
    void addHyperlink();
    void addStrike();
    void addQuote();
    void addHighlight();
    void addAdmonition(String type);
    void addSidebarBlock();
    void addExampleBlock();
    void addPassthroughBlock();
    void addIndexSelection();
}

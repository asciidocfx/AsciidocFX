package com.kodcu.bean;

/**
 * Created by usta on 30.08.2014.
 */
public class Config {

    private String fontSize ;
    private String fontFamily;
    private Integer recentFileListSize;
    private boolean directoryPanel;
    private String theme;
    private String scrollSpeed;
    private String workingDirectory;
    private String kindlegenDir;
    private String defaultLanguage;

    public String getDefaultLanguage() {
        return defaultLanguage;
    }

    public void setDefaultLanguage(String defaultLanguage) {
        this.defaultLanguage = defaultLanguage;
    }

    public String getFontSize() {
        return fontSize;
    }

    public void setFontSize(String fontSize) {
        this.fontSize = fontSize;
    }

    public String getFontFamily() {
        return fontFamily;
    }

    public void setFontFamily(String fontFamily) {
        this.fontFamily = fontFamily;
    }

    public void setDirectoryPanel(boolean directoryPanel) {
        this.directoryPanel = directoryPanel;
    }

    public boolean isDirectoryPanel() {
        return directoryPanel;
    }

    public boolean getDirectoryPanel() {
        return directoryPanel;
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }

    public String getTheme() {
        return theme;
    }

    public void setScrollSpeed(String scrollSpeed) {
        this.scrollSpeed = scrollSpeed;
    }

    public String getScrollSpeed() {
        return scrollSpeed;
    }

    public Integer getRecentFileListSize() {
        return recentFileListSize;
    }

    public void setRecentFileListSize(Integer recentFileListSize) {
        this.recentFileListSize = recentFileListSize;
    }

    public String getWorkingDirectory() {
        return workingDirectory;
    }

    public void setWorkingDirectory(String workingDirectory) {
        this.workingDirectory = workingDirectory;
    }

    public String getKindlegenDir() {
        return kindlegenDir;
    }

    public void setKindlegenDir(String kindlegenDir) {
        this.kindlegenDir = kindlegenDir;
    }
}

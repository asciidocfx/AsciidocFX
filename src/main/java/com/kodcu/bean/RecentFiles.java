package com.kodcu.bean;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Created by usta on 30.08.2014.
 */
public class RecentFiles {

    private List<String> files;
    private List<String> favoriteDirectories;
    private String workingDirectory;

    public RecentFiles(List<String> files) {
        this.files = files;
    }

    public RecentFiles() {
    }

    public List<String> getFiles() {
        if(Objects.isNull(files))
            files = new ArrayList<>();
        return files;
    }

    public List<String> getFavoriteDirectories() {
        if(Objects.isNull(favoriteDirectories))
            favoriteDirectories = new ArrayList<>();
        return favoriteDirectories;
    }

    public void setFavoriteDirectories(List<String> favoriteDirectories) {
        this.favoriteDirectories = favoriteDirectories;
    }

    public void setFiles(List<String> files) {
        this.files = files;
    }

    public String getWorkingDirectory() {
        return workingDirectory;
    }

    public void setWorkingDirectory(String workingDirectory) {
        this.workingDirectory = workingDirectory;
    }
}

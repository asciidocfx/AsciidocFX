package com.kodcu.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by usta on 30.08.2014.
 */
public class RecentFiles {

    private List<String> files=new ArrayList<>();

    public RecentFiles(List<String> files) {
        this.files = files;
    }

    public RecentFiles() {  }

    public List<String> getFiles() {
        return files;
    }

    public void setFiles(List<String> files) {
        this.files = files;
    }


}

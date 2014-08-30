package com.kodcu.bean;

import com.esotericsoftware.yamlbeans.YamlException;
import com.esotericsoftware.yamlbeans.YamlReader;
import com.esotericsoftware.yamlbeans.YamlWriter;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
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

    public RecentFiles() {
    }

    public List<String> getFiles() {
        return files;
    }

    public void setFiles(List<String> files) {
        this.files = files;
    }

    public static void main(String[] args) throws IOException {


//       writeYaml();
       readYaml();
    }

    private static void readYaml() throws FileNotFoundException, YamlException {

        YamlReader yamlReader =
                new YamlReader(new FileReader("C:\\Users\\usta\\Dropbox\\AsciidocFX\\conf\\recentFiles.yml"));
        yamlReader.getConfig().setClassTag("RecentFiles", RecentFiles.class);
        RecentFiles readed=yamlReader.read(RecentFiles.class);
        readed=readed;
    }

    private static void writeYaml() throws IOException {
        YamlWriter writer = new YamlWriter(new FileWriter("C:\\Users\\usta\\Dropbox\\AsciidocFX\\conf\\recentFiles.yml"));
        writer.getConfig().setClassTag("Recent Files", RecentFiles.class);


        RecentFiles recentFiles=new RecentFiles();

        recentFiles.getFiles().add("01-chap.asc");
        recentFiles.getFiles().add("02-chap.asc");
        recentFiles.getFiles().add("03-chap.asc");
        recentFiles.getFiles().add("05-chap.asc");

        writer.write(recentFiles);
        writer.close();
    }
}

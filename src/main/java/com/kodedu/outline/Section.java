package com.kodedu.outline;

import java.nio.file.Path;
import java.util.Objects;
import java.util.TreeSet;

/**
 * Created by usta on 31.05.2015.
 */
public class Section implements Comparable<Section> {

    public Section() {
    }

    public Section(Integer level, String title, Integer lineno, String id, Path path, Section parent) {
        this.level = level;
        this.title = title;
        this.lineno = lineno;
        this.id = id;
        this.path = path;
        this.parent = parent;
    }

    private Integer level;
    private String title;
    private Integer lineno;
    private String id;
    private TreeSet<Section> subsections = new TreeSet<>();
    private Section parent;

    private Path path;

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getLineno() {
        return lineno;
    }

    public void setLineno(Integer lineno) {
        this.lineno = lineno;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public TreeSet<Section> getSubsections() {
        return subsections;
    }

    public Path getPath() {
        return path;
    }

    public void setPath(Path path) {
        this.path = path;
    }

    @Override
    public int compareTo(Section o) {
        Integer lineno = this.getLineno();
        Integer otherLine = o.getLineno();
        return lineno.compareTo(otherLine);
    }

    @Override
    public String toString() {
        if (level == -1)
            return String.format("%s", title);

        return String.format("H%d - %s", level + 1, title);
    }

    public void setParent(Section parent) {
        this.parent = parent;
    }

    public Section getParent() {
        return parent;
    }
}

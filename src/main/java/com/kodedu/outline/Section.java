package com.kodedu.outline;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.TreeSet;

/**
 * Created by usta on 31.05.2015.
 */
public class Section implements Comparable<Section> {

    private Integer level;
    private String title;
    private Integer lineno;
    private String id;
    private TreeSet<Section> subsections;
    private Section parent;

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
        if (Objects.isNull(subsections))
            subsections = new TreeSet<>();
        return subsections;
    }

    public void setSubsections(TreeSet<Section> subsections) {
        this.subsections = subsections;
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

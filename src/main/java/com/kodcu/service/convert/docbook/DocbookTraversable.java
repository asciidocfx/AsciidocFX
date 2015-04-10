package com.kodcu.service.convert.docbook;

import com.kodcu.service.convert.Traversable;

/**
 * Created by usta on 09.04.2015.
 */
public interface DocbookTraversable extends Traversable {

    @Override
    public default void traverseLine(String line, StringBuffer buffer) {
        if (line.matches("^=+ +.*:.*")) // Replace : in headers for a asciidoctor bug
            line = line.replace(":", "00HEADER00COLON00");
        Traversable.super.traverseLine(line, buffer);
    }
}

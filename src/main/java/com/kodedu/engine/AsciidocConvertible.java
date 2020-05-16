package com.kodedu.engine;

import com.kodedu.controller.TextChangeEvent;
import com.kodedu.other.ConverterResult;

/**
 * Created by usta on 22.08.2015.
 */
public interface AsciidocConvertible {

    ConverterResult convertDocbook(TextChangeEvent textChangeEvent);

    ConverterResult convertAsciidoc(TextChangeEvent textChangeEvent);

    ConverterResult convertHtml(TextChangeEvent textChangeEvent);

    void convertOdf(String asciidoc);

    void fillOutlines(Object doc);

    String applyReplacements(String asciidoc);
}

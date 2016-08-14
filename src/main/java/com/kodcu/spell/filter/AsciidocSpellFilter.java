package com.kodcu.spell.filter;

import org.springframework.stereotype.Component;

/**
 * Created by usta on 07.12.2015.
 */
@Component("asciidocSpellFilter")
public class AsciidocSpellFilter extends AbstractSpellFilter {

    public AsciidocSpellFilter() {
        addAllowedToken(
                "constant.numeric",
                "markup.heading",
                "keyword.bold", // *abcd*
                "string.italic" // _abcd_
        );
    }


}

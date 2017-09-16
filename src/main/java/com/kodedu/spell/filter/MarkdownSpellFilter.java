package com.kodedu.spell.filter;

import org.springframework.stereotype.Component;

/**
 * Created by usta on 07.12.2015.
 */
@Component("markdownSpellFilter")
public class MarkdownSpellFilter extends AbstractSpellFilter {
    public MarkdownSpellFilter() {
        getAllowedTokenTypes().add("text.xml");
        getAllowedTokenTypes().add("list");
        getAllowedTokenTypes().add("string.blockquote");
        getAllowedTokenTypes().add("string.strong");
        getAllowedTokenTypes().add("string.emphasis");
        getAllowedTokenTypes().add("heading");
    }
}

package com.kodedu.spell.filter;

import org.springframework.stereotype.Component;

/**
 * Created by usta on 07.12.2015.
 */
@Component("htmlSpellFilter")
public class HtmlSpellFilter extends AbstractSpellFilter {

    public HtmlSpellFilter() {
        addAllowedToken("text.xml");
    }

}

package com.kodcu.spell.filter;

import org.springframework.stereotype.Component;

/**
 * Created by usta on 07.12.2015.
 */
@Component("xmlSpellFilter")
public class XmlSpellFilter extends AbstractSpellFilter {

    public XmlSpellFilter() {
        addAllowedToken("text.xml");
    }

}

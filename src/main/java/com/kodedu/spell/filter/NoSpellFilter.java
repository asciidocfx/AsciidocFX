package com.kodedu.spell.filter;

import com.kodedu.spell.dictionary.Token;
import org.springframework.stereotype.Component;

/**
 * Created by usta on 07.12.2015.
 */
@Component("noSpellFilter")
public class NoSpellFilter extends AbstractSpellFilter {
    @Override
    public boolean test(Token token) {
        return true;
    }
}

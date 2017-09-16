package com.kodedu.spell.filter;

import com.kodedu.spell.dictionary.Token;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

/**
 * Created by usta on 07.12.2015.
 */
public class AbstractSpellFilter implements Predicate<Token> {

    private Set<String> allowedTokenTypes = new HashSet<>(Arrays.asList("text", "string"));

    @Override
    public boolean test(Token token) {
        return allowedTokenTypes.contains(token.getType());
    }

    public Set<String> getAllowedTokenTypes() {
        return allowedTokenTypes;
    }

    public void addAllowedToken(String... type) {
        getAllowedTokenTypes().addAll(Arrays.asList(type));
    }
}

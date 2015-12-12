package com.kodcu.spell.filter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

/**
 * Created by usta on 07.12.2015.
 */
@Component
public class SpellFilterProvider {

    private final ApplicationContext context;

    @Autowired
    public SpellFilterProvider(ApplicationContext context) {
        this.context = context;
    }

    public AbstractSpellFilter filterByMode(String mode) {
        final String filterName = mode + "SpellFilter";

        if (!context.containsBean(filterName)) {
            return context.getBean(NoSpellFilter.class);
        }

        return context.getBean(filterName, AbstractSpellFilter.class);
    }
}

package com.kodcu.service.shortcut;

import com.kodcu.other.Current;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

/**
 * Created by usta on 13.03.2015.
 */
@Component
public class ShortcutProvider {

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private Current current;

    public ShortcutService getProvider() {
        return (ShortcutService) applicationContext.getBean(current.currentTab().getShortcutType());
    }

}

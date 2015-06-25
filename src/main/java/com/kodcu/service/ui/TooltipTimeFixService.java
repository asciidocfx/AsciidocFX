package com.kodcu.service.ui;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.control.Tooltip;
import javafx.util.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;

/**
 * Created by usta on 25.01.2015.
 */
@Component
public class TooltipTimeFixService {

    private final Logger logger = LoggerFactory.getLogger(TooltipTimeFixService.class);

    public void fix(){

        Tooltip tooltip=new Tooltip();
        try {

            Field fieldBehavior = tooltip.getClass().getDeclaredField("BEHAVIOR");
            fieldBehavior.setAccessible(true);
            Object objBehavior = fieldBehavior.get(tooltip);

            Field fieldTimer = objBehavior.getClass().getDeclaredField("activationTimer");
            fieldTimer.setAccessible(true);
            Timeline objTimer = (Timeline) fieldTimer.get(objBehavior);

            objTimer.getKeyFrames().clear();
            objTimer.getKeyFrames().add(new KeyFrame(new Duration(600)));
        } catch (Exception e) {
            logger.debug("Problem occured while fixing tooltip time, but dont worry",e);
        }
    }
}

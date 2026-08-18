package com.kodedu.component;

import javafx.scene.control.DialogPane;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Hakan on 4/1/2015.
 */
interface DefenderDialog {

    public Logger logger = LoggerFactory.getLogger(DefenderDialog.class);

    default void setDefaultIcon(DialogPane dialog) {
        Runnable apply = () -> {
            if (dialog.getScene() == null || !(dialog.getScene().getWindow() instanceof Stage stage)) {
                return;
            }
            try (InputStream logoStream = DefenderDialog.class.getResourceAsStream("/logo.png")) {
                if (logoStream != null) {
                    stage.getIcons().add(new Image(logoStream));
                }
            } catch (IOException e) {
                logger.error("logo.png is not found", e);
            }
        };
        if (dialog.getScene() != null) {
            apply.run();
            return;
        }
        dialog.sceneProperty().addListener((obs, oldScene, scene) -> {
            if (scene != null) {
                apply.run();
            }
        });
    }
}

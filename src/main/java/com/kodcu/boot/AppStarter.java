package com.kodcu.boot;

import static javafx.scene.input.KeyCombination.SHORTCUT_DOWN;

import java.io.File;
import java.nio.file.Path;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

import com.kodcu.controller.ApplicationController;
import com.kodcu.service.ThreadService;
import com.kodcu.service.ui.TabService;
import com.sun.deploy.uitoolkit.impl.fx.HostServicesFactory;
import com.sun.javafx.application.HostServicesDelegate;

import de.tototec.cmdoption.CmdlineParser;
import de.tototec.cmdoption.CmdlineParserException;

public class AppStarter extends Application {

    private Logger logger = LoggerFactory.getLogger(AppStarter.class);

    private ApplicationController controller;
    private ConfigurableApplicationContext context;

    @Override
    public void start(final Stage stage) {
        final CmdlineConfig config = new CmdlineConfig();
        final CmdlineParser cp = new CmdlineParser(config);

        try {
            cp.parse(getParameters().getRaw().toArray(new String[0]));
        } catch (final CmdlineParserException e) {
            System.err.println("Invalid commandline given: " + e.getMessage());
            System.exit(1);
        }

        if (config.help) {
            cp.usage();
            System.exit(0);
        }

        try {
            startApp(stage, config);
        } catch (final Throwable e) {
            logger.error(e.getMessage(), e);
        }

    }

    private void startApp(final Stage stage, final CmdlineConfig config) throws Throwable {
        final FXMLLoader parentLoader = new FXMLLoader();
        final FXMLLoader tablePopupLoader = new FXMLLoader();

        context = SpringApplication.run(SpringAppConfig.class);
        tablePopupLoader.setControllerFactory(context::getBean);
        parentLoader.setControllerFactory(context::getBean);

        AnchorPane tableAnchor = tablePopupLoader.load(getClass().getResourceAsStream("/fxml/TablePopup.fxml"));
        Parent root = parentLoader.load(getClass().getResourceAsStream("/fxml/Scene.fxml"));
        controller = parentLoader.getController();
        HostServicesDelegate hostServices = HostServicesFactory.getInstance(this);
        controller.setHostServices(hostServices);

        Scene scene = new Scene(root);

        scene.getStylesheets().add("/styles/Styles.css");
        Rectangle2D bounds = Screen.getPrimary().getVisualBounds();
        stage.setWidth(bounds.getWidth());
        stage.setHeight(bounds.getHeight());
        stage.setX(0);
        stage.setY(0);
        stage.setTitle("AsciidocFX");
        stage.getIcons().add(new Image(getClass().getResourceAsStream("/logo.png")));

        Stage tableStage = new Stage();
        tableStage.setScene(new Scene(tableAnchor));
        tableStage.setTitle("Table Generator");
        tableStage.initModality(Modality.WINDOW_MODAL);
        tableStage.initOwner(scene.getWindow());
        tableStage.getIcons().add(new Image(getClass().getResourceAsStream("/logo.png")));

        controller.setStage(stage);
        controller.setScene(scene);
        controller.setTableAnchor(tableAnchor);
        controller.setTableStage(tableStage);

        stage.setScene(scene);
        stage.show();

        scene.getAccelerators().put(new KeyCodeCombination(KeyCode.S, SHORTCUT_DOWN), controller::saveDoc);
        scene.getAccelerators().put(new KeyCodeCombination(KeyCode.N, SHORTCUT_DOWN), () -> {
            controller.newDoc(null);
        });

        if (!config.files.isEmpty()) {
            final ThreadService threadService = context.getBean(ThreadService.class);
            final TabService tabService = context.getBean(TabService.class);
            threadService.runActionLater(() -> {
                config.files.stream().forEach(f -> {
                    File file = new File(f).getAbsoluteFile();
                    if (file.exists()) {
                        logger.info("Opening file as requsted from cmdline: {}", file);
                        tabService.addTab(file.toPath());
                    } else {
                        // TODO: do we want to create such a file on demand?
                        logger.error("Cannot open non-existent file: {}", file);
                    }
                });
            });
        }
    }

    @Override
    public void stop() throws Exception {
        controller.closeApp(null);
        context.registerShutdownHook();
        Platform.exit();
        System.exit(0);
    }

    /**
     * The main() method is ignored in correctly deployed JavaFX application.
     * main() serves only as fallback in case the application can not be
     * launched through deployment artifacts, e.g., in IDEs with limited FX
     * support. NetBeans ignores main().
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

}

package com.kodcu;


import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.stage.Screen;
import javafx.stage.Stage;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

import static javafx.scene.input.KeyCombination.CONTROL_DOWN;


public class Start extends Application {

    private AsciiDocController controller;
    private ConfigurableApplicationContext context;

    @Override
    public void start(Stage stage) throws Exception {
        setUserAgentStylesheet(STYLESHEET_MODENA);

        context = SpringApplication.run(AsciiDocConfig.class);
        FXMLLoader loader = new FXMLLoader();
        loader.setControllerFactory(context::getBean);
        Parent root = loader.load(getClass().getResourceAsStream("/fxml/Scene.fxml"));

        controller = loader.getController();


        Scene scene = new Scene(root);
        scene.getStylesheets().add("/styles/Styles.css");
        Rectangle2D bounds = Screen.getPrimary().getVisualBounds();
        System.out.println(bounds.getWidth() + " : " + bounds.getHeight());
        stage.setWidth(bounds.getWidth());
        stage.setHeight(bounds.getHeight());
        stage.setX(0);
        stage.setY(0);
        stage.setTitle("AsciidocFX");

        controller.setStage(stage);
        controller.setScene(scene);
        stage.setScene(scene);
        stage.show();

        scene.getAccelerators().put(new KeyCodeCombination(KeyCode.S, CONTROL_DOWN), controller::saveDoc);

    }

    @Override
    public void stop() throws Exception {
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

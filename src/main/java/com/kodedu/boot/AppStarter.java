package com.kodedu.boot;

import com.install4j.api.launcher.StartupNotification;
import com.kodedu.config.ConfigurationService;
import com.kodedu.config.EditorConfigBean;
import com.kodedu.controller.ApplicationController;
import com.kodedu.service.DirectoryService;
import com.kodedu.service.FileOpenListener;
import com.kodedu.service.ThreadService;
import com.kodedu.service.ui.TabService;
import com.kodedu.terminalfx.helper.ThreadHelper;
import de.tototec.cmdoption.CmdlineParser;
import de.tototec.cmdoption.CmdlineParserException;
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
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.NotDirectoryException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static javafx.scene.input.KeyCombination.SHORTCUT_DOWN;

public class AppStarter extends Application {

    private static Logger logger = LoggerFactory.getLogger(AppStarter.class);

    private static ApplicationController controller;
    private static ConfigurableApplicationContext context;
    private EditorConfigBean editorConfigBean;
    private static Stage stage;
    private ThreadService threadService;
    private ConfigurationService configurationService;

    @Override
    public void start(final Stage stage) {

        Thread.setDefaultUncaughtExceptionHandler((t, e) -> logger.error(e.getMessage(), e));

        loadRequiredFonts();

//        System.setProperty("nashorn.typeInfo.maxFiles", "5");

        // http://bit.ly/1Euk8hh
        System.setProperty("jsse.enableSNIExtension", "false");
//        System.setProperty("https.protocols", "SSLv3");

        System.setProperty("sun.net.http.allowRestrictedHeaders", "true");
        System.setProperty("java.awt.headless", "false");

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

        new Thread(() -> {
            try {
                startApp(stage, config);
            } catch (final Throwable e) {
                logger.error("Problem occured while starting AsciidocFX", e);
            }
        }).start();

    }

    static {
        ThreadHelper.start(() -> {
            StartupNotification.registerStartupListener(parameters -> {
                ThreadHelper.start(() -> {
                    while ((context == null && stage == null) || !stage.isShowing()) {
                        ThreadHelper.sleep(10);
                    }
                    FileOpenListener fileOpenListener = context.getBean(FileOpenListener.class);
                    fileOpenListener.startupPerformed(parameters);
                });
            });
        });
    }

    private void loadRequiredFonts() {
        var fonts = List.of("/font/NotoSerif-Regular.ttf", "/font/NotoSerif-Italic.ttf", "/font/NotoSerif-Bold.ttf",
                "/font/NotoSerif-BoldItalic.ttf", "/font/DejaVuSansMono.ttf", "/font/DejaVuSansMono-Bold.ttf",
                "/font/DejaVuSansMono-Oblique.ttf");

        for (String font : fonts) {
            try (var in = AppStarter.class.getResourceAsStream(font);) {
                Font.loadFont(in, -1);
            } catch (IOException e) {
                logger.error("Error when loading font {}", font, e);
            }
        }

    }

    private void startApp(final Stage stage, final CmdlineConfig config) throws Throwable {

        this.stage = stage;
        context = SpringApplication.run(SpringAppConfig.class);
        editorConfigBean = context.getBean(EditorConfigBean.class);
        controller = context.getBean(ApplicationController.class);
        threadService = context.getBean(ThreadService.class);
        configurationService = context.getBean(ConfigurationService.class);

        final FXMLLoader parentLoader = new FXMLLoader();
        parentLoader.setControllerFactory(context::getBean);

        Parent root;
        try (InputStream sceneStream = AppStarter.class.getResourceAsStream("/scenes/AsciidocFX_Scene.fxml")) {
            root = parentLoader.load(sceneStream);
        }

        Scene scene = new Scene(root);

        stage.setTitle("AsciidocFX");
        Image logoImage;
        try (InputStream logoStream = AppStarter.class.getResourceAsStream("/logo.png")) {
            logoImage = new Image(logoStream);
        }
        stage.getIcons().add(logoImage);
        threadService.runActionLater(stage::setScene, scene);

        controller.initializeApp();

        stage.setOnShowing(e -> {

            controller.setStage(stage);
            controller.setScene(scene);
            controller.setHostServices(getHostServices());

            configurationService.loadConfigurations();
            controller.applyInitialConfigurations();
            controller.checkStageInsideScreens();

        });

        stage.setOnShown(e -> {
            controller.bindConfigurations();
            controller.showConfigLoaderOnNewInstall();
        });

        threadService.runActionLater(() -> {
            setMaximized();

            if (controller.getTabPane().getTabs().isEmpty()) {
                controller.newDoc();
            }

            stage.show();
        });

        final FXMLLoader asciidocTableLoader = new FXMLLoader();
        final FXMLLoader markdownTableLoader = new FXMLLoader();

        asciidocTableLoader.setControllerFactory(context::getBean);
        markdownTableLoader.setControllerFactory(context::getBean);

        AnchorPane asciidocTableAnchor;
        try (InputStream asciidocTableStream = AppStarter.class.getResourceAsStream("/scenes/AsciidocTablePopup.fxml")) {
            asciidocTableAnchor = asciidocTableLoader.load(asciidocTableStream);
        }

        AnchorPane markdownTableAnchor;
        try (InputStream markdownTableStream = AppStarter.class.getResourceAsStream("/scenes/MarkdownTablePopup.fxml")) {
            markdownTableAnchor = markdownTableLoader.load(markdownTableStream);
        }

        Stage asciidocTableStage = threadService.supply(Stage::new);
        threadService.runActionLater(asciidocTableStage::setScene, new Scene(asciidocTableAnchor));
        asciidocTableStage.setTitle("Table Generator");
        asciidocTableStage.initModality(Modality.WINDOW_MODAL);
        asciidocTableStage.initOwner(scene.getWindow());
        asciidocTableStage.getIcons().add(logoImage);

        Stage markdownTableStage = threadService.supply(Stage::new);
        threadService.runActionLater(markdownTableStage::setScene, new Scene(markdownTableAnchor));
        markdownTableStage.setTitle("Table Generator");
        markdownTableStage.initModality(Modality.WINDOW_MODAL);
        markdownTableStage.initOwner(scene.getWindow());
        markdownTableStage.getIcons().add(logoImage);

        controller.setAsciidocTableAnchor(asciidocTableAnchor);
        controller.setMarkdownTableAnchor(markdownTableAnchor);
        controller.setAsciidocTableStage(asciidocTableStage);
        controller.setAsciidocTableScene(asciidocTableStage.getScene());
        controller.setMarkdownTableStage(markdownTableStage);
        controller.setMarkdownTableScene(markdownTableStage.getScene());

        controller.applyCurrentTheme(asciidocTableStage, markdownTableStage);
        controller.applyCurrentFontFamily(asciidocTableStage, markdownTableStage);

        controller.initializeSaveOnBlur();

        scene.getAccelerators().put(new KeyCodeCombination(KeyCode.S, SHORTCUT_DOWN), controller::saveDoc);
        scene.getAccelerators().put(new KeyCodeCombination(KeyCode.M, SHORTCUT_DOWN), controller::adjustSplitPane);
        scene.getAccelerators().put(new KeyCodeCombination(KeyCode.N, SHORTCUT_DOWN), controller::newDoc);
        scene.getAccelerators().put(new KeyCodeCombination(KeyCode.T, SHORTCUT_DOWN), controller::newDoc);
        scene.getAccelerators().put(new KeyCodeCombination(KeyCode.O, SHORTCUT_DOWN), controller::openDoc);
        scene.getAccelerators().put(new KeyCodeCombination(KeyCode.W, SHORTCUT_DOWN), controller::saveAndCloseCurrentTab);

        final ThreadService threadService = context.getBean(ThreadService.class);

        controller.initializeTabWatchListener();

        threadService.start(() -> {
            try {
                registerStartupListener(config);
            } catch (Exception e) {
                logger.error("Problem occured in startup listener", e);
            }
        });

        scene.getWindow().setOnCloseRequest(controller::closeAllTabs);

        stage.widthProperty().addListener(controller::stageWidthChanged);
        stage.heightProperty().addListener(controller::stageWidthChanged);

    }

    private void setMaximized() {

        Rectangle2D bounds = Screen.getPrimary().getVisualBounds();
        stage.setX(bounds.getMinX());
        stage.setY(bounds.getMinY());
        stage.setWidth(bounds.getWidth());
        stage.setHeight(bounds.getHeight());

    }

    private void registerStartupListener(CmdlineConfig config) {

        final ThreadService threadService = context.getBean(ThreadService.class);
        final TabService tabService = context.getBean(TabService.class);
        final DirectoryService directoryService = context.getBean(DirectoryService.class);

        Path workingDirectory = resolveWorkingDirectory(config);

        threadService.runActionLater(() -> {

            if (workingDirectory != null) {
                directoryService.changeWorkigDir(workingDirectory);
            }

            if (!config.files.isEmpty()) {
                config.files.stream().forEach(f -> {
                    File file = new File(f).getAbsoluteFile();
                    if (file.exists()) {
                        logger.info("Opening file as requsted from cmdline: {}", file);
                        if (workingDirectory == null) {
                            directoryService.changeWorkigDir(file.toPath().getParent());
                        }
                        tabService.addTab(file.toPath());
                    } else {
                        // TODO: do we want to create such a file on demand?
                        logger.error("Cannot open non-existent file: {}", file);
                    }
                });
            }
        });
    }

    private Path resolveWorkingDirectory(CmdlineConfig config) {
        if (config.workingDirectory != null) {
            File workingDirectory = new File(config.workingDirectory);
            if (workingDirectory.isDirectory()) {
                Path absoluteWorkingDirectoryPath = workingDirectory.getAbsoluteFile().toPath();
                return absoluteWorkingDirectoryPath;
            } else {
                logger.error("Can't set path as working directory", new NotDirectoryException(workingDirectory.toString()));
            }
        }

        Set<Path> workingDirectories = config.files.stream()
                .map(Paths::get)
                .map(Path::toAbsolutePath)
                .map(Path::getParent)
                .distinct()
                .mapMulti((Path path, Consumer<Path> consumer) -> {
                    Path currentPath = path;
                    long maxIteration = 50;
                    while ((maxIteration--) > 0 && currentPath != null) {
                        Path localConfigPath = currentPath.resolve(".asciidocfx");
                        if (Files.isReadable(localConfigPath) &&
                                Files.exists(localConfigPath) &&
                                Files.isDirectory(localConfigPath)) {
                            consumer.accept(currentPath);
                            break;
                        } else {
                            currentPath = currentPath.getParent();
                        }
                    }
                })
                .collect(Collectors.toSet());

        if (workingDirectories.size() > 1) {
            logger.error("Resolved multiple working directory candidate." +
                    "Skipping setting working path: ", workingDirectories);
        } else if (workingDirectories.size() == 1) {
            return workingDirectories.iterator().next();
        }

        return null;
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

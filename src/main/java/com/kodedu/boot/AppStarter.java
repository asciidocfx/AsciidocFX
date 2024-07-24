package com.kodedu.boot;

import com.install4j.api.launcher.StartupNotification;
import com.kodedu.config.ConfigurationService;
import com.kodedu.controller.ApplicationController;
import com.kodedu.helper.IOHelper;
import com.kodedu.helper.TaskbarHelper;
import com.kodedu.other.RenderResult;
import com.kodedu.service.DirectoryService;
import com.kodedu.service.FileOpenListener;
import com.kodedu.service.ThreadService;
import com.kodedu.service.convert.docbook.DocBookConverter;
import com.kodedu.service.convert.ebook.EpubConverter;
import com.kodedu.service.convert.html.HtmlBookConverter;
import com.kodedu.service.convert.pdf.PdfBookConverter;
import com.kodedu.service.ui.TabService;
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
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.NotDirectoryException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.awt.Taskbar.Feature.ICON_IMAGE;
import static javafx.scene.input.KeyCombination.SHORTCUT_DOWN;

public class AppStarter extends Application {

    private static Logger logger = LoggerFactory.getLogger(AppStarter.class);

    private static AtomicReference<String> startupParameters = new AtomicReference<>();
    private static CountDownLatch startupParameterLatch = new CountDownLatch(1);

    private static ApplicationController controller;
    private static ConfigurableApplicationContext context;
    private static Stage stage;
    private ThreadService threadService;
    private ConfigurationService configurationService;
    private Image logoImage;
    private long startTime;
    public static CmdlineConfig config;

    @Override
    public void start(final Stage stage) {
        this.startTime = System.currentTimeMillis();
        stage.setTitle("AsciidocFX");
        logoImage = setApplicationIcon(stage);
        Thread.setDefaultUncaughtExceptionHandler((t, e) -> logger.error(e.getMessage(), e));
        Thread.startVirtualThread(() -> {
            loadRequiredFonts();
        });

        Thread.startVirtualThread(() -> {
            try {
                startApp(stage);
            } catch (final Throwable e) {
                logger.error("Problem occured while starting AsciidocFX", e);
            }
        });

    }

    private static void setupMonocle() {
        System.setProperty("monocle.platform", "Headless");
        System.setProperty("glass.platform", "Monocle");
        System.setProperty("prism.order", "sw");
    }

    private static CmdlineConfig parseCmdArgs(String[] args) {
        final CmdlineConfig config = new CmdlineConfig();
        final CmdlineParser cp = new CmdlineParser(config);

        try {
            cp.parse(args);
        } catch (final CmdlineParserException e) {
            System.err.println("Invalid commandline given: " + e.getMessage());
            System.exit(1);
        }

        if (config.help) {
            cp.usage();
            System.exit(0);
        }
        return config;
    }

    private static void setJvmProperties() {
        System.setProperty("sun.net.http.allowRestrictedHeaders", "true");
        System.setProperty("java.awt.headless", "false");
    }

    private static void initializeSSLContext() {
        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, new TrustManager[]{new FakeTrustManager()}, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            HttpsURLConnection.setDefaultHostnameVerifier((h, s) -> true);
            SSLContext.setDefault(sc);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    public static void registerAssociatedFileHandler() {
        StartupNotification.registerStartupListener(parameters -> {
            startupParameters.set(parameters);
            startupParameterLatch.countDown();
        });
    }

    public void loadRequiredFonts() {
        try {
            Files.walk(IOHelper.getInstallationPath().resolve("conf/fonts"))
                    .filter(f -> f.toString().endsWith(".ttf"))
                    .forEach(font -> {
                        try (var in = new FileInputStream(font.toFile())) {
                            IntStream.range(8, 32)
                                    .forEach(size -> {
                                        Font.loadFont(in, size);
                                    });
                        } catch (IOException e) {
                            logger.error("Error when loading font {}", font, e);
                        }
                    });
        } catch (IOException e) {
            logger.warn("Couldn't load fonts", e);
        }
    }

    private void startApp(final Stage stage) throws Throwable {

        this.stage = stage;
        SpringApplication app = new SpringApplication(SpringAppConfig.class);
//        app.setApplicationStartup(new BufferingApplicationStartup(20000));
        context = app.run(new String[]{});
        logger.debug("Spring context loaded in {} ms.", System.currentTimeMillis() - startTime);
        controller = context.getBean(ApplicationController.class);
        threadService = context.getBean(ThreadService.class);
        configurationService = context.getBean(ConfigurationService.class);
        openStartupFiles();

        final FXMLLoader parentLoader = new FXMLLoader();
        parentLoader.setControllerFactory(context::getBean);

        Parent root;
        try (InputStream sceneStream = AppStarter.class.getResourceAsStream("/scenes/AsciidocFX_Scene.fxml")) {
            root = parentLoader.load(sceneStream);
        }

        Scene scene = new Scene(root);
        threadService.runActionLater(stage::setScene, scene);

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
            controller.initializeApp();
            controller.showConfigLoaderOnNewInstall();
            logger.debug("AsciidocFX started in {} ms.", System.currentTimeMillis() - startTime);
        });

        threadService.runActionLater(() -> {
            setMaximized();

            int random = ThreadLocalRandom.current().nextInt(1, 11);
            if (random == 1) {
                controller.showSupportAsciidocFX();
            } else {
                if (controller.getTabPane().getTabs().isEmpty()) {
                    controller.newDoc();
                }
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
        controller.initializeTaskBarPopupMenuListener();

        threadService.start(() -> {
            try {
                controller.waitAdocPreviewReadyLatch();
                registerStartupListener(config);
            } catch (Exception e) {
                logger.error("Problem occured in startup listener", e);
            }
        });

        scene.getWindow().setOnCloseRequest(controller::closeAllTabs);

        stage.widthProperty().addListener(controller::stageWidthChanged);
        stage.heightProperty().addListener(controller::stageWidthChanged);

    }

    private void openStartupFiles() {
        threadService.runTaskLater(() -> {
            try {
                startupParameterLatch.await();
                context.getBean(ApplicationController.class).waitAdocPreviewReadyLatch();
                context.getBean(FileOpenListener.class).startupPerformed(startupParameters.get());
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private Image setApplicationIcon(Stage stage) {
        Image logoImage = null;
        try (InputStream logoStream = AppStarter.class.getResourceAsStream("/logo.png")) {
            logoImage = new Image(logoStream);
            stage.getIcons().clear();
            stage.getIcons().add(logoImage);
            Thread.startVirtualThread(() -> {
                TaskbarHelper.getTaskBar()
                        .filter(t -> t.isSupported(ICON_IMAGE))
                        .ifPresent(t -> {
                            java.awt.Image image = Toolkit.getDefaultToolkit().getImage(AppStarter.class.getResource("/logo.png"));
                            t.setIconImage(image);
                        });
            });
        } catch (Exception e) {

        }
        return logoImage;
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
                        logger.info("Opening file as requested from cmdline: {}", file);
                        if (workingDirectory == null) {
                            directoryService.changeWorkigDir(file.toPath().getParent());
                        }
                        tabService.addTab(file.toPath(), () -> {
                            threadService.runTaskLater(() -> {
                                if (Objects.nonNull(config.backend)) {
                                    processFileConversion(config);
                                }
                            });
                        });
                    } else {
                        // TODO: do we want to create such a file on demand?
                        logger.error("Cannot open non-existent file: {}", file);
                    }
                });
            }
        });
    }

    private void processFileConversion(CmdlineConfig config) {
        Consumer<RenderResult> renderResultConsumer = getRenderResultConsumer();
        if (StringUtils.containsIgnoreCase(config.backend, "pdf")) {
            PdfBookConverter pdfBookConverter = context.getBean(PdfBookConverter.class);
            pdfBookConverter.convert(false, renderResultConsumer);
        } else if (StringUtils.containsIgnoreCase(config.backend, "html")) {
            HtmlBookConverter htmlBookConverter = context.getBean(HtmlBookConverter.class);
            htmlBookConverter.convert(false, renderResultConsumer);
        } else if (StringUtils.containsIgnoreCase(config.backend, "docbook")) {
            DocBookConverter docBookConverter = context.getBean(DocBookConverter.class);
            docBookConverter.convert(false, renderResultConsumer);
        } else if (StringUtils.containsIgnoreCase(config.backend, "epub")) {
            EpubConverter epubConverter = context.getBean(EpubConverter.class);
            epubConverter.convert(false, renderResultConsumer);
        }
    }

    private Consumer<RenderResult> getRenderResultConsumer() {
        return renderResult -> {
            if (renderResult.isSuccessful()) {
                logger.info("Completed {}", renderResult.getDestination());
            } else {
                Exception exception = renderResult.getException();
                logger.info("Error {}", exception.getMessage(), exception);
            }

            if (!config.noQuitAfter) {
                try {
                    stop();
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                }
            }
        };
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
        registerAssociatedFileHandler();
        setJvmProperties();
        config = parseCmdArgs(args);
        logger.info("Args {}", Arrays.stream(args).collect(Collectors.joining(",")));
        logger.info("Parsed cmd configs: {}", config);
        if (config.headless) {
            logger.info("Starting in headless mode..");
            setupMonocle();
        }
        initializeSSLContext();
        launch(args);
    }

}

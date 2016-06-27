package com.kodcu.service;

import com.kodcu.config.StoredConfigBean;
import com.kodcu.controller.ApplicationController;
import com.kodcu.other.Current;
import com.kodcu.service.ui.FileBrowseService;
import javafx.application.Platform;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * Created by usta on 25.12.2014.
 */
@Component
public class DirectoryService {

    private final ApplicationController controller;
    private final FileBrowseService fileBrowser;
    private final Current current;
    private final PathResolverService pathResolver;
    private final StoredConfigBean storedConfigBean;

    private final Logger logger = LoggerFactory.getLogger(DirectoryService.class);

    private Optional<Path> workingDirectory = Optional.of(Paths.get(System.getProperty("user.home")));
    private Optional<File> initialDirectory = Optional.empty();

    private Supplier<Path> pathSaveSupplier;
    private final FileWatchService fileWatchService;
    private final ThreadService threadService;


    @Autowired
    public DirectoryService(final ApplicationController controller, final FileBrowseService fileBrowser, final Current current, PathResolverService pathResolver, StoredConfigBean storedConfigBean, FileWatchService fileWatchService, ThreadService threadService) {
        this.controller = controller;
        this.fileBrowser = fileBrowser;
        this.current = current;
        this.pathResolver = pathResolver;
        this.storedConfigBean = storedConfigBean;
        this.fileWatchService = fileWatchService;
        this.threadService = threadService;

        pathSaveSupplier = () -> {
            final FileChooser chooser = newFileChooser("Save Document");
            chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Asciidoc", "*.adoc", "*.asciidoc", "*.asc", "*.ad", "*.txt", "*.*"));
            chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Markdown", "*.md", "*.markdown", "*.txt", "*.*"));
            File file = chooser.showSaveDialog(null);
            return Objects.nonNull(file) ? file.toPath() : null;
        };

    }

    public DirectoryChooser newDirectoryChooser(String title) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle(title);
        initialDirectory.ifPresent(file -> {
            if (Files.isDirectory(file.toPath()))
                directoryChooser.setInitialDirectory(file);
            else
                directoryChooser.setInitialDirectory(file.toPath().getParent().toFile());
        });
        return directoryChooser;
    }

    public FileChooser newFileChooser(String title) {
        final FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(title);
        initialDirectory.ifPresent(file -> {
            if (Files.isDirectory(file.toPath()))
                fileChooser.setInitialDirectory(file);
            else
                fileChooser.setInitialDirectory(file.toPath().getParent().toFile());
        });

        return fileChooser;
    }

    public Path workingDirectory() {
        return workingDirectory.orElseGet(this::workingDirectorySupplier);
    }

    private Path workingDirectorySupplier() {

        if (!Platform.isFxApplicationThread()) {
            final CompletableFuture<Path> completableFuture = new CompletableFuture<>();
            completableFuture.runAsync(() -> {
                threadService.runActionLater(() -> {
                    try {
                        Path path = workingDirectorySupplier();
                        completableFuture.complete(path);
                    } catch (Exception e) {
                        completableFuture.completeExceptionally(e);
                    }
                });
            }, threadService.executor());
            return completableFuture.join();
        }

        final DirectoryChooser directoryChooser = newDirectoryChooser("Select working directory");
        final File file = directoryChooser.showDialog(null);

        workingDirectory = Optional.ofNullable(file.toPath());

        workingDirectory.ifPresent(fileBrowser::browse);

        return Objects.nonNull(file) ? file.toPath() : null;
    }

    public Path currentPath() {
        return current.currentPath().orElseGet(pathSaveSupplier);
    }

    public Supplier<Path> getPathSaveSupplier() {
        return pathSaveSupplier;
    }

    public void setPathSaveSupplier(Supplier<Path> pathSaveSupplier) {
        this.pathSaveSupplier = pathSaveSupplier;
    }

    public void setWorkingDirectory(Optional<Path> workingDirectory) {
        this.workingDirectory = workingDirectory;
    }

    public Optional<Path> getWorkingDirectory() {
        return workingDirectory;
    }

    public Optional<File> getInitialDirectory() {
        return initialDirectory;
    }

    public void setInitialDirectory(Optional<File> initialDirectory) {
        this.initialDirectory = initialDirectory;
    }

    public void askWorkingDir() {
        DirectoryChooser directoryChooser = this.newDirectoryChooser("Select Working Directory");
        File selectedDir = directoryChooser.showDialog(null);
        if (Objects.nonNull(selectedDir)) {
            changeWorkigDir(selectedDir.toPath());
        }
    }

    public void changeWorkigDir(Path path) {
        if (Objects.isNull(path))
            return;

        storedConfigBean.setWorkingDirectory(path.toString());

        this.setWorkingDirectory(Optional.of(path));
        fileBrowser.browse(path);
        fileWatchService.registerWatcher(path);
        this.setInitialDirectory(Optional.ofNullable(path.toFile()));
        controller.getStage().setTitle(String.format("AsciidocFX - %s", path));

    }

    public void goUp() {
        workingDirectory.map(Path::getParent).ifPresent(this::changeWorkigDir);
    }

    public void refreshWorkingDir() {
        workingDirectory.ifPresent(this::changeWorkigDir);
    }

    public String interPath() {

        try {
            Path workingDirectory = current.currentPath().map(Path::getParent).orElse(this.workingDirectory());
            Path subpath = workingDirectory.subpath(0, workingDirectory.getNameCount());
            return subpath.toString().replace('\\', '/');
        } catch (Exception e) {
            return ".";
        }

    }

    public String interPath(Path path) {

        try {
            Path workingDirectory = Optional.ofNullable(path)
                    .map(Path::getParent).orElse(this.workingDirectory());
            Path subpath = workingDirectory.subpath(0, workingDirectory.getNameCount());
            return subpath.toString().replace('\\', '/');
        } catch (Exception e) {
            return ".";
        }
    }

    public Path getSaveOutputPath(FileChooser.ExtensionFilter extensionFilter, boolean askPath) {

        if (!Platform.isFxApplicationThread()) {
            final CompletableFuture<Path> completableFuture = new CompletableFuture<>();

            completableFuture.runAsync(() -> {
                threadService.runActionLater(() -> {
                    try {
                        Path outputPath = getSaveOutputPath(extensionFilter, askPath);
                        completableFuture.complete(outputPath);
                    } catch (Exception e) {
                        completableFuture.completeExceptionally(e);
                    }
                });
            }, threadService.executor());

            return completableFuture.join();
        }

        boolean isNew = current.currentTab().isNew();

        if (isNew) {
            controller.saveDoc();
        }

        final Path currentTabPath = current.currentPath().get();
        final Path currentTabPathDir = currentTabPath.getParent();
        String tabText = current.getCurrentTabText().replace("*", "").trim();
        tabText = tabText.contains(".") ? tabText.split("\\.")[0] : tabText;

        if (!askPath) {
            return currentTabPathDir.resolve(extensionFilter.getExtensions().get(0).replace("*", tabText));
        }

        final FileChooser fileChooser = this.newFileChooser(String.format("Save %s file", extensionFilter.getDescription()));
        fileChooser.getExtensionFilters().addAll(extensionFilter);
        File file = fileChooser.showSaveDialog(null);

        if (Objects.isNull(file)) {
            return currentTabPathDir.resolve(extensionFilter.getExtensions().get(0).replace("*", tabText));
        }

        return file.toPath();
    }

    public Path findPathInCurrentOrWorkDir(String uri) {

        Optional<Path> inCurrentParent = Optional.empty();
        Optional<Path> inRoot = Optional.empty();

        try {
            inCurrentParent = Optional.ofNullable(current.currentTab().getPath().getParent());
        } catch (Exception e) {
            //no-op
        }

        try {
            if (inCurrentParent.isPresent()) {
                inRoot = inCurrentParent.map(Path::getRoot);
            } else {
                inRoot = workingDirectory.map(Path::getRoot);
            }
        } catch (Exception e) {
            //no-op
        }

        return Stream.<Optional<Path>>of(inCurrentParent, workingDirectory, inRoot)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(path -> path.resolve(uri))
                .filter(Files::exists)
                .findFirst()
                .orElseGet(() -> null);

    }

    public Path findPathInConfigOrCurrentOrWorkDir(String uri) {

        Path configPath = controller.getConfigPath();

        Optional<Path> inConfig = Optional.empty();
        Optional<Path> inCurrentParent = Optional.empty();
        Optional<Path> inRoot = Optional.empty();

        try {
            inConfig = Optional.ofNullable(configPath.resolve("public"));
        } catch (Exception e) {
            //no-op
        }

        try {
            inCurrentParent = Optional.ofNullable(current.currentTab().getPath().getParent());
        } catch (Exception e) {
            //no-op
        }

        try {
            if (inCurrentParent.isPresent()) {
                inRoot = inCurrentParent.map(Path::getRoot);
            } else {
                inRoot = workingDirectory.map(Path::getRoot);
            }
        } catch (Exception e) {
            //no-op
        }

        return Stream.<Optional<Path>>of(inConfig, inCurrentParent, workingDirectory, inRoot)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(path -> path.resolve(uri))
                .filter(Files::exists)
                .findFirst()
                .orElseGet(() -> null);

    }

    public Path findPathInRoot(String uri) {

        return Stream.<Optional<Path>>of(current.currentPath(), workingDirectory)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(Path::getRoot)
                .map(path -> path.resolve(uri))
                .findFirst()
                .orElseGet(() -> null);
    }

    public Path currentParentOrWorkdir() {
        return current.currentPath().map(Path::getParent).orElse(this.workingDirectory());
    }
}

package com.kodedu.service.impl;

import com.kodedu.config.StoredConfigBean;
import com.kodedu.controller.ApplicationController;
import com.kodedu.helper.IOHelper;
import com.kodedu.other.Current;
import com.kodedu.service.DirectoryService;
import com.kodedu.service.PathMapper;
import com.kodedu.service.ThreadService;
import com.kodedu.service.ui.FileBrowseService;
import javafx.application.Platform;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static java.util.Arrays.asList;

/**
 * Created by usta on 25.12.2014.
 */
@Component
public class DirectoryServiceImpl implements DirectoryService {

    @Autowired
    private ApplicationController controller;
    @Autowired
    private FileBrowseService fileBrowser;
    @Autowired
    private ThreadService threadService;
    @Autowired
    private PathMapper pathMapper;
    @Autowired
    private Current current;
    @Autowired
    private StoredConfigBean storedConfigBean;

    private final Logger logger = LoggerFactory.getLogger(DirectoryService.class);

    private Optional<Path> workingDirectory = Optional.of(IOHelper.getPath(System.getProperty("user.home")));
    private Optional<File> initialDirectory = Optional.empty();

    private Supplier<Path> pathSaveSupplier = () -> {
        final FileChooser chooser = newFileChooser("Save Document");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Asciidoc", "*.adoc", "*.asciidoc", "*.asc", "*.ad", "*.txt", "*.*"));
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Markdown", "*.md", "*.markdown", "*.txt", "*.*"));
        File file = chooser.showSaveDialog(null);
        return Objects.nonNull(file) ? file.toPath() : null;
    };

    @Override
    public DirectoryChooser newDirectoryChooser(String title) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle(title);
        initialDirectory.ifPresent(file -> {
            directoryChooser.setInitialDirectory(getChooserInitialDirectory(file));
        });
        return directoryChooser;
    }

    @Override
    public FileChooser newFileChooser(String title) {
        final FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(title);
        initialDirectory.ifPresent(file -> {
            fileChooser.setInitialDirectory(getChooserInitialDirectory(file));
        });

        return fileChooser;
    }

    /**
     * Resolve a file to a valid base browsing directory
     * @param referenceFile The base directory or a child file that should be used as a browsing base path
     * @return A valid directory's File object
     */
    private static File getChooserInitialDirectory(File referenceFile) {
        // Search for an existing directory
        while (referenceFile != null){
            if(Files.isDirectory(referenceFile.toPath())) {
                return referenceFile;
            }
            referenceFile = referenceFile.getParentFile();
        }

        // No parent file could be found or referenceFile
        // Default to returning a path to the first drive available
        return FileSystems.getDefault().getRootDirectories().iterator().next().toFile();
    }

    @Override
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

    @Override
    public Path currentPath() {
        return current.currentPath().orElseGet(pathSaveSupplier);
    }

    @Override
    public Supplier<Path> getPathSaveSupplier() {
        return pathSaveSupplier;
    }

    @Override
    public void setPathSaveSupplier(Supplier<Path> pathSaveSupplier) {
        this.pathSaveSupplier = pathSaveSupplier;
    }

    @Override
    public void setWorkingDirectory(Optional<Path> workingDirectory) {
        this.workingDirectory = workingDirectory;
    }

    @Override
    public Optional<Path> getWorkingDirectory() {
        return workingDirectory;
    }

    @Override
    public Optional<File> getInitialDirectory() {
        return initialDirectory;
    }

    @Override
    public void setInitialDirectory(Optional<File> initialDirectory) {
        this.initialDirectory = initialDirectory;
    }

    @Override
    public void askWorkingDir() {
        DirectoryChooser directoryChooser = this.newDirectoryChooser("Select Working Directory");
        File selectedDir = directoryChooser.showDialog(null);
        if (Objects.nonNull(selectedDir)) {
            changeWorkigDir(selectedDir.toPath());
        }
    }

    @Override
    public void changeWorkigDir(Path path) {
        if (Objects.isNull(path))
            return;

        pathMapper.addRootPath(path);

        storedConfigBean.setWorkingDirectory(path.toString());

        this.setWorkingDirectory(Optional.of(path));
        fileBrowser.browse(path);
        this.setInitialDirectory(Optional.ofNullable(path.toFile()));
        controller.getStage().setTitle(String.format("AsciidocFX - %s", path));

    }

    @Override
    public void goUp() {
        workingDirectory.map(Path::getParent).ifPresent(this::changeWorkigDir);
    }

    @Override
    public void refreshWorkingDir() {
        workingDirectory.ifPresent(this::changeWorkigDir);
    }

    @Override
    public String interPath() {
        return interPath(currentParentOrWorkdir());
    }

    @Override
    public String interPath(Path path) {

        try {
            Path subpath = path.subpath(0, path.getNameCount());
            return subpath.toString().replace('\\', '/');
        } catch (Exception e) {
            return ".";
        }
    }

    @Override
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
        tabText = tabText.contains(".") ? tabText.substring(0, tabText.lastIndexOf(".")) : tabText;

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

    @Override
    public Path findPathInCurrentOrWorkDir(String uri) {

        Optional<Path> lookUpFile = pathMapper.lookUpFile(uri);

        if (lookUpFile.isPresent()) {
            return lookUpFile.get();
        }

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
                .orElse(null);

    }

    @Override
    public Path findPathInPublic(String finalUri) {
        List<String> uris = new ArrayList<>(asList(finalUri, finalUri.replaceFirst("/", "")));
        Path result = null;
        for (String uri : uris) {
            Path configPath = controller.getConfigPath().resolve("public");
            Path resolve = configPath.resolve(uri);
            if (Files.exists(resolve)) {
                result = resolve;
                break;
            }
        }
        return result;
    }

    @Override
    public Path findPathInWorkdirOrLookup(Path uri) {

        Optional<Path> lookUpFile = pathMapper.lookUpFile(uri);

        if (lookUpFile.isPresent()) {
            return lookUpFile.get();
        }

        Optional<Path> optional = current.currentPath().map(Path::getParent);

        if (optional.isPresent()) {
            Path resolve = optional.get().resolve(uri);

            if (Files.exists(resolve)) {
                return resolve;
            }
        }

        Path workingDirectory = workingDirectory();
        Path resolve = workingDirectory.resolve(uri);

        if (Files.exists(resolve)) {
            return resolve;
        }

        if (optional.isPresent()) {
            for (Path currentParent = optional.get(); Objects.nonNull(currentParent); currentParent = currentParent.getParent()) {
                Path candidate = currentParent.resolve(uri);
                if (Files.exists(candidate)) {
                    return candidate;
                }
            }

        }

        return null;

    }

    @Override
    public Path currentParentOrWorkdir() {
        return current.currentPath().map(Path::getParent).orElse(this.workingDirectory());
    }
}

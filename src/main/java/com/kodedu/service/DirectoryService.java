package com.kodedu.service;

import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;

import java.io.File;
import java.nio.file.Path;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * Created by usta on 25.12.2014.
 */
public interface DirectoryService {
    public final static String label = "core::service::DirectoryService";

    /**
     * Notify a working directory update. Sends a nullable {@link java.nio.file.Path}.
     */
    public final static String WORKING_DIRECTORY_UPDATE_EVENT = "event::directory::workingdirectory::update";

    public DirectoryChooser newDirectoryChooser(String title);

    public FileChooser newFileChooser(String title);

    public Path workingDirectory();

    public Path currentPath();

    public Supplier<Path> getPathSaveSupplier();

    public void setPathSaveSupplier(Supplier<Path> pathSaveSupplier);

    public void setWorkingDirectory(Optional<Path> workingDirectory);

    public Optional<Path> getWorkingDirectory();

    public Optional<File> getInitialDirectory();

    public void setInitialDirectory(Optional<File> initialDirectory);

    public void askWorkingDir();

    public void changeWorkigDir(Path path);

    public void goUp();

    public void refreshWorkingDir();

    public String interPath();

    public String interPath(Path path);

    public Path getSaveOutputPath(FileChooser.ExtensionFilter extensionFilter, boolean askPath);

    public Path findPathInCurrentOrWorkDir(String uri);

    public Path findPathInPublic(String finalUri);

    public Path findPathInWorkdirOrLookup(Path uri);

    public Path currentParentOrWorkdir();
}
